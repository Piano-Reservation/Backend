package com.backend_piano.student.repository;

import com.backend_piano.student.model.Student;
import java.util.Optional;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByStudentNumber(String studentNumber);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select s
            from Student s
            where s.id = :id
            """)
    Optional<Student> findByIdForUpdate(@Param("id") Long id);
}
