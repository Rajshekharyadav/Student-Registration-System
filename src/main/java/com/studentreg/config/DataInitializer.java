package com.studentreg.config;

import com.studentreg.model.User;
import com.studentreg.model.Student;
import com.studentreg.repository.UserRepository;
import com.studentreg.service.CourseService;
import com.studentreg.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private CourseService courseService;
    
    @Autowired
    private StudentService studentService;

    @Override
    public void run(String... args) throws Exception {
        // Create default admin user
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(User.Role.ADMIN);
            userRepository.save(admin);
            System.out.println("Default admin user created: username=admin, password=admin123");
        }
        
        // Create default courses
        String[] defaultCourses = {
            "Computer Science", "Engineering", "Business Administration", 
            "Medicine", "Mathematics", "Physics", "Chemistry", "Biology"
        };
        
        for (String courseName : defaultCourses) {
            try {
                courseService.addCourse(courseName);
            } catch (RuntimeException e) {
                // Course already exists, skip
            }
        }
        System.out.println("Default courses initialized: " + java.util.Arrays.toString(defaultCourses));
        
        // Create default student user for testing
        if (userRepository.findByUsername("student").isEmpty()) {
            try {
                User studentUser = new User();
                studentUser.setUsername("student");
                studentUser.setPassword(passwordEncoder.encode("student123"));
                studentUser.setRole(User.Role.STUDENT);
                studentUser = userRepository.save(studentUser);
                System.out.println("Student user created with ID: " + studentUser.getId());
                
                Student student = new Student();
                student.setFirstName("John");
                student.setLastName("Doe");
                student.setEmail("john.doe@example.com");
                student.setPhone("1234567890");
                student.setDateOfBirth(java.time.LocalDate.of(2000, 1, 1));
                student.setCourse("Computer Science");
                student.setStatus(Student.RegistrationStatus.APPROVED);
                student.setUser(studentUser);
                
                Student savedStudent = studentService.registerStudent(student);
                System.out.println("Student profile created with ID: " + savedStudent.getId());
                System.out.println("Default student user created: username=student, password=student123");
            } catch (Exception e) {
                System.err.println("Error creating default student: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("Student user already exists, skipping creation");
        }
    }
}