package ott.assembler.parsables.operations;

import ott.*;
import ott.assembler.*;
import ott.assembler.parsables.*;
import ott.assembler.parsables.types.*;
import ott.parsing.*;

import java.util.*;

import static ott.assembler.parsables.Helper.*;

/**
 * <pre>
 * 1. DATAPROC is CMP | MOV
 * DATAPROC {cond} I REGISTER, NUMBER
 * DATAPROC [cond] I REGISTER, LABEL
 * DATAPROC [cond] REGISTER, REGISTER [, SHIFT NUMBER]
 * DATAPROC [cond] REGISTER, REGISTER [, SHIFT REGISTER]
 * 2. DATAPROC is AND | ORR | EOR | ADD | SUB
 * DATAPROC [cond] I REGISTER, REGISTER, NUMBER
 * DATAPROC [cond] REGISTER, REGISTER, REGISTER [, SHIFT NUMBER]
 * DATAPROC [cond] REGISTER, REGISTER, REGISTER [, SHIFT REGISTER]
 * </pre>
 */
public class DataProc implements Parsable {

    private String label = null;

    public DataProcType type = null;
    public Condition condition = null;
    public boolean immediate = false;
    public int immediateValue = -1;
    public int[] registries;

    private DataProc[] alternative = null;

    // used for shift
    private ShiftType shiftType = null;
    private boolean shiftImmediate;
    private int shiftValue;

    @Override
    public void parse(Queue<Token> input, Index index) {
        assert input.peek().type() == AssemblerTokens.DATAPROC;

        type = DataProcType.getDataProcType(input.poll().value());
        condition = checkForCondition(input);
        immediate = isImmediate(input);

        // parse the right immediateValue of params based on type and if it's immediate
        if (type.takesTwoArguments()) {
            if (immediate)
                params(input, 1);
            else
                params(input, 2);
        } else {
            if (immediate)
                params(input, 2);
            else
                params(input, 3);
        }

        if (immediate) {
            pullSpecial(input, ",");
            if (type == DataProcType.MOVE && input.peek().type() == AssemblerTokens.NAME) {
                label = input.poll().value();
            } else {
                immediateValue = getNumber(input);
            }
        } else { // not immediate
            if (!input.isEmpty() && input.peek().type() == AssemblerTokens.SPECIAL) { // check if next is special, because if it is it has a value - otherwise ignore because this isn't required
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
        if (label != null) {
            index.add(5); // labels will always use 5 slots
        } else if (immediate && type == DataProcType.MOVE && isMoreThan8ContiguousBits(immediateValue)) {
            setupForMoreThan8Bits();
            index.add(5);
        } else {
            index.inc(); // add one word
        }
    }

    private void params(Queue<Token> input, int amount) {
        registries = new int[amount];
        pullRegistries(input);
    }

    private void pullRegistries(Queue<Token> input) {
        for (int i = 0; i < registries.length; i++) {
            if (i != 0) {
                pullSpecial(input, ",");
            }
            registries[i] = getRegister(input);
        }
    }

    @Override
    public void secondParse(Map<String, Integer> labels) {
        if (label != null) {
            immediateValue = labels.get(label) * 4; // conver to byte format
            setupForMoreThan8Bits();
        }
    }

    private void setupForMoreThan8Bits() {
        List<DataProc> dataProcs = new ArrayList<>(4);
        dataProcs.add(createMOV0(registries[0]));

        // setup orr dataprocs to move orr the value into
        for (int i = 0; i < 4; i++) {
            int shiftedNum = immediateValue & (0xFF << (i * 8));
//            if (shiftedNum != 0)
            dataProcs.add(createORR(registries[0], shiftedNum));
        }

        this.alternative = dataProcs.toArray(new DataProc[dataProcs.size()]);
//        index.add(alternative.length);
    }

    @Override
    public int[] toBits() {
        if (alternative != null)
            return pullFirstBits(alternative);

        int value = condition.get32bitsWithCondition();
        value |= binaryValue(immediate) << 25;
        value |= type.value() << 21; // opcode
        value |= binaryValue(type.altersConditionCodes()) << 20;
        int shiftedRegister = -1; // only used if not immediate
        if (type.takesTwoArguments()) {
            if (type == DataProcType.COMPARE)
                value |= registries[0] << 16; // source register
            else
                value |= registries[0] << 12; // destination register
            if (!immediate)
                shiftedRegister = registries[1];
        } else {
            value |= registries[0] << 12; // destination register
            value |= registries[1] << 16; // source register
            if (!immediate)
                shiftedRegister = registries[2];
        }
        if (immediate) {
            value |= get12bitImmediateWithRotate(immediateValue);
        } else {
            value |= get12bitRegisterShifted(shiftedRegister, shiftType, shiftImmediate, shiftValue);
        }

        return new int[]{value};
    }

    @Override
    public String toString() {
        return type + "{" +
                "condition=" + condition +
                ", immediate=" + immediate +
                ", immediateValue=0x" + Integer.toHexString(immediateValue).toUpperCase() +
                ", registries=" + Arrays.toString(registries) +
                '}';
    }

    private static DataProc createORR(int register, int number) {
        DataProc proc = new DataProc();
        proc.condition = Condition.ALWAYS;
        proc.type = DataProcType.OR;
        proc.immediate = true;
        proc.immediateValue = number;
        proc.registries = new int[]{
                register,
                register
        };
        return proc;
    }

    private static DataProc createMOV0(int register) {
        DataProc proc = new DataProc();
        proc.condition = Condition.ALWAYS;
        proc.type = DataProcType.MOVE;
        proc.immediate = true;
        proc.immediateValue = 0;
        proc.registries = new int[]{
                register
        };
        return proc;
    }
}

