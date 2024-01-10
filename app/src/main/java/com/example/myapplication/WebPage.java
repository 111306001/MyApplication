package com.example.myapplication;

import java.io.IOException;
import java.util.ArrayList;

public class WebPage
{
    public String url;
    public String name;
    public WordCounter counter;
    public double score;

    public WebPage(String url)
    {
        this.url = url;
        this.counter = new WordCounter(url);
    }

    public void setScore(ArrayList<Keyword> keywords) throws IOException
    {
        score = 0;
//		1. calculate score
        for (Keyword k : keywords)
        {
            int count = counter.countKeyword(k.name);
            score += count * k.weight;
        }
    }

}
