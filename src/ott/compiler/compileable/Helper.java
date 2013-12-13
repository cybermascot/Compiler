package ott.compiler.compileable;

import ott.compiler.*;
import ott.parsing.*;

import java.util.*;
import java.util.function.*;

public class Helper {

    public static String NEWLINE = "\r\n";

    public static void appendLine(StringBuilder builder, Object ... vals) {
        for (Object val : vals) {
            builder.append(val);
        }
        builder.append(NEWLINE);
    }

    /**
     * polls a number off the queue and returns it
     *
     * @param input can be in decimal or hexadecimal format
     * @return
     */
    public static int getNumber(Queue<Token> input) {
        Token number = pullToken(input, CompilerTokens.NUMBER);
        String textNum = number.value();
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
        if (!input.peek().value().equals("-")) {
            input.poll(); // poll the negative sign off the queue
            negative = -1;
        }
        int number = getNumber(input);
        return number * negative;
    }

    public static Token pullToken(Queue<Token> input, TokenType expected) {
        if (!isNextType(input, expected)) {
            throw new ParseException("Expected: " + expected + " but was: " + input.peek());
        }
        return input.poll();
    }

    public static Token pullToken(Queue<Token> input, String expectedValue) {
        if (!isNextValue(input, expectedValue)) {
            throw new ParseException("Expected: '" + expectedValue + "' but was: " + input.peek());
        }
        return input.poll();
    }

    public static boolean isNextType(Queue<Token> input, TokenType expected) {
        return !input.isEmpty() && input.peek().type() == expected;
    }

    public static boolean isNextValue(Queue<Token> input, String value) {
        return !input.isEmpty() && input.peek().value().equals(value);
    }

    public static String toHex(int i) {
        return "0x" + Integer.toHexString(i).toUpperCase();
    }

    public static Token peek(Queue<Token> input, int deepness) {
        assert deepness >= 0;
        int i = 0;
        for (Token t : input) {
            if (deepness == i)
                return t;
            i++;
        }
        return null;
    }

    public static boolean isFunctionInvoke(Queue<Token> input) {
        boolean ret = input.size() > 2;
        ret = ret && isNextType(input, CompilerTokens.LABEL);
        ret = ret && peek(input, 1).value().equals("("); // see if next item is '('  eg. LABEL(  - then is function invoke
        return ret;
    }

    public static boolean isElseIf(Queue<Token> input) {
        boolean ret = input.size() > 2;
        ret = ret && isNextValue(input, "else");
        ret = ret && peek(input, 1).value().equals("if"); // see if next item is '('  eg. LABEL(  - then is function invoke
        return ret;
    }

    public static <K, V> Map<K, V> copy(Map<K, V> map) {
        Map<K, V> copy = new HashMap<>();
        for (K key : map.keySet()) {
            copy.put(key, map.get(key));
        }
        return copy;
    }

    public static <K, V> void mimic(Map<K,V> source, Map<K,V> destination) {
        destination.clear();
        for (K key : source.keySet()) {
            destination.put(key, source.get(key));
        }
    }

    public static <K> void removeIf(Map<K,?> map, Predicate<K> predicate) {
        Set<K> toRemove = new HashSet<>();
        for (K key : map.keySet()) {
            if (predicate.test(key))
                toRemove.add(key);
        }
        for (K key : toRemove) {
            map.remove(key);
        }
    }
}
