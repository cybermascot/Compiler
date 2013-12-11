package ott.compiler.compileable;

import ott.*;
import ott.compiler.*;
import ott.compiler.compileable.code.*;
import ott.parsing.*;

import java.util.*;

import static ott.compiler.compileable.Helper.*;

public class FuncDeclaration implements Compilable {

    private String function;
    private CodeBlock code;
    private int stackFrameSize;
    private int numOfParameters;

    @Override
    public void parse(CompilableInfo info) {
        int previousStackIndex = info.stackIndex;
        Map<String, Integer> newVariables = new HashMap<>();

        pullToken(info.tokens, CompilerTokens.FUNCTION);
        function = pullToken(info.tokens, CompilerTokens.LABEL).value();

        String[] params = pullParams(info);
        numOfParameters = params.length;
        info.stackIndex++; // add pushed R14
        code = new CodeBlock();
        code.parse(info);
        info.stackIndex--; // remove pushed R14 (into R15)

        info.stackIndex -= params.length; // pop params off stack (virtually)

        stackFrameSize = info.stackIndex - previousStackIndex;

        // todo remove after block implemented correctly - failsafe here
        assert info.stackIndex == previousStackIndex;
        info.stackIndex = previousStackIndex; // reset stack index
    }

    @Override
    public void secondParse(Map<String, Integer> functions) {
        code.secondParse(functions);
    }

    private String[] pullParams(CompilableInfo info) {
        List<String> parameters = new ArrayList<>();
        pullToken(info.tokens, "(");
        if (!isNextValue(info.tokens, ")")) {
            pullToken(info.tokens, CompilerTokens.VARIABLE);
            parameters.add(pullToken(info.tokens, CompilerTokens.LABEL).value());
        }
        while (!isNextValue(info.tokens, ")")) {
            pullToken(info.tokens, ",");
            pullToken(info.tokens, CompilerTokens.VARIABLE);
            parameters.add(pullToken(info.tokens, CompilerTokens.LABEL).value());
        }
        pullToken(info.tokens, ")");
        String[] params = parameters.toArray(new String[parameters.size()]);

        // add params to virtual stack
        for (String param : params) {
            info.stackIndex++;
            info.variables.put(param, info.stackIndex);
        }
        return params;
    }

    @Override
    public void generate(StringBuilder builder) {
        appendLine(builder, function, ":"); // add function label
        appendLine(builder, "PUSH R14");

        code.generate(builder);

        for (int i = 0; i < stackFrameSize; i++) {
            appendLine(builder, "POP R1"); // TODO fix to be just 'POP' instead of popping into R1
        }

        // value left in R0 from last 'Code' operation will be the return value
        appendLine(builder, "POP R15");
    }

    public int getNumOfParameters() {
        return numOfParameters;
    }

    public String getFunctionName() {
        return function;
    }
}
