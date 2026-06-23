package com.backend_piano.restriction.repository;

import com.backend_piano.restriction.model.Restriction;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestrictionRepository extends JpaRepository<Restriction, Long> {

    Optional<Restriction> findByStudentIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            Long studentId, LocalDate now1, LocalDate now2);
}
