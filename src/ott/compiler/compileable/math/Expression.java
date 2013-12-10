package ott.compiler.compileable.math;

import ott.compiler.*;
import ott.compiler.compileable.*;

import java.util.*;
import static ott.compiler.compileable.Helper.*;

public class Expression implements Compilable {

    private Compilable[] terms;
    private Boolean[] areAdd;

    @Override
    public void parse(CompilableInfo info) {
        List<? super Compilable> terms = new ArrayList<>();
        List<Boolean> areAdd = new ArrayList<>();

        if (isNextValue(info.tokens, "-")) {
            terms.add(new Constant(0));
        } else {
            Compilable first = new Term();
            first.parse(info);
            terms.add(first);
        }

        // loop through any more +/-
        while (isNextType(info.tokens, CompilerTokens.ADD_OP)) {
            areAdd.add(info.tokens.poll().value().equals("+")); // add true if the next thing is plus sign

            Compilable term = new Term();
            info.stackIndex++;
            term.parse(info);
            info.stackIndex--;
            terms.add(term);
        }
        this.terms = terms.toArray(new Compilable[terms.size()]);
        this.areAdd = areAdd.toArray(new Boolean[areAdd.size()]);
    }

    @Override
    public void secondParse(Map<String, Integer> functions) {
        for (Compilable term : terms) {
            term.secondParse(functions);
        }
    }

    @Override
    public void generate(StringBuilder builder) {
        terms[0].generate(builder);
        for (int i = 0; i < areAdd.length; i++) {
            appendLine(builder, "PUSH R0");
            terms[i+1].generate(builder);
            appendLine(builder, "POP R1");
            if (areAdd[i])
                appendLine(builder, "ADD R0,R0,R1");
            else
                appendLine(builder, "SUB R0,R1,R0"); // todo verify this sub is correct order
        }
    }
}
