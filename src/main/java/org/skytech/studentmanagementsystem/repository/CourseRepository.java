package org.skytech.studentmanagementsystem.repository;

import org.skytech.studentmanagementsystem.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    Optional<Course> findByCourseCode(String courseCode);

    List<Course> findByActive(Boolean active);

    List<Course> findBySemester(String semester);

    @Query("SELECT c FROM Course c WHERE c.active = true AND c.semester = :semester")
    List<Course> findActiveCoursesForSemester(@Param("semester") String semester);

    @Query("SELECT c FROM Course c WHERE " +
            "LOWER(c.courseCode) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(c.courseName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Course> searchCourses(@Param("searchTerm") String searchTerm);

    @Query("SELECT c FROM Course c WHERE SIZE(c.enrollments) < c.capacity AND c.active = true")
    List<Course> findAvailableCourses();

    boolean existsByCourseCode(String courseCode);
}
