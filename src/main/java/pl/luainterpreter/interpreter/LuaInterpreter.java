package pl.luainterpreter.interpreter;

import lua.LuaParser;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.tree.ParseTree;


public class LuaInterpreter {

    public void interpret(LuaParser parser) {
        prepareParser(parser);
        LuaVisitor visitor = new LuaVisitor();

        try {
            ParseTree tree = parser.chunk();
            visitor.visit(tree);

        } catch (RecognitionException e) {
            e.printStackTrace();
        }
    }

    private void prepareParser(LuaParser parser) {
        parser.setErrorHandler(new BailErrorStrategy());
    }
}
