package ott.compiler;

import ott.assembler.*;
import ott.compiler.compileable.*;
import ott.parsing.*;

import java.io.*;
import java.util.*;
import static ott.compiler.compileable.Helper.*;
import static ott.FileIO.*;

public class Compiler {
    private final static String PROGRAM_START_LABEL = "COMPILED_PROGRAM_START";
    private final static int PROGRAM_START = 0x8000;
    private final static int GLOBALS_START = 0x2000;
    private final static int STACK_START = 0x1000;
    private final static int STACK_SIZE = 0x1000;

    public static void compile(File source, File dest) throws IOException{
        String sourceText = readFile(source);

        String result = compile(sourceText);

        writeFile(result, dest);
    }

    private static String compile(String source) {
        Queue<Token> tokens = Tokenizer.tokenize(source, CompilerTokens.values());
        tokens.removeIf(t -> t.type() == CompilerTokens.COMMENT);

        Program program = new Program();
        program.parse(tokens);

        StringBuilder result = new StringBuilder();
        setupInterrupts(result);

        appendLine(result, "ADDRESS = ", toHex(STACK_START));
        appendLine(result, "COMPILED_STACK: BLOCK ", toHex(STACK_SIZE));

        appendLine(result, "ADDRESS = ", toHex(GLOBALS_START));
        program.generateGlobals(result);

        appendLine(result, "ADDRESS = ", toHex(PROGRAM_START));
        appendLine(result, PROGRAM_START_LABEL, ":\r\nMOVI R13,COMPILED_STACK");
        program.generate(result);
        return result.toString();
    }

    public static void setupInterrupts(StringBuilder builder) {
        appendLine(builder, "B ", PROGRAM_START_LABEL); // reset
    }

    public static void main(String[] args) throws Exception {
        File source = new File("program.ott");
        File dest = new File("assembly.asm");
        compile(source, dest);


        System.out.println(readFile(dest));
        Assembler.assemble(dest, new File("kernel.img"));
        System.out.println("Wrote to kernel.img");
    }

//    public static void main(String[] args) throws Exception {
//        Scanner scan = new Scanner(System.in);
//        String line = scan.nextLine();
//        String compiled = compile(line);
//        System.out.println("--------------------");
//        System.out.println(compiled);
//
//        Assembler.assemble(compiled, new File("kernel.img"));
//    }

}
