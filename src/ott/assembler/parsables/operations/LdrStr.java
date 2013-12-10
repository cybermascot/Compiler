package ott.assembler.parsables.operations;

import ott.*;
import ott.assembler.*;
import ott.assembler.parsables.*;
import ott.assembler.parsables.types.*;
import ott.parsing.*;

import java.util.*;

import static ott.assembler.parsables.Helper.*;

/**<pre>
    1. LDRSTR is LDR | LDRB | STR | STRB
        LDRSTR [cond] I [!] REGISTER, REGISTER, SIGNED
        LDRSTR [cond] [!] REGISTER, REGISTER, REGISTER [, SHIFT NUMBER]
        LDRSTR [cond] [!] REGISTER, REGISTER, REGISTER [, SHIFT REGISTER]
    2. LDRSTR is LDM | STM
        LDRSTR [cond] [!] REGISTER, REGISTERLIST
            Where REGISTERLIST is { REGISTERSECTION [, REGISTERSECTION] }
            Where REGISTERSECTION is REGISTER | REGISTER - REGISTER
 </pre>*/
public class LdrStr implements Parsable {
    Condition condition;
    LdrStrType type;
    int[] registries;
    boolean immediate;
    boolean writeback;
    boolean preindexing;
    boolean psr = false;
    int number;

    // used for shift
    private ShiftType shiftType = null;
    private boolean shiftImmediate;
    private int shiftValue;

    @Override
    public void parse(Queue<Token> input, Index index) {
        assert input.peek().type() == AssemblerTokens.LDRSTR;
        type = LdrStrType.getLdrStrType(input.poll().value());
        preindexing = true; // preindex

        condition = checkForCondition(input);
        if (!type.isBlockTransfer())
            immediate = isImmediate(input);
        writeback = isWriteback(input);

        if (type.isBlockTransfer()) {
            int rn = getRegister(input);
            pullSpecial(input, ",");
            pullRegistersForBlock(input, rn);
            if (!input.isEmpty() && input.peek().type() == AssemblerTokens.SPECIAL) {
                pullSpecial(input, "^");
                psr = true;
            }
        } else {
            if (immediate) {
                params2(input);
                pullSpecial(input, ",");
                number = getSignedNumber(input);
            } else {
                params3(input);
                if (!input.isEmpty() && input.peek().type() == AssemblerTokens.SPECIAL) {
                    pullSpecial(input, ",");
                    if (input.peek().type() != AssemblerTokens.SHIFT)
                        throw new ParseException("Expecting shift type, but was " + input.peek());
                    shiftType = ShiftType.getDataProcType(input.poll().value());
                    if (input.peek().type() == AssemblerTokens.NUMBER) {
                        shiftValue = getNumber(input);
                        shiftImmediate = true;
                    } else if (input.peek().type() == AssemblerTokens.REGISTER) {
                        shiftValue = getRegister(input);
                        shiftImmediate = false;
                    } else {
                        throw new ParseException("Was expecting a number or a register, but was " + input.peek());
                    }
                }
            }
        }

        index.inc(); // add one word
    }

    public void pullRegistersForBlock(Queue<Token> input, int rn) {
        List<Integer> registers = new ArrayList<>();
        registers.add(rn);
        pullSpecial(input, "{");
        do {
            int first = getRegister(input);
            registers.add(first);
            if ("-".equals(input.peek().value())) {
                input.poll(); // pull the dash '-'
                int second = getRegister(input);
                if (second < first)
                    throw new ParseException("Register after dash cannot be smaller than the value before the dash");
                for (int i = first+1; i < second; i++) {
                    registers.add(i);
                }
                registers.add(second);
            }
            if (!"}".equals(input.peek().value()))
                pullSpecial(input, ",");
        } while (input.peek().type() == AssemblerTokens.REGISTER);
        pullSpecial(input, "}");
        registries = toArray(registers);
    }

    private void params2(Queue<Token> input) {
        registries = new int[2];
        pullRegistries(input);
    }

    private void params3(Queue<Token> input) {
        registries = new int[3];
        pullRegistries(input);
    }

    private void pullRegistries(Queue<Token> input) {
        for (int i = 0; i < registries.length; i++) {
            if (i != 0) {
                pullSpecial(input, ","); // pull comma unless it is the first item in list
            }
            registries[i] = getRegister(input);
        }
    }

    @Override
    public void secondParse(Map<String, Integer> labels) {
        // do nothing
    }

    @Override
    public int[] toBits() {
        int value = condition.get32bitsWithCondition();
        value |= binaryValue(preindexing) << 24;
        value |= binaryValue(number >= 0) << 23;
        value |= binaryValue(writeback) << 21;
        value |= binaryValue(type.isLoad()) << 20;

        if (type.isBlockTransfer()) {
            value |= 1 << 27; // constant
            value |= binaryValue(psr) << 22; // PSR & force user bit
            value |= registries[0] << 16; // base register
            for (int i = 1; i < registries.length; i++) {
                value |= 1 << registries[i];
            }
        } else {
            value |= 1 << 26;
            value |= binaryValue(!immediate) << 25;
            value |= binaryValue(type.isByteTransfer()) << 22;
            value |= registries[0] << 12; // source destination register
            value |= registries[1] << 16; // base register
            if (immediate) {
                value |= get12bitImmediate(Math.abs(number));
            } else {
                value |= get12bitRegisterShifted(registries[2], shiftType, shiftImmediate, shiftValue);
            }
        }
        return new int[] { value };
    }

    @Override
    public String toString() {
        return type + "{" +
                "condition=" + condition +
                ", registries=" + Arrays.toString(registries) +
                ", immediate=" + immediate +
                ", writeback=" + writeback +
                ", preindexing=" + preindexing +
                ", immediateValue=" + number +
                '}';
    }
}
