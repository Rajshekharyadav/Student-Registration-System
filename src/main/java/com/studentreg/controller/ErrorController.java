package com.studentreg.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.servlet.http.HttpServletRequest;

@Controller
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        String errorMessage = "An unexpected error occurred.";
        
        if (statusCode != null) {
            switch (statusCode) {
                case 403:
                    errorMessage = "Access denied. You don't have permission to access this resource.";
                    break;
                case 404:
                    errorMessage = "Page not found.";
                    break;
                case 500:
                    errorMessage = "Internal server error. Please try again later.";
                    break;
                default:
                    errorMessage = "Error " + statusCode + " occurred.";
            }
        }
        
        model.addAttribute("error", errorMessage);
        return "error";
    }
}