package view;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class HowToPlayView {

    private Stage stage;

    public HowToPlayView(Stage stage) {
        this.stage = stage;
    }
    public void show() {
        StackPane root = new StackPane();

        ImageView background = new ImageView();
        try {
            background.setImage(new Image("file:assets/EXAMPLE_MAIN_D.png"));
        } catch (Exception e) {
            System.out.println("How to play image not found.");
        }

        background.setPreserveRatio(false);
        background.fitWidthProperty().bind(root.widthProperty());
        background.fitHeightProperty().bind(root.heightProperty());

        Pane overlay = new Pane();
        overlay.prefWidthProperty().bind(root.widthProperty());
        overlay.prefHeightProperty().bind(root.heightProperty());

        Button startGameButton = makeInvisibleButton();
        startGameButton.layoutXProperty().bind(overlay.widthProperty().multiply(0.30));
        startGameButton.layoutYProperty().bind(overlay.heightProperty().multiply(0.905));
        startGameButton.prefWidthProperty().bind(overlay.widthProperty().multiply(0.40));
        startGameButton.prefHeightProperty().bind(overlay.heightProperty().multiply(0.065));
        startGameButton.setOnAction(e -> {
            StartView startView = new StartView(stage);
            startView.show();
        });

        Button backButton = makeBackButton();
        backButton.setLayoutX(40);
        backButton.setLayoutY(30);
        backButton.setOnAction(e -> {
            StartView startView = new StartView(stage);
            startView.show();
        });

        overlay.getChildren().addAll(startGameButton, backButton);
        root.getChildren().addAll(background, overlay);

        Scene scene = new Scene(root, 1400, 900);
        stage.setTitle("Jackaroo");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    private Button makeDebugButton(String text) {
        Button button = new Button(text);
        button.setStyle(
            "-fx-background-color: rgba(255,0,0,0.35);" +
            "-fx-border-color: red;" +
            "-fx-border-width: 2;" +
            "-fx-text-fill: black;" +
            "-fx-font-weight: bold;" +
            "-fx-cursor: hand;"
        );
        return button;
    }

    private Button makeBackButton() {
        Button button = new Button("BACK");
        button.setStyle(
            "-fx-background-color: black;" +
            "-fx-text-fill: #D4AF37;" +
            "-fx-border-color: #D4AF37;" +
            "-fx-border-width: 2;" +
            "-fx-font-size: 16px;" +
            "-fx-font-weight: bold;" +
            "-fx-cursor: hand;"
        );
        return button;
    }
    private Button makeInvisibleButton() {
        Button button = new Button();
        button.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-border-color: transparent;" +
            "-fx-cursor: hand;"
        );

        button.setOnMouseEntered(e -> {
            button.setScaleX(1.03);
            button.setScaleY(1.03);
        });

        button.setOnMouseExited(e -> {
            button.setScaleX(1.0);
            button.setScaleY(1.0);
        });

        return button;
    }
}