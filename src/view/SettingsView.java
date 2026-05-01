package view;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class SettingsView {

    private Stage stage;
    private Scene previousScene;
    private String previousTitle;

    public SettingsView(Stage stage) {
        this.stage = stage;
        this.previousScene = stage.getScene();
        this.previousTitle = stage.getTitle();
    }

    public void show() {
        StackPane root = new StackPane();

        ImageView background = new ImageView();
        try {
            background.setImage(new Image("file:assets/board.png"));
        } catch (Exception e) {
            root.setStyle("-fx-background-color: #1a0d04;");
        }

        background.setPreserveRatio(false);
        background.fitWidthProperty().bind(root.widthProperty());
        background.fitHeightProperty().bind(root.heightProperty());

        Pane dimmer = new Pane();
        dimmer.setStyle("-fx-background-color: rgba(0,0,0,0.65);");
        dimmer.prefWidthProperty().bind(root.widthProperty());
        dimmer.prefHeightProperty().bind(root.heightProperty());

        VBox panel = new VBox(28);
        panel.setAlignment(Pos.CENTER);
        panel.setMaxWidth(480);
        panel.setMaxHeight(420);
        panel.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #2b1800, #0e0800);" +
            "-fx-border-color: #d4af37;" +
            "-fx-border-width: 3;" +
            "-fx-border-radius: 16;" +
            "-fx-background-radius: 16;" +
            "-fx-padding: 40 50 40 50;"
        );

        DropShadow glow = new DropShadow();
        glow.setColor(Color.web("#d4af37", 0.6));
        glow.setRadius(30);
        panel.setEffect(glow);

        Label title = new Label("⚙  SETTINGS");
        title.setStyle(
            "-fx-text-fill: #f5d06f;" +
            "-fx-font-size: 28px;" +
            "-fx-font-weight: bold;" +
            "-fx-font-family: 'Palatino Linotype';"
        );

        Label divider1 = new Label("───────────────────────");
        divider1.setStyle("-fx-text-fill: #8b6914; -fx-font-size: 13px;");

        Label divider2 = new Label("───────────────────────");
        divider2.setStyle("-fx-text-fill: #8b6914; -fx-font-size: 13px;");

        Label volLabel = new Label("♪  MUSIC VOLUME");
        volLabel.setStyle(
            "-fx-text-fill: #e8c97a;" +
            "-fx-font-size: 15px;" +
            "-fx-font-weight: bold;" +
            "-fx-font-family: 'Palatino Linotype';"
        );

        Slider volumeSlider = new Slider(0, 1, MusicManager.getVolume());
        volumeSlider.setPrefWidth(320);
        volumeSlider.setStyle(
            "-fx-control-inner-background: #3b2208;" +
            "-fx-accent: #d4af37;"
        );

        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            MusicManager.setVolume(newVal.doubleValue());

            if (MusicManager.isMuted() && newVal.doubleValue() > 0) {
                MusicManager.setMuted(false);
            }
        });

        Label volPercent = new Label();
        volPercent.setStyle(
            "-fx-text-fill: #c8a85a;" +
            "-fx-font-size: 13px;" +
            "-fx-font-weight: bold;"
        );

        volPercent.textProperty().bind(
            volumeSlider.valueProperty().multiply(100).asString("%.0f%%")
        );

        HBox sliderRow = new HBox(14, volumeSlider, volPercent);
        sliderRow.setAlignment(Pos.CENTER);

        Button muteBtn = makeMuteButton();
        updateMuteButton(muteBtn);

        muteBtn.setOnAction(e -> {
            MusicManager.setMuted(!MusicManager.isMuted());
            updateMuteButton(muteBtn);
        });

        Button backBtn = makeGoldButton("◀   BACK");
        backBtn.setOnAction(e -> {
            stage.setScene(previousScene);
            stage.setTitle(previousTitle);
        });

        panel.getChildren().addAll(
            title,
            divider1,
            volLabel,
            sliderRow,
            muteBtn,
            divider2,
            backBtn
        );

        root.getChildren().addAll(background, dimmer, panel);
        StackPane.setAlignment(panel, Pos.CENTER);

        Scene scene = new Scene(root, 1400, 900);
        ViewUtils.showFullScreen(stage, scene, "Jackaroo — Settings");
    }

    private void updateMuteButton(Button btn) {
        if (MusicManager.isMuted()) {
            btn.setText("🔇   UNMUTE");
            btn.setStyle(getMuteStyle("#7b2d2d", "#a33030"));
        } else {
            btn.setText("🔊   MUTE");
            btn.setStyle(getMuteStyle("#2d5a2d", "#3a7a3a"));
        }
    }

    private String getMuteStyle(String bg, String border) {
        return  "-fx-background-color: " + bg + ";" +
                "-fx-text-fill: #f5d06f;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-font-family: 'Palatino Linotype';" +
                "-fx-border-color: " + border + ";" +
                "-fx-border-width: 2;" +
                "-fx-border-radius: 20;" +
                "-fx-background-radius: 20;" +
                "-fx-padding: 8 32;" +
                "-fx-cursor: hand;";
    }

    private Button makeMuteButton() {
        Button btn = new Button();
        btn.setOnMouseEntered(e -> btn.setOpacity(0.85));
        btn.setOnMouseExited(e -> btn.setOpacity(1.0));
        return btn;
    }

    private Button makeGoldButton(String text) {
        Button btn = new Button(text);

        String base =
            "-fx-background-color: linear-gradient(to bottom, #c9952a, #7a5510);" +
            "-fx-text-fill: #1a0d04;" +
            "-fx-font-size: 15px;" +
            "-fx-font-weight: bold;" +
            "-fx-font-family: 'Palatino Linotype';" +
            "-fx-border-color: #f5d06f;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 20;" +
            "-fx-background-radius: 20;" +
            "-fx-padding: 9 36;" +
            "-fx-cursor: hand;";

        btn.setStyle(base);
        btn.setOnMouseEntered(e -> btn.setOpacity(0.85));
        btn.setOnMouseExited(e -> btn.setOpacity(1.0));

        return btn;
    }
}