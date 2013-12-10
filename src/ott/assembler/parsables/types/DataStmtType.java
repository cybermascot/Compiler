package ott.assembler.parsables.types;
import ott.parsing.*;

public enum DataStmtType {
    BYTE("BYTE"),
    WORD("WORD"),
    BLOCK("BLOCK"),
    ADDRESS("ADDRESS");

    private final String regex;

    private DataStmtType(String regex) {
        this.regex = regex;
    }

    public static DataStmtType getDataProcType(String dataStmt) {
        for (DataStmtType t : DataStmtType.values()) {
            if (t.regex.equals(dataStmt))
                return t;
        }
        throw new ParseException("DataStmt string is not valid dataStmt: " + dataStmt);
    }

    public static String getRegex() {
        StringBuilder regexBuilder = new StringBuilder();
        for (DataStmtType type : DataStmtType.values()) {
            regexBuilder.append(type.regex);
            regexBuilder.append("|");
        }
        regexBuilder.deleteCharAt(regexBuilder.length() - 1);
        return regexBuilder.toString();
    }
}
