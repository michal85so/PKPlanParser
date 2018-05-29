package pl.pk.policht;

import org.apache.poi.ss.usermodel.Sheet;
import pl.pk.policht.util.DataParser;
import pl.pk.policht.util.FileConnector;

public class Main {

    public static void main(String[] args) {
        FileConnector connector = new FileConnector();
        Sheet sheet = connector.connectAndGetSheet();

        DataParser dataParser = new DataParser(sheet);
        dataParser.parse();




    }
}
