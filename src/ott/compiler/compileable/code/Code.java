package ott.compiler.compileable.code;

import ott.compiler.*;
import ott.compiler.compileable.*;
import ott.compiler.compileable.control.*;

import java.util.*;
import static ott.compiler.compileable.Helper.*;


public class Code implements Compilable {

    private Compilable comp;

    @Override
    public void parse(CompilableInfo info) {
        if (isNextType(info.tokens, CompilerTokens.CONTROL)) {
            comp = new Control();
        } else {
            comp = new Statement();
        }
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
