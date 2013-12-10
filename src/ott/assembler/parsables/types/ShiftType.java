package ott.assembler.parsables.types;
import ott.parsing.*;

public enum ShiftType {
    LOGICAL_SHIFT_LEFT("LSL", 0b00),
    LOGICAL_SHIFT_RIGHT("LSR", 0b01),
    ARITHMETIC_SHIFT_RIGHT("ASR", 0b10),
    ROTATE_RIGHT("ROR", 0b11);

    private String regex;
    private int value;

    private ShiftType(String regex, int value) {
        this.regex = regex;
        this.value = value;
    }

    public int value() {
        return value;
    }

    public static ShiftType getDataProcType(String shift) {
        for (ShiftType t : ShiftType.values()) {
            if (t.regex.equals(shift))
                return t;
        }
        throw new ParseException("ShiftType string is not valid shiftType: " + shift);
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
