package ott.compiler.compileable.math;

import ott.compiler.*;
import ott.compiler.compileable.*;

import java.util.*;

import static ott.compiler.compileable.Helper.*;

public class Term implements Compilable {
    private Factor first;
    private Term second;
    private boolean isMulOp;

    @Override
    public void parse(CompilableInfo info) {
        first = new Factor();
        first.parse(info);
        if (isNextType(info.tokens, CompilerTokens.MUL_OP)) {
            isMulOp = info.tokens.poll().value().equals("*");
            second = new Term();
            info.stackIndex++;
            second.parse(info);
            info.stackIndex--;
        }
    }

    @Override
    public void secondParse(Map<String, Integer> functions) {
        first.secondParse(functions);
        if (second != null)
            second.secondParse(functions);
    }

    @Override
    public void generate(StringBuilder builder) {
        first.generate(builder);
        if (second != null) {
            appendLine(builder, "PUSH R0");
            second.generate(builder);
            appendLine(builder, "POP R1");
            if (isMulOp)
                appendLine(builder, "MUL R0,R0,R1");
            else
                appendLine(builder, "DIV R0,R1,R0");
        }
    }
}
