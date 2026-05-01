package view;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MultiplayerView {

    private Stage stage;
    private int selectedPlayers = 0;

    public MultiplayerView(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        StackPane root = new StackPane();

        ImageView background = new ImageView();
        try {
            background.setImage(new Image("file:assets/Multiplayer.png"));
        } catch (Exception e) {
            System.out.println("Multiplayer image not found.");
        }

        background.setPreserveRatio(false);
        background.fitWidthProperty().bind(root.widthProperty());
        background.fitHeightProperty().bind(root.heightProperty());

        Pane overlay = new Pane();
        overlay.prefWidthProperty().bind(root.widthProperty());
        overlay.prefHeightProperty().bind(root.heightProperty());

        // ===== LABEL =====
        Label selectedLabel = new Label("Selected pharaohs are: 0 pharaohs");

        selectedLabel.layoutXProperty().bind(overlay.widthProperty().multiply(0.35));
        selectedLabel.layoutYProperty().bind(overlay.heightProperty().multiply(0.475));

        selectedLabel.setStyle(
            "-fx-text-fill: black;" +
            "-fx-font-size: 26px;" +
            "-fx-font-weight: bold;" +
            "-fx-font-family: 'Georgia';"
        );

        // ===== PLAYER BUTTONS =====
        Button twoBtn = makeInvisibleButton();
        Button threeBtn = makeInvisibleButton();
        Button fourBtn = makeInvisibleButton();

        // positions (already tuned from your previous work)
        twoBtn.layoutXProperty().bind(overlay.widthProperty().multiply(0.30));
        twoBtn.layoutYProperty().bind(overlay.heightProperty().multiply(0.48));
        twoBtn.prefWidthProperty().bind(overlay.widthProperty().multiply(0.13));
        twoBtn.prefHeightProperty().bind(overlay.heightProperty().multiply(0.18));

        threeBtn.layoutXProperty().bind(overlay.widthProperty().multiply(0.435));
        threeBtn.layoutYProperty().bind(overlay.heightProperty().multiply(0.48));
        threeBtn.prefWidthProperty().bind(overlay.widthProperty().multiply(0.13));
        threeBtn.prefHeightProperty().bind(overlay.heightProperty().multiply(0.18));

        fourBtn.layoutXProperty().bind(overlay.widthProperty().multiply(0.57));
        fourBtn.layoutYProperty().bind(overlay.heightProperty().multiply(0.48));
        fourBtn.prefWidthProperty().bind(overlay.widthProperty().multiply(0.13));
        fourBtn.prefHeightProperty().bind(overlay.heightProperty().multiply(0.18));

        // ===== ACTIONS =====
        twoBtn.setOnAction(e -> updateSelection(2, selectedLabel));
        threeBtn.setOnAction(e -> updateSelection(3, selectedLabel));
        fourBtn.setOnAction(e -> updateSelection(4, selectedLabel));

        // ===== CONTINUE BUTTON =====
        Button continueBtn = makeInvisibleButton();

        continueBtn.layoutXProperty().bind(overlay.widthProperty().multiply(0.39));
        continueBtn.layoutYProperty().bind(overlay.heightProperty().multiply(0.72));
        continueBtn.prefWidthProperty().bind(overlay.widthProperty().multiply(0.22));
        continueBtn.prefHeightProperty().bind(overlay.heightProperty().multiply(0.09));

        continueBtn.setOnAction(e -> {
            if (selectedPlayers == 0) {
                DialogUtils.showError("Selection Required", "Choose number of players first.");
                return;
            }

            // go directly to name entry
            EnterNameView enterNameView = new EnterNameView(stage);
            enterNameView.show();
        });

        // ===== BACK BUTTON =====
        Button backBtn = makeBackButton();
        backBtn.setLayoutX(40);
        backBtn.setLayoutY(30);

        backBtn.setOnAction(e -> {
            StartView startView = new StartView(stage);
            startView.show();
        });

        overlay.getChildren().addAll(
                selectedLabel,
                twoBtn, threeBtn, fourBtn,
                continueBtn,
                backBtn
        );

        root.getChildren().addAll(background, overlay);

        Scene scene = new Scene(root, 1400, 900);
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    private void updateSelection(int players, Label label) {
        selectedPlayers = players;
        label.setText("Selected pharaohs are: " + players + " pharaohs");
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
}