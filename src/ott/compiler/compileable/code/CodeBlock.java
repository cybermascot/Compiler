package ott.compiler.compileable.code;

import ott.compiler.compileable.*;

import java.util.*;

import static ott.compiler.compileable.Helper.*;

public class CodeBlock implements Compilable {

    private Code[] code;
    private int variablesAdded;

    @Override
    public void parse(CompilableInfo info) {
        pullToken(info.tokens, "{");
        List<Code> codes = new ArrayList<>();
        Set<String> oldVariables = new HashSet<>(info.variables.keySet());
        while (!isNextValue(info.tokens, "}")) {
            Code c = new Code();
            c.parse(info);
            codes.add(c);
        }
        this.code = codes.toArray(new Code[codes.size()]);

        variablesAdded = info.variables.keySet().size() - oldVariables.size();
        info.stackIndex -= variablesAdded; // lower stack back to where it was
        removeIf(info.variables, k -> !oldVariables.contains(k));

        pullToken(info.tokens, "}");
    }

    @Override
    public void secondParse(Map<String, Integer> functions) {
        for (Code c : code) {
            c.secondParse(functions);
        }
    }

    @Override
    public void generate(StringBuilder builder) {
        for (Code c : code) {
            c.generate(builder);
        }

        for (int i = 0; i < variablesAdded; i++) {
            appendLine(builder, "POP R1");
        }
    }
}
