package view;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class StartView {

    private Stage stage;

    private Pane overlay;
    private Button playButton;
    private Button multiButton;
    private Button howToPlayButton;
    private Button exitButton;
    private Button settingsButton;
    public StartView(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        StackPane root = new StackPane();

        ImageView background = new ImageView();
        try {
            background.setImage(new Image("file:assets/Interance.png"));
        } catch (Exception e) {
            System.out.println("Main menu image not found.");
        }

        background.setPreserveRatio(false);
        background.fitWidthProperty().bind(root.widthProperty());
        background.fitHeightProperty().bind(root.heightProperty());

        overlay = new Pane();
        overlay.prefWidthProperty().bind(root.widthProperty());
        overlay.prefHeightProperty().bind(root.heightProperty());
        settingsButton = makeInvisibleButton();
        playButton = makeInvisibleButton();
        multiButton = makeInvisibleButton();
        howToPlayButton = makeInvisibleButton();
        exitButton = makeInvisibleButton();

        playButton.setOnAction(e -> {
            EnterNameView enterNameView = new EnterNameView(stage);
            enterNameView.show();
        });

        multiButton.setOnAction(e -> {
            MultiplayerView multiplayerView = new MultiplayerView(stage);
            multiplayerView.show();
        });
        settingsButton.setOnAction(e -> {
            SettingsView settingsView = new SettingsView(stage);
            settingsView.show();
        });

        howToPlayButton.setOnAction(e -> {
            HowToPlayView howToPlayView = new HowToPlayView(stage);
            howToPlayView.show();
        });

        exitButton.setOnAction(e -> stage.close());

        overlay.getChildren().addAll(playButton, multiButton, howToPlayButton, exitButton,settingsButton);
        root.getChildren().addAll(background, overlay);

        Scene scene = new Scene(root, 1400, 900);
        ViewUtils.showFullScreen(stage, scene, "Jackaroo");

        ChangeListener<Number> resizeListener = (obs, oldVal, newVal) -> positionButtons();
        overlay.widthProperty().addListener(resizeListener);
        overlay.heightProperty().addListener(resizeListener);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                positionButtons();
            }
        });
    }

    private void positionButtons() {
        double w = overlay.getWidth();
        double h = overlay.getHeight();

        if (w <= 0 || h <= 0) {
            return;
        }

        double buttonY = h * 0.595;
        double buttonW = w * 0.102;
        double buttonH = h * 0.145;
        placeCentered(playButton,      w * 0.242, buttonY, buttonW, buttonH);
        placeCentered(multiButton,     w * 0.362, buttonY, buttonW, buttonH);
        placeCentered(settingsButton,  w * 0.482, buttonY, buttonW, buttonH);
        placeCentered(howToPlayButton, w * 0.602, buttonY, buttonW, buttonH);
        placeCentered(exitButton,      w * 0.722, buttonY, buttonW, buttonH);
    }

    private void placeCentered(Button button, double centerX, double y, double width, double height) {
        button.setPrefWidth(width);
        button.setPrefHeight(height);
        button.setLayoutX(centerX - width / 2.0);
        button.setLayoutY(y);
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