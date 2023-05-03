package com.example.demo.controllers;

import com.example.demo.models.Redmine;
import com.example.demo.models.RedmineData;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class HomeController {

    @GetMapping("/home")
    public String home(Model model)
    {
        model.addAttribute("title", "Главная страница");
        return "home";
    }

    @GetMapping("/")
    public String loginForm(Model model)
    {
        model.addAttribute("title", "Вход в Redmine");
        RedmineData redmineData = new RedmineData();
        model.addAttribute("redmine", redmineData);
        return "login";
    }
    @GetMapping("/login")
    public String loginForm2(Model model)
    {
        model.addAttribute("title", "Вход в Redmine");
        RedmineData redmineData = new RedmineData();
        model.addAttribute("redmine", redmineData);
        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute RedmineData redmine, Model model) {
        Redmine.initializeRedmineData(redmine.URL, redmine.api);
        model.addAttribute("title", "Главная страница");
        return "redirect:/home";
    }
}
