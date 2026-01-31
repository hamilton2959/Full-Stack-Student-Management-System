package org.skytech.studentmanagementsystem.service;

import org.skytech.studentmanagementsystem.entity.Course;
import org.skytech.studentmanagementsystem.exception.DuplicateResourceException;
import org.skytech.studentmanagementsystem.exception.ResourceNotFoundException;
import org.skytech.studentmanagementsystem.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CourseService {
    private final CourseRepository courseRepository;

    @Autowired
    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Course getCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
    }

    public Course getCourseByCourseCode(String courseCode) {
        return courseRepository.findByCourseCode(courseCode)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with code: " + courseCode));
    }

    public Course createCourse(Course course) {
        if (courseRepository.existsByCourseCode(course.getCourseCode())) {
            throw new DuplicateResourceException("Course already exists with code: " + course.getCourseCode());
        }
        return courseRepository.save(course);
    }

    public Course updateCourse(Long id, Course courseDetails) {
        Course course = getCourseById(id);

        // Check if course code is being changed and if new code already exists
        if (!course.getCourseCode().equals(courseDetails.getCourseCode()) &&
                courseRepository.existsByCourseCode(courseDetails.getCourseCode())) {
            throw new DuplicateResourceException("Course code already in use: " + courseDetails.getCourseCode());
        }

        course.setCourseCode(courseDetails.getCourseCode());
        course.setCourseName(courseDetails.getCourseName());
        course.setDescription(courseDetails.getDescription());
        course.setCredits(courseDetails.getCredits());
        course.setCapacity(courseDetails.getCapacity());
        course.setSemester(courseDetails.getSemester());
        course.setInstructor(courseDetails.getInstructor());

        return courseRepository.save(course);
    }

    public void deleteCourse(Long id) {
        Course course = getCourseById(id);
        courseRepository.delete(course);
    }

    public List<Course> searchCourses(String searchTerm) {
        return courseRepository.searchCourses(searchTerm);
    }

    public List<Course> getActiveCourses() {
        return courseRepository.findByActive(true);
    }

    public List<Course> getCoursesBySemester(String semester) {
        return courseRepository.findBySemester(semester);
    }

    public List<Course> getAvailableCourses() {
        return courseRepository.findAvailableCourses();
    }

    public Course deactivateCourse(Long id) {
        Course course = getCourseById(id);
        course.setActive(false);
        return courseRepository.save(course);
    }

    public Course activateCourse(Long id) {
        Course course = getCourseById(id);
        course.setActive(true);
        return courseRepository.save(course);
    }
}
