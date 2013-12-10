package ott.compiler.compileable.code;

import ott.compiler.*;
import ott.compiler.compileable.*;
import ott.compiler.compileable.math.*;
import ott.parsing.*;

import java.util.*;

import static ott.compiler.compileable.Helper.*;

public class VarDeclaration implements Compilable {

    private Expression expression;

    @Override
    public void parse(CompilableInfo info) {
        pullToken(info.tokens, CompilerTokens.VARIABLE);

        Token label = pullToken(info.tokens, CompilerTokens.LABEL);
        String var = label.value();

        if (info.variables.containsKey(var))
            throw new ParseException("Variable " + var + " has already been declared: " + label);

        pullToken(info.tokens, "=");

        expression = new Expression();
        expression.parse(info);

        info.stackIndex++;
        info.variables.put(var, info.stackIndex);
    }

    @Override
    public void secondParse(Map<String, Integer> functions) {
        expression.secondParse(functions);
    }

    @Override
    public void generate(StringBuilder builder) {
        expression.generate(builder); // evaluate expression leaving result in R0
        appendLine(builder, "PUSH R0"); // push result of expression
    }
}
