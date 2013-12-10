package ott.compiler.compileable.code;

import ott.compiler.*;
import ott.compiler.compileable.*;
import ott.parsing.*;

import java.util.*;
import java.util.function.*;

import static ott.compiler.compileable.Helper.*;

public class Statement implements Compilable {

    private final static Map<Predicate<Queue<Token>>, Class<? extends Compilable>> PARSABLES = new HashMap<>();

    static {
        PARSABLES.put(q -> isNextType(q, CompilerTokens.VARIABLE), VarDeclaration.class);
        PARSABLES.put(q -> isFunctionInvoke(q), FuncInvoke.class);
        PARSABLES.put(q -> isNextType(q, CompilerTokens.LABEL), Assignment.class);
    }

    private Compilable comp;

    @Override
    public void parse(CompilableInfo info) {
        for (Predicate<Queue<Token>> p : PARSABLES.keySet()) {
            if (p.test(info.tokens)) {
                try {
                    comp = PARSABLES.get(p).newInstance();
                } catch (Exception e) {
                    throw new ParseException("Could not instantiate Parser", e);
                }
                break;
            }
        }

        if (comp == null)
            throw new ParseException("Was expecting a statement, but was " + info.tokens.peek());

        comp.parse(info);

        pullToken(info.tokens, ";");
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
