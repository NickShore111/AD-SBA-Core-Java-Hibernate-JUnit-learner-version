package sba.sms.services;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.*;
import sba.sms.models.Course;
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

    @Test
    @Order(2)
    void createStudent() {
        final int sizeOfAllStudentsBeforeCreate = studentService.getAllStudents().size();
        Student newStudent = new Student("test@gmail.com", "test student", "testPassword");
        studentService.createStudent(newStudent);
        assertThat(studentService.getAllStudents()).contains(newStudent);
        // Student body size should be 1 more than student body size before create
        final int sizeOfAllStudentsAfterCreate = sizeOfAllStudentsBeforeCreate + 1;
        assertThat(studentService.getAllStudents().size()).isEqualTo(sizeOfAllStudentsAfterCreate);
    }

    @Test
    @Order(3)
    void getStudentByEmail() {
        // Positive test
        Student expectedPositive = new Student("reema@gmail.com", "reema brown", "password");
        Student actualPositive1 = studentService.getStudentByEmail("reema@gmail.com");
        assertThat(actualPositive1).isEqualTo(expectedPositive);
        Student actualPositive2 = studentService.getStudentByEmail("REEMA@GMAIL.com");
        assertThat(actualPositive2).isEqualTo(expectedPositive);

        //Negative test
        String fakeEmail = "FakeEmail@NotReal.com";
        assertThat(studentService.getStudentByEmail(fakeEmail)).isNull();
    }

    @Test
    @Order(4)
    void validateStudent() {
        // Negative test
        final String wrongEmail = "fakeEmail@test.com";
        final String wrongPassword = "fakePassword";
        boolean failResult = studentService.validateStudent(wrongEmail,wrongPassword);
        assertThat(failResult).isFalse();

        // Positive test
        final String correctEmail = "reema@gmail.com";
        final String correctPassword = "password";
        boolean acceptResult = studentService.validateStudent(correctEmail, correctPassword);
        assertThat(acceptResult).isTrue();
    }

    @Test
    @Order(6)
    void registerStudentToCourse() {
        String instructorPhillip = "Phillip Witkin";
        Course course = new Course(1,"Java", instructorPhillip);
        Student student = studentService.getStudentByEmail("reema@gmail.com");
        studentService.registerStudentToCourse(student.getEmail(), 1);
        assertThat(studentService.getStudentCourses(student.getEmail())).contains(course);
    }

    @Test
    @Order(5)
    void getStudentCourses() {
        Student student = studentService.getStudentByEmail("annette@gmail.com");
        int[] courseIdList = new int[]{1,2,3,4,5};
        for (int id : courseIdList) {
            studentService.registerStudentToCourse(student.getEmail(), id);
        }
        assertThat(studentService.getStudentCourses(student.getEmail())).size().isEqualTo(courseIdList.length);

    }
}