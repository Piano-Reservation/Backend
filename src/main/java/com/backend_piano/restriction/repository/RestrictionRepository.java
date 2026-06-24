package com.backend_piano.restriction.repository;

import com.backend_piano.restriction.model.Restriction;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RestrictionRepository extends JpaRepository<Restriction, Long> {

    @Query("SELECT r FROM Restriction r WHERE r.student.id = :studentId " +
            "AND r.startDate <= :date AND r.endDate >= :date " +
            "ORDER BY r.endDate DESC LIMIT 1")
    Optional<Restriction> findCurrentByStudentId(@Param("studentId") Long studentId,
                                                  @Param("date") LocalDate date);
}
