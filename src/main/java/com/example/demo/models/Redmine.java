package com.example.demo.models;

import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.RedmineManagerFactory;

public class Redmine {
    private static String URL;
    private static String api;

    static public void initializeRedmineData(String URL, String api) {
        Redmine.URL = URL;
        Redmine.api = api;
    }

    static public RedmineManager getManager() {
        return RedmineManagerFactory.createWithApiKey(URL, api);
    }
}
