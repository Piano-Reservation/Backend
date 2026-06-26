package com.backend_piano.reservation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.backend_piano.auth.service.StudentDetails;
import com.backend_piano.global.exception.ApiException;
import com.backend_piano.reservation.dto.ReservationCreateRequest;
import com.backend_piano.reservation.dto.ReservationResponse;
import com.backend_piano.reservation.exception.ReservationErrorCode;
import com.backend_piano.reservation.model.Reservation;
import com.backend_piano.reservation.model.ReservationStatus;
import com.backend_piano.reservation.repository.ReservationRepository;
import com.backend_piano.restriction.service.RestrictionService;
import com.backend_piano.room.model.Room;
import com.backend_piano.room.model.RoomFloor;
import com.backend_piano.room.repository.RoomAllowedCourseRepository;
import com.backend_piano.room.repository.RoomRepository;
import com.backend_piano.student.model.Grade;
import com.backend_piano.student.model.PracticeCourse;
import com.backend_piano.student.model.Student;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private RoomAllowedCourseRepository roomAllowedCourseRepository;

    @Mock
    private RestrictionService restrictionService;

    private Clock clock;
    private ReservationService reservationService;

    @BeforeEach
    void setUp() {
        clock = Clock.fixed(
                Instant.parse("2026-06-26T01:00:00Z"),
                ZoneId.of("Asia/Seoul")
        );
        reservationService = new ReservationService(
                reservationRepository,
                roomRepository,
                roomAllowedCourseRepository,
                restrictionService,
                clock
        );
    }

    @Test
    void 예약을_생성한다() {
        Student student = createStudent(1L, PracticeCourse.PRACTICE_3);
        StudentDetails studentDetails = new StudentDetails(student);
        Room room = createRoom(2L, RoomFloor.THIRD, "301", "예술체육대학2-301호");
        ReservationCreateRequest request = new ReservationCreateRequest(
                room.getId(),
                LocalDate.now(clock),
                LocalTime.of(10, 0),
                LocalTime.of(12, 0)
        );

        when(restrictionService.hasCurrentRestriction(student.getId())).thenReturn(false);
        when(roomRepository.findByIdAndActiveTrue(room.getId())).thenReturn(Optional.of(room));
        when(reservationRepository.findByStudentAndReservationDateAndStatusInOrderByStartTimeAsc(
                eq(student), eq(request.date()), any())).thenReturn(List.of());
        when(reservationRepository.existsByRoomAndReservationDateAndStatusInAndStartTimeLessThanAndEndTimeGreaterThan(
                eq(room), eq(request.date()), any(), eq(request.endTime()), eq(request.startTime()))).thenReturn(false);
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> {
            Reservation saved = invocation.getArgument(0);
            ReflectionTestUtils.setField(saved, "id", 100L);
            return saved;
        });

        ReservationResponse response = reservationService.createReservation(studentDetails, request);

        assertThat(response.reservationId()).isEqualTo(100L);
        assertThat(response.roomId()).isEqualTo(room.getId());
        assertThat(response.status()).isEqualTo(ReservationStatus.RESERVED);

        ArgumentCaptor<Reservation> captor = ArgumentCaptor.forClass(Reservation.class);
        verify(reservationRepository).save(captor.capture());
        assertThat(captor.getValue().getStudent()).isEqualTo(student);
        assertThat(captor.getValue().getRoom()).isEqualTo(room);
        assertThat(captor.getValue().getStartTime()).isEqualTo(LocalTime.of(10, 0));
    }

    @Test
    void 당일이_아닌_예약은_생성할_수_없다() {
        Student student = createStudent(1L, PracticeCourse.PRACTICE_3);
        StudentDetails studentDetails = new StudentDetails(student);
        ReservationCreateRequest request = new ReservationCreateRequest(
                2L,
                LocalDate.now(clock).plusDays(1),
                LocalTime.of(10, 0),
                LocalTime.of(11, 0)
        );

        when(restrictionService.hasCurrentRestriction(student.getId())).thenReturn(false);

        assertThatThrownBy(() -> reservationService.createReservation(studentDetails, request))
                .isInstanceOf(ApiException.class)
                .extracting("errorCode")
                .isEqualTo(ReservationErrorCode.RESERVATION_DATE_MUST_BE_TODAY);

        verify(roomRepository, never()).findByIdAndActiveTrue(any());
    }

    @Test
    void 일층_하루_최대_예약_시간을_초과하면_예외가_발생한다() {
        Student student = createStudent(1L, PracticeCourse.PRACTICE_1);
        StudentDetails studentDetails = new StudentDetails(student);
        Room room = createRoom(1L, RoomFloor.FIRST, "101", "1층 연습실");
        ReservationCreateRequest request = new ReservationCreateRequest(
                room.getId(),
                LocalDate.now(clock),
                LocalTime.of(13, 0),
                LocalTime.of(15, 0)
        );

        Reservation existing1 = createReservation(10L, student, room, request.date(), LocalTime.of(9, 0), LocalTime.of(11, 0), ReservationStatus.RESERVED);
        Reservation existing2 = createReservation(11L, student, room, request.date(), LocalTime.of(11, 0), LocalTime.of(13, 0), ReservationStatus.COMPLETED);

        when(restrictionService.hasCurrentRestriction(student.getId())).thenReturn(false);
        when(roomRepository.findByIdAndActiveTrue(room.getId())).thenReturn(Optional.of(room));
        when(reservationRepository.findByStudentAndReservationDateAndStatusInOrderByStartTimeAsc(
                eq(student), eq(request.date()), any())).thenReturn(List.of(existing1, existing2));

        assertThatThrownBy(() -> reservationService.createReservation(studentDetails, request))
                .isInstanceOf(ApiException.class)
                .extracting("errorCode")
                .isEqualTo(ReservationErrorCode.FIRST_FLOOR_DAILY_LIMIT_EXCEEDED);
    }

    @Test
    void 오후_한시_이전에는_삼층_전공실기_배정에_맞지_않는_방을_예약할_수_없다() {
        Student student = createStudent(1L, PracticeCourse.PRACTICE_1);
        StudentDetails studentDetails = new StudentDetails(student);
        Room room = createRoom(2L, RoomFloor.THIRD, "301", "예술체육대학2-301호");
        ReservationCreateRequest request = new ReservationCreateRequest(
                room.getId(),
                LocalDate.now(clock),
                LocalTime.of(18, 0),
                LocalTime.of(20, 0)
        );

        when(restrictionService.hasCurrentRestriction(student.getId())).thenReturn(false);
        when(roomRepository.findByIdAndActiveTrue(room.getId())).thenReturn(Optional.of(room));
        when(reservationRepository.findByStudentAndReservationDateAndStatusInOrderByStartTimeAsc(
                eq(student), eq(request.date()), any())).thenReturn(List.of());
        when(roomAllowedCourseRepository.existsByRoomAndPracticeCourse(room, student.getPracticeCourse())).thenReturn(false);
        when(roomAllowedCourseRepository.findByRoom(room)).thenReturn(List.of(org.mockito.Mockito.mock(com.backend_piano.room.model.RoomAllowedCourse.class)));

        assertThatThrownBy(() -> reservationService.createReservation(studentDetails, request))
                .isInstanceOf(ApiException.class)
                .extracting("errorCode")
                .isEqualTo(ReservationErrorCode.EVENING_ROOM_MAJOR_RESTRICTION);
    }

    @Test
    void 예약_취소는_RESERVED_상태에서만_가능하다() {
        Student student = createStudent(1L, PracticeCourse.PRACTICE_3);
        StudentDetails studentDetails = new StudentDetails(student);
        Room room = createRoom(2L, RoomFloor.THIRD, "301", "예술체육대학2-301호");
        Reservation reservation = createReservation(
                200L,
                student,
                room,
                LocalDate.now(clock),
                LocalTime.of(10, 0),
                LocalTime.of(12, 0),
                ReservationStatus.CHECKED_IN
        );

        when(reservationRepository.findById(200L)).thenReturn(Optional.of(reservation));

        assertThatThrownBy(() -> reservationService.cancelReservation(studentDetails, 200L))
                .isInstanceOf(ApiException.class)
                .extracting("errorCode")
                .isEqualTo(ReservationErrorCode.RESERVATION_CANNOT_BE_CANCELLED);
    }

    @Test
    void 본인_예약은_취소할_수_있다() {
        Student student = createStudent(1L, PracticeCourse.PRACTICE_3);
        StudentDetails studentDetails = new StudentDetails(student);
        Room room = createRoom(2L, RoomFloor.THIRD, "301", "예술체육대학2-301호");
        Reservation reservation = createReservation(
                201L,
                student,
                room,
                LocalDate.now(clock),
                LocalTime.of(10, 0),
                LocalTime.of(12, 0),
                ReservationStatus.RESERVED
        );

        when(reservationRepository.findById(201L)).thenReturn(Optional.of(reservation));

        reservationService.cancelReservation(studentDetails, 201L);

        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CANCELLED);
        assertThat(ReflectionTestUtils.getField(reservation, "cancelReason")).isEqualTo("USER_CANCELLED");
        assertThat(ReflectionTestUtils.getField(reservation, "cancelledAt")).isEqualTo(LocalDateTime.now(clock));
    }

    private Student createStudent(Long id, PracticeCourse practiceCourse) {
        Student student = Student.create(
                "202312001",
                "김테스트",
                "encoded-password",
                LocalDate.of(2005, 3, 15),
                Grade.FRESHMAN,
                practiceCourse
        );
        ReflectionTestUtils.setField(student, "id", id);
        return student;
    }

    private Room createRoom(Long id, RoomFloor floor, String code, String name) {
        Room room = Room.create(floor, code, name);
        ReflectionTestUtils.setField(room, "id", id);
        return room;
    }

    private Reservation createReservation(
            Long id,
            Student student,
            Room room,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime,
            ReservationStatus status
    ) {
        Reservation reservation = Reservation.create(student, room, date, startTime, endTime);
        ReflectionTestUtils.setField(reservation, "id", id);
        ReflectionTestUtils.setField(reservation, "status", status);
        return reservation;
    }
}
