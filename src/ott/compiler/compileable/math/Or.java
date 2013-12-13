package ott.compiler.compileable.math;

import ott.compiler.compileable.*;

import java.util.*;
import static ott.compiler.compileable.Helper.*;

public class Or implements Compilable {

    private Compilable expression;
    private Or or;

    @Override
    public void parse(CompilableInfo info) {
        expression = new Xor();
        expression.parse(info);

        if (isNextValue(info.tokens, "|")) {
            pullToken(info.tokens, "|");
            or = new Or();
            or.parse(info);
        }
    }

    @Override
    public void secondParse(Map<String, Integer> functions) {
        expression.secondParse(functions);
        if (or != null)
            or.secondParse(functions);
    }

    @Override
    public void generate(StringBuilder builder) {
        expression.generate(builder);
        if (or != null) {
            appendLine(builder, "PUSH R0"); // store result
            or.generate(builder);
            appendLine(builder, "POP R1");
            appendLine(builder, "ORR R0,R0,R1");
        }
    }
}
