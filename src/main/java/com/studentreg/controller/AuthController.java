package com.studentreg.controller;

import com.studentreg.model.Student;
import com.studentreg.service.StudentService;
import com.studentreg.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@Controller
public class AuthController {

    @Autowired
    private StudentService studentService;
    
    @Autowired
    private CourseService courseService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }
    
    @GetMapping("/create-account")
    public String showCreateAccountForm(Model model) {
        model.addAttribute("student", new Student());
        model.addAttribute("courses", courseService.getAllCourseNames());
        return "create-account";
    }
    
    @PostMapping("/create-account")
    public String createAccount(@Valid @ModelAttribute Student student, 
                               BindingResult result, Model model,
                               @RequestParam String username,
                               @RequestParam String password) {
        if (result.hasErrors()) {
            model.addAttribute("courses", courseService.getAllCourseNames());
            return "create-account";
        }
        try {
            studentService.registerStudentWithUser(student, username, password);
            model.addAttribute("success", "Account created successfully! You can now login.");
            return "login";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("courses", courseService.getAllCourseNames());
            return "create-account";
        }
    }
}