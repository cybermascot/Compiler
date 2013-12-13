package ott.compiler.compileable.math;

import ott.compiler.*;
import ott.compiler.compileable.*;
import ott.compiler.compileable.types.*;
import ott.parsing.*;

import java.util.*;

import static ott.compiler.compileable.Helper.*;

public class Shift implements Compilable {

    private MathExpression expression;
    private ShiftType type;
    private Shift shift;

    @Override
    public void parse(CompilableInfo info) {
        expression = new MathExpression();
        expression.parse(info);

        if (isNextType(info.tokens, CompilerTokens.SHIFT)) {
            Token shiftToken = pullToken(info.tokens, CompilerTokens.SHIFT);
            type = ShiftType.getShiftType(shiftToken.value());

            shift = new Shift();
            shift.parse(info);
        }
    }

    @Override
    public void secondParse(Map<String, Integer> functions) {
        expression.secondParse(functions);
        if (shift != null)
            shift.secondParse(functions);
    }

    @Override
    public void generate(StringBuilder builder) {
        expression.generate(builder);
        if (shift != null) {
            appendLine(builder, "PUSH R0");
            shift.generate(builder); // leaves shift amount in R0
            appendLine(builder, "POP R1"); // leaves value in R1
            appendLine(builder, "MOV R0,R1,", type.getSyntax(), " R0");
        }
    }
}
