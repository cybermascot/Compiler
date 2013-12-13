package ott.compiler.compileable.types;

import ott.parsing.*;

public enum ShiftType {
    RIGHT(">>>", "LSR"),
    RIGHT_WITH_SIGN(">>", "ASR"),
    LEFT("<<", "LSL");

    private String regex;
    private String syntax;

    private ShiftType(String regex, String syntax) {
        this.regex = regex;
        this.syntax = syntax;
    }

    public String getSyntax() {
        return syntax;
    }

    public static ShiftType getShiftType(String type) {
        for (ShiftType t : ShiftType.values()) {
            if (t.regex.equals(type))
                return t;
        }
        throw new ParseException("ShiftType string is not valid ShiftType: " + type);
    }

    public static String getRegex() {
        StringBuilder regexBuilder = new StringBuilder();
        for (ShiftType type : ShiftType.values()) {
            regexBuilder.append(type.regex);
            regexBuilder.append("|");
        }
        regexBuilder.deleteCharAt(regexBuilder.length() - 1);
        return regexBuilder.toString();
    }
}
