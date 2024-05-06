package com.reign.kat.lib.voice.speech.tokens;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TokenPattern
{
    private final Set<TokenType> pattern;
    
    public TokenPattern(TokenType... types)
    {
        pattern = new HashSet<>(List.of(types));
    }

    public boolean compare(List<Token> tokens)
    {
        Set<Token> deduped = new HashSet<>(tokens);

        int s = 0;
        int total = 0;

        for (Token t :
                deduped)
        {
            if (t.type == pattern.toArray()[s])
            {
                s++;
            }
            else if (t.type != TokenType.NONE)
            {
                s = 0;
            }

            if (s == pattern.size() - 1)
            {
                // That's a match!
                return true;
            }
        }
        return false;
    }
    public boolean compare(Token... tokens)
    {
        return compare(List.of(tokens));
    }
}
