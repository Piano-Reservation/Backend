package com.backend_piano.reservation.service;

import static org.mockito.Mockito.verify;

import com.backend_piano.reservation.model.ReservationStatus;
import com.backend_piano.reservation.repository.ReservationRepository;
import com.backend_piano.room.model.RoomFloor;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.time.ZoneId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReservationCompletionSchedulerTest {

    @Mock
    private ReservationRepository reservationRepository;

    private Clock clock;
    private ReservationCompletionScheduler reservationCompletionScheduler;

    @BeforeEach
    void setUp() {
        clock = Clock.fixed(
                Instant.parse("2026-07-01T09:15:00Z"),
                ZoneId.of("Asia/Seoul")
        );
        reservationCompletionScheduler = new ReservationCompletionScheduler(reservationRepository, clock);
    }

    @Test
    void 만료된_체크인_예약을_완료_처리한다() {
        reservationCompletionScheduler.completeExpiredCheckedInReservations();

        verify(reservationRepository).completeExpiredCheckedInReservations(
                ReservationStatus.CHECKED_IN,
                ReservationStatus.COMPLETED,
                List.of(RoomFloor.FIRST, RoomFloor.THIRD),
                LocalDate.now(clock),
                LocalTime.now(clock)
        );
    }
}
