package pl.luainterpreter.interpreter;

import lua.LuaBaseVisitor;
import lua.LuaParser.*;
import org.antlr.v4.runtime.tree.TerminalNode;
import pl.luainterpreter.program.function.Function;
import pl.luainterpreter.program.function.FunctionDef;
import pl.luainterpreter.program.Program;
import pl.luainterpreter.program.value.Value;
import pl.luainterpreter.program.value.Value.ValueOperations;
import pl.luainterpreter.program.value.ValueList;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static lua.LuaParser.*;
import static pl.luainterpreter.program.value.Value.NIL;
import static pl.luainterpreter.program.value.Value.ValueOperations.calculate;
import static pl.luainterpreter.program.value.Value.ValueOperations.compare;

public class LuaVisitor extends LuaBaseVisitor<Value> {

    private final Program program = new Program();

    //=============================================
    //================== BLOCK ====================
    //=============================================
    //region Block

    @Override
    public Value visitBlock(BlockContext ctx) {
        for (StatContext statCtx : ctx.stat())
        {
            Value retValue = visit(statCtx);
            if (program.isInLoopBreak()) {
                return NIL;
            }
            if (retValue.isReturn()) {
                return retValue;
            }
        }
        if (ctx.retStat() != null) {
            return visit(ctx.retStat());
        }
        return NIL;
    }

    //endregion
    //=============================================
    //=============== STATEMENTS ==================
    //=============================================
    //region Statements


    @Override
    public Value visitSemicolonStat(SemicolonStatContext ctx) {
        return NIL;
    }

    @Override
    public Value visitBreakStat(BreakStatContext ctx) {
        program.breakLoop();
        return NIL;
    }

    @Override
    public Value visitAssignStat(AssignStatContext ctx) {
        String varName = ctx.var().NAME().getText();
        List<Value> indexList = new ArrayList<>();
        for (TableIndexContext indexCtx : ctx.var().tableIndex()) {
            indexList.add(visit(indexCtx));
        }
        Value value = visit(ctx.exp());
        if (ctx.LOCAL() != null) {
            program.addLocalVar(varName, value);
        } else {
            program.setVar(varName, value, indexList);
        }
        return NIL;
    }

    @Override
    public Value visitIfStat(IfStatContext ctx) {
        Iterator<ExpContext> conditionIterator = ctx.exp().iterator();
        Iterator<BlockContext> blockIterator = ctx.block().iterator();

        while(conditionIterator.hasNext())
        {
            ExpContext expContext = conditionIterator.next(); //IF / ELSEIF
            BlockContext blockContext = blockIterator.next();
            boolean condition = visit(expContext).get(Boolean.class);
            if (condition) {
                return visit(blockContext);
            }
        }
        if (blockIterator.hasNext())
        {
            return visit(blockIterator.next()); //ELSE
        }

        return NIL;
    }

    @Override
    public Value visitFuncDefStat(FuncDefStatContext ctx) {
        String funcName = ctx.NAME().getText();
        List<String> args = ctx.argList().NAME().stream()
                .map(TerminalNode::getText)
                .collect(Collectors.toList());
        program.addFunctionDef(new FunctionDef(funcName, args, ctx.block()));
        return NIL;
    }

    @Override
    public Value visitRetStat(RetStatContext ctx) {
        Value retValue = new Value(visit(ctx.expList()));
        retValue.setReturn(true);
        return retValue;
    }
    //endregion
    //=============================================
    //============== FUNCTION CALLS ===============
    //=============================================
    //region Function calls
    @Override
    public Value visitFuncCall(FuncCallContext ctx) {
        String funcName = ctx.NAME().getText();
        Value argValues = visit(ctx.expList());
        List<Value> argValueList;
        if (argValues instanceof ValueList) {
            argValueList = ((ValueList) argValues).getList();
        } else {
            argValueList = List.of(argValues);
        }
        Function func = program.callFunction(funcName, argValueList);
        Value funcValue = visit(func.getCtx());
        funcValue.setReturn(false);
        program.endFunction();
        return funcValue;
    }

