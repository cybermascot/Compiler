package ott.assembler.parsables.types;
import ott.parsing.*;

public enum DataProcType {
    AND("AND", 0x0),
    OR("ORR", 0xC),
    EXCLUSIVE_OR("EOR", 0x1),
    ADD("ADD", 0x4),
    SUBTRACT("SUB", 0x2),
    SUBTRACT_NOT("BIC", 0xE),
    COMPARE("CMP", 0xA),
    NOT("MVN", 0xF),
    MOVE("MOV", 0xD);

    private String regex;
    private int value;

    private DataProcType(String regex, int value) {
        this.regex = regex;
        this.value = value;
    }

    public int value() {
        return value;
    }

    public boolean altersConditionCodes() {
        return this == DataProcType.COMPARE;
    }

    public boolean takesTwoArguments() {
        return this == COMPARE || this == MOVE || this == NOT;
    }

    public static DataProcType getDataProcType(String dataProcType) {
        for (DataProcType t : DataProcType.values()) {
            if (t.regex.equals(dataProcType))
                return t;
        }
        throw new ParseException("DataProc string is not valid dataProc: " + dataProcType);
    }

    public static String getRegex() {
        StringBuilder regexBuilder = new StringBuilder();
        for (DataProcType type : DataProcType.values()) {
            regexBuilder.append(type.regex);
            regexBuilder.append("|");
        }
        regexBuilder.deleteCharAt(regexBuilder.length() - 1);
        return regexBuilder.toString();
    }
}