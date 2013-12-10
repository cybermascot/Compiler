package ott.assembler.parsables.types;
import ott.parsing.*;

public enum StackType {
    PUSH("PUSH"),
    POKE("POKE"),
    POP("POP"),
    PEEK("PEEK"),
    INIT("INITSTACK");

    private String regex;

    private StackType(String s) {
        this.regex = s;
    }
    public static StackType getStackType(String stackType) {
        for (StackType t : StackType.values()) {
            if (t.regex.equals(stackType))
                return t;
        }
        throw new ParseException("StackType string is not valid stackType: " + stackType);
    }

    public boolean needsWriteback() {
        return this == POP; // peek doesn't want to writeback - push doesn't need writeback because writeback is forced on post indexes
    }

    public boolean isLoadOperation() {
        return this != PUSH && this != POKE;
    }

    public static String getRegex() {
        StringBuilder regexBuilder = new StringBuilder();
        for (StackType type : StackType.values()) {
            regexBuilder.append(type.regex);
            regexBuilder.append("|");
        }
        regexBuilder.deleteCharAt(regexBuilder.length() - 1);
        return regexBuilder.toString();
    }
}
