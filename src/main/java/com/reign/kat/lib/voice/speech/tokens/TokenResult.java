package com.reign.kat.lib.voice.speech.tokens;

import java.util.List;

public class TokenResult
{
    public List<Token> tokens;
    public String text;

    public TokenResult(List<Token> tokens, String text)
    {
        this.tokens = tokens;
        this.text = text;
    }
}
