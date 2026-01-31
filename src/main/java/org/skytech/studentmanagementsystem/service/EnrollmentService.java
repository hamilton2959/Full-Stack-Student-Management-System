package org.skytech.studentmanagementsystem.service;

import org.skytech.studentmanagementsystem.entity.*;
import org.skytech.studentmanagementsystem.exception.BusinessException;
import org.skytech.studentmanagementsystem.exception.ResourceNotFoundException;
import org.skytech.studentmanagementsystem.repository.EnrollmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final StudentService studentService;
    private final CourseService courseService;

    @Autowired
    public EnrollmentService(EnrollmentRepository enrollmentRepository,
                             StudentService studentService,
                             CourseService courseService) {
        this.enrollmentRepository = enrollmentRepository;
        this.studentService = studentService;
        this.courseService = courseService;
    }

    public Enrollment enrollStudent(Long studentId, Long courseId) {
        Student student = studentService.getStudentById(studentId);
        Course course = courseService.getCourseById(courseId);

        // Validate student status
        if (student.getStatus() != StudentStatus.ACTIVE) {
            throw new BusinessException("Cannot enroll inactive student");
        }

        // Check if already enrolled
        if (enrollmentRepository.existsByStudentAndCourse(student, course)) {
            throw new BusinessException("Student is already enrolled in this course");
        }

        // Check course capacity
        if (course.isFull()) {
            throw new BusinessException("Course is full");
        }

        // Check if course is active
        if (!course.getActive()) {
            throw new BusinessException("Cannot enroll in inactive course");
        }

        Enrollment enrollment = new Enrollment(student, course);
        return enrollmentRepository.save(enrollment);
    }

    public Enrollment getEnrollmentById(Long id) {
        return enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id: " + id));
    }

    public List<Enrollment> getStudentEnrollments(Long studentId) {
        Student student = studentService.getStudentById(studentId);
        return enrollmentRepository.findByStudent(student);
    }

    public List<Enrollment> getCourseEnrollments(Long courseId) {
        Course course = courseService.getCourseById(courseId);
        return enrollmentRepository.findByCourse(course);
    }

    public Enrollment updateGrade(Long enrollmentId, Double grade) {
        Enrollment enrollment = getEnrollmentById(enrollmentId);

        if (grade < 0 || grade > 100) {
            throw new BusinessException("Grade must be between 0 and 100");
        }

        enrollment.setGrade(grade);

        // Automatically complete enrollment when grade is assigned
        if (enrollment.getStatus() == EnrollmentStatus.ENROLLED) {
            enrollment.setStatus(EnrollmentStatus.COMPLETED);
        }

        return enrollmentRepository.save(enrollment);
    }

    public void dropEnrollment(Long enrollmentId) {
        Enrollment enrollment = getEnrollmentById(enrollmentId);

        if (enrollment.getStatus() == EnrollmentStatus.COMPLETED) {
            throw new BusinessException("Cannot drop a completed course");
        }

        enrollment.setStatus(EnrollmentStatus.DROPPED);
        enrollmentRepository.save(enrollment);
    }

    public Double calculateStudentGPA(Long studentId) {
        Double gpa = enrollmentRepository.calculateGPA(studentId);
        return gpa != null ? gpa : 0.0;
    }

    public List<Enrollment> getActiveEnrollments(Long studentId) {
        return enrollmentRepository.findEnrollmentsByStudentIdAndStatus(
                studentId, EnrollmentStatus.ENROLLED);
    }

    public long getCourseEnrollmentCount(Long courseId) {
        return enrollmentRepository.countEnrolledStudents(courseId);
    }
}
