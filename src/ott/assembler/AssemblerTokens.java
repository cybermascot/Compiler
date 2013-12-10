package ott.assembler;


import ott.assembler.parsables.*;
import ott.assembler.parsables.operations.*;
import ott.assembler.parsables.types.*;
import ott.parsing.*;

public enum AssemblerTokens implements TokenType, Operational {
    COMMENT("/\\*(?>(?:(?>[^*]+)|\\*(?!/))*)\\*/|//.*"),
    LABEL("[a-zA-z]([a-zA-z]|\\d)*:"),
    DATASTMT(DataStmtType.getRegex(), DataStmt.class),
    BRANCH("BL|B", Branch.class),
    RETURN("RET", Return.class),
    PSR(PSRType.getRegex(), PSR.class),
    STACK(StackType.getRegex(), Stack.class),
    DATAPROC(DataProcType.getRegex(), DataProc.class),
    LDRSTR(LdrStrType.getRegex(), LdrStr.class),
    CONDITION(Condition.getRegex()),
    SHIFT(ShiftType.getRegex()),
    PSRSOURCE("CPSR|SPSR"),
    REGISTER("R\\d+"),
    NUMBER("0x(\\d|[A-F])+|\\d+"),
    SPECIAL("(I\\s)|,|-|!|\\{|\\}|=|\\^"),
    NAME("[a-zA-z]([a-zA-z]|\\d)*"), // Alpha Alphanumeric*
    UNKOWN("\\S+"); // aggressive regex for anything not already registered

    private String regex;
    private Class<? extends Parsable> operation;

    private AssemblerTokens(String regex, Class<? extends Parsable> operation) {
        this.regex = regex;
        this.operation = operation;
    }

    private AssemblerTokens(String regex) {
        this.regex = regex;
        operation = null;
    }

    @Override
    public boolean isOperation() {
        return operation != null;
    }

    @Override
    public String regex() {
        return regex;
    }
    
    @Override
    public Parsable getOperation() {
        if (!isOperation()) {
            throw new ParseException("Is not a parsable operation");
        }
        try {
            return operation.newInstance();
        } catch (Exception e) {
            throw new ParseException("Operation must have a no-arg constructor: " + operation);
        }
    }
}
