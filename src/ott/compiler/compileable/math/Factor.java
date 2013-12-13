package ott.compiler.compileable.math;

import ott.compiler.*;
import ott.compiler.compileable.*;
import ott.compiler.compileable.code.*;

import static ott.compiler.compileable.Helper.*;

import java.util.*;

public class Factor implements Compilable {
    private Compilable value; // can either be expression, a constant, or a variable
    private boolean not = false;

    @Override
    public void parse(CompilableInfo info) {
        if (isNextValue(info.tokens, "~")) {
            not = true;
            pullToken(info.tokens, "~");
        }

        if (isNextValue(info.tokens, "(")) {
            pullToken(info.tokens, "(");
            value = new Expression();
            value.parse(info);
            pullToken(info.tokens, ")");
        } else if (isNextType(info.tokens, CompilerTokens.LABEL)) {
            if (isFunctionInvoke(info.tokens)) {
                value = new FuncInvoke();
                value.parse(info);
            } else {
                // if not function - then must be a variable
                value = new Variable();
                value.parse(info);
            }
        } else {
            value = new Constant();
            value.parse(info);
        }
    }

    @Override
    public void secondParse(Map<String, Integer> functions) {
        value.secondParse(functions);
    }

    @Override
    public void generate(StringBuilder builder) {
        value.generate(builder);
        if (not)
            appendLine(builder, "MVN R0,R0"); // not R0 before moving on
    }
}
