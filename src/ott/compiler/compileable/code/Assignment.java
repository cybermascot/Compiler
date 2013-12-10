package ott.compiler.compileable.code;

import ott.compiler.*;
import ott.compiler.compileable.*;
import ott.compiler.compileable.math.*;
import ott.parsing.*;

import java.util.*;

import static ott.compiler.compileable.Helper.*;

public class Assignment implements Compilable {

    private Expression expression;
    private int distanceDownStack;

    @Override
    public void parse(CompilableInfo info) {
        Token label = pullToken(info.tokens, CompilerTokens.LABEL);
        String var = label.value();

        if (!info.variables.containsKey(var))
            throw new ParseException("Variable " + var + " has not been declared yet: " + label);

        pullToken(info.tokens, "=");

        expression = new Expression();
        expression.parse(info);
        distanceDownStack = info.stackIndex - info.variables.get(var);
    }

    @Override
    public void secondParse(Map<String, Integer> functions) {
        expression.secondParse(functions);
    }

    @Override
    public void generate(StringBuilder builder) {
        expression.generate(builder); // evaluate expression leaving result in R0
        appendLine(builder, "POKE R0,", distanceDownStack);
    }
}
