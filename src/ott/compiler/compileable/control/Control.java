package ott.compiler.compileable.control;

import ott.compiler.compileable.*;

import java.util.*;

import static ott.compiler.compileable.Helper.*;

public class Control implements Compilable {

    private Compilable control;

    @Override
    public void parse(CompilableInfo info) {
        if (isNextValue(info.tokens, "while")) {
            control = new While();
        } else {
            control = new If();
        }
        control.parse(info);
    }

    @Override
    public void secondParse(Map<String, Integer> functions) {
        control.secondParse(functions);
    }

    @Override
    public void generate(StringBuilder builder) {
        control.generate(builder);
    }
}
