package pl.pk.policht;

import org.apache.poi.ss.usermodel.Sheet;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import pl.pk.policht.domain.Date;
import pl.pk.policht.util.DataParser;
import pl.pk.policht.util.FileConnector;
import pl.pk.policht.util.InitSessionFactory;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        FileConnector connector = new FileConnector();
        Sheet sheet = connector.connectAndGetSheet();

        DataParser dataParser = new DataParser(sheet);
        dataParser.parse();

        SessionFactory instance = InitSessionFactory.getInstance();
        Session currentSession = instance.openSession();
        Transaction transaction = currentSession.beginTransaction();

        List<Date> dates = dataParser.getDates();
        dates.stream().forEach(currentSession::save);
        transaction.commit();
        currentSession.close();
    }
}
