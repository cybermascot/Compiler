package ott.compiler.compileable.control;

import ott.compiler.compileable.Compilable;
import ott.compiler.compileable.CompilableInfo;
import ott.compiler.compileable.math.Expression;
import ott.compiler.compileable.types.ConditionOpType;

import java.util.Map;
import static ott.compiler.compileable.Helper.*;

public class Condition implements Compilable {
    private String falseLabel;

    private Expression left;
    private Expression right;
    private ConditionOpType op;

    /**
     * Constructs Condition
     * @param falseLabel label which this condition will branch to if the condition is false
     */
    public Condition(String falseLabel) {
        this.falseLabel = falseLabel;
    }

    @Override
    public void parse(CompilableInfo info) {
        left = new Expression();
        left.parse(info);
        op = ConditionOpType.getCondtionOpType(info.tokens.poll().value());
        right = new Expression();
        right.parse(info);
    }

    @Override
    public void secondParse(Map<String, Integer> functions) {
        left.secondParse(functions);
        right.secondParse(functions);
    }

    @Override
    public void generate(StringBuilder builder) {
        left.generate(builder);
        appendLine(builder, "PUSH R0");
        right.generate(builder);
        appendLine(builder, "POP R1");

        appendLine(builder, "CMP R1,R0");
        appendLine(builder, "B ", op.getNegativeText(), " ", falseLabel); // if (1 == 2) ...  -> B NE falseLabel

    }
}
