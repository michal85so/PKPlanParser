package pl.pk.policht;

import org.apache.poi.ss.usermodel.Sheet;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import pl.pk.policht.dao.LectureDao;
import pl.pk.policht.dao.LecturerDao;
import pl.pk.policht.util.DataParser;
import pl.pk.policht.util.FileConnector;
import pl.pk.policht.util.InitSessionFactory;

public class Main {

    public static void main(String[] args) {
        FileConnector connector = new FileConnector(args[0]);
        Sheet sheet = connector.connectAndGetSheet();


        SessionFactory instance = InitSessionFactory.getInstance();
        Session currentSession = instance.openSession();
        Transaction transaction = currentSession.beginTransaction();

        LecturerDao lecturerDao = new LecturerDao(currentSession);

        DataParser dataParser = new DataParser(sheet, lecturerDao);
        dataParser.parse();

        new LectureDao(currentSession).save(dataParser.getLectures());

        transaction.commit();
        currentSession.close();

        System.exit(0);
    }
}
