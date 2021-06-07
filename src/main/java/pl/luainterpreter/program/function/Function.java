package pl.luainterpreter.program.function;

import lua.LuaParser.BlockContext;
import pl.luainterpreter.program.value.Value;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Function {
    private final Map<String, Value> variables = new HashMap<>();
    private final String name;
    private int depth;
    private BlockContext ctx;

    public Function(FunctionDef functionDef, List<Value> argValues, int depth) {
        this.name = functionDef.getName() + "@" + depth;
        this.depth = depth;
        this.ctx = functionDef.getCtx();
        setArgs(functionDef.getArgNames(), argValues);
    }

    public Function(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public BlockContext getCtx() {
        return ctx;
    }

    public int getDepth() {
        return depth;
    }

    public void addVar(String name, Value value) {
        variables.put(name, value);
    }

    public Value getVar(String name) {
        return variables.get(name);
    }

    private void setArgs(List<String> argNames, List<Value> argValues) {
        Iterator<Value> valueIterator = argValues.iterator();
        Iterator<String> nameIterator = argNames.iterator();

        while(valueIterator.hasNext())
        {
            String name = nameIterator.next();
            Value value = valueIterator.next();
            variables.put(name, value);
        }
    }
}