    @Override
    public Value visitPrintCall(PrintCallContext ctx) {
        Value printValue = visit(ctx.expList());
        String printString = "";
        if (printValue instanceof ValueList)
        {
            printString = ((ValueList) printValue).getList().stream()
                    .map(Value::toString)
                    .collect(Collectors.joining("\t"));
        } else {
            printString = printValue.toString();
        }

        System.out.println(printString);
        return NIL;
    }

    @Override
    public Value visitReadCall(ReadCallContext ctx) {
        String valueType = visit(ctx.string()).toString();
        Scanner sc = new Scanner(System.in);
        String value = sc.nextLine();
        sc.close();
        boolean isFloat = value.contains(".");
        switch (valueType) {
            case "*n", "*number" -> {
                return isFloat ?
                        new Value(Float.parseFloat(value), Float.class) :
                        new Value(Integer.parseInt(value), Integer.class);
            }
            default -> {
                return new Value(value, String.class);
            }
        }
    }
    //endregion
    //=============================================
    //=================== LOOPS ===================
    //=============================================
    //region Loops
    @Override
    public Value visitLoopStat(LoopStatContext ctx) {
        program.enterLoop(ctx.getStart().getText());
        visit(ctx.loop());
        program.exitLoop();
        return NIL;
    }

    @Override
    public Value visitDoLoop(DoLoopContext ctx) {
        visit(ctx.block());
        return NIL;
    }

    @Override
    public Value visitWhileLoop(WhileLoopContext ctx) {
        while(visit(ctx.exp()).get(Boolean.class)) {
            visit(ctx.block());
            if (program.isInLoopBreak()) {
                return NIL;
            }
        }
        return NIL;
    }

    @Override
    public Value visitRepeatLoop(RepeatLoopContext ctx) {
        do {
            visit(ctx.block());
            if (program.isInLoopBreak()) {
                return NIL;
            }
        } while (!visit(ctx.exp()).get(Boolean.class));
        return NIL;
    }

    @Override
    public Value visitForLoop(ForLoopContext ctx) {
        String controlVar = ctx.NAME().getText();
        Value controlValue = visit(ctx.exp(0));
        Value stopValue = visit(ctx.exp(1));
        Value step = ctx.exp().size() == 3 ? visit(ctx.exp(2)) : new Value(1, Integer.class);
        boolean stepPositive = compare(GT, step, new Value(0, Integer.class)).get(Boolean.class);
        int stopConditionOp = stepPositive ? GT : LT;

        if (compare(stopConditionOp, controlValue, stopValue).get(Boolean.class)) {
            return NIL;
        }
        program.addLocalVar(controlVar, controlValue);
        do {
            visit(ctx.block());
            if (program.isInLoopBreak()) {
                return NIL;
            }
            controlValue = calculate(ADD, program.getVar(controlVar), step);
            program.setVar(controlVar, controlValue);
        } while (!compare(stopConditionOp, controlValue, stopValue).get(Boolean.class));
        return NIL;
    }

    //endregion
    //=============================================
    //================ EXPRESSIONS ================
    //=============================================
    //region Expressions
    @Override
    public Value visitNilExp(NilExpContext ctx) {
        return NIL;
    }

    @Override
    public Value visitLogical(LogicalContext ctx) {
        if (ctx.FALSE() != null)
        {
            return new Value(false, Boolean.class);
        }
        else if (ctx.TRUE() != null)
        {
            return new Value(true, Boolean.class);
        }
        return NIL;
    }

    @Override
    public Value visitVar(VarContext ctx) {
        String varName = ctx.NAME().getText();
        List<Value> indexList = new ArrayList<>();
        for (TableIndexContext indexCtx : ctx.tableIndex()) {
            indexList.add(visit(indexCtx));
        }
        return program.getVar(varName, indexList);
    }

    @Override
    public Value visitNumber(NumberContext ctx) {
        if (ctx.INT() != null)
        {
            return new Value(Integer.valueOf(ctx.INT().getText()), Integer.class);
        }
        else if (ctx.FLOAT() != null)
        {
            return new Value(Float.valueOf(ctx.FLOAT().getText()), Float.class);
        }
        return NIL;
    }

