package com.reign.kat.lib.voice.speech.tokens;

public class Token
{
    public TokenType type;
    public String value;

    public Token(TokenType type, String value)
    {
        this.type = type;
        this.value = value;
    }

    public String toString()
    {
        return String.format("Token<%s>(%s)", type.toString(), value);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Token t = (Token) o;
        return t.type == this.type;
    }

    @Override
    public int hashCode()
    {
        return type.hashCode();
    }
}
