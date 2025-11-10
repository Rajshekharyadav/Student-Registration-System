package com.studentreg.model;

import javax.persistence.*;
import javax.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

@Entity
@Table(name = "students")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "First name is required")
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits")
    private String phone;

    @Past(message = "Date of birth must be in the past")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Course is required")
    private String course;

    @Enumerated(EnumType.STRING)
    private RegistrationStatus status = RegistrationStatus.PENDING;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Constructors
    public Student() {}

    public Student(String firstName, String lastName, String email, String phone, 
                  LocalDate dateOfBirth, String course) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.dateOfBirth = dateOfBirth;
        this.course = course;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }

    public RegistrationStatus getStatus() { return status; }
    public void setStatus(RegistrationStatus status) { this.status = status; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public enum RegistrationStatus {
        PENDING, APPROVED, REJECTED
    }
}