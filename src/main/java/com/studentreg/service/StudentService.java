package com.studentreg.service;

import com.studentreg.model.Student;
import com.studentreg.model.User;
import com.studentreg.repository.StudentRepository;
import com.studentreg.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class StudentService {
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    public Student registerStudent(Student student) {
        if (student.getEmail() != null && studentRepository.existsByEmail(student.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        return studentRepository.save(student);
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Optional<Student> getStudentById(Long id) {
        return studentRepository.findById(id);
    }

    public Student updateStudentStatus(Long id, Student.RegistrationStatus status) {
        Student student = studentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Student not found"));
        student.setStatus(status);
        return studentRepository.save(student);
    }

    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
    }
    
    public Student getStudentByUsername(String username) {
        return studentRepository.findByUserUsername(username).orElse(null);
    }
    
    public Student registerStudentWithUser(Student student, String username, String password) {
        if (student.getEmail() != null && studentRepository.existsByEmail(student.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists");
        }
        
        // Create user account
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(User.Role.STUDENT);
        user = userRepository.save(user);
        
        // Link student to user
        student.setUser(user);
        return studentRepository.save(student);
    }
    
    public Student updateStudent(Long id, String firstName, String lastName, String email, 
                               String phone, String dateOfBirth, String course, String status) {
        Student student = studentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Student not found"));
        
        student.setFirstName(firstName);
        student.setLastName(lastName);
        student.setEmail(email);
        student.setPhone(phone);
        student.setDateOfBirth(java.time.LocalDate.parse(dateOfBirth));
        student.setCourse(course);
        student.setStatus(Student.RegistrationStatus.valueOf(status));
        
        return studentRepository.save(student);
    }
}