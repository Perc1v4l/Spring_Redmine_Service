package com.example.demo.dao;

import com.example.demo.models.Entry;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EntryListDao implements Dao<Entry>{
    static private final List<Entry> timeEntries = new ArrayList<>();


    @Override
    public Optional<Entry> getLast() {
        return Optional.of(timeEntries.get(timeEntries.size()-1));
    }

    @Override
    public void save(Entry timeEntry) {
        this.timeEntries.add(timeEntry);
    }

    @Override
    public void delete(Entry timeEntry) {
        timeEntries.remove(timeEntry);
    }
    public int size(){return timeEntries.size();}
}
