package org.skytech.studentmanagementsystem.repository;

import org.skytech.studentmanagementsystem.entity.Course;
import org.skytech.studentmanagementsystem.entity.Enrollment;
import org.skytech.studentmanagementsystem.entity.EnrollmentStatus;
import org.skytech.studentmanagementsystem.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByStudent(Student student);

    List<Enrollment> findByCourse(Course course);

    List<Enrollment> findByStudentAndStatus(Student student, EnrollmentStatus status);

    Optional<Enrollment> findByStudentAndCourse(Student student, Course course);

    boolean existsByStudentAndCourse(Student student, Course course);

    @Query("SELECT e FROM Enrollment e WHERE e.student.id = :studentId AND e.status = :status")
    List<Enrollment> findEnrollmentsByStudentIdAndStatus(
            @Param("studentId") Long studentId,
            @Param("status") EnrollmentStatus status
    );

    @Query("SELECT AVG(e.grade) FROM Enrollment e WHERE e.student.id = :studentId AND e.grade IS NOT NULL")
    Double calculateGPA(@Param("studentId") Long studentId);

    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.course.id = :courseId AND e.status = 'ENROLLED'")
    long countEnrolledStudents(@Param("courseId") Long courseId);
}
