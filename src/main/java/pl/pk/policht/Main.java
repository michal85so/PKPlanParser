package pl.pk.policht;

import org.apache.poi.ss.usermodel.Sheet;

public class Main {

    public static void main(String[] args) {
        FileConnector connector = new FileConnector();
        Sheet sheet = connector.connectAndGetSheet();

        StaticDataParser staticDataParser = new StaticDataParser(sheet);
        staticDataParser.parse();




    }
}
