package ott.compiler.compileable.control;

import ott.compiler.compileable.*;
import ott.compiler.compileable.code.*;

import java.util.*;
import static ott.compiler.compileable.Helper.*;

public class Else implements Compilable {

    private CodeBlock code;

    @Override
    public void parse(CompilableInfo info) {
        pullToken(info.tokens, "else");
        code = new CodeBlock();
        code.parse(info);
    }

    @Override
    public void secondParse(Map<String, Integer> functions) {
        code.secondParse(functions);
    }

    @Override
    public void generate(StringBuilder builder) {
        code.generate(builder);
    }
}
