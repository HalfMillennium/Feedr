package com.digitalnode.glc22.feedr.Sorts;

import com.digitalnode.glc22.feedr.Entry;

import java.util.Comparator;

public class SortByNewest implements Comparator<Entry>
{
    // Used for sorting in ascending order of
    // roll name
    public int compare(Entry a, Entry b)
    {
        long diffInMillies = a.getDateCreated().getTime() - b.getDateCreated().getTime();

        if(diffInMillies >= 0)
            return 1;
        else
            return -1;
    }
}
