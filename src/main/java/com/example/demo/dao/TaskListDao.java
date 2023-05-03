package com.example.demo.dao;

import com.example.demo.models.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TaskListDao implements Dao<Task>{
    static private final List<Task> tasks = new ArrayList<>();


    @Override
    public Optional<Task> getLast() {
        return Optional.of(tasks.get(tasks.size()-1));
    }

    @Override
    public void save(Task task) {
        tasks.add(task);
    }

    @Override
    public void delete(Task task) {
        tasks.remove(task);
    }
    public int size(){return tasks.size();}
}
