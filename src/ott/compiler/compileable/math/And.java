package ott.compiler.compileable.math;

import ott.compiler.compileable.*;

import java.util.*;
import static ott.compiler.compileable.Helper.*;

public class And implements Compilable {

    private Compilable expression;
    private And and;

    @Override
    public void parse(CompilableInfo info) {
        expression = new Shift();
        expression.parse(info);

        if (isNextValue(info.tokens, "&")) {
            pullToken(info.tokens, "&");
            and = new And();
            and.parse(info);
        }
    }

    @Override
    public void secondParse(Map<String, Integer> functions) {
        expression.secondParse(functions);
        if (and != null)
            and.secondParse(functions);
    }

    @Override
    public void generate(StringBuilder builder) {
        expression.generate(builder);
        if (and != null) {
            appendLine(builder, "PUSH R0"); // sotre result
            and.generate(builder);
            appendLine(builder, "POP R1");
            appendLine(builder, "AND R0,R0,R1");
        }
    }
}
