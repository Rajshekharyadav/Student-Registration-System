package com.studentreg.service;

import com.studentreg.model.Course;
import com.studentreg.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    public List<String> getAllCourseNames() {
        return courseRepository.findAll().stream()
                .map(Course::getName)
                .collect(Collectors.toList());
    }

    public void addCourse(String courseName) {
        if (courseRepository.existsByName(courseName)) {
            throw new RuntimeException("Course already exists");
        }
        courseRepository.save(new Course(courseName));
    }

    public void deleteCourse(String courseName) {
        courseRepository.findByName(courseName)
                .ifPresent(courseRepository::delete);
    }
}