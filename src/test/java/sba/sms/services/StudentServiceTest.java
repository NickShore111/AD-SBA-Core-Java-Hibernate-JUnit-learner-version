package sba.sms.services;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import sba.sms.models.Student;
import sba.sms.utils.CommandLine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class StudentServiceTest {

    static StudentService studentService;

    @BeforeAll
    static void beforeAll() {
        studentService = new StudentService();
        CommandLine.addData();
    }

    @Test
    @Order(1)
    void getAllStudents() {

        List<Student> expected = new ArrayList<>(Arrays.asList(
                new Student("reema@gmail.com", "reema brown", "password"),
                new Student("annette@gmail.com", "annette allen", "password"),
                new Student("anthony@gmail.com", "anthony gallegos", "password"),
                new Student("ariadna@gmail.com", "ariadna ramirez", "password"),
                new Student("bolaji@gmail.com", "bolaji saibu", "password")
        ));

        assertThat(studentService.getAllStudents()).hasSameElementsAs(expected);

    }

    @Order(4)
    @ParameterizedTest
    @ValueSource(strings = {"reema@gmail.com", "annette@gmail.com", "anthony@gmail.com", "ariadna@gmail.com", "bolaji@gmail.com"})
    void validateStudent(String emails) {
        String PASSWORD = "password";
        assertThat(studentService.validateStudent(emails, PASSWORD)).isTrue();
    }

    @Test
    @Order(2)
    void createStudent() {
        int countBeforeNewStudent = studentService.getAllStudents().size();
        Student newTestStudent = new Student("test@gmail.com", "test student", "testPassword");
        studentService.createStudent(newTestStudent);
        assertThat(studentService.getAllStudents()).contains(newTestStudent);
    }

    @Test
    @Order(3)
    void getStudentByEmail() {
        Student expected = new Student("reema@gmail.com", "reema brown", "password");
        Student actual = studentService.getStudentByEmail("reema@gmail.com");
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @Order(5)
    void registerStudentToCourse() {

//        Assertions.assertDoesNotThrow(studentService.registerStudentToCourse("reema@gmail.com", 1));
        studentService.registerStudentToCourse("reema@gmail.com", 2);
        studentService.registerStudentToCourse("reema@gmail.com", 3);

    }

    @Test
    @Order(6)
    void getStudentCourses() {

    }
}