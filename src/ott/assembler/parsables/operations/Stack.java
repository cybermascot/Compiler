package ott.assembler.parsables.operations;

import ott.*;
import ott.assembler.*;
import ott.assembler.parsables.*;
import ott.assembler.parsables.types.*;
import ott.parsing.*;

import java.util.*;

import static ott.assembler.parsables.Helper.*;

/**<pre>
 1. STACK is PUSH | POP
    STACK REGISTER
    STACK REGISTERLIST
        Where REGISTERLIST is { REGISTERSECTION [, REGISTERSECTION] }
        Where REGISTERSECTION is REGISTER | REGISTER - REGISTER
 2. STACK is PEEK | POKE
    STACK REGISTER [NUMBER]
 3. STACK is INITSTACK
    STACK [NUMBER]
 </pre>*/
public class Stack implements Parsable {

    public Parsable operation;
    private StackType type;
    public final int STACK_MEMORY_LOCATION = 0x00200000;
    public final int STACK_REGISTER = 13;

    @Override
    public void parse(Queue<Token> input, Index index) throws ParseException {
        assert input.peek().type() == AssemblerTokens.STACK;
        type = StackType.getStackType(input.poll().value());
        if (type == StackType.INIT) {
            DataProc dataProc = new DataProc();
            dataProc.type = DataProcType.MOVE;
            dataProc.condition = Condition.ALWAYS;
            dataProc.immediate = true;
            dataProc.registries = new int[]{STACK_REGISTER}; // initialize register 13
            if (!input.isEmpty() && input.peek().type() == AssemblerTokens.NUMBER) {
                dataProc.immediateValue = getNumber(input);
            } else {
                dataProc.immediateValue = STACK_MEMORY_LOCATION;
            }
            operation = dataProc;
        } else {
            LdrStr ldrstr = new LdrStr();
            ldrstr.condition = checkForCondition(input);
            boolean isBlock = !singleRegister(input);
            ldrstr.type = type.isLoadOperation() ? (isBlock ? LdrStrType.LOAD_BLOCK : LdrStrType.LOAD) : (isBlock ? LdrStrType.STORE_BLOCK : LdrStrType.STORE);
            ldrstr.immediate = true;
            ldrstr.writeback = type.needsWriteback() || isBlock; // block always needs writeback - post doesn't - pop does
            ldrstr.preindexing = type.isLoadOperation(); // since stack pointer will point to empty slot-want to use pre-indexing for a load from stack to move back 4 before pulling the value
            if (isBlock) {
                ldrstr.pullRegistersForBlock(input, STACK_REGISTER);
                ldrstr.number = type.isLoadOperation() ? -1 : 1;
                if (!input.isEmpty() && input.peek().type() == AssemblerTokens.SPECIAL) {
                    pullSpecial(input, "^");
                    ldrstr.psr = true;
                }
            } else {
                ldrstr.registries = pullRegistries(input);
                ldrstr.number = type.isLoadOperation() ? -4 : 4; // go back 4 for load - go forward 4 for a push
            }
            if ((type == StackType.PEEK || type == StackType.POKE) && !input.isEmpty() && input.peek().type() == AssemblerTokens.SPECIAL) {
                pullSpecial(input, ",");
                int val = getNumber(input);
                ldrstr.number = val * -4; // peek down the stack as many times as the immediateValue - times 4 per 4 bytes
                ldrstr.number -= 4;
            }
            operation = ldrstr;
        }

        index.inc(); // add one word
    }

    private boolean singleRegister(Queue<Token> input) {
        return input.peek().type() == AssemblerTokens.REGISTER;
    }

    private int[] pullRegistries(Queue<Token> input) {
        int[] registries = new int[2];
        registries[0] = getRegister(input);
        registries[1] = STACK_REGISTER; // always use register 13 for stack parsables
        return registries;
    }

    @Override
    public void secondParse(Map<String, Integer> labels) throws ParseException {
        // do nothing
    }

    @Override
    public int[] toBits() {
        return operation.toBits();
    }

    @Override
    public String toString() {
        return type + "{" +
                "operation=" + operation +
                '}';
    }
}
