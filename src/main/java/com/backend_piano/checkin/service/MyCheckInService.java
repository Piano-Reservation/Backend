package com.backend_piano.checkin.service;

import com.backend_piano.auth.service.StudentDetails;
import com.backend_piano.checkin.dto.CheckInResponse;
import com.backend_piano.checkin.repository.CheckInRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyCheckInService {

    private final CheckInRepository checkInRepository;

    public List<CheckInResponse> getMyCheckIns(StudentDetails studentDetails) {
        return checkInRepository.findByStudentOrderByCheckedInAtDesc(studentDetails.getStudent()).stream()
                .map(CheckInResponse::from)
                .toList();
    }
}
