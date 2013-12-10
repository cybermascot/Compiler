package ott.compiler.compileable.code;

import ott.compiler.*;
import ott.compiler.compileable.*;
import ott.parsing.*;

import java.util.*;

import static ott.compiler.compileable.Helper.*;

public class Variable implements Compilable {
    private int deepness;
    private String variable = null;

    @Override
    public void parse(CompilableInfo info) {
        Token label = pullToken(info.tokens, CompilerTokens.LABEL);
        String var = label.value();

        if (info.globals.contains(var)) {
            variable =  var;
        } else {
            if (!info.variables.containsKey(var))
                throw new ParseException("Variable has not been declared: " + var);

            deepness = info.stackIndex - info.variables.get(var);
        }
    }

    @Override
    public void secondParse(Map<String, Integer> functions) {
        // do nothing
    }

    @Override
    public void generate(StringBuilder builder) {
        if (variable == null) { // stack variable
            appendLine(builder, "PEEK R0," + deepness);
        } else { // global variable
            appendLine(builder, "MOVI R1,", variable);
            appendLine(builder, "LDRI R0,R1,0");
        }
    }
}
