package sba.sms;

import lombok.extern.java.Log;
import sba.sms.models.Course;
import sba.sms.models.Student;
import sba.sms.services.CourseService;
import sba.sms.services.StudentService;
import sba.sms.utils.CommandLine;

import java.util.List;
import java.util.Locale;
import java.util.Scanner;

/**
 * SBA Core Java Hibernate/Junit
 * Business Requirement:
 * task is to create a basic School Management System
 * where students can register for courses, and view the course assigned to them.
 *<br />
 * App uses <br />
 * Initialize dummy data: {@link CommandLine#addData()} <br />
 * Two models: {@link Student} & {@link Course} <br />
 * Two services: {@link StudentService} & {@link CourseService}
 *
 * @author  Jafer Alhaboubi
 * @since sba-core-java-hibernate-junit 1.0
 */
@Log
public class App {
    static final  StudentService studentService = new StudentService();
    static final  CourseService courseService = new CourseService();

    public static void main(String[] args) {
        CommandLine.addData();
        Scanner input = new Scanner(System.in);
        int userInput = 0;

        while (userInput != 3) {
            System.out.printf("Select # from menu:%n1.Add Student%n2.Login Student%n3.Quit%n");
            userInput = input.nextInt();
            switch (userInput) {
                // Manually create and add new student to system
                case 1:
                    Student newStudent = new Student();
                    System.out.println("Student first name:");
                    String firstName = input.next();
                    System.out.println("Student last name:");
                    String lastName = input.next();
                    newStudent.setName(firstName + " " + lastName);
                    System.out.println("Student email:");
                    newStudent.setEmail(input.next());
                    System.out.println("Password:");
                    newStudent.setPassword(input.next());
                    if (studentService.createStudent(newStudent)) {
                        System.out.printf("%s successfully added to system%n", newStudent.getName());
                    } else {
                        System.out.printf("Student %s with email %s already in system!%n", newStudent.getName(), newStudent.getEmail());
                    }
                    break;
                    // Login existing student
                case 2:
                    System.out.print("Enter student email: ");
                    String email = input.next();
                    Student student = studentService.getStudentByEmail(email.toLowerCase(Locale.ROOT));
                    if (student != null) {
                        System.out.printf("Enter %s's password: ", student.getName());
                        String password = input.next();
                        if (studentService.validateStudent(email, password)) {
                            printStudentCourses(email);
                            while (userInput != 3) {
                                System.out.printf("select # from menu: %n1.Register classes for %s%n2.Unregister for classes%n3.Logout%n", studentService.getStudentByEmail(email).getName());
                                userInput = input.nextInt();
                                if (userInput == 3) {
                                    break;
                                } else if (userInput == 2) {
                                    // Unregister for a class
                                    System.out.println("Unregister for course #: ");
                                    int unregisterCourseId = input.nextInt();
                                    if (studentService.unregisterStudentToCourse(student.getEmail(), unregisterCourseId)) {
                                        System.out.printf("Successfully unregistered %s from %s%n", student.getName(), courseService.getCourseById(unregisterCourseId).getName());
                                    }
                                } else if (userInput == 1){
                                    // Register for a class
                                    List<Course> courseList = courseService.getAllCourses();
                                    System.out.printf("All courses:%n-----------------------------%n");
                                    System.out.printf("%-2s | %-20s | %s%n", "ID", "Course", "Instructor");
                                    if (courseList.isEmpty()) System.out.printf("No courses to view%n");
                                    for (Course course : courseList) {
                                        System.out.printf("%-2d | %-20s | %s%n", course.getId(), course.getName(), course.getInstructor());
                                    }
                                    System.out.print("select course #: ");
                                    int courseId = input.nextInt();
                                    if (courseId > 0 && courseId <= courseList.size()) {
                                        if (studentService.registerStudentToCourse(email, (courseId))) {
                                            System.out.printf("Successfully registered %s to %s%n", studentService.getStudentByEmail(email).getName(), courseService.getCourseById(courseId).getName());
                                            printStudentCourses(email);
                                        }
                                    } else {
                                        System.out.printf("course id not found!%n");
                                    }
                                } else {
                                    System.out.println("Try again!%n");
                                }
                            }// Registration while loop ends
                            System.out.printf("session ended!%n");
                        } else {
                            System.out.printf("Incorrect username or password%n");
                        }
                    } else {
                        System.out.printf("Student with email %s not found!%n", email);
                    }
                    break;
                case 3:
                    System.exit(0);
                    break;
                default:
                    System.out.println("Try again!%n");
            }
        }
        input.close();
    }

    private static void printStudentCourses(String email) {
        System.out.printf("%s courses:%n-----------------------------%n", email);
        System.out.printf("%-2s | %-20s | %s%n", "ID", "Course", "Instructor");
        List<Course> userCourses = studentService.getStudentCourses(email.toLowerCase());
        if (userCourses.isEmpty()) System.out.printf("No courses to view%n");
        for (Course course : userCourses) {
            System.out.printf("%-2d | %-20s | %s%n", course.getId(), course.getName(), course.getInstructor());
        }
    }
}
