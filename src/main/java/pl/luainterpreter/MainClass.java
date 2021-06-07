package pl.luainterpreter;

import lua.LuaLexer;
import lua.LuaParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import pl.luainterpreter.interpreter.LuaInterpreter;

import java.io.File;
import java.io.IOException;

public class MainClass {
    public static void main(String args[]) throws IOException {
        if (args.length != 1) {
            System.out.println("Wrong number of arguments. Input lua file path should be given as an argument.");
            return;
        }

        String filePath = args[0];
        File inputFile = new File(filePath);

        if(!inputFile.isFile()){
            System.out.println("Given file path is invalid.");
            return;
        }

        CharStream charStream = CharStreams.fromPath(inputFile.toPath());
        LuaLexer lexer = new LuaLexer(charStream);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        LuaParser parser = new LuaParser(tokenStream);

        LuaInterpreter interpreter = new LuaInterpreter();
        interpreter.interpret(parser);
    }
}
