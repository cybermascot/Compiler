package ott.compiler.compileable.control;

import ott.compiler.compileable.*;
import ott.compiler.compileable.code.*;

import java.util.*;
import static ott.compiler.compileable.Helper.*;

public class ElseIf implements Compilable {

    private String endIf;
    private Condition condition;
    private CodeBlock code;

    private Compilable elseif;
    private String endLabel;

    public ElseIf(String endLabel) {
        this.endLabel = endLabel;
    }

    @Override
    public void parse(CompilableInfo info) {
        endIf = info.generateLabel();
        pullToken(info.tokens, "else");
        pullToken(info.tokens, "if");
        pullToken(info.tokens, "(");
        condition = new Condition(endIf);
        condition.parse(info);
        pullToken(info.tokens, ")");

        code = new CodeBlock();
        code.parse(info);

        if (isNextValue(info.tokens, "else")) {
            elseif = isElseIf(info.tokens) ? new ElseIf(endLabel) : new Else();
            elseif.parse(info);
        }
    }

    @Override
    public void secondParse(Map<String, Integer> functions) {
        condition.secondParse(functions);
        code.secondParse(functions);
        if (elseif != null)
            elseif.secondParse(functions);
    }

    @Override
    public void generate(StringBuilder builder) {
        condition.generate(builder);
        code.generate(builder);

        if (elseif != null) {
            appendLine(builder, "B ", endLabel);
            appendLine(builder, endIf, ":");
            elseif.generate(builder);
        } else {
            appendLine(builder, endIf, ":");
        }
    }
}