    @Override
    public Value visitString(StringContext ctx) {
        String text = ctx.getText();
        text = text.substring(1, text.length()-1);
        return new Value(text, String.class);
    }

    @Override
    public Value visitExpList(ExpListContext ctx) {
        List<Value> values = new ArrayList<>();
        for(ExpContext exp : ctx.exp())
        {
            values.add(visit(exp));
        }

        if (values.size() == 1)
        {
            return values.get(0);
        }

        return new ValueList(values);
    }
    //endregion
    //=============================================
    //================== TABLES ===================
    //=============================================
    //region Tables


    @Override
    public Value visitKeyValue(KeyValueContext ctx) {
        Value key = ctx.NAME() == null ? NIL : new Value(ctx.NAME().getText(), String.class);
        Value value = visit(ctx.exp());
        return new Value(Map.entry(key, value), Entry.class);
    }

    @Override
    public Value visitKeyValueList(KeyValueListContext ctx) {
        int numberIndex = 1;
        List<Value> keyValueList = new ArrayList<>();
        for (KeyValueContext keyValueContext : ctx.keyValue()) {
            Value entryValue = visit(keyValueContext);
            Entry entry = entryValue.get(Entry.class);
            if (NIL.equals(entry.getKey())) {
                Value key = new Value(numberIndex++, Integer.class);
                keyValueList.add(new Value(Map.entry(key, entry.getValue()), Entry.class));
            } else {
                keyValueList.add(entryValue);
            }
        }

        return new Value(keyValueList, List.class);
    }

    @Override
    public Value visitTableIndex(TableIndexContext ctx) {
        if (ctx.NAME() != null) {
            return new Value(ctx.NAME().getText(), String.class);
        } else {
            return visit(ctx.exp());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Value visitTableConstructor(TableConstructorContext ctx) {
        Map<Value, Value> tableMap = new HashMap<>();
        if (ctx.keyValueList() != null) {
            List<Value> entryList = visit(ctx.keyValueList()).get(List.class);
            for(Value entryValue : entryList) {
                Entry<Value, Value> entry = entryValue.get(Entry.class);
                tableMap.put(entry.getKey(), entry.getValue());
            }
        }

        return new Value(tableMap, Map.class);
    }

    //endregion
    //=============================================
    //================ OPERATIONS =================
    //=============================================
    //region Operations
    @Override
    public Value visitArithmeticOp(ArithmeticOpContext ctx) {
        Value left = visit(ctx.exp(0));
        Value right = visit(ctx.exp(1));
        return calculate(ctx.op.getType(), left, right);
    }

    @Override
    public Value visitRelationalOp(RelationalOpContext ctx) {
        Value left = visit(ctx.exp(0));
        Value right = visit(ctx.exp(1));
        return compare(ctx.op.getType(), left, right);
    }

    @Override
    public Value visitLogicalOp(LogicalOpContext ctx) {
        boolean left = visit(ctx.exp(0)).get(Boolean.class);
        boolean right = visit(ctx.exp(1)).get(Boolean.class);

        return switch (ctx.op.getType()) {
            case AND -> new Value(left && right, Boolean.class);
            case OR -> new Value(left || right, Boolean.class);
            default -> throw new IllegalStateException("Unexpected logical operation: " + ctx.op.getType());
        };
    }

    @Override
    public Value visitUnaryOp(UnaryOpContext ctx) {
        Value value = visit(ctx.exp());
        return switch (ctx.op.getType()) {
            case NOT -> new Value(!value.get(Boolean.class), Boolean.class);
            case SUB -> ValueOperations.negate(value);
            case LEN -> new Value(value.get(List.class).size(), Integer.class);
            default -> throw new IllegalStateException("Unexpected unary operation: " + ctx.op.getType());
        };
    }

    @Override
    public Value visitConcatOp(ConcatOpContext ctx) {
        Value left = visit(ctx.exp(0));
        Value right = visit(ctx.exp(1));
        return new Value(left.toString() + right.toString(), String.class);
    }

    @Override
    public Value visitPowerOp(PowerOpContext ctx) {
        Value left = visit(ctx.exp(0));
        Value right = visit(ctx.exp(1));
        return ValueOperations.calculatePower(left, right);
    }
    //endregion
}
