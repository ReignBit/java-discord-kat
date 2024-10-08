package com.reign.kat.lib.utils;

import java.util.ArrayList;
import java.util.List;

public class PaginatedList<T>
{
    private final List<T> entries;
    private final int maxPerPage;

    private int curPage = 1;

    public PaginatedList(List<T> entries, int maxPerPage)
    {
        this.entries = entries;
        this.maxPerPage = maxPerPage;
    }

    public List<T> next()
    {
        curPage += 1;
        return getPage();
    }

    public List<T> prev()
    {
        curPage -= 1;
        return getPage();
    }

    public List<T> get(int pageNumber)
    {
        curPage = pageNumber;
        return getPage();
    }

    private List<T> getPage()
    {
        ArrayList<T> page = new ArrayList<>();
        for(int i = 0; i < (curPage + 1) * maxPerPage; i++)
        {
            if (entries.size() < i)
            {
                page.add(entries.get(i));
            }
        }

        return page;
    }
}
