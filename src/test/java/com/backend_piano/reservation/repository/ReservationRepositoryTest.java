package com.backend_piano.reservation.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.backend_piano.reservation.model.Reservation;
import com.backend_piano.reservation.model.ReservationStatus;
import com.backend_piano.room.model.Room;
import com.backend_piano.room.model.RoomFloor;
import com.backend_piano.room.repository.RoomRepository;
import com.backend_piano.student.model.Grade;
import com.backend_piano.student.model.PracticeCourse;
import com.backend_piano.student.model.Student;
import com.backend_piano.student.repository.StudentRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class ReservationRepositoryTest {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Test
    void 종료된_체크인_예약만_완료_처리한다() {
        Student student = studentRepository.save(Student.create(
                "203012001",
                "자동완료테스트",
                "encoded-password",
                LocalDate.of(2005, 3, 15),
                Grade.FRESHMAN,
                PracticeCourse.PRACTICE_1
        ));
        Room room = roomRepository.save(Room.create(
                RoomFloor.THIRD,
                "399",
                "예술체육대학2-399호"
        ));

        Reservation expiredCheckedIn = reservationRepository.save(createReservation(
                student,
                room,
                LocalDate.of(2026, 7, 1),
                LocalTime.of(15, 0),
                LocalTime.of(16, 0),
                ReservationStatus.CHECKED_IN
        ));
        Reservation activeCheckedIn = reservationRepository.save(createReservation(
                student,
                room,
                LocalDate.of(2026, 7, 1),
                LocalTime.of(17, 0),
                LocalTime.of(18, 0),
                ReservationStatus.CHECKED_IN
        ));
        Reservation reserved = reservationRepository.save(createReservation(
                student,
                room,
                LocalDate.of(2026, 7, 1),
                LocalTime.of(14, 0),
                LocalTime.of(15, 0),
                ReservationStatus.RESERVED
        ));
        Reservation cancelled = reservationRepository.save(createReservation(
                student,
                room,
                LocalDate.of(2026, 7, 1),
                LocalTime.of(13, 0),
                LocalTime.of(14, 0),
                ReservationStatus.CANCELLED
        ));

        int updatedCount = reservationRepository.completeExpiredCheckedInReservations(
                ReservationStatus.CHECKED_IN,
                ReservationStatus.COMPLETED,
                LocalDate.of(2026, 7, 1),
                LocalTime.of(16, 30)
        );

        assertThat(updatedCount).isEqualTo(1);
        assertThat(reservationRepository.findById(expiredCheckedIn.getId()).orElseThrow().getStatus())
                .isEqualTo(ReservationStatus.COMPLETED);
        assertThat(reservationRepository.findById(activeCheckedIn.getId()).orElseThrow().getStatus())
                .isEqualTo(ReservationStatus.CHECKED_IN);
        assertThat(reservationRepository.findById(reserved.getId()).orElseThrow().getStatus())
                .isEqualTo(ReservationStatus.RESERVED);
        assertThat(reservationRepository.findById(cancelled.getId()).orElseThrow().getStatus())
                .isEqualTo(ReservationStatus.CANCELLED);
    }

    private Reservation createReservation(
            Student student,
            Room room,
            LocalDate reservationDate,
            LocalTime startTime,
            LocalTime endTime,
            ReservationStatus status
    ) {
        Reservation reservation = Reservation.create(student, room, reservationDate, startTime, endTime);
        if (status == ReservationStatus.CHECKED_IN) {
            reservation.markCheckedIn(reservationDate.atTime(startTime));
            return reservation;
        }
        if (status == ReservationStatus.CANCELLED) {
            reservation.cancel("USER_CANCELLED", reservationDate.atTime(endTime));
            return reservation;
        }
        return reservation;
    }
}
