package pl.pk.policht.dao;

import com.sun.istack.internal.Nullable;
import org.hibernate.Session;
import org.hibernate.query.Query;
import pl.pk.policht.domain.Lecturer;

import java.util.Iterator;
import java.util.List;

public class LecturerDao {

    private Session session;

    public LecturerDao(Session session) {
        this.session = session;
    }

    @Nullable
    public Lecturer findByName(String name) {
        Query<Lecturer> query = session.createQuery("select l from Lecturer l where l.name = ? or l.shortcut = ? or CONCAT(title, ' ', name) = ?", Lecturer.class);
        List<Lecturer> lecturers = query.setParameter(0, name).setParameter(1, name).setParameter(2, name).list();
        Iterator<Lecturer> iterator = lecturers.iterator();
        if (iterator.hasNext())
            return iterator.next();
        return null;
    }
}
