package view;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
    	primaryStage.getIcons().add(new Image("file:assets/icon.png"));
    	MusicManager.play("assets/background.mp3"); 
        StartView startView = new StartView(primaryStage);
        startView.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
