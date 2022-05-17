package sba.sms.services;


import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import sba.sms.dao.CourseI;
import sba.sms.models.Course;
import sba.sms.utils.HibernateUtil;

import java.util.List;

public class CourseService implements CourseI {

    @Override
    public void createCourse(Course course) {
        Session s = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = s.beginTransaction();
            s.persist(course);
            tx.commit();
        } catch (HibernateException exception) {
            if (tx!=null) tx.rollback();
            exception.printStackTrace();
        } finally {
            s.close();
        }
    }

    @Override
    public Course getCourseById(int courseId) {
        Session s = HibernateUtil.getSessionFactory().openSession();
        try {
            Course course = s.get(Course.class, courseId);
            if(course == null)
                throw new HibernateException("Could not find course");
            else return course;
        } catch (HibernateException e) {
            e.printStackTrace();
        } finally {
            s.close();
        }
        return new Course();
    }

    @Override
    public List<Course> getAllCourses() {
        Session s = HibernateUtil.getSessionFactory().openSession();
        List<Course> allCourses = s.createQuery("From Course", Course.class).getResultList();
        s.close();
        return allCourses;
    }
}
