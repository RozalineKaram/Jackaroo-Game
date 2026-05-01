package view;

import controller.GameController;
import engine.Game;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class EnterNameView {

    private Stage stage;
    private StringBuilder typedName = new StringBuilder();
    private Label nameDisplayLabel;   // shows what the player types

    public EnterNameView(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        StackPane root = new StackPane();

        ImageView background = new ImageView();
        try {
            background.setImage(new Image("file:assets/Enter Name.png"));
        } catch (Exception e) {
            System.out.println("Enter name image not found.");
        }
        background.setPreserveRatio(false);
        background.fitWidthProperty().bind(root.widthProperty());
        background.fitHeightProperty().bind(root.heightProperty());

        Pane overlay = new Pane();
        overlay.prefWidthProperty().bind(root.widthProperty());
        overlay.prefHeightProperty().bind(root.heightProperty());

        // ── Name display label (shows typed text over the text-field area) ──
        nameDisplayLabel = new Label("");
        nameDisplayLabel.setStyle(
        		"-fx-background-color: #E5C88F ;" +
        	    "-fx-text-fill: #1a0e00;" +
        	    "-fx-font-size: 26px;" +
        	    "-fx-font-weight: bold;" +
        	    "-fx-font-family: 'Palatino Linotype';" +
        	    "-fx-alignment: center-left;" +
        	    "-fx-padding: 8 12 8 16;" +
        	    "-fx-background-radius: 4;"
        	);
        // Centred roughly over the name field in the background image
        nameDisplayLabel.layoutXProperty().bind(overlay.widthProperty().multiply(0.29));
        nameDisplayLabel.layoutYProperty().bind(overlay.heightProperty().multiply(0.530));
        nameDisplayLabel.prefWidthProperty().bind(overlay.widthProperty().multiply(0.36));
        nameDisplayLabel.prefHeightProperty().bind(overlay.heightProperty().multiply(0.06));

        // ── CONTINUE button ──
        Button continueButton = makeInvisibleButton();
        continueButton.layoutXProperty().bind(overlay.widthProperty().multiply(0.418));
        continueButton.layoutYProperty().bind(overlay.heightProperty().multiply(0.635));
        continueButton.prefWidthProperty().bind(overlay.widthProperty().multiply(0.176));
        continueButton.prefHeightProperty().bind(overlay.heightProperty().multiply(0.072));

        continueButton.setOnAction(e -> {
            String playerName = typedName.toString().trim();
            if (playerName.isEmpty()) {
                DialogUtils.showError("Invalid Name", "Please enter your name first.");
                return;
            }
            try {
                Game game = new Game(playerName);
                GameController controller = new GameController(game);
                GameView gameView = new GameView(stage, controller);
                gameView.show();
            } catch (Exception ex) {
                ex.printStackTrace();
                DialogUtils.showError("Error", ex.getMessage());
            }
        });

        // ── BACK button ──
        Button backButton = makeBackButton();
        backButton.setLayoutX(40);
        backButton.setLayoutY(30);
        backButton.setOnAction(e -> new StartView(stage).show());

        overlay.getChildren().addAll(nameDisplayLabel, continueButton, backButton);
        root.getChildren().addAll(background, overlay);

        Scene scene = new Scene(root, 1400, 900);

        // ── Keyboard input captures every character typed ──
        scene.setOnKeyTyped(e -> {
            String ch = e.getCharacter();
            if (ch.equals("\r") || ch.equals("\n")) return; // ignore Enter
            if (ch.equals("\b")) {                           // backspace
                if (typedName.length() > 0)
                    typedName.deleteCharAt(typedName.length() - 1);
            } else if (typedName.length() < 20) {           // max 20 chars
                typedName.append(ch);
            }
            updateNameLabel();
        });

        ViewUtils.showFullScreen(stage, scene, "Jackaroo");
        root.requestFocus();
    }

    private void updateNameLabel() {
    	String display = typedName.toString(); // no underscore, no placeholder
        nameDisplayLabel.setText(display);
    }

    private Button makeInvisibleButton() {
        Button button = new Button();
        button.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-border-color: transparent;" +
            "-fx-cursor: hand;"
        );
        button.setOnMouseEntered(e -> { button.setScaleX(1.03); button.setScaleY(1.03); });
        button.setOnMouseExited(e  -> { button.setScaleX(1.0);  button.setScaleY(1.0);  });
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