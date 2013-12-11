package ott.compiler.compileable.types;

import ott.parsing.ParseException;

public enum ConditionOpType {
    EQUAL("==", "NE"),
    NOT_EQUAL("!=", "EQ"),
    LESS_THAN("<", "GE"),
    LESS_THAN_OR_EQUAL("<=", "GT"),
    GREATER_THAN(">", "LE"),
    GREATER_THAN_OR_EQUAL(">=", "LT");

    private String regex;
    private String negative;

    private ConditionOpType(String regex, String negative) {
        this.regex = regex;
        this.negative = negative;
    }

    public String getNegativeText() {
        return negative;
    }

    public static ConditionOpType getCondtionOpType(String conditionOp) {
        for (ConditionOpType t : ConditionOpType.values()) {
            if (t.regex.equals(conditionOp))
                return t;
        }
        throw new ParseException("ConditionOpType string is not valid ConditionOpType: " + conditionOp);
    }

    public static String getRegex() {
        StringBuilder regexBuilder = new StringBuilder();
        for (ConditionOpType type : ConditionOpType.values()) {
            regexBuilder.append(type.regex);
            regexBuilder.append("|");
        }
        regexBuilder.deleteCharAt(regexBuilder.length() - 1);
        return regexBuilder.toString();
    }
}
