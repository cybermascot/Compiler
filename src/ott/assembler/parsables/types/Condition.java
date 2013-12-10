package ott.assembler.parsables.types;
import ott.parsing.*;

public enum Condition {
    EQUAL("EQ", 0x0),
    NOT_EQUAL("NE", 0x1),
    LESS_THAN("LT", 0xB),
    LESS_THAN_OR_EQUAL_TO("LE", 0xD),
    GREATER_THAN("GT", 0xC),
    GREATER_THAN_OR_EQUAL_TO("GE", 0xA),
    ALWAYS("AL", 0xE);

    private String regex;
    private int value;

    private Condition(String regex, int value) {
        this.regex = regex;
        this.value = value;
    }

    public static Condition getCondition(String condition) {
        for (Condition c : Condition.values()) {
            if (c.regex.equals(condition))
                return c;
        }
        throw new ParseException("Condition string is not valid condition: " + condition);
    }

    public int get32bitsWithCondition() {
        return value << 28;
    }

    public static String getRegex() {
        StringBuilder regexBuilder = new StringBuilder();
        for (Condition type : Condition.values()) {
            regexBuilder.append(type.regex);
            regexBuilder.append("|");
        }
        regexBuilder.deleteCharAt(regexBuilder.length() - 1);
        return regexBuilder.toString();
    }
}
