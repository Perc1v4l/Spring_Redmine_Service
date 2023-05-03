package com.example.demo.controllers;

import com.example.demo.dao.TaskListDao;
import com.example.demo.models.Redmine;
import com.example.demo.models.Task;
import com.sun.xml.internal.ws.client.ClientSchemaValidationTube;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class TaskController {

    @GetMapping("/tasks")
    public String tasksForm(Model model) {
        model.addAttribute("title", "Задачи");
        return "tasks";
    }

    @GetMapping("/addtask/{id}")
    public String inpForTasksForm(Model model) throws RedmineException {
        RedmineManager mgr = Redmine.getManager();

        List<User> users = mgr.getUserManager().getUsers();

        Task task = new Task();

        model.addAttribute("title", "Задачи");
        model.addAttribute("users", users);
        model.addAttribute("task", task);

        return "addtask";
    }

    @PostMapping("/addtask/{id}")
    public String inpForTasks(@ModelAttribute Task task, @PathVariable int id, Model model) {
        TaskListDao taskListDao = new TaskListDao();
        taskListDao.save(task);
        model.addAttribute("id", id);
        return "redirect:/task/" + id;
    }
    @GetMapping("/task/{id}")
    public String task(@PathVariable int id, Model model) throws RedmineException {
        RedmineManager mgr = Redmine.getManager();

        List<String> tasks = null;

        model.addAttribute("title", "Задачи");

        TaskListDao taskListDao = new TaskListDao();
        Task task = taskListDao.getLast().orElse(null);

        switch (id) {
            case 1 -> {
                assert task != null;
                tasks = task.outAllTasks(mgr);
            }
            case 2 -> {
                assert task != null;
                tasks = task.outHighPriorityTasks(mgr);
            }
            case 3 -> {
                assert task != null;
                tasks = task.outRejectTasks(mgr);
            }
            case 4 -> {
                assert task != null;
                tasks = task.outInProcessTasks(mgr);
            }
            case 5 -> {
                assert task != null;
                tasks = task.outClosedTasksNew(mgr);
            }
            case 6 -> {
                assert task != null;
                tasks = task.outHalfCompletedTasks(mgr);
            }
            case 7 -> {
                assert task != null;
                tasks = task.outWithoutSubTasks(mgr);
            }
        }
        model.addAttribute("tasks", tasks);
        return "task";
    }
}
