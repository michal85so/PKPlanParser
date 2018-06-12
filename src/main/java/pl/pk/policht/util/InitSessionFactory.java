package pl.pk.policht.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import pl.pk.policht.domain.Date;
import pl.pk.policht.domain.Group;

public class InitSessionFactory {
    private static org.hibernate.SessionFactory sessionFactory;
    private InitSessionFactory() {
    }
    static {
        final Configuration cfg = new Configuration();
        cfg.configure("/hibernate.cfg.xml");
        cfg.addAnnotatedClass(Date.class);
        cfg.addAnnotatedClass(Group.class);
        sessionFactory = cfg.buildSessionFactory();
    }
    public static SessionFactory getInstance() {
        return sessionFactory;
    }
}
