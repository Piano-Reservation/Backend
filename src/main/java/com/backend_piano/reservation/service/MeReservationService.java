package com.backend_piano.reservation.service;

import com.backend_piano.auth.service.StudentDetails;
import com.backend_piano.reservation.dto.MyReservationResponse;
import com.backend_piano.reservation.repository.ReservationRepository;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MeReservationService {

    private final ReservationRepository reservationRepository;
    private final Clock clock;

    public List<MyReservationResponse> getMyReservations(StudentDetails studentDetails, LocalDate date) {
        return reservationRepository.findByStudentAndReservationDateOrderByStartTimeAsc(
                        studentDetails.getStudent(),
                        date
                ).stream()
                .map(MyReservationResponse::from)
                .toList();
    }

    public Page<MyReservationResponse> getMyReservationHistory(StudentDetails studentDetails, int page, int size) {
        PageRequest pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "reservationDate")
                        .and(Sort.by(Sort.Direction.ASC, "startTime"))
                        .and(Sort.by(Sort.Direction.DESC, "id"))
        );

        return reservationRepository.findByStudentAndReservationDateBefore(
                        studentDetails.getStudent(),
                        LocalDate.now(clock),
                        pageable
                )
                .map(MyReservationResponse::from);
    }
}
