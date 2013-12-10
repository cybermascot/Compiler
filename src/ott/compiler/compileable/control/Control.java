package ott.compiler.compileable.control;

import ott.compiler.compileable.*;

import java.util.*;

import static ott.compiler.compileable.Helper.*;

public class Control implements Compilable {

    Compilable control;

    @Override
    public void parse(CompilableInfo info) {
        if (isNextValue(info.tokens, "if")) {
            control = new If();
        } else {
            control = new While();
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
