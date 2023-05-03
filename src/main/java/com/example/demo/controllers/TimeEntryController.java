package com.example.demo.controllers;

import com.example.demo.dao.EntryListDao;
import com.example.demo.models.Redmine;
import com.example.demo.models.Entry;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.text.ParseException;
import java.util.List;

@Controller
public class TimeEntryController {

    @GetMapping("/timeentries")
    public String timeEntriesForm(Model model) {
        model.addAttribute("title", "Трудозатраты");
        return "timeentries";
    }

    @GetMapping("/addtimeentry/{id}")
    public String inpForTimeEntryForm(Model model) throws RedmineException {
        RedmineManager mgr = Redmine.getManager();

        List<User> users = mgr.getUserManager().getUsers();

        Entry timeentry = new Entry();

        model.addAttribute("title", "Задачи");
        model.addAttribute("users", users);
        model.addAttribute("timeentry", timeentry);

        return "addtimeentry";
    }

    @PostMapping("/addtimeentry/{id}")
    public String inpForTimeEntry(@ModelAttribute Entry timeEntry, @PathVariable int id, Model model) {
        EntryListDao entryListDao = new EntryListDao();
        entryListDao.save(timeEntry);
        model.addAttribute("id", id);
        return "redirect:/timeentry/" + id;
    }

    @GetMapping("/timeentry/{id}")
    public String task(@PathVariable int id, Model model) throws RedmineException, ParseException {
        RedmineManager mgr = Redmine.getManager();

        List<String> timeEntries = null;

        model.addAttribute("title", "Трудозатраты");

        EntryListDao entryListDao = new EntryListDao();
        Entry timeEntry = entryListDao.getLast().orElse(null);

        switch (id) {
            case 1 -> {
                assert timeEntry != null;
                timeEntries = timeEntry.LessThan8Hours(mgr);
            }
            case 2 -> {
                assert timeEntry != null;
                timeEntries = timeEntry.LessThan40InWeek(mgr);
            }
            case 3 -> {
                assert timeEntry != null;
                timeEntries = timeEntry.LessThenInCalendar(mgr);
            }
        }
        model.addAttribute("timeentries", timeEntries);
        return "timeentry";
    }
}
