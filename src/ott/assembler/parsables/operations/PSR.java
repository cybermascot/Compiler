package ott.assembler.parsables.operations;

import ott.*;
import ott.assembler.*;
import ott.assembler.parsables.*;
import ott.assembler.parsables.types.*;
import ott.parsing.*;

import java.util.*;

import static ott.assembler.parsables.Helper.*;

/**<pre>
    1. PSR is MRS
        PSR REGISTER, PSRSOURCE
    2. PSR is MSR
        PSR PSRSOURCE, REGISTER
    WHERE PSRSOURCE is CPSR | SPSR
 </pre>*/
public class PSR implements Parsable {

    private Condition cond;
    private PSRType type;
    private int register;
    private boolean csprSource;

    @Override
    public void parse(Queue<Token> input, Index index) throws ParseException {
        checkType(input, AssemblerTokens.PSR);

        type = PSRType.getDataProcType(input.poll().value());

        cond = checkForCondition(input);

        if (type == PSRType.MRS) {
            register = getRegister(input);
            pullSpecial(input, ",");
            checkType(input, AssemblerTokens.PSRSOURCE);
            csprSource = input.poll().value().equals("CPSR");
        } else {
            checkType(input, AssemblerTokens.PSRSOURCE);
            csprSource = input.poll().value().equals("CPSR");
            pullSpecial(input, ",");
            register = getRegister(input);
        }

        index.inc();
    }

    @Override
    public void secondParse(Map<String, Integer> labels) throws ParseException {
        // do nothing
    }

    @Override
    public String toString() {
        return "PSR{" +
                "cond=" + cond +
                ", type=" + type +
                ", register=" + register +
                ", csprSource=" + csprSource +
                '}';
    }

    @Override
    public int[] toBits() {
        int value = cond.get32bitsWithCondition();
        value |= 0b10 << 23;
        value |= binaryValue(!csprSource) << 22;

        if (type == PSRType.MRS) {
            value |= 0b1111 << 16;
            value |= register << 12; // rd
        } else {
            value |= 0b1010011111 << 12;
            value |= register; // rm
        }
        return new int[] { value };
    }
}
