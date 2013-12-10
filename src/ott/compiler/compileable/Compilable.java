package ott.compiler.compileable;

import ott.*;
import ott.parsing.*;

import java.util.*;

public interface Compilable {
    void parse(CompilableInfo info);
    void secondParse(Map<String, Integer> functions);
    void generate(StringBuilder builder);
}
