package com.hanguyen.registercourses.config;

import com.hanguyen.registercourses.constant.CourseStatus;
import com.hanguyen.registercourses.constant.Roles;
import com.hanguyen.registercourses.entity.*;
import com.hanguyen.registercourses.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitConfig {
    PasswordEncoder passwordEncoder;

    @Bean
    @Transactional
    ApplicationRunner applicationRunner(
            UserRepository userRepository,
            DepartmentRepository departmentRepository,
            MajorRepository majorRepository,
            SemesterRepository semesterRepository,
            SubjectRepository subjectRepository,
            LecturerRepository lecturerRepository,
            StudentRepository studentRepository,
            CourseRepository courseRepository,
            TimeSlotRepository timeSlotRepository) {
        return args -> {
            if (userRepository.findByUsername("superadmin").isEmpty()) {
                createUser(userRepository, "superadmin", "admin", "Super", "Admin", Roles.ADMIN);
                log.info("Admin account created.");
            }
            List<TimeSlot> timeSlots = initializeTimeSlots(timeSlotRepository);
            if (departmentRepository.count() > 0)
                return;
            log.info("Starting massive data seeding...");
            Map<String, Department> deptMap = new HashMap<>();
            Department cntt = createDepartment(departmentRepository, "Công nghệ thông tin", "CNTT");
            createMajor(majorRepository, "Kỹ thuật phần mềm", "SE", cntt, 500000);
            createMajor(majorRepository, "An toàn thông tin", "IA", cntt, 550000);
            createMajor(majorRepository, "Hệ thống thông tin", "IS", cntt, 500000);
            deptMap.put("CNTT", cntt);
            Department kt = createDepartment(departmentRepository, "Kinh tế", "KT");
            createMajor(majorRepository, "Quản trị kinh doanh", "BA", kt, 450000);
            createMajor(majorRepository, "Marketing", "MKT", kt, 480000);
            deptMap.put("KT", kt);
            Department nn = createDepartment(departmentRepository, "Ngoại ngữ", "NN");
            createMajor(majorRepository, "Ngôn ngữ Anh", "ENG", nn, 420000);
            deptMap.put("NN", nn);
            List<Lecturer> lecturers = new ArrayList<>();
            for (Map.Entry<String, Department> entry : deptMap.entrySet()) {
                Department dept = entry.getValue();
                for (int i = 1; i <= 5; i++) {
                    String deptCode = entry.getKey();
                    String username = "gv_" + deptCode.toLowerCase() + "_" + i;
                    User user = createUser(userRepository, username, "12345678", "Giảng viên", deptCode + " " + i,
                            Roles.LECTURER);
                    Lecturer lec = Lecturer.builder()
                            .user(user)
                            .lecturerCode("GV" + deptCode + i)
                            .degree(i % 3 == 0 ? "Tiến sĩ" : "Thạc sĩ")
                            .department(dept)
                            .build();
                    lecturers.add(lecturerRepository.save(lec));
                }
            }
            List<Subject> subjects = new ArrayList<>();
            subjects.add(createSubject(subjectRepository, "Lập trình Java", "IT001", 3, cntt));
            subjects.add(createSubject(subjectRepository, "Cấu trúc dữ liệu", "IT002", 4, cntt));
            subjects.add(createSubject(subjectRepository, "Cơ sở dữ liệu", "IT003", 4, cntt));
            subjects.add(createSubject(subjectRepository, "Mạng máy tính", "IT004", 3, cntt));
            subjects.add(createSubject(subjectRepository, "Trí tuệ nhân tạo", "IT005", 3, cntt));
            subjects.add(createSubject(subjectRepository, "Kinh tế vi mô", "ECO001", 3, kt));
            subjects.add(createSubject(subjectRepository, "Kinh tế vĩ mô", "ECO002", 3, kt));
            subjects.add(createSubject(subjectRepository, "Marketing căn bản", "MKT001", 3, kt));
            subjects.add(createSubject(subjectRepository, "Tiếng Anh 1", "ENG001", 4, nn));
            subjects.add(createSubject(subjectRepository, "Tiếng Anh 2", "ENG002", 4, nn));
            Semester sem = Semester.builder()
                    .name("Học kỳ 1 2026-2027")
                    .startDate(LocalDate.now())
                    .endDate(LocalDate.now().plusMonths(4))
                    .build();
            semesterRepository.save(sem);
            Random rand = new Random();
            for (Subject sub : subjects) {
                List<Lecturer> deptLecturers = lecturers.stream()
                        .filter(l -> l.getDepartment().getId().equals(sub.getDepartment().getId()))
                        .toList();
                int numCourses = 2 + rand.nextInt(2);
                for (int i = 1; i <= numCourses; i++) {
                    Lecturer lec = deptLecturers.get(rand.nextInt(deptLecturers.size()));
                    String courseCode = sub.getSubjectCode() + ".M" + (10 + i);
                    Course course = Course.builder()
                            .courseCode(courseCode)
                            .subject(sub)
                            .semester(sem)
                            .lecturer(lec)
                            .maxStudents(30 + rand.nextInt(31))
                            .currentStudents(0)
                            .status(CourseStatus.OPEN_FOR_REGISTRATION)
                            .scheduleInfo(generateScheduleInfo(rand))
                            .schedules(new ArrayList<>())
                            .build();
                    int day = 2 + rand.nextInt(6);
                    int startPeriodIdx = rand.nextInt(Math.min(5, timeSlots.size()));
                    int endPeriodIdx = Math.min(startPeriodIdx + 3, timeSlots.size() - 1);
                    CourseSchedule sch = CourseSchedule.builder()
                            .dayOfWeek(day)
                            .startTimeSlot(timeSlots.get(startPeriodIdx))
                            .endTimeSlot(timeSlots.get(endPeriodIdx))
                            .course(course)
                            .build();
                    course.getSchedules().add(sch);
                    courseRepository.save(course);
                }
            }
            List<Major> allMajors = majorRepository.findAll();
            for (int i = 1; i <= 50; i++) {
                String username = "sv_" + i;
                User user = createUser(userRepository, username, "12345678", "Sinh viên", "Thứ " + i, Roles.STUDENT);
                Student student = Student.builder()
                        .user(user)
                        .studentCode(String.format("SV%05d", i))
                        .enrollmentYear(2022 + rand.nextInt(4))
                        .major(allMajors.get(rand.nextInt(allMajors.size())))
                        .build();
                studentRepository.save(student);
            }
            log.info("Massive data seeding completed!");
        };
    }

    private List<TimeSlot> initializeTimeSlots(TimeSlotRepository repo) {
        if (repo.count() > 0) {
            return repo.findAllByIsActiveTrueOrderByPeriodNumber();
        }
        List<TimeSlot> slots = new ArrayList<>();
        String[][] slotData = {
                { "1", "07:00", "07:45", "Tiết 1" },
                { "2", "07:50", "08:35", "Tiết 2" },
                { "3", "08:40", "09:25", "Tiết 3" },
                { "4", "09:35", "10:20", "Tiết 4" },
                { "5", "10:25", "11:10", "Tiết 5" },
                { "6", "11:15", "12:00", "Tiết 6" },
                { "7", "12:30", "13:15", "Tiết 7" },
                { "8", "13:20", "14:05", "Tiết 8" },
                { "9", "14:10", "14:55", "Tiết 9" },
                { "10", "15:05", "15:50", "Tiết 10" },
                { "11", "15:55", "16:40", "Tiết 11" },
                { "12", "16:45", "17:30", "Tiết 12" }
        };
        for (String[] data : slotData) {
            TimeSlot slot = TimeSlot.builder()
                    .periodNumber(Integer.parseInt(data[0]))
                    .startTime(LocalTime.parse(data[1]))
                    .endTime(LocalTime.parse(data[2]))
                    .displayName(data[3])
                    .isActive(true)
                    .build();
            slots.add(repo.save(slot));
        }
        log.info("TimeSlots initialized with {} periods", slots.size());
        return slots;
    }

    private User createUser(UserRepository repo, String username, String password, String first, String last,
            Roles role) {
        Set<Roles> roles = new HashSet<>();
        roles.add(role);
        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .firstname(first)
                .lastname(last)
                .roles(roles)
                .build();
        return repo.save(user);
    }

    private Department createDepartment(DepartmentRepository repo, String name, String code) {
        return repo.save(Department.builder().name(name).departmentCode(code).build());
    }

    private void createMajor(MajorRepository repo, String name, String code, Department dept, double price) {
        repo.save(Major.builder().name(name).majorCode(code).department(dept).pricePerCredit(BigDecimal.valueOf(price))
                .build());
    }

    private Subject createSubject(SubjectRepository repo, String name, String code, int credits, Department dept) {
        return repo.save(Subject.builder().name(name).subjectCode(code).credits(credits).department(dept)
                .prerequisites(new HashSet<>()).build());
    }

    private String generateScheduleInfo(Random rand) {
        int day = 2 + rand.nextInt(6);
        return "Thứ " + day + " (Tiết " + (1 + rand.nextInt(3)) + "-" + (4 + rand.nextInt(3)) + ")";
    }
}