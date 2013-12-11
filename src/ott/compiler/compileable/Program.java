package ott.compiler.compileable;

import ott.*;
import ott.compiler.*;
import ott.parsing.*;

import java.io.*;
import java.util.*;

import static ott.compiler.compileable.Helper.*;

public class Program {

    private static final File SUBROUTINES = new File("subroutines.asm");

    private FuncDeclaration[] funcDeclarations;
    private GlobalDeclaration[] globalDeclarations;

    public void parse(Queue<Token> tokens) {
        CompilableInfo info = new CompilableInfo(tokens);
        Map<String, Integer> functions = new HashMap<>();

        // todo TEMP FUNCTIONS START
        functions.put("Write", 2);
        functions.put("Read", 1);
        functions.put("Delay", 1);
        // TEMP FUNCTIONS END

        List<GlobalDeclaration> vars = new ArrayList<>();
        List<FuncDeclaration> funcs = new ArrayList<>();
        while (!tokens.isEmpty()) {
            if (isNextType(tokens, CompilerTokens.VARIABLE)) {
                GlobalDeclaration var = new GlobalDeclaration();
                var.parse(info);
                vars.add(var);
            } else {
                FuncDeclaration func = new FuncDeclaration();
                func.parse(info);
                funcs.add(func);

                if (functions.containsKey(func.getFunctionName()))
                    throw new ParseException("Function has already been defined: " + func.getFunctionName());

                functions.put(func.getFunctionName(), func.getNumOfParameters());
            }
        }

        if (!functions.containsKey("main"))
            throw new ParseException("Could not find 'main' function");
        if (functions.get("main") != 0)
            throw new ParseException("Function 'main' cannot have any parameters");

        this.funcDeclarations = funcs.toArray(new FuncDeclaration[funcs.size()]);
        this.globalDeclarations = vars.toArray(new GlobalDeclaration[vars.size()]);

        for (FuncDeclaration f : funcDeclarations) {
            f.secondParse(functions);
        }

        for (GlobalDeclaration v : globalDeclarations) {
            v.secondParse(functions);
        }
    }

    public void generate(StringBuilder builder) {
        for (GlobalDeclaration v : globalDeclarations) {
            v.generate(builder);
        }

        appendLine(builder, "B main"); // start of program - branch to main subroutine

        for (FuncDeclaration f : funcDeclarations) {
            f.generate(builder);
        }

        // todo temp subroutines
        try {
            String subroutines = FileIO.readFile(SUBROUTINES);
            appendLine(builder, subroutines);
        } catch (IOException ex) {
            throw new ParseException(ex);
        }
    }

    public void generateGlobals(StringBuilder builder) {
        for (GlobalDeclaration g : globalDeclarations) {
            g.generateDeclaration(builder);
        }
    }
}
