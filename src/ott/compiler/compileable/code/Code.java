package ott.compiler.compileable.code;

import ott.compiler.compileable.*;

import java.util.*;

public class Code implements Compilable {

    private Compilable comp;

    @Override
    public void parse(CompilableInfo info) {
        comp = new Statement();
        comp.parse(info);
    }

    @Override
    public void secondParse(Map<String, Integer> functions) {
        comp.secondParse(functions);
    }

    @Override
    public void generate(StringBuilder builder) {
        comp.generate(builder);
    }
}
