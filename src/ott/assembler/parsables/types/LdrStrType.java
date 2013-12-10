package ott.assembler.parsables.types;
import ott.parsing.*;

public enum LdrStrType {
    LOAD_BYTE("LDRB"),
    LOAD("LDR"),
    STORE_BYTE("STRB"),
    STORE("STR"),
    LOAD_BLOCK("LDM"),
    STORE_BLOCK("STM");

    private String regex;

    private LdrStrType(String regex) {
        this.regex = regex;
    }

    public boolean isBlockTransfer() {
        return this == LOAD_BLOCK || this == STORE_BLOCK;
    }

    public boolean isLoad() {
        return this == LOAD || this == LOAD_BLOCK || this == LOAD_BYTE;
    }

    public boolean isByteTransfer() {
        return this == LOAD_BYTE || this == STORE_BYTE;
    }

    public static LdrStrType getLdrStrType(String ldrstrType) {
        for (LdrStrType t : LdrStrType.values()) {
            if (t.regex.equals(ldrstrType))
                return t;
        }
        throw new ParseException("LdrStr string is not valid ldrStr: " + ldrstrType);
    }

    public static String getRegex() {
        StringBuilder regexBuilder = new StringBuilder();
        for (LdrStrType type : LdrStrType.values()) {
            regexBuilder.append(type.regex);
            regexBuilder.append("|");
        }
        regexBuilder.deleteCharAt(regexBuilder.length() - 1);
        return regexBuilder.toString();
    }
}