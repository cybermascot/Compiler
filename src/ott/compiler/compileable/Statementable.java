package ott.compiler.compileable;

public interface Statementable {
    boolean isStatement();
    Compilable getStatement();
}
