package ott.assembler.parsables.operations;

import ott.*;
import ott.assembler.*;
import ott.assembler.parsables.*;
import ott.assembler.parsables.types.*;
import ott.parsing.*;

import java.util.*;

import static ott.assembler.parsables.Helper.*;

/**<pre>
    B [L] [cond] LABEL
 </pre>*/
public class Branch implements Parsable {

    public Condition condition = null;
    public String label = null;
    public int index = -1;
    public int difference = -1; // difference of current index to index of label = current - labelIndex
    public boolean link;

    @Override
    public void parse(Queue<Token> input, Index index) {
        assert input.peek().type() == AssemblerTokens.BRANCH;

        String branch = input.poll().value(); // pop off the branch command

        link = branch.equals("BL"); // set link if is link

        condition = checkForCondition(input);

        if (input.peek().type() != AssemblerTokens.NAME) // verify it is in fact a label
            throw new ParseException("Expecting name for branch, but was " + input.peek());

        label = input.poll().value();
        this.index = index.inc(); // index after it has moved up // word index
    }

    @Override
    public void secondParse(Map<String, Integer> labels) {
        if (!labels.containsKey(label))
            throw new ParseException("Label not found on second parse: " + label);
        Integer labelIndex = labels.get(label) + 1; // branch uses word value of reference // uses index after or somethign so + 1
        difference = labelIndex - index;
        difference -= 2; // offset for branch command (look ahead thing)
    }

    @Override
    public int[] toBits() {
        int value = condition.get32bitsWithCondition();
        value |= 0x5 << 25; // constant
        value |= binaryValue(link) << 24; // link bit
        value |= convertTo24bit2Comp(difference);
        return new int[] { value };
    }

    @Override
    public String toString() {
        return "B{" +
                "condition=" + condition +
                ", label='" + label + '\'' +
                ", index=" + index +
                ", difference=" + difference +
                ", link=" + link +
                '}';
    }
}
