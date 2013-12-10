package ott.assembler.parsables;

import ott.*;
import ott.assembler.*;
import ott.parsing.*;

import java.util.*;

import static ott.assembler.parsables.Helper.*;


public class Program {

    private static final int RAM_SIZE = 256 * 1024 / 4; // 256 bytes times 1 Killibyte * 1 megabyte / 4 for word

    public int[] parse(Queue<Token> paramInput) {
        Queue<Token> input = new LinkedList<>(paramInput);
        Map<String, Integer> labels = new HashMap<>();
        Map<Integer, Parsable> result = new HashMap<>();
        Index index = new Index();
        while (!input.isEmpty()) {
            parseOperation(input, result, labels, index);
        }
        for (int i : result.keySet()) {
            result.get(i).secondParse(labels);
        }

//        int largest = largest(result.keySet());
//        largest += result.get(largest).toBits().length; // + 1 - 1  // if length is over 1 size should be that much larger but since its size it should be plus one which the array should at least be one

        int[] img = new int[RAM_SIZE]; // FIXME figure out how to replace RAM_SIZE with something more dynamic

        for (int i : result.keySet()) {
            int[] bitsToAdd = result.get(i).toBits();
            int j = i;
            for (int bits : bitsToAdd) {
                img[j] = bits;
                j++;
            }
        }

        return img;
    }

    private int largest(Set<Integer> ints) { // TODO make this method take the map and find the largest using the toBits as well as the index
        int largest = 0;
        for (int i : ints) {
            if (i > largest) {
                largest = i;
            }
        }
        return largest;
    }

    private void parseOperation(Queue<Token> input, Map<Integer, Parsable> result, Map<String, Integer> labels, Index index) {
        if (input.peek().type() == AssemblerTokens.LABEL) {
            labels.put(cleanLabel(input.poll().value()), index.value()); // word value // puts the label in the map (removing the colon) and puts it at the location in the list (hense the size - the next spot to be taken)
        }

        Operational nextType = (Operational) input.peek().type();
        if (!nextType.isOperation()) {
            throw new ParseException("Expecting operation, but was " + input.peek());
        }

        Parsable operation = nextType.getOperation();
        result.put(index.value(), operation);
        operation.parse(input, index);
    }
}
