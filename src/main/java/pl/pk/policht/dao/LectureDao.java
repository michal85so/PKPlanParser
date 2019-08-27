package pl.pk.policht.dao;

import org.hibernate.Session;
import pl.pk.policht.domain.Lecture;

import java.util.List;

public class LectureDao {

    private Session session;

    public LectureDao(Session session) {
        this.session = session;
    }

    public void saveAll(List<Lecture> lectures) {
        lectures.forEach(session::save);
    }

    public void save(Lecture lecture) {
        session.save(lecture);
    }
}
