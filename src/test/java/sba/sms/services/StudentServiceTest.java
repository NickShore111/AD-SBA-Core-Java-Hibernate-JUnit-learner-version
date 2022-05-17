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
                new Student("reema@gmail.com", "Reema Brown", "password"),
                new Student("annette@gmail.com", "Annette Allen", "password"),
                new Student("anthony@gmail.com", "Anthony Gallegos", "password"),
                new Student("ariadna@gmail.com", "Ariadna Ramirez", "password"),
                new Student("bolaji@gmail.com", "Bolaji Saibu", "password")
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
        int countAfterNewStudent = studentService.getAllStudents().size();
        assertThat(studentService.getAllStudents()).contains(newTestStudent);
        assertThat(countBeforeNewStudent).isLessThan(countAfterNewStudent);
    }

    @Test
    @Order(3)
    void getStudentByEmail() {
        Student expected = new Student("reema@gmail.com", "Reema Brown", "password");
        Student actual = studentService.getStudentByEmail("reema@gmail.com");
        assertThat(actual).isEqualTo(expected);
        Student capitolActual = studentService.getStudentByEmail("REEMA@gmail.com");
        assertThat(capitolActual).isEqualTo(expected);
    }

    @Test
    @Order(5)
    void registerStudentToCourse() {

    }

    @Test
    @Order(6)
    void getStudentCourses() {

    }
}