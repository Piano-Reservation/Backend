package com.backend_piano.restriction.service;

import com.backend_piano.auth.service.StudentDetails;
import com.backend_piano.restriction.dto.RestrictionResponse;
import com.backend_piano.restriction.repository.RestrictionRepository;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RestrictionService {

    private final RestrictionRepository restrictionRepository;

    @Transactional(readOnly = true)
    public RestrictionResponse getCurrentRestriction(StudentDetails studentDetails) {
        LocalDate today = LocalDate.now();
        return restrictionRepository
                .findFirstByStudentIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByEndDateDesc(
                        studentDetails.getStudent().getId(), today, today)
                .map(RestrictionResponse::from)
                .orElse(RestrictionResponse.none());
    }

    @Transactional(readOnly = true)
    public boolean hasCurrentRestriction(Long studentId) {
        LocalDate today = LocalDate.now();
        return restrictionRepository
                .findFirstByStudentIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByEndDateDesc(
                        studentId, today, today)
                .isPresent();
    }
}
