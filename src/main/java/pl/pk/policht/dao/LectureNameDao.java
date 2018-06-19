package pl.pk.policht.dao;

import com.sun.istack.internal.Nullable;
import org.hibernate.Session;
import org.hibernate.query.Query;
import pl.pk.policht.domain.LectureName;

import java.util.Iterator;
import java.util.List;

public class LectureNameDao {
    private Session session;

    public LectureNameDao(Session session) {
        this.session = session;
    }

    @Nullable
    public LectureName findByName(String name) {
        Query<LectureName> query = session.createQuery("select l from Lecture_name l where l.name = ? or l.shortcut = ?", LectureName.class);
        List<LectureName> lecturers = query.setParameter(0, name).setParameter(1, name).list();
        Iterator<LectureName> iterator = lecturers.iterator();
        if (iterator.hasNext())
            return iterator.next();
        return null;
    }
}
