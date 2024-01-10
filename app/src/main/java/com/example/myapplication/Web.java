package com.example.myapplication;

import java.io.IOException;
import java.util.ArrayList;

public class Web {
    public WebPage rootPage;
    public WebTree tree;
    public KeywordList keywordList;
    public Web(String url) {
        // TODO Auto-generated method stub
        rootPage = new WebPage(url);
        tree = new WebTree(rootPage);
        this.keywordList = new KeywordList();
    }
    public void treeRootAddChild(String query, String word) {
        tree.root.addChild(new WebNode(new WebPage(query)));
    }
    public double getWebScore(ArrayList<Keyword> keywordList) throws IOException {
        tree.root.webPage.setScore(keywordList);
        return tree.root.webPage.score;
    }
}

