package com.example.myapplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class GoogleQuery {

    public String searchKeyword;
    public String url;
    public String content;
    public Web webs;
    public WordCounter wordCounter;
    public WebNode node;
    public ArrayList<String> urlList;
    public ArrayList<Keyword> keywordList;
    public PriorityQueue<WebNode> heap;
    public Client client;

    public GoogleQuery(String searchKeyword) throws IOException {
        this.searchKeyword = searchKeyword;
        this.keywordList = new ArrayList<Keyword>();
        setKeyword(searchKeyword);
        this.url = "http://www.google.com/search?q="+searchKeyword+"&oe=utf8&num=10";
        node = new WebNode(new WebPage(url));
        urlList = new ArrayList<String>();
    }
    public void setKeyword(String searchKeyword) throws IOException {

        String[] inputList = searchKeyword.split(" ");
        ArrayList<String> inputs = new ArrayList<String>();
        client = new Client(keywordList);
        for(int i = 0; i < inputList.length; i++) {
            inputs.add(inputList[i]);
        }
        for(int i = 0; i < inputList.length; i++) {
            keywordList.add(new Keyword(inputList[i], 0 ,1));

        }
        client.setKeywordWeight(inputs);

    }

    public ArrayList<String> getUrlList() {
        return urlList != null ? urlList : new ArrayList<>();
    }

    private String fetchContent() throws IOException{

        String retVal = "";
        URL u = new URL(url);
        URLConnection conn = u.openConnection();
        conn.setRequestProperty("User-agent", "Chrome/7.0.517.44");
        InputStream in = conn.getInputStream();
        InputStreamReader inReader = new InputStreamReader(in,"utf-8");
        BufferedReader bufReader = new BufferedReader(inReader);

        String line = null;
        while((line = bufReader.readLine())!= null)
        {
            webs = new Web(line);
            webs.treeRootAddChild(line, searchKeyword);
            urlList.add(line);
            retVal += line;
        }
        if (retVal.isEmpty()) { // Add a null check here
            throw new IOException("Failed to fetch content or content is empty");
        }

        return retVal;
    }
    public void rank() throws IOException {
        if(content == null) {
            content = fetchContent();
        }
        if (node.children != null) {
            for (WebNode child : node.children) {
                heap.add(child);
            }
        }

        heap = new PriorityQueue<>((a, b) -> {
            try {
                return Double.compare(b.getNodeScore(keywordList), a.getNodeScore(keywordList));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return 0;
        });

    }
    public HashMap<String, String> query() throws IOException{

        if(content == null) {
            content = fetchContent();
        }
        rank();

        HashMap<String, String> retVal = new HashMap<String, String>();

        Document doc = Jsoup.parse(content);
        //System.out.println(doc.text());
        Elements lis = doc.select("div");
        //System.out.println(lis);
        lis = lis.select(".kCrYT");
        //System.out.println(lis.size());

        for(Element li : lis){
            try {
                String citeUrl = li.select("a").get(0).attr("href");
                String title = li.select("a").get(0).select(".vvjwJb").text();
                if(title.equals("")) {
                    continue;
                }
                System.out.println(title + ","+citeUrl);
                retVal.put(title, citeUrl);

            } catch (IndexOutOfBoundsException e) {
                //e.printStackTrace();
            }
        }
        return retVal;
    }
}
