package ott.compiler.compileable.math;

import ott.compiler.compileable.*;

import java.util.*;

import static ott.compiler.compileable.Helper.*;

public class Constant implements Compilable {
    
    private int number;

    public Constant() {
    }

    public Constant(int num) {
        number = num;
    }
    
    @Override
    public void parse(CompilableInfo info) {
        number = getNumber(info.tokens);
    }

    @Override
    public void secondParse(Map<String, Integer> functions) {
        // do nothing
    }

    @Override
    public void generate(StringBuilder builder) {
        appendLine(builder, "MOVI R0,", toHex(number));
    }
}
