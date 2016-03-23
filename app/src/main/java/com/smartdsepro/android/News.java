package com.smartdsepro.android;

/**
 * Created by mashnoor on 1/14/16.
 */
public class News {

    String item_name;
    String news;
    String date;

    public String getItem_name() {
        return item_name;
    }

    public String getNews() {
        return news;
    }

    public String getDate() {
        return date;
    }

    public News(String item_name, String news, String date) {

        this.item_name = item_name;
        this.news = news;
        this.date = date;
    }
}
