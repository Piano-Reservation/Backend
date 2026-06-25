package com.backend_piano.reservation.repository;

import com.backend_piano.reservation.model.Reservation;
import com.backend_piano.reservation.model.ReservationStatus;
import com.backend_piano.room.model.Room;
import com.backend_piano.student.model.Student;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByStudentAndReservationDateOrderByStartTimeAsc(Student student, LocalDate reservationDate);

    List<Reservation> findByStudentAndReservationDateAndStatusInOrderByStartTimeAsc(
            Student student,
            LocalDate reservationDate,
            Collection<ReservationStatus> statuses
    );

    List<Reservation> findByRoomAndReservationDateOrderByStartTimeAsc(Room room, LocalDate reservationDate);

    boolean existsByRoomAndReservationDateAndStatusInAndStartTimeLessThanAndEndTimeGreaterThan(
            Room room,
            LocalDate reservationDate,
            Collection<ReservationStatus> statuses,
            LocalTime endTime,
            LocalTime startTime
    );
}
