package ott.compiler.compileable;

import ott.compiler.*;
import ott.compiler.compileable.math.*;
import ott.parsing.*;

import java.util.*;

import static ott.compiler.compileable.Helper.appendLine;
import static ott.compiler.compileable.Helper.isNextValue;
import static ott.compiler.compileable.Helper.pullToken;

public class GlobalDeclaration implements Compilable {

    private Expression expression;
    private String variable;

    @Override
    public void parse(CompilableInfo info) {

        pullToken(info.tokens, CompilerTokens.VARIABLE);

        Token label = pullToken(info.tokens, CompilerTokens.LABEL);
        variable = label.value();

        if (info.globals.contains(variable))
            throw new ParseException("Global " + variable + " has already been declared: " + label);

        if (isNextValue(info.tokens, "=")) {
            pullToken(info.tokens, "=");

            expression = new Expression();
            expression.parse(info);
        }

        pullToken(info.tokens, ";");
        info.globals.add(variable);
    }

    @Override
    public void secondParse(Map<String, Integer> functions) {
        if (expression != null)
            expression.secondParse(functions);
    }

    @Override
    public void generate(StringBuilder builder) {
        if (expression != null) {
            expression.generate(builder);
            appendLine(builder, "MOVI R1,", variable);
            appendLine(builder, "STRI R0,R1,0");
        }
    }

    public void generateDeclaration(StringBuilder builder) {
        appendLine(builder, variable, ": WORD=0");
    }

    public String getVariable() {
        return variable;
    }
}
