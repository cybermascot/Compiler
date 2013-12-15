package ott.assembler.parsables;

import ott.assembler.*;
import ott.assembler.parsables.types.*;
import ott.parsing.*;

import java.util.*;

public class Helper {

    public static String cleanLabel(String label) {
        return label.replace(":", "");
    }

    public static Condition checkForCondition(Queue<Token> input) {
        if (!input.isEmpty() && input.peek().type() == AssemblerTokens.CONDITION) // pop condition if one exists
            return Condition.getCondition(input.poll().value());
        return Condition.ALWAYS;
    }

    /**
     * polls a register off the queue and returns it
     *
     * @param input
     * @return
     */
    public static int getRegister(Queue<Token> input) {
        if (input.peek().type() != AssemblerTokens.REGISTER)
            throw new ParseException("Expecting a register, but was " + input.peek());
        String regNum = input.poll().value();
        regNum = regNum.replace("R", "");
        int registry = Integer.parseInt(regNum);
        if (registry < 0 || registry > 15)
            throw new ParseException("Registry must be within 0 and 15, but was " + registry);
        return registry;
    }

    /**
     * polls a number off the queue and returns it
     *
     * @param input can be in decimal or hexadecimal format
     * @return
     */
    public static int getNumber(Queue<Token> input) {
        if (input.peek().type() != AssemblerTokens.NUMBER)
            throw new ParseException("Expecting a number, but was " + input.peek());
        String textNum = input.poll().value();
        return getNumber(textNum);
    }

    public static int getNumber(String textNum) {
        int result;
        if (textNum.startsWith("0x")) { // parse hex vs decimal
            textNum = textNum.replace("0x", "");
            result = Integer.parseUnsignedInt(textNum, 16);
        } else {
            result = Integer.parseUnsignedInt(textNum);
        }
        return result;
    }

    public static int getByte(Queue<Token> input) {
        int number = getNumber(input);
        if (number < 0x0 || number > 0xFF)
            throw new ParseException("Byte was smaller than 0 or larger than 255: 0x" + Integer.toHexString(number));
        return number;
    }

    public static int getSignedNumber(Queue<Token> input) {
        int negative = 1;
        if (input.peek().value().equals("-")) {
            input.poll(); // poll the negative sign off the queue
            negative = -1;
        }
        int number = getNumber(input);
        return number * negative;
    }

    public static int convertTo24bit2Comp(int signedNum) {
        if (Math.abs(signedNum) >= 0x800000) // make sure its less than 24 signed bits
            throw new ParseException("Number is more than 24 signed bits: " + signedNum);

        return signedNum & 0xFFFFFF;
    }

    public static int get12bitImmediate(int num) {
        int num12bits = num & 0xFFF; // grab the first 12 bits

        if (num != num12bits)
            throw new ParseException("Number is larger than 12 bits: " + num);
        return num12bits;
    }

    public static boolean isMoreThan8ContiguousBits(int num) {
        int firstBit = -1;
        for (int i = 0, multof2 = 0; i < 32; i++) { // find first bit from right to left
            if (i % 2 == 0)
                multof2 = i;
            if ((num & (1 << i)) != 0) {
                firstBit = multof2;
                break;
            }
        }

        if (firstBit == -1)
            return false; // no bits are on which means its a constant zero

        int value8bit = num & (0xFF << firstBit);
        return num != value8bit;
    }

    public static int get12bitImmediateWithRotate(int num) {
        int firstBit = -1;
        for (int i = 0, multof2 = 0; i < 32; i++) { // find first bit from right to left
            if (i % 2 == 0)
                multof2 = i;
            if ((num & (1 << i)) != 0) {
                firstBit = multof2;
                break;
            }
        }

        if (firstBit == -1)
            return 0; // no bits are on which means its a constant zero

        int value8bit = num & (0xFF << firstBit);
        if (num - value8bit != 0)
            throw new ParseException("Number uses more than 8 contiguous bits: 0x" + Integer.toHexString(num));

        assert (firstBit % 2 == 0);

        int rotate = 32 - firstBit;
        int value = value8bit >> firstBit; // shift amount - plus an extra if the first bit is in odd loc
        value |= (rotate / 2) << 8;

        value &= 0xFFF; // only grab 12 bits in case of roll over

        return value;
    }

    public static int get12bitRegisterShifted(int register, ShiftType type, boolean immediate, int shiftAmount) {
        if (type == null)
            return register;
        int value = register;
        value |= binaryValue(!immediate) << 4; // immediate bit
        value |= type.value() << 5; // shift code
        if (immediate) {
            if (shiftAmount < 0 || shiftAmount > 0b11111)
                throw new ParseException("Immediate shift amount must be less than " + 0b11111 + ", but was " + shiftAmount);
            value |= shiftAmount << 7;
        } else {
            value |= shiftAmount << 8; // if the shift amount is by the register
        }
        assert ((value & 0xFFF) == value); // assert there is only 12 bits in this command
        return value;
    }

    public static void main(String[] args) {
//        int value = 0x20000000;
//        System.out.println(toBinary(value));

        int result = get12bitImmediateWithRotate(0x1000);
        System.out.println(toBinary(result));
    }

    public static String toBinary(int val) {
        StringBuilder builder = new StringBuilder();
        builder.append("0x").append(Integer.toHexString(val)).append(" = ");
        for (int i = 31; i >= 0; i--) {
            String digit = ((1 << i) & val) == 0 ? "0" : "1";
            builder.append(digit).append((i % 4 == 0) ? " " : "");
        }
        return builder.toString();
    }

    public static boolean isImmediate(Queue<Token> input) {
        if (input.peek().type() == AssemblerTokens.SPECIAL &&
                input.peek().value().equals("I ")) {
            input.poll();
            return true;
        }
        return false;
    }

    public static boolean isWriteback(Queue<Token> input) {
        if (input.peek().type() == AssemblerTokens.SPECIAL &&
                input.peek().value().equals("!")) {
            input.poll();
            return true;
        }
        return false;
    }

    public static int binaryValue(boolean bool) {
        return bool ? 1 : 0;
    }

    public static void pullSpecial(Queue<Token> input, String value) {
        if (input.peek().type() != AssemblerTokens.SPECIAL || !input.peek().value().equals(value))
            throw new ParseException("Expected " + value + ", but was " + input.peek());
        input.poll(); // get rid of special
    }

    public static int[] toArray(List<Integer> list) {
        int[] array = new int[list.size()];
        for (int i = 0; i < array.length; i++) {
            array[i] = list.get(i);
        }
        return array;
    }

    public static List<Integer> toList(int[] ints) {
        List<Integer> integers = new ArrayList<>(ints.length);
        for (int i : ints) {
            integers.add(i);
        }
        return integers;
    }

    public static void checkType(Queue<Token> input, AssemblerTokens expected) {
        if (input.peek().type() != expected) {
            throw new ParseException("Expected " + expected.name() + ", but was " + input.peek());
        }
    }

    public static int[] pullFirstBits(Parsable[] parsables) {
        int[] bits = new int[parsables.length];
        for (int i = 0; i < bits.length; i++) {
            bits[i] = parsables[i].toBits()[0];
        }
        return bits;
    }
}
