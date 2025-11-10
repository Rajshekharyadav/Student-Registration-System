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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.security.Principal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Controller
public class StudentController {

    @Autowired
    private StudentService studentService;
    
    @Autowired
    private CourseService courseService;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("student", new Student());
        model.addAttribute("courses", courseService.getAllCourseNames());
        return "register";
    }

    @PostMapping("/register")
    public String registerStudent(@Valid @ModelAttribute Student student, 
                                 BindingResult result, Model model,
                                 @RequestParam String username,
                                 @RequestParam String password) {
        if (result.hasErrors()) {
            model.addAttribute("courses", courseService.getAllCourseNames());
            return "register";
        }
        try {
            studentService.registerStudentWithUser(student, username, password);
            model.addAttribute("success", "Registration successful! You can now login with your credentials.");
            model.addAttribute("student", new Student());
            model.addAttribute("courses", courseService.getAllCourseNames());
            return "register";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("courses", courseService.getAllCourseNames());
            return "register";
        }
    }

    @GetMapping("/admin")
    public String adminPanel(Model model) {
        try {
            List<Student> students = studentService.getAllStudents();
            List<String> courses = courseService.getAllCourseNames();
            
            // Statistics
            model.addAttribute("totalStudents", students.size());
            model.addAttribute("pendingStudents", students.stream().mapToInt(s -> s.getStatus() == Student.RegistrationStatus.PENDING ? 1 : 0).sum());
            model.addAttribute("approvedStudents", students.stream().mapToInt(s -> s.getStatus() == Student.RegistrationStatus.APPROVED ? 1 : 0).sum());
            model.addAttribute("rejectedStudents", students.stream().mapToInt(s -> s.getStatus() == Student.RegistrationStatus.REJECTED ? 1 : 0).sum());
            
            // Course statistics
            Map<String, Long> courseStats = students.stream()
                .filter(s -> s.getCourse() != null)
                .collect(Collectors.groupingBy(Student::getCourse, Collectors.counting()));
            
            model.addAttribute("students", students);
            model.addAttribute("courses", courses);
            model.addAttribute("courseStats", courseStats);
            return "admin";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading admin panel: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/admin/approve/{id}")
    public String approveStudent(@PathVariable Long id) {
        studentService.updateStudentStatus(id, Student.RegistrationStatus.APPROVED);
        return "redirect:/admin";
    }

    @PostMapping("/admin/reject/{id}")
    public String rejectStudent(@PathVariable Long id) {
        studentService.updateStudentStatus(id, Student.RegistrationStatus.REJECTED);
        return "redirect:/admin";
    }
    
    @GetMapping("/admin/status/{id}")
    public String changeStudentStatus(@PathVariable Long id, @RequestParam String status) {
        Student.RegistrationStatus newStatus = Student.RegistrationStatus.valueOf(status);
        studentService.updateStudentStatus(id, newStatus);
        return "redirect:/admin";
    }
    
    @PostMapping("/admin/edit/{id}")
    public String editStudent(@PathVariable Long id,
                             @RequestParam String firstName,
                             @RequestParam String lastName,
                             @RequestParam String email,
                             @RequestParam String phone,
                             @RequestParam String dateOfBirth,
                             @RequestParam String course,
                             @RequestParam String status) {
        studentService.updateStudent(id, firstName, lastName, email, phone, dateOfBirth, course, status);
        return "redirect:/admin";
    }
    
    @PostMapping("/admin/add-student")
    public String addStudent(@RequestParam String username,
                            @RequestParam String password,
                            @RequestParam String firstName,
                            @RequestParam String lastName,
                            @RequestParam String email,
                            @RequestParam String phone,
                            @RequestParam String dateOfBirth,
                            @RequestParam String course) {
        Student student = new Student();
        student.setFirstName(firstName);
        student.setLastName(lastName);
        student.setEmail(email);
        student.setPhone(phone);
        student.setDateOfBirth(java.time.LocalDate.parse(dateOfBirth));
        student.setCourse(course);
        studentService.registerStudentWithUser(student, username, password);
        return "redirect:/admin";
    }
    
    @PostMapping("/admin/delete/{id}")
    public String deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return "redirect:/admin";
    }
    
    @PostMapping("/admin/courses/add")
    public String addCourse(@RequestParam String courseName) {
        try {
            courseService.addCourse(courseName);
        } catch (RuntimeException e) {
            // Handle error silently for now
        }
        return "redirect:/admin";
    }
    
    @PostMapping("/admin/courses/delete")
    public String deleteCourse(@RequestParam String courseName) {
        courseService.deleteCourse(courseName);
        return "redirect:/admin";
    }
    
    @GetMapping("/student/dashboard")
    public String studentDashboard(Model model, Principal principal) {
        try {
            if (principal == null) {
                return "redirect:/login";
            }
            
            String username = principal.getName();
            System.out.println("Loading dashboard for user: " + username);
            
            Student student = studentService.getStudentByUsername(username);
            System.out.println("Student found: " + (student != null ? student.getFirstName() : "null"));
            
            if (student == null) {
                System.out.println("No student profile found for username: " + username);
                model.addAttribute("error", "Student profile not found. Please contact administrator.");
                return "error";
            }
            
            List<String> courses = courseService.getAllCourseNames();
            System.out.println("Courses loaded: " + courses.size());
            
            model.addAttribute("student", student);
            model.addAttribute("courses", courses);
            return "student-dashboard";
        } catch (Exception e) {
            System.err.println("Error in student dashboard: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Error loading dashboard: " + e.getMessage());
            return "error";
        }
    }
    
    @PostMapping("/student/update-profile")
    public String updateStudentProfile(Principal principal,
                                     @RequestParam String firstName,
                                     @RequestParam String lastName,
                                     @RequestParam String email,
                                     @RequestParam String phone,
                                     @RequestParam String dateOfBirth,
                                     @RequestParam String course) {
        String username = principal.getName();
        Student student = studentService.getStudentByUsername(username);
        if (student != null) {
            studentService.updateStudent(student.getId(), firstName, lastName, email, phone, dateOfBirth, course, student.getStatus().name());
        }
        return "redirect:/student/dashboard";
    }
    
    @PostMapping("/student/upload-document")
    public String uploadDocument(Principal principal,
                               @RequestParam String documentType,
                               @RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
        // Document upload logic would go here
        return "redirect:/student/dashboard";
    }
}