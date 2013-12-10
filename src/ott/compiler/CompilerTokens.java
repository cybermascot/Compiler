package ott.compiler;

import ott.compiler.compileable.*;
import ott.compiler.compileable.code.*;
import ott.parsing.*;

public enum CompilerTokens implements TokenType {
    COMMENT("/\\*(?>(?:(?>[^*]+)|\\*(?!/))*)\\*/|//.*"),
    ADD_OP("\\+|-"),
    MUL_OP("\\*"),
    NUMBER("0x(\\d|[A-F])+|\\d+"),
    SPECIAL("\\(|\\)|=|;|,"),
    FUNCTION("function "),
    VARIABLE("var "),
//    CONTROL("if|else|while|do|for", Control.class),
    LABEL("[a-zA-z]([a-zA-z]|\\d)*"),
    UNKOWN("\\S+"); // aggressive regex for anything not already registered

    private String regex;
//    private Class<? extends Compilable> statement;

    private CompilerTokens(String regex) {
        this.regex = regex;
//        statement = null;
    }

//    private CompilerTokens(String regex, Class<? extends Compilable> statement) {
//        this.regex = regex;
//        this.statement = statement;
//    }

    @Override
    public String regex() {
        return regex;
    }

//    @Override
//    public boolean isStatement() {
//        return statement != null;
//    }
//
//    @Override
//    public Compilable getStatement() {
//        if (!isStatement()) {
//            throw new ParseException("Is not a compilable statement");
//        }
//        try {
//            return statement.newInstance();
//        } catch (Exception e) {
//            throw new ParseException("Statement must have a no-arg constructor: " + statement);
//        }
//    }
}
