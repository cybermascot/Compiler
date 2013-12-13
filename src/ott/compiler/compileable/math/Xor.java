package ott.compiler.compileable.math;

import ott.compiler.compileable.*;

import java.util.*;
import static ott.compiler.compileable.Helper.*;

public class Xor implements Compilable {

    private Compilable expression;
    private Xor xor;

    @Override
    public void parse(CompilableInfo info) {
        expression = new And();
        expression.parse(info);

        if (isNextValue(info.tokens, "^")) {
            pullToken(info.tokens, "^");
            xor = new Xor();
            xor.parse(info);
        }
    }

    @Override
    public void secondParse(Map<String, Integer> functions) {
        expression.secondParse(functions);
        if (xor != null)
            xor.secondParse(functions);
    }

    @Override
    public void generate(StringBuilder builder) {
        expression.generate(builder);
        if (xor != null) {
            appendLine(builder, "PUSH R0"); // store result
            xor.generate(builder);
            appendLine(builder, "POP R1");
            appendLine(builder, "EOR R0,R0,R1");
        }
    }
}
