package pl.pk.policht.dao;

import com.sun.istack.internal.Nullable;
import org.hibernate.Session;
import org.hibernate.query.Query;
import pl.pk.policht.domain.LectureType;

import java.util.Iterator;
import java.util.List;

public class LectureTypeDao {
    private Session session;

    public LectureTypeDao(Session session) {
        this.session = session;
    }

    @Nullable
    public LectureType findByName(String name) {
        Query<LectureType> query = session.createQuery("select l from Lecture_type l where l.name = ? or l.shortcut = ?", LectureType.class);
        List<LectureType> lecturers = query.setParameter(0, name).setParameter(1, name).list();
        Iterator<LectureType> iterator = lecturers.iterator();
        if (iterator.hasNext())
            return iterator.next();
        return null;
    }
}
