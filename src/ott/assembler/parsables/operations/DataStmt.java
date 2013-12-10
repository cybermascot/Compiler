package ott.assembler.parsables.operations;

import ott.*;
import ott.assembler.*;
import ott.assembler.parsables.*;
import ott.assembler.parsables.types.*;
import ott.parsing.*;

import java.util.*;

import static ott.assembler.parsables.Helper.*;


/**<pre>
    1. DATASTMT is BLOCK
    DATASTMT NUMBER
    2. DATASTMT is BYTE
    DATASTMT = NUMBER [, NUMBER]*
        WHERE NUMBER is between 0 and 255
    3. DATASTMT is WORD
    DATASTMT = NUMBER [, NUMBER]*
        WHERE NUMBER is between 0 and 0xFFFFFFFF
    4. DATASTMT is ADDRESS
    DATASTMT = NUMBER
</pre>*/
public class DataStmt implements Parsable {

    private DataStmtType type;
    private int[] result;

    @Override
    public void parse(Queue<Token> input, Index index) throws ParseException {
        if (input.peek().type() != AssemblerTokens.DATASTMT)
            throw new ParseException("Was expecting type datastmt, but was " + input.peek());
        type = DataStmtType.getDataProcType(input.poll().value());

        if (type == DataStmtType.BLOCK) {
            int amount = getNumber(input);
            amount /= 4; // word value
            result = new int[amount];
            index.add(amount);
        } else if (type == DataStmtType.ADDRESS) {
            pullSpecial(input, "=");
            int address = getNumber(input);
            address /= 4; // put into word format
            index.value(address); // set the index - for the next command to be that address
            result = new int[0]; // empty array because no data should be taken for this command
        } else if (type == DataStmtType.WORD) {
            pullSpecial(input, "=");
            List<Integer> words = new ArrayList<>(8); // guess 8 so it doesn't have to grow much
            int firstWord = getNumber(input);
            words.add(firstWord);
            while (!input.isEmpty() && input.peek().type() == AssemblerTokens.SPECIAL &&
                    input.peek().value().equals(",")) {
                pullSpecial(input, ",");
                words.add(getNumber(input));
            }
            result = toArray(words);
            index.add(result.length); // word value because result array is in words
        } else if (type == DataStmtType.BYTE) {
            pullSpecial(input, "=");
            List<Integer> bytes = new ArrayList<>(8);
            int firstByte = getByte(input);
            bytes.add(firstByte);
            while (!input.isEmpty() && input.peek().type() == AssemblerTokens.SPECIAL &&
                    input.peek().value().equals(",")) {
                pullSpecial(input, ",");
                bytes.add(getByte(input));
            }
            int[] byteArray = toArray(bytes);
            result = convertBytesToWords(byteArray);
            index.add(result.length); // result is in word length
        } else {
            throw new ParseException("Invalid DATASTMT type: " + type);
        }
    }

    private static int[] convertBytesToWords(int[] bytes) {
        boolean leftOver = bytes.length % 4 != 0;
        int extra = (leftOver) ? 1 : 0;
        int[] words = new int[bytes.length / 4 + extra];

        for (int b = 0, w = -1; b < bytes.length; b++) {
            int rem = b % 4;
            if (rem == 0) {
                w++;
            }
            words[w] |= bytes[b] << (8 * rem);
        }

        // bad code, but what the for loop above is doing in a safer manner
//        for (int i = 0, j = 0; i < words.length; i++, j+=4) {
//            words[i] |= bytes[j];
//            words[i] |= bytes[j+1] << 8;
//            words[i] |= bytes[j+2] << 16;
//            words[i] |= bytes[j+3] << 24;
//        }
        return words;
    }

    @Override
    public void secondParse(Map<String, Integer> labels) throws ParseException {
        // do nothing
    }

    @Override
    public int[] toBits() {
        return result;
    }

    public DataStmtType getType() {
        return type;
    }
}
