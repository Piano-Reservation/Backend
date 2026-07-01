package com.backend_piano.reservation.service;

import com.backend_piano.reservation.repository.ReservationRepository;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationCompletionScheduler {

    private final ReservationRepository reservationRepository;
    private final Clock clock;

    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void completeExpiredCheckedInReservations() {
        LocalDate today = LocalDate.now(clock);
        LocalTime currentTime = LocalTime.now(clock);

        int updatedCount = reservationRepository.completeExpiredCheckedInReservations(
                com.backend_piano.reservation.model.ReservationStatus.CHECKED_IN,
                com.backend_piano.reservation.model.ReservationStatus.COMPLETED,
                today,
                currentTime
        );

        if (updatedCount > 0) {
            log.info("만료된 체크인 예약 {}건을 완료 처리했습니다.", updatedCount);
        }
    }
}
