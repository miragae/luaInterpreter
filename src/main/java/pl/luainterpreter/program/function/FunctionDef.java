package pl.luainterpreter.program.function;

import lua.LuaParser.BlockContext;
import java.util.List;

public class FunctionDef {
    private final String name;
    private final BlockContext ctx;
    private final List<String> argNames;

    public FunctionDef(String name, List<String> argNames, BlockContext ctx) {
        this.name = name;
        this.ctx = ctx;
        this.argNames = argNames;
    }

    public String getName() {
        return name;
    }

    public BlockContext getCtx() {
        return ctx;
    }

    public List<String> getArgNames() {
        return argNames;
    }
}
