package ott.compiler.compileable.control;

import ott.compiler.compileable.*;
import ott.compiler.compileable.code.*;

import java.util.*;
import static ott.compiler.compileable.Helper.*;

public class While implements Compilable {
    private String beforeLabel;
    private String afterLabel;
    private Condition condition;
    private CodeBlock code;

    @Override
    public void parse(CompilableInfo info) {
        beforeLabel = info.generateLabel();
        afterLabel = info.generateLabel();
        pullToken(info.tokens, "while");
        pullToken(info.tokens, "(");
        condition = new Condition(afterLabel);
        condition.parse(info);
        pullToken(info.tokens, ")");

        code = new CodeBlock();
        code.parse(info);
    }

    @Override
    public void secondParse(Map<String, Integer> functions) {
        condition.secondParse(functions);
        code.secondParse(functions);
    }

    @Override
    public void generate(StringBuilder builder) {
        appendLine(builder, beforeLabel, ":");
        condition.generate(builder);
        code.generate(builder);
        appendLine(builder, "B ", beforeLabel);
        appendLine(builder, afterLabel, ":");
    }
}
