package pl.luainterpreter.program;

import pl.luainterpreter.program.function.Function;
import pl.luainterpreter.program.function.FunctionDef;
import pl.luainterpreter.program.value.Value;

import java.util.*;

import static java.util.Collections.emptyList;

public class Program {
    private final Map<String, Value> variables;
    private final Map<String, FunctionDef> functions;
    private final Stack<Function> functionStack = new Stack<>();
    private boolean loopBreak = false;

    public Program() {
        this.variables = new HashMap<>();
        this.functions = new HashMap<>();
    }

    public Value getVar(String name) {
        return getVar(name, emptyList());
    }

    public Value getVar(String name, List<Value> indexList) {
        Function currentFunction = functionStack.empty() ? null : functionStack.peek();
        boolean isTableVar = !indexList.isEmpty();
        Value varValue = null;
        if (currentFunction != null) {
            varValue = currentFunction.getVar(name);
        }
        if (varValue == null) {
            varValue = variables.get(name);
        }
        varValue = isTableVar ? getTableVar(varValue, indexList) : varValue;
        return varValue == null ? Value.NIL : varValue;
    }

    @SuppressWarnings("unchecked")
    public Value getTableVar(Value value, List<Value> indexList) {
        for (Value index : indexList) {
            Map<Value, Value> tableMap = value.get(Map.class);
            value = tableMap.get(index);
        }
        return value;
    }

    public void setVar(String name, Value value) {
        setVar(name, value, emptyList());
    }

    public void setVar(String name, Value value, List<Value> indexList) {
        Function currentFunction = functionStack.empty() ? null : functionStack.peek();
        if (currentFunction != null && currentFunction.getVar(name) != null) {
            if (indexList.isEmpty()) {
                currentFunction.addVar(name, value);
            } else {
                addTableVar(currentFunction.getVar(name), value, indexList);
            }

        } else {
            addGlobalVar(name,value, indexList);
        }
    }

    public void addGlobalVar(String name, Value value, List<Value> indexList) {
        if (!indexList.isEmpty()) {
            addTableVar(variables.get(name), value, indexList);
        } else {
            variables.put(name, value);
        }
    }

    public void addLocalVar(String name, Value value) {
        if (functionStack.empty()) {
            addGlobalVar(name, value, List.of());
        } else {
            functionStack.peek().addVar(name, value);
        }
    }

    @SuppressWarnings("unchecked")
    public void addTableVar(Value startTable, Value value, List<Value> indexList) {
        Iterator<Value> indexIterator = indexList.iterator();
        Value table = startTable;
        while (indexIterator.hasNext()) {
            Value index = indexIterator.next();
            Map<Value, Value> tableMap = table.get(Map.class);
            if (!indexIterator.hasNext()) {
                tableMap.put(index, value);
            }
            table = tableMap.get(index);
        }
    }

    public Function callFunction(String name, List<Value> argValues) {
        FunctionDef functionDef = functions.get(name);
        int depth = functionStack.empty() ? 0 : functionStack.peek().getDepth();
        Function function = new Function(functionDef, argValues, ++depth);
        functionStack.push(function);
        return function;
    }

    public void enterLoop(String name) {
        Function loop = new Function(name);
        functionStack.push(loop);
    }

    public void breakLoop()
    {
        loopBreak = true;
    }

    public boolean isInLoopBreak()
    {
        return loopBreak;
    }

    public void exitLoop() {
        loopBreak = false;
        functionStack.pop();
    }

    public void endFunction() {
        functionStack.pop();
    }

    public void addFunctionDef(FunctionDef functionDef) {
        functions.put(functionDef.getName(), functionDef);
    }
}
