package pl.pk.policht.gui;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.Sheet;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import pl.pk.policht.dao.LectureDao;
import pl.pk.policht.dao.LectureNameDao;
import pl.pk.policht.dao.LectureTypeDao;
import pl.pk.policht.dao.LecturerDao;
import pl.pk.policht.util.ExtramuralStudiesDataParser;
import pl.pk.policht.util.FileConnector;
import pl.pk.policht.util.FullTimeStudiesDataParser;
import pl.pk.policht.util.InitSessionFactory;

import java.io.File;

public class DesktopGui {

    private Stage primaryStage;

    public DesktopGui(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void createGUI() {
        Pane panel = createMainPanel();
        Scene scene = new Scene(panel, 320, 240);

        createButtons(panel);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Pane createMainPanel() {
        FlowPane panel = new FlowPane(10, 10);
        panel.setAlignment(Pos.CENTER);
        return panel;
    }

    private void createButtons(Pane panel) {
        final RadioButton stacButton = new RadioButton("Stacjonarne");
        final RadioButton niestacButton = new RadioButton("Niestacjonarne");
        ToggleGroup toggleGroup = new ToggleGroup();
        stacButton.setToggleGroup(toggleGroup);
        niestacButton.setToggleGroup(toggleGroup);

        Button fileChooserButton = new Button("Wybierz plik i parsuj");

        FileChooser fileChooser = createFileChooser();

        fileChooserButton.setOnAction(i -> {
            File file = fileChooser.showOpenDialog(primaryStage);
            FileConnector connector = new FileConnector(file.getPath());

            Sheet sheet = connector.connectAndGetSheet();

            SessionFactory instance = InitSessionFactory.getInstance();
            Session currentSession = instance.openSession();
            Transaction transaction = currentSession.beginTransaction();

            LecturerDao lecturerDao = new LecturerDao(currentSession);
            LectureTypeDao lectureTypeDao = new LectureTypeDao(currentSession);
            LectureNameDao lectureNameDao = new LectureNameDao(currentSession);

            if (stacButton.isSelected()) {
                FullTimeStudiesDataParser FullTimeStudiesDataParser = new FullTimeStudiesDataParser(sheet, lecturerDao, lectureTypeDao, lectureNameDao);
                FullTimeStudiesDataParser.parse();
                new LectureDao(currentSession).saveAll(FullTimeStudiesDataParser.getLectures());
            }
            else if (niestacButton.isSelected()) {
                ExtramuralStudiesDataParser extramuralStudiesDataParser = new ExtramuralStudiesDataParser(sheet, lecturerDao, lectureTypeDao, lectureNameDao, new LectureDao(currentSession));
                extramuralStudiesDataParser.parse();
//                new LectureDao(currentSession).saveAll(extramuralStudiesDataParser.getLectures());
            }

            transaction.commit();
            currentSession.close();
        });

        panel.getChildren().addAll(stacButton, niestacButton, fileChooserButton);
    }

    private FileChooser createFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Wybierz plik xls");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XLS", "*.xls"));
        return fileChooser;
    }
}
