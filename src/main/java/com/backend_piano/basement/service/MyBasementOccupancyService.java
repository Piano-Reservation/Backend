package com.backend_piano.basement.service;

import com.backend_piano.auth.service.StudentDetails;
import com.backend_piano.basement.dto.BasementOccupancyResponse;
import com.backend_piano.basement.repository.BasementOccupancyRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyBasementOccupancyService {

    private final BasementOccupancyRepository basementOccupancyRepository;

    public List<BasementOccupancyResponse> getMyOccupancies(StudentDetails studentDetails) {
        return basementOccupancyRepository.findByStudentOrderByEnteredAtDesc(studentDetails.getStudent()).stream()
                .map(BasementOccupancyResponse::from)
                .toList();
    }
}
