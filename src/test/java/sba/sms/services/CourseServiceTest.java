package sba.sms.services;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.*;
import sba.sms.models.Course;
import sba.sms.utils.CommandLine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class CourseServiceTest {
    static CourseService courseService;
    @BeforeAll
    static void beforeAll() {
        courseService = new CourseService();
        CommandLine.addData();
    }
    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @Order(3)
    void createCourse() {
        Course newTestCourse = new Course("testCourse", "testInstructor");
        courseService.createCourse(newTestCourse);
        assertThat(courseService.getAllCourses()).contains(newTestCourse);
    }

    @Test
    @Order(2)
    void getCourseById() {
        String instructorPhillip = "Phillip Witkin";
        Course expected = new Course(1,"Java", instructorPhillip);
        Course actual = courseService.getCourseById(1);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @Order(1)
    void getAllCourses() {
        String instructorPhillip = "Phillip Witkin";

        List<Course> expected = new ArrayList<>(Arrays.asList(
            new Course(1,"Java", instructorPhillip),
            new Course(2,"Frontend", "Kasper Kain"),
            new Course(3,"JPA", "Jafer Alhaboubi"),
            new Course(4,"Spring Framework", instructorPhillip),
            new Course(5,"SQL", instructorPhillip)
        ));
        assertThat(courseService.getAllCourses()).hasSameElementsAs(expected);
    }
}