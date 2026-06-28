package com.backend_piano.basement.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.backend_piano.auth.service.StudentDetails;
import com.backend_piano.basement.dto.BasementOccupancyCreateRequest;
import com.backend_piano.basement.dto.BasementOccupancyResponse;
import com.backend_piano.basement.exception.BasementOccupancyErrorCode;
import com.backend_piano.basement.model.BasementOccupancy;
import com.backend_piano.basement.model.BasementOccupancyStatus;
import com.backend_piano.basement.repository.BasementOccupancyRepository;
import com.backend_piano.global.exception.ApiException;
import com.backend_piano.restriction.service.RestrictionService;
import com.backend_piano.room.model.Room;
import com.backend_piano.room.model.RoomFloor;
import com.backend_piano.room.repository.RoomRepository;
import com.backend_piano.student.model.Grade;
import com.backend_piano.student.model.PracticeCourse;
import com.backend_piano.student.model.Student;
import com.backend_piano.student.repository.StudentRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class BasementOccupancyServiceTest {

    @Mock
    private BasementOccupancyRepository basementOccupancyRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private RestrictionService restrictionService;

    private Clock clock;
    private BasementOccupancyService basementOccupancyService;

    @BeforeEach
    void setUp() {
        clock = Clock.fixed(
                Instant.parse("2026-06-27T08:00:00Z"),
                ZoneId.of("Asia/Seoul")
        );
        basementOccupancyService = new BasementOccupancyService(
                basementOccupancyRepository,
                roomRepository,
                studentRepository,
                restrictionService,
                clock
        );
    }

    @Test
    void 지하_입실_기록을_생성한다() {
        Student student = createStudent(1L, "202312001");
        StudentDetails studentDetails = new StudentDetails(student);
        Room room = createRoom(8L, RoomFloor.BASEMENT, "B115", "예술체육대학2-B115호");
        BasementOccupancyCreateRequest request = new BasementOccupancyCreateRequest(room.getId());

        when(studentRepository.findByIdForUpdate(student.getId())).thenReturn(Optional.of(student));
        when(restrictionService.hasCurrentRestriction(student.getId())).thenReturn(false);
        when(roomRepository.findByIdAndActiveTrueForUpdate(room.getId())).thenReturn(Optional.of(room));
        when(basementOccupancyRepository.existsByRoomAndStatus(room, BasementOccupancyStatus.IN_USE)).thenReturn(false);
        when(basementOccupancyRepository.existsByStudentAndStatus(student, BasementOccupancyStatus.IN_USE)).thenReturn(false);
        when(basementOccupancyRepository.save(any(BasementOccupancy.class))).thenAnswer(invocation -> {
            BasementOccupancy saved = invocation.getArgument(0);
            ReflectionTestUtils.setField(saved, "id", 31L);
            return saved;
        });

        BasementOccupancyResponse response = basementOccupancyService.createOccupancy(studentDetails, request);

        assertThat(response.occupancyId()).isEqualTo(31L);
        assertThat(response.roomId()).isEqualTo(room.getId());
        assertThat(response.status()).isEqualTo(BasementOccupancyStatus.IN_USE);
        assertThat(response.enteredAt()).isEqualTo(LocalDateTime.now(clock));

        ArgumentCaptor<BasementOccupancy> captor = ArgumentCaptor.forClass(BasementOccupancy.class);
        verify(basementOccupancyRepository).save(captor.capture());
        assertThat(captor.getValue().getStudent()).isEqualTo(student);
        assertThat(captor.getValue().getRoom()).isEqualTo(room);
        assertThat(ReflectionTestUtils.getField(captor.getValue(), "activeStudentId")).isEqualTo(student.getId());
        assertThat(ReflectionTestUtils.getField(captor.getValue(), "activeRoomId")).isEqualTo(room.getId());
    }

    @Test
    void 지하실이_아닌_방에는_입실_기록을_생성할_수_없다() {
        Student student = createStudent(1L, "202312001");
        StudentDetails studentDetails = new StudentDetails(student);
        Room room = createRoom(1L, RoomFloor.FIRST, "101", "1층 연습실");
        BasementOccupancyCreateRequest request = new BasementOccupancyCreateRequest(room.getId());

        when(studentRepository.findByIdForUpdate(student.getId())).thenReturn(Optional.of(student));
        when(restrictionService.hasCurrentRestriction(student.getId())).thenReturn(false);
        when(roomRepository.findByIdAndActiveTrueForUpdate(room.getId())).thenReturn(Optional.of(room));

        assertThatThrownBy(() -> basementOccupancyService.createOccupancy(studentDetails, request))
                .isInstanceOf(ApiException.class)
                .extracting("errorCode")
                .isEqualTo(BasementOccupancyErrorCode.BASEMENT_ROOM_ONLY);
    }

    @Test
    void 이미_사용중인_지하실에는_입실할_수_없다() {
        Student student = createStudent(1L, "202312001");
        StudentDetails studentDetails = new StudentDetails(student);
        Room room = createRoom(8L, RoomFloor.BASEMENT, "B115", "예술체육대학2-B115호");
        BasementOccupancyCreateRequest request = new BasementOccupancyCreateRequest(room.getId());

        when(studentRepository.findByIdForUpdate(student.getId())).thenReturn(Optional.of(student));
        when(restrictionService.hasCurrentRestriction(student.getId())).thenReturn(false);
        when(roomRepository.findByIdAndActiveTrueForUpdate(room.getId())).thenReturn(Optional.of(room));
        when(basementOccupancyRepository.existsByRoomAndStatus(room, BasementOccupancyStatus.IN_USE)).thenReturn(true);

        assertThatThrownBy(() -> basementOccupancyService.createOccupancy(studentDetails, request))
                .isInstanceOf(ApiException.class)
                .extracting("errorCode")
                .isEqualTo(BasementOccupancyErrorCode.BASEMENT_ROOM_ALREADY_OCCUPIED);
    }

    @Test
    void 이미_다른_지하실을_사용중이면_중복_입실할_수_없다() {
        Student student = createStudent(1L, "202312001");
        StudentDetails studentDetails = new StudentDetails(student);
        Room room = createRoom(8L, RoomFloor.BASEMENT, "B115", "예술체육대학2-B115호");
        BasementOccupancyCreateRequest request = new BasementOccupancyCreateRequest(room.getId());

        when(studentRepository.findByIdForUpdate(student.getId())).thenReturn(Optional.of(student));
        when(restrictionService.hasCurrentRestriction(student.getId())).thenReturn(false);
        when(roomRepository.findByIdAndActiveTrueForUpdate(room.getId())).thenReturn(Optional.of(room));
        when(basementOccupancyRepository.existsByRoomAndStatus(room, BasementOccupancyStatus.IN_USE)).thenReturn(false);
        when(basementOccupancyRepository.existsByStudentAndStatus(student, BasementOccupancyStatus.IN_USE)).thenReturn(true);

        assertThatThrownBy(() -> basementOccupancyService.createOccupancy(studentDetails, request))
                .isInstanceOf(ApiException.class)
                .extracting("errorCode")
                .isEqualTo(BasementOccupancyErrorCode.BASEMENT_DUPLICATE_OCCUPANCY);
    }

    @Test
    void 본인_입실_기록은_퇴실_처리할_수_있다() {
        Student student = createStudent(1L, "202312001");
        StudentDetails studentDetails = new StudentDetails(student);
        Room room = createRoom(8L, RoomFloor.BASEMENT, "B115", "예술체육대학2-B115호");
        BasementOccupancy occupancy = createOccupancy(
                41L,
                student,
                room,
                LocalDateTime.of(2026, 6, 27, 16, 0),
                BasementOccupancyStatus.IN_USE
        );

        when(basementOccupancyRepository.findByIdForUpdate(41L)).thenReturn(Optional.of(occupancy));

        BasementOccupancyResponse response = basementOccupancyService.exitOccupancy(studentDetails, 41L);

        assertThat(response.status()).isEqualTo(BasementOccupancyStatus.EXITED);
        assertThat(response.exitedAt()).isEqualTo(LocalDateTime.now(clock));
        assertThat(ReflectionTestUtils.getField(occupancy, "activeStudentId")).isNull();
        assertThat(ReflectionTestUtils.getField(occupancy, "activeRoomId")).isNull();
    }

    @Test
    void 이미_퇴실한_기록은_다시_퇴실_처리할_수_없다() {
        Student student = createStudent(1L, "202312001");
        StudentDetails studentDetails = new StudentDetails(student);
        Room room = createRoom(8L, RoomFloor.BASEMENT, "B115", "예술체육대학2-B115호");
        BasementOccupancy occupancy = createOccupancy(
                42L,
                student,
                room,
                LocalDateTime.of(2026, 6, 27, 16, 0),
                BasementOccupancyStatus.EXITED
        );

        when(basementOccupancyRepository.findByIdForUpdate(42L)).thenReturn(Optional.of(occupancy));

        assertThatThrownBy(() -> basementOccupancyService.exitOccupancy(studentDetails, 42L))
                .isInstanceOf(ApiException.class)
                .extracting("errorCode")
                .isEqualTo(BasementOccupancyErrorCode.BASEMENT_OCCUPANCY_ALREADY_EXITED);
    }

    @Test
    void 다른_학생의_입실_기록은_퇴실_처리할_수_없다() {
        Student student = createStudent(1L, "202312001");
        Student otherStudent = createStudent(2L, "202212001");
        StudentDetails studentDetails = new StudentDetails(student);
        Room room = createRoom(8L, RoomFloor.BASEMENT, "B115", "예술체육대학2-B115호");
        BasementOccupancy occupancy = createOccupancy(
                43L,
                otherStudent,
                room,
                LocalDateTime.of(2026, 6, 27, 16, 0),
                BasementOccupancyStatus.IN_USE
        );

        when(basementOccupancyRepository.findByIdForUpdate(43L)).thenReturn(Optional.of(occupancy));

        assertThatThrownBy(() -> basementOccupancyService.exitOccupancy(studentDetails, 43L))
                .isInstanceOf(ApiException.class)
                .extracting("errorCode")
                .isEqualTo(BasementOccupancyErrorCode.BASEMENT_OCCUPANCY_ACCESS_DENIED);
    }

    private Student createStudent(Long id, String studentNumber) {
        Student student = Student.create(
                studentNumber,
                "김테스트",
                "encoded-password",
                LocalDate.of(2005, 3, 15),
                Grade.FRESHMAN,
                PracticeCourse.PRACTICE_1
        );
        ReflectionTestUtils.setField(student, "id", id);
        return student;
    }

    private Room createRoom(Long id, RoomFloor floor, String code, String name) {
        Room room = Room.create(floor, code, name);
        ReflectionTestUtils.setField(room, "id", id);
        return room;
    }

    private BasementOccupancy createOccupancy(
            Long id,
            Student student,
            Room room,
            LocalDateTime enteredAt,
            BasementOccupancyStatus status
    ) {
        BasementOccupancy occupancy = BasementOccupancy.create(student, room, enteredAt);
        ReflectionTestUtils.setField(occupancy, "id", id);
        ReflectionTestUtils.setField(occupancy, "status", status);
        return occupancy;
    }
}
