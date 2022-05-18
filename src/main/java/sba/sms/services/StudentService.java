package sba.sms.services;

import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.Metamodel;
import lombok.extern.java.Log;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
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
                System.out.printf("%s already registered for %s%n", student.getName(),course.getName());
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

//    @Override
//    public List<Course> getStudentCourses(String email) {
//        List<Course> coursesList = null;
//        try (Session s = HibernateUtil.getSessionFactory().openSession()){
//            NativeQuery q = s.createNativeQuery("SELECT c.id, c.name, c.instructor FROM Course as c JOIN student_courses as sc ON sc.course_id = c.id JOIN Student as s ON s.email = sc.student_email WHERE s.email = :email",Course.class);
//            q.setParameter("email",email);
//            coursesList = q.getResultList();
//        } catch (HibernateException exception) {
//            exception.printStackTrace();
//        }
//        return coursesList;
//    }

//    @Override

    public List<Course> getStudentCourses(String student_email) {
        // Session s = HibernateUtil.getSessionFactory().openSession();
        List<Course> coursesList = new ArrayList<>();
        Transaction tx = null;
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            tx = s.beginTransaction();
            CriteriaBuilder cb = s.getCriteriaBuilder();
            CriteriaQuery<Student> sq = cb.createQuery(Student.class);
            CriteriaQuery<Course> cq = cb.createQuery(Course.class);

//            Root<Student> student = sq.from(Student.class);
//            Metamodel sm = s.getMetamodel();
            // Creating metaModel of class entity
            Metamodel m = s.getMetamodel();
            EntityType<Student> Student_ = m.entity(Student.class);
//            EntityType<Course> Course_ = m.entity(Course.class);
//            Root<Student> student = sq.from(Student_);
//            Root<Course> course = cq.from(Course_);

            // A valid alternative to create Root<Course> using Class entity instead of metaModel
            Root<Course> courseRoot = cq.from(Course.class);
            Root<Student> studentRoot = sq.from(Student.class);
            // Student query build and results
//            cr.select(root).where(cb.like(root.get("itemName"), "%chair%"));
            sq.select(studentRoot).where(cb.like(studentRoot.get("email"), student_email));
            TypedQuery<Student> ss = s.createQuery(sq);
            Student student = ss.getSingleResult();
            System.out.println(student);
//            List<Student> allStudents = ss.getResultList();
//            System.out.println(allStudents.toString());
            // Course query build and results
//            cq.select(courseRoot);
            TypedQuery<Course> q = s.createQuery(cq);
            List<Course> allCourses = q.getResultList();
            System.out.println(allCourses.toString());

        } catch (Exception e) {
            e.printStackTrace();
            if (tx!=null) {
                tx.rollback();
            }
        }
        return coursesList;
    }
}
