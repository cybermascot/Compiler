package ott.assembler.parsables.operations;


import ott.*;
import ott.assembler.*;
import ott.assembler.parsables.*;
import ott.assembler.parsables.types.*;
import ott.parsing.*;

import java.util.*;

import static ott.assembler.parsables.Helper.*;

/**<pre>
    RET
 </pre>*/
public class Return implements Parsable {

    DataProc ret;

    @Override
    public void parse(Queue<Token> input, Index index) throws ParseException {
        if (input.peek().type() != AssemblerTokens.RETURN)
           throw new ParseException("Was expecting Return, but was " + input.peek());
        input.poll(); // poll return token

        ret = new DataProc();
        ret.type = DataProcType.MOVE;
        ret.condition = checkForCondition(input);
        ret.immediate = false;
        ret.registries = new int[] { 15, 14 }; // move value from 14 to 15
        ret.immediateValue = 0;

        index.inc(); // add one word
    }

    @Override
    public void secondParse(Map<String, Integer> labels) throws ParseException {
        // do nothing
    }

    @Override
    public int[] toBits() {
        return ret.toBits();
    }

    @Override
    public String toString() {
        return "RET";
    }
}
