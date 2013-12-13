package ott.compiler;

import ott.compiler.compileable.types.*;
import ott.parsing.*;

public enum CompilerTokens implements TokenType {
    COMMENT("/\\*(?>(?:(?>[^*]+)|\\*(?!/))*)\\*/|//.*"),
    ADD_OP("\\+|-"),
    MUL_OP("\\*"),
    NUMBER("0x(\\d|[A-F])+|\\d+"),
    SHIFT(ShiftType.getRegex()),
    CONDITION_OP(ConditionOpType.getRegex()),
    SPECIAL("\\(|\\)|=|;|,"),
    FUNCTION("function "),
    VARIABLE("var "),
    BITWISE("&|\\||\\^|~"),
    CONTROL("if|else|while"), // do|for
    LABEL("[a-zA-z]([a-zA-z]|\\d)*"),
    UNKOWN("\\S+"); // aggressive regex for anything not already registered

    private String regex;

    private CompilerTokens(String regex) {
        this.regex = regex;
    }

    @Override
    public String regex() {
        return regex;
    }
}
