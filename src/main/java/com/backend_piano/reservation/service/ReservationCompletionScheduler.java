package com.backend_piano.reservation.service;

import com.backend_piano.reservation.model.ReservationStatus;
import com.backend_piano.reservation.repository.ReservationRepository;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationCompletionScheduler {

    private static final List<com.backend_piano.room.model.RoomFloor> AUTO_COMPLETION_FLOORS = List.of(
            com.backend_piano.room.model.RoomFloor.FIRST,
            com.backend_piano.room.model.RoomFloor.THIRD
    );

    private final ReservationRepository reservationRepository;
    private final Clock clock;

    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void completeExpiredCheckedInReservations() {
        LocalDateTime now = LocalDateTime.now(clock);
        LocalDate today = now.toLocalDate();
        LocalTime currentTime = now.toLocalTime();

        int updatedCount = reservationRepository.completeExpiredCheckedInReservations(
                ReservationStatus.CHECKED_IN,
                ReservationStatus.COMPLETED,
                AUTO_COMPLETION_FLOORS,
                today,
                currentTime
        );

        if (updatedCount > 0) {
            log.info("만료된 체크인 예약 {}건을 완료 처리했습니다.", updatedCount);
        }
    }
}
