package com.example.demo.models;

import com.taskadapter.redmineapi.Params;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.Issue;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Task {
    private String user_id;

    public Task()
    {
        this.user_id = "";
    }
    public Task(String user_id)
    {
        this.user_id = user_id;
    }

    public String getUser_id(){ return this.user_id;}

    public void setUser_id(String user_id){ this.user_id = user_id;}

    public List<String> outAllTasks(RedmineManager mgr) throws RedmineException {
        List<String> allTasks = new ArrayList<>();

        Params params = new Params().add("status_id", "*")
            .add("assigned_to_id", user_id);

        List<Issue> issues = mgr.getIssueManager().getIssues(params).getResults();

        issues.stream()
            .sorted(Comparator.comparing(Issue::getId))
            .forEach(issue ->
                allTasks.add(issue.getId().toString() + ") " + issue.getSubject()));
        return allTasks;
    }

    public List<String> outHighPriorityTasks(@NotNull RedmineManager mgr) throws RedmineException {
        List<String> highPriorityTasks = new ArrayList<>();

        Params params = new Params().add("status_id", "*")
            .add("priority_id", "1")
            .add("assigned_to_id", user_id);

        List<Issue> issues = mgr.getIssueManager().getIssues(params).getResults();

        issues.stream()
            .sorted(Comparator.comparing(Issue::getId))
            .forEach(issue -> highPriorityTasks.add(issue.getId().toString() + ") " + issue.getSubject()));
        return highPriorityTasks;
    }

    public List<String> outRejectTasks(@NotNull RedmineManager mgr) throws RedmineException {
        List<String> rejectTasks = new ArrayList<>();
        Params params = new Params().add("status_id", "4")
            .add("assigned_to_id",user_id);

        List<Issue> issues = mgr.getIssueManager().getIssues(params).getResults();

        issues.stream()
            .sorted(Comparator.comparing(Issue::getId))
            .forEach(issue -> rejectTasks.add(issue.getId().toString() + ") " + issue.getSubject()));
        return rejectTasks;
    }

    public List<String> outInProcessTasks(@NotNull RedmineManager mgr) throws RedmineException {
        List<String> inProcessTasks = new ArrayList<>();
        Params params = new Params().add("status_id", "2")
            .add("assigned_to_id", user_id);

        List<Issue> issues = mgr.getIssueManager().getIssues(params).getResults();

        issues.stream()
            .sorted(Comparator.comparing(Issue::getId))
            .forEach(issue -> inProcessTasks.add(issue.getId().toString() + ") " + issue.getSubject()));
        return inProcessTasks;
    }

    public List<String> outClosedTasksNew(@NotNull RedmineManager mgr) throws RedmineException {
        List<String> closedTasks = new ArrayList<>();
        Params params = new Params().add("status_id", "3")
            .add("assigned_to_id", user_id);

        List<Issue> issues = mgr.getIssueManager().getIssues(params).getResults();

        issues.stream()
            .sorted(Comparator.comparing(Issue::getId))
            .forEach(issue -> closedTasks.add(issue.getId().toString() + ") " + issue.getSubject()));
        return closedTasks;
    }
    public List<String> outHalfCompletedTasks(@NotNull RedmineManager mgr) throws RedmineException {
        List<String> halfCompletedTasks = new ArrayList<>();
        Params params = new Params().add("status_id", "*")
            .add("assigned_to_id", user_id)
            .add("done_ratio", ">=" + 50)
            .add("done_ratio", "<=" + 100);

        List<Issue> issues = mgr.getIssueManager().getIssues(params).getResults();

        issues.stream()
            .sorted(Comparator.comparing(Issue::getId))
            .forEach(issue -> halfCompletedTasks.add(issue.getId().toString() + ") " + issue.getSubject()));
        return halfCompletedTasks;
    }
    public List<String> outWithoutSubTasks(@NotNull RedmineManager mgr) throws RedmineException {
        List<String> withoutSubTasks = new ArrayList<>();
        Params params = new Params().add("status_id", "*")
            .add("assigned_to_id", user_id);

        List<Issue> issues = mgr.getIssueManager().getIssues(params).getResults();

        issues.stream()
            .sorted(Comparator.comparing(Issue::getId))
            .filter(issue -> issue.getParentId() == null)
            .forEach(issue -> withoutSubTasks.add(issue.getId().toString() + ") " + issue.getSubject()));
        return withoutSubTasks;
    }
}
