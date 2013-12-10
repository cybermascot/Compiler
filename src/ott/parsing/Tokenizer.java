package ott.parsing;


import java.util.*;
import java.util.regex.*;

public final class Tokenizer {
    private Tokenizer() {

    }

    public static Queue<Token> tokenize(String input, TokenType[] types) throws ParseException {
        int line = 1;
        final Pattern all_types_regex = allTypesPattern(types);
        final Matcher all_matcher = all_types_regex.matcher(input);
        Queue<Token> result = new LinkedList<>();
        while (all_matcher.find()) {
            String matched = all_matcher.group();
            if (matched.matches("\r?\n")) // count immediateValue of lines and keep track to store the line immediateValue of a token
                line++;
            else
                result.add(getToken(types, matched, line));
        }
        return result;
    }

    private static Token getToken(TokenType[] types, String text, int line) throws ParseException {
        for (TokenType type : types) {
            if (exactlyMatchesText(type, text))
                return new Token(type, text, line);
        }
        throw new ParseException("Internal Error: Considered regex input but didn\'t match any TokenType. Text: " + text);
    }

    private static boolean exactlyMatchesText(TokenType type, String text) {
        return text.split(type.regex()).length == 0;
    }

    private static Pattern allTypesPattern(TokenType[] types) {
        StringBuilder regexBuilder = new StringBuilder();
        for (TokenType type : types) {
            regexBuilder.append(type.regex());
            regexBuilder.append("|");
        }
//        regexBuilder.deleteCharAt(regexBuilder.length() - 1);
        regexBuilder.append("\r?\n");
        String regex = regexBuilder.toString();
        return Pattern.compile(regex);
    }

}
