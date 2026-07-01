package com.backend_piano.reservation.repository;

import com.backend_piano.reservation.model.Reservation;
import com.backend_piano.reservation.model.ReservationStatus;
import com.backend_piano.room.model.Room;
import com.backend_piano.room.model.RoomFloor;
import com.backend_piano.student.model.Student;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByStudentAndReservationDateOrderByStartTimeAsc(Student student, LocalDate reservationDate);

    @Query("""
            select r
            from Reservation r
            where r.student = :student
              and r.reservationDate = :reservationDate
              and r.status in :statuses
            order by r.startTime asc
            """)
    List<Reservation> findDailyReservationsByStudent(
            @Param("student") Student student,
            @Param("reservationDate") LocalDate reservationDate,
            @Param("statuses") Collection<ReservationStatus> statuses
    );

    Page<Reservation> findByStudentAndReservationDateBefore(
            Student student,
            LocalDate reservationDate,
            Pageable pageable
    );

    @Query("""
            select r
            from Reservation r
            where r.room = :room
              and r.reservationDate = :reservationDate
              and r.status in :statuses
            order by r.startTime asc
            """)
    List<Reservation> findActiveReservationsByRoomAndDate(
            @Param("room") Room room,
            @Param("reservationDate") LocalDate reservationDate,
            @Param("statuses") Collection<ReservationStatus> statuses
    );

    @Query("""
            select count(r) > 0
            from Reservation r
            where r.room = :room
              and r.reservationDate = :reservationDate
              and r.status in :statuses
              and r.startTime < :endTime
              and r.endTime > :startTime
            """)
    boolean existsTimeConflict(
            @Param("room") Room room,
            @Param("reservationDate") LocalDate reservationDate,
            @Param("statuses") Collection<ReservationStatus> statuses,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select r
            from Reservation r
            where r.student = :student
              and r.room = :room
              and r.reservationDate = :reservationDate
              and r.status in :statuses
              and r.startTime <= :currentTime
              and r.endTime > :currentTime
            """)
    Optional<Reservation> findCheckInTargetReservationForUpdate(
            @Param("student") Student student,
            @Param("room") Room room,
            @Param("reservationDate") LocalDate reservationDate,
            @Param("currentTime") LocalTime currentTime,
            @Param("statuses") Collection<ReservationStatus> statuses
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update Reservation r
            set r.status = :completedStatus
            where r.status = :checkedInStatus
              and r.room.floor in :floors
              and (
                    r.reservationDate < :today
                    or (r.reservationDate = :today and r.endTime <= :currentTime)
                  )
            """)
    int completeExpiredCheckedInReservations(
            @Param("checkedInStatus") ReservationStatus checkedInStatus,
            @Param("completedStatus") ReservationStatus completedStatus,
            @Param("floors") Collection<RoomFloor> floors,
            @Param("today") LocalDate today,
            @Param("currentTime") LocalTime currentTime
    );
}
