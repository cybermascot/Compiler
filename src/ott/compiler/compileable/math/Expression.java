package ott.compiler.compileable.math;

import ott.compiler.*;
import ott.compiler.compileable.*;

import java.util.*;
import static ott.compiler.compileable.Helper.*;

public class Expression implements Compilable {

    private Compilable expression;

    @Override
    public void parse(CompilableInfo info) {
        expression = new Or();
        expression.parse(info);
    }

    @Override
    public void secondParse(Map<String, Integer> functions) {
        expression.secondParse(functions);
    }

    @Override
    public void generate(StringBuilder builder) {
        expression.generate(builder);
    }
}
