package ott.assembler.parsables.types;
import ott.parsing.*;

public enum PSRType {
    MRS("MRS"),
    MSR("MSR");

    private String regex;

    private PSRType(String regex) {
        this.regex = regex;
    }

    public static PSRType getDataProcType(String psrType) {
        for (PSRType t : PSRType.values()) {
            if (t.regex.equals(psrType))
                return t;
        }
        throw new ParseException("PSRType string is not valid PSRType: " + psrType);
    }

    public static String getRegex() {
        StringBuilder regexBuilder = new StringBuilder();
        for (PSRType type : PSRType.values()) {
            regexBuilder.append(type.regex);
            regexBuilder.append("|");
        }
        regexBuilder.deleteCharAt(regexBuilder.length() - 1);
        return regexBuilder.toString();
    }
}
