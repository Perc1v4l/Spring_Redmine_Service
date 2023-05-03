package com.example.demo.dao;

import com.example.demo.models.TimeEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EntryListDao implements Dao<TimeEntry>{
    static private final List<TimeEntry> timeEntries = new ArrayList<>();


    @Override
    public Optional<TimeEntry> getLast() {
        return Optional.of(timeEntries.get(timeEntries.size()-1));
    }

    @Override
    public void save(TimeEntry timeEntry) {
        this.timeEntries.add(timeEntry);
    }

    @Override
    public void delete(TimeEntry timeEntry) {
        timeEntries.remove(timeEntry);
    }
    public int size(){return timeEntries.size();}
}
