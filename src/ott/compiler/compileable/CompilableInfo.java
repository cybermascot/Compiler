package ott.compiler.compileable;

import ott.parsing.*;

import java.util.*;

public class CompilableInfo {
    public Queue<Token> tokens;
    public Map<String, Integer> variables;
    public Set<String> globals;
    public int stackIndex;

    private int labelIndex;

    public CompilableInfo(Queue<Token> tokens) {
        this.tokens = tokens;
        variables = new HashMap<>();
        globals = new HashSet<>();
        stackIndex = 0;
        labelIndex = 0;
    }

    public String generateLabel() {
        return "LABEL_" + Integer.toHexString(labelIndex++).toUpperCase();
    }
}
