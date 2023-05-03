package com.example.demo.models;

public class RedmineData {
    public String URL;
    public String api;

    public RedmineData() {
        this.URL ="";
        this.api="";
    }
    public void setURL(String URL) {
        this.URL = URL;
    }
    public String getURL() {
        return this.URL ;
    }

    public void setApi(String api) {
        this.api = api;
    }
    public String getApi() {
        return this.api ;
    }
}
