package pl.pk.policht;

import javafx.application.Application;
import javafx.stage.Stage;
import pl.pk.policht.gui.DesktopGui;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Plan parser");

        DesktopGui gui = new DesktopGui(primaryStage);
        gui.createGUI();
    }
}
