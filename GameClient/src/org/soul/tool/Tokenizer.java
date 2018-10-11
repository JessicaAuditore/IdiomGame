package org.soul.tool;

public class Tokenizer {

    private String[] tokens;
    private int tokenIndex = 0;

    public Tokenizer(String message, String delimiter) {
        tokens = message.split(delimiter);
    }

    public String nextToken() {
        tokenIndex++;
        return tokens[tokenIndex - 1];
    }
}
