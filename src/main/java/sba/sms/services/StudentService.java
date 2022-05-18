package sba.sms.services;

import lombok.extern.java.Log;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import sba.sms.dao.StudentI;
import sba.sms.models.Course;
import sba.sms.models.Student;
import sba.sms.utils.HibernateUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


@Log
public class StudentService implements StudentI {

    @Override
    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            students = s.createQuery("From Student", Student.class).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return students;
    }

    @Override
    public void createStudent(Student student) {
        Transaction tx = null;
        String[] splitName = student.getName().trim().split(" ");
        String titleFirstName =  splitName[0].substring(0,1).toUpperCase().concat(splitName[0].substring(1).toLowerCase());
        String titleLastName =  splitName[1].substring(0,1).toUpperCase().concat(splitName[1].substring(1).toLowerCase());
        student.setName(titleFirstName+" "+titleLastName);
        student.setPassword(student.getPassword().toLowerCase(Locale.ROOT));
        try (Session s = HibernateUtil.getSessionFactory().openSession()){
            List<Student> allStudents = getAllStudents();
            tx = s.beginTransaction();
            if (!allStudents.contains(student)) {
                s.persist(student);
                tx.commit();
            }
        } catch (HibernateException exception) {
            if (tx!=null) tx.rollback();
            exception.printStackTrace();
        }
    }

    @Override
    public Student getStudentByEmail(String email) {
        Student student = null;
        try (Session s = HibernateUtil.getSessionFactory().openSession()){
            student = s.get(Student.class, email.toLowerCase(Locale.ROOT));
            if (student == null) {
                System.out.println("Unable to locate student!");
            }
        } catch (HibernateException e) {
            e.printStackTrace();
        }
        return student;
    }

    @Override
    public boolean validateStudent(String email, String password) {
        Student student = null;
        try (Session s = HibernateUtil.getSessionFactory().openSession()){
            student = s.get(Student.class, email.toLowerCase(Locale.ROOT));
            if (student == null) {
                System.out.println("Unable to locate student!");
                return false;
            }
            return student.getPassword().equals(password);
        } catch (HibernateException exception) {
            exception.printStackTrace();
        }
        return false;
    }

    @Override
    public void registerStudentToCourse(String email, int courseId) {
        Transaction tx = null;
        try (Session s = HibernateUtil.getSessionFactory().openSession()){
            tx = s.beginTransaction();
            Student student = s.get(Student.class, email.toLowerCase(Locale.ROOT));
            Course course = s.get(Course.class, courseId);
            if (student.getCourses().contains(course)) {
                System.out.printf("%s already registered with %s%n", student.getName(),course.getName());
                return;
            }
            student.addCourse(course);
            System.out.printf("Successfully registered %s for %s%n", student.getName(), course.getName());
            s.merge(student);
            tx.commit();
        } catch (HibernateException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public List<Course> getStudentCourses(String email) {
        Session s = HibernateUtil.getSessionFactory().openSession();
        List<Course> coursesList = null;
        try {
            NativeQuery q = s.createNativeQuery("SELECT c.id, c.name, c.instructor FROM Course as c JOIN student_courses as sc ON sc.course_id = c.id JOIN Student as s ON s.email = sc.student_email WHERE s.email = :email",Course.class);
            q.setParameter("email",email);
            coursesList = q.getResultList();
        } catch (HibernateException exception) {
            exception.printStackTrace();
        } finally {
            s.close();
        }
        return coursesList;
    }

//    @Override
//    public List<Course> getStudentCourses(String email) {
//        // Session s = HibernateUtil.getSessionFactory().openSession();
//        List<Course> coursesList = new ArrayList<>();
//        Transaction tx = null;
//        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
//            tx = s.beginTransaction();
//            CriteriaBuilder builder = s.getCriteriaBuilder();
//
//            CriteriaQuery<Object[]> criteriaQuery = builder.createQuery(Object[].class);
//            Root<Course> courseRoot = criteriaQuery.from(Course.class);
//            Root<Student> studentRoot = criteriaQuery.from(Student.class);
//
//            criteriaQuery.multiselect(courseRoot, studentRoot);
//            criteriaQuery.where(builder.equal(studentRoot.get("courses"), courseRoot.get("id")));
//
//            Query<Object[]> query = s.createQuery(criteriaQuery);
//            List<Object[]> list = query.getResultList();
//            if (!list.isEmpty()) {
//                for (Object[] objects : list) {
//                    Course course = (Course) objects[0];
//                    Student student = (Student) objects[1];
//                    log.info("Course: " + course.getName() + " Student: " + student.getName());
//                }
//                tx.commit();
//            }
//
//            //            Query q = s.createQuery("SELECT c.id, c.name, c.instructor from Course as c LEFT JOIN FETCH Student as s WHERE s.email= :email");
////            q.setParameter("email", email);
////            List<Object[]> courses = q.getResultList();
////            coursesList = courses.stream().map(objects -> new Course((Integer)objects[0], (String)objects[1], (String)objects[2])).collect(Collectors.toList());
//
////        } catch (HibernateException exception) {
////            exception.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//            if (tx!=null) {
//                tx.rollback();
//            }
//        }
//        return coursesList;
//    }
}
