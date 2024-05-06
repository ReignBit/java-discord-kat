package com.reign.api.lib;

public class Pair<T,Y>
{
    public T key;
    public Y value;

    public Pair(T key, Y value)
    {
        this.key = key;
        this.value = value;
    }

    public T getKey() { return key; }
    public Y getValue() { return value; }
}
