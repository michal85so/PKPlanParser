package pl.pk.policht.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import pl.pk.policht.domain.*;

public class InitSessionFactory {
    private static org.hibernate.SessionFactory sessionFactory;
    private InitSessionFactory() {
    }
    static {
        final Configuration cfg = new Configuration();
        cfg.configure("/hibernate.cfg.xml");
        cfg.addAnnotatedClass(Date.class);
        cfg.addAnnotatedClass(Group.class);
        cfg.addAnnotatedClass(Lecture.class);
        cfg.addAnnotatedClass(Lecturer.class);
        cfg.addAnnotatedClass(LectureType.class);
        cfg.addAnnotatedClass(ClassRoom.class);
        sessionFactory = cfg.buildSessionFactory();
    }
    public static SessionFactory getInstance() {
        return sessionFactory;
    }
}
