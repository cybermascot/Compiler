package ott.compiler.compileable.code;

import ott.compiler.*;
import ott.compiler.compileable.*;
import ott.compiler.compileable.math.*;
import ott.parsing.*;

import java.util.*;

import static ott.compiler.compileable.Helper.*;

public class FuncInvoke implements Compilable {

    private String function;
    private Expression[] params;
    private Token functionToken;

    @Override
    public void parse(CompilableInfo info) {
        functionToken = pullToken(info.tokens, CompilerTokens.LABEL);
        function = functionToken.value();
        pullToken(info.tokens, "(");

        pullParams(info);
        info.stackIndex -= params.length;

        pullToken(info.tokens, ")");
    }

    @Override
    public void secondParse(Map<String, Integer> functions) {
        for (Expression e : params) {
            e.secondParse(functions);
        }

        if (!functions.containsKey(function)) {
            throw new ParseException("Function has not been declared: " + functionToken);
        }

        if (functions.get(function) != params.length) {
            throw new ParseException("Function has wrong number of params, was expecting " + functions.get(function) +
                " but was " + params.length + ": " + functionToken);
        }
    }

    private void pullParams(CompilableInfo info) {
        List<Expression> exprs = new ArrayList<>();

        if (!isNextValue(info.tokens, ")")) {
            Expression e = new Expression();
            e.parse(info);
            exprs.add(e);
            info.stackIndex++;
        }
        while (!isNextValue(info.tokens, ")")) {
            pullToken(info.tokens, ",");
            Expression e = new Expression();
            e.parse(info);
            exprs.add(e);
            info.stackIndex++;
        }
        params = exprs.toArray(new Expression[exprs.size()]);
    }

    @Override
    public void generate(StringBuilder builder) {
        // push params on
        for (Expression e : params) {
            e.generate(builder);
            appendLine(builder, "PUSH R0");
        }

        appendLine(builder, "BL ", function);
        // result should be left in R0 from the BL subroutine call

        // pop params off
        for (int i = 0; i < params.length; i++) {
            appendLine(builder, "POP R1"); // TODO change to just 'POP' after implementing it in the assembler
        }
    }
}
