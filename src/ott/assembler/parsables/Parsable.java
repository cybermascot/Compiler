package ott.assembler.parsables;

import ott.*;
import ott.parsing.*;

import java.util.*;

public interface Parsable {
    void parse(Queue<Token> input, Index index) throws ParseException;

    void secondParse(Map<String, Integer> labels) throws ParseException;

    int[] toBits();
}
