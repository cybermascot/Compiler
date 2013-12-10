package ott.compiler.compileable;

import ott.parsing.*;

import java.util.*;

public class CompilableInfo {
    public Queue<Token> tokens;
    public Map<String, Integer> variables;
    public Set<String> globals;
    public int stackIndex;

    public CompilableInfo(Queue<Token> tokens) {
        this.tokens = tokens;
        variables = new HashMap<>();
        globals = new HashSet<>();
        stackIndex = 0;
    }
}
