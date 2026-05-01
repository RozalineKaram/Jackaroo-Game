package view;

import java.util.ArrayList;

import controller.GameController;
import engine.Game;
import exception.GameException;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.card.Card;
import model.card.standard.Seven;
import model.player.Player;

public class GameView {
	private Label cardActionLabel;
    private Stage stage;
   
    private GameController controller;

    private BoardView boardView;
    private Pane overlayPane;

    private Label currentPlayerLabel;
    private Label nextPlayerLabel;
    private Label turnLabel;

    private Label bottomPlayerNameLabel;
    private Label leftPlayerNameLabel;
    private Label topPlayerNameLabel;
    private Label rightPlayerNameLabel;

    private Label firePitLabel;
    private ImageView firePitCardView;

    private VBox playerInfoBox;

    private Button playButton;
    private Button discardButton;
    private Button deselectButton;

    private ArrayList<CardView> cardViews;

    private boolean cpuTurnInProgress = false;
    private ArrayList<ArrayList<ImageView>> cpuCardViews = new ArrayList<>();

    public GameView(Stage stage, GameController controller) {
        this.stage = stage;
        this.controller = controller;
        this.cardViews = new ArrayList<CardView>();
    }

    public void show() {
    	cardActionLabel = new Label();
    	cardActionLabel.setVisible(false);

    	cardActionLabel.setStyle(
    	    "-fx-text-fill: #3b2b0f;" +
    	    "-fx-font-size: 15px;" +
    	    "-fx-font-weight: bold;" +
    	    "-fx-background-color: rgba(229,200,143,0.85);" +
    	    "-fx-padding: 6 10 6 10;" +
    	    "-fx-background-radius: 10;"
    	);
        boardView = new BoardView();
        boardView.setController(controller, this::refreshUI);
        overlayPane = boardView.getOverlayPane();

        currentPlayerLabel = new Label();
        nextPlayerLabel = new Label();
        turnLabel = new Label();

        styleInfoLabel(currentPlayerLabel);
        styleInfoLabel(nextPlayerLabel);
        styleInfoLabel(turnLabel);

        bottomPlayerNameLabel = new Label();
        leftPlayerNameLabel = new Label();
        topPlayerNameLabel = new Label();
        rightPlayerNameLabel = new Label();

        stylePlayerNameLabel(bottomPlayerNameLabel);
        stylePlayerNameLabel(leftPlayerNameLabel);
        stylePlayerNameLabel(topPlayerNameLabel);
        stylePlayerNameLabel(rightPlayerNameLabel);

        firePitLabel = new Label("Fire Pit");
        firePitLabel.setStyle(
            "-fx-text-fill: #f5d06f;" +
            "-fx-font-size: 11px;" +
            "-fx-font-weight: bold;"
        );

        firePitCardView = new ImageView();
        firePitCardView.setFitWidth(110);
        firePitCardView.setFitHeight(160);
        firePitCardView.setPreserveRatio(false);
        firePitCardView.setStyle(
            "-fx-effect: dropshadow(gaussian, rgba(255,215,0,0.6), 25, 0.5, 0, 0);"
        );

        playerInfoBox = new VBox(4);
        playerInfoBox.setStyle(
            "-fx-background-color: rgba(0,0,0,0.55);" +
            "-fx-padding: 8 10 8 10;" +
            "-fx-background-radius: 8;"
        );

        playButton = new Button("▶  PLAY TURN");
        discardButton = new Button("🂠  DISCARD CARD");
        deselectButton = new Button("✕  DESELECT");

        styleMainButton(playButton, "#27ae60", "#1e8449");
        styleMainButton(discardButton, "#8e44ad", "#6c3483");
        styleMainButton(deselectButton, "#c0392b", "#922b21");

        playButton.setOnAction(e -> {
            if (cpuTurnInProgress)
                return;

            handleHumanTurn();
        });

        discardButton.setOnAction(e -> {
            if (cpuTurnInProgress)
                return;

            handleDiscardTurn();
        });

        deselectButton.setOnAction(e -> {
            if (cpuTurnInProgress)
                return;

            controller.deselectAll();
            refreshUI();
        });

        overlayPane.getChildren().addAll(
            currentPlayerLabel,
            nextPlayerLabel,
            turnLabel,
            bottomPlayerNameLabel,
            leftPlayerNameLabel,
            topPlayerNameLabel,
            rightPlayerNameLabel,
            firePitLabel,
            firePitCardView,
            playerInfoBox,
            playButton,
            discardButton,
            cardActionLabel,
            deselectButton
        );

        StackPane root = boardView.getRoot();
        root.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
        root.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        Scene scene = new Scene(root, 1400, 900);

        scene.setOnKeyPressed(e -> {
            if (cpuTurnInProgress)
                return;

            switch (e.getCode()) {
                case F:
                    handleFieldShortcut();
                    break;
                default:
                    break;
            }
        });

        ViewUtils.showFullScreen(stage, scene, "Jackaroo");

        ChangeListener<Number> resizeListener = (obs, oldVal, newVal) -> {
            positionElements();
            refreshHand();
            refreshCpuHands(); 
        };

        overlayPane.widthProperty().addListener(resizeListener);
        overlayPane.heightProperty().addListener(resizeListener);

        Platform.runLater(() -> {
            positionElements();
            refreshUI();
        });
    }

    private void handleHumanTurn() {
        Game game = controller.getGame();

        if (cpuTurnInProgress)
            return;

        if (game.isCurrentPlayerCPU())
            return;

        if (!controller.canPlayTurn()) {
            controller.endCurrentTurn();
            refreshUI();

            if (checkAndHandleWin())
                return;

            runCpuTurnsSequentially();
            return;
        }

        Player currentPlayer = game.getCurrentPlayer();
        Card selectedCard = currentPlayer.getSelectedCard();

        if (selectedCard == null) {
            DialogUtils.showError("Play Error", "You must select a card first.");
            return;
        }

        if (selectedCard instanceof Seven && currentPlayer.getSelectedMarbles().size() == 2) {
            int distance = DialogUtils.showSplitDialog();

            try {
                controller.setSplitDistance(distance);
            } catch (Exception ex) {
                DialogUtils.showError("Invalid Split", ex.getMessage());
                return;
            }
        }

        try {
            controller.playCurrentTurnAndEnd();
            refreshUI();

            if (checkAndHandleWin())
                return;

            runCpuTurnsSequentially();

        } catch (GameException ex) {
            DialogUtils.showError("Invalid Action", ex.getMessage());
            refreshUI();

        } catch (Exception ex) {
            DialogUtils.showError("Error", ex.getMessage() == null ? "Unexpected error." : ex.getMessage());
            refreshUI();
        }
        cardActionLabel.setVisible(false);
    }
    private String getCardDescription(Card card) {
        if (card == null) return "";

        switch (card.getName()) {
            case "Ace":
                return "Field marble OR move 1 step";
            case "King":
                return "Field OR move 13 (destroy path)";
            case "Queen":
                return "Discard opponent card OR move 12";
            case "Jack":
                return "Swap marbles OR move 11";
            case "Ten":
                return "Discard opponent card OR move 10";
            case "Seven":
                return "Split 7 between two marbles";
            case "Four":
                return "Move 4 steps BACKWARD";
            case "Burner":
                return "Destroy opponent marble";
            case "Saver":
                return "Save marble to Safe Zone";
            default:
            	return "Play card";
        }
    }
    private void handleDiscardTurn() {
        Game game = controller.getGame();

        if (cpuTurnInProgress)
            return;

        if (game.isCurrentPlayerCPU())
            return;

        Player currentPlayer = game.getCurrentPlayer();
        Card selectedCard = currentPlayer.getSelectedCard();

        if (selectedCard == null) {
            DialogUtils.showError("Discard Error", "Please select a card to discard first.");
            return;
        }

        controller.endCurrentTurn();
        refreshUI();

        if (checkAndHandleWin())
            return;

        runCpuTurnsSequentially();
    }

    private void runCpuTurnsSequentially() {
        Game game = controller.getGame();

        if (!game.isCurrentPlayerCPU()) {
            if (!controller.canPlayTurn()) {
                controller.endCurrentTurn();
                refreshUI();

                if (checkAndHandleWin()) {
                    cpuTurnInProgress = false;
                    return;
                }

                runCpuTurnsSequentially();
                return;
            }

            cpuTurnInProgress = false;
            setHumanInputEnabled(true);
            refreshUI();
            return;
        }

        setHumanInputEnabled(false);
        cpuTurnInProgress = true;

        Timeline delay = new Timeline(new KeyFrame(Duration.millis(1200), event -> {
            try {
                if (controller.canPlayTurn()) {
                    game.getCurrentPlayer().play();
                }
            } catch (Exception ex) {
                System.out.println("CPU exception ignored: " + ex.getMessage());
            }

            controller.endCurrentTurn();
            refreshUI();

            if (checkAndHandleWin()) {
                cpuTurnInProgress = false;
                return;
            }

            runCpuTurnsSequentially();
        }));

        delay.setCycleCount(1);
        delay.play();
    }

    private void handleFieldShortcut() {
        try {
            controller.fieldMarble();
            controller.endCurrentTurn();
            refreshUI();

            if (checkAndHandleWin())
                return;

            runCpuTurnsSequentially();

        } catch (GameException ex) {
            DialogUtils.showError("Cannot Field", ex.getMessage());

        } catch (Exception ex) {
            DialogUtils.showError("Error", ex.getMessage() != null ? ex.getMessage() : "Cannot field marble.");
        }
    }

    private boolean checkAndHandleWin() {
        model.Colour winner = controller.getGame().checkWin();

        if (winner != null) {
            cpuTurnInProgress = false;
            setHumanInputEnabled(false);
            showWinnerPopup(winner);
            return true;
        }

        return false;
    }

    private void showWinnerPopup(model.Colour winner) {
        String colour = winner.toString();
        String msg = "🏆  " + colour + " wins the game!\n\nCongratulations!";

        javafx.stage.Stage popup = new javafx.stage.Stage();
        popup.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        popup.setTitle("Game Over");
        popup.setResizable(false);

        javafx.scene.layout.VBox box = new javafx.scene.layout.VBox(18);
        box.setAlignment(javafx.geometry.Pos.CENTER);
        box.setPadding(new javafx.geometry.Insets(30));
        box.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #3b2208, #120805);" +
            "-fx-border-color: #d4af37;" +
            "-fx-border-width: 4;" +
            "-fx-background-radius: 16;" +
            "-fx-border-radius: 16;"
        );

        javafx.scene.control.Label title = new javafx.scene.control.Label("🎉 Game Over!");
        title.setStyle("-fx-text-fill: #f5d06f; -fx-font-size: 26px; -fx-font-weight: bold;");

        javafx.scene.control.Label body = new javafx.scene.control.Label(msg);
        body.setWrapText(true);
        body.setMaxWidth(380);
        body.setAlignment(javafx.geometry.Pos.CENTER);
        body.setStyle("-fx-text-fill: #fff1c7; -fx-font-size: 16px; -fx-font-weight: bold;");

        javafx.scene.control.Button okBtn = new javafx.scene.control.Button("Return to Menu");
        okBtn.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #f5d06f, #b98222);" +
            "-fx-text-fill: #1a0d04; -fx-font-size: 14px; -fx-font-weight: bold;" +
            "-fx-background-radius: 18; -fx-border-radius: 18;" +
            "-fx-border-color: #fff1a8; -fx-border-width: 1.5;" +
            "-fx-padding: 8 28; -fx-cursor: hand;"
        );

        okBtn.setOnAction(e -> {
            popup.close();
            StartView startView = new StartView(stage);
            startView.show();
        });

        box.getChildren().addAll(title, body, okBtn);

        javafx.scene.Scene scene = new javafx.scene.Scene(box, 460, 240);
        popup.setScene(scene);
        popup.showAndWait();
    }

    public void refreshUI() {
        Game game = controller.getGame();
        Player current = game.getCurrentPlayer();

        currentPlayerLabel.setText("▶  " + current.getName() + " (" + game.getActivePlayerColour() + ")");
        nextPlayerLabel.setText("Next: " + game.getNextPlayer().getName() + " (" + game.getNextPlayerColour() + ")");
        turnLabel.setText("Turn: " + (game.getTurn() + 1));

        ArrayList<Card> firePit = game.getFirePit();

        if (!firePit.isEmpty()) {
            Card topCard = firePit.get(firePit.size() - 1);
            firePitCardView.setImage(AssetManager.getCardImage(topCard));
        } else {
            firePitCardView.setImage(null);
        }

        updatePlayerInfoPanel(game);
        updatePlayerNameLabels(game);

        positionElements();
        boardView.refreshBoardFromEngine(game);
        refreshHand();
        refreshCpuHands(); 
    }

    private void updatePlayerInfoPanel(Game game) {
        playerInfoBox.getChildren().clear();

        for (Player p : game.getPlayers()) {
            boolean isCurrent = p == game.getCurrentPlayer();
            String indicator = isCurrent ? "▶ " : "   ";
            String text = indicator + p.getName() + " [" + p.getColour() + "]  Cards: " + p.getHand().size();

            Label lbl = new Label(text);
            lbl.setStyle(
                "-fx-text-fill: " + (isCurrent ? "#f5d06f" : "#cccccc") + ";" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: " + (isCurrent ? "bold" : "normal") + ";"
            );

            playerInfoBox.getChildren().add(lbl);
        }
    }

    private void updatePlayerNameLabels(Game game) {
        ArrayList<Player> players = game.getPlayers();

        setPlayerNameLabel(bottomPlayerNameLabel, players.get(0));
        setPlayerNameLabel(leftPlayerNameLabel, players.get(1));
        setPlayerNameLabel(topPlayerNameLabel, players.get(2));
        setPlayerNameLabel(rightPlayerNameLabel, players.get(3));
    }

    private void setPlayerNameLabel(Label label, Player player) {
        label.setText(player.getName() + "  [" + player.getColour() + "]");
    }

    private void stylePlayerNameLabel(Label label) {
        label.setStyle(
            "-fx-text-fill: #f5d06f;" +
            "-fx-font-size: 15px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-color: rgba(35, 18, 4, 0.78);" +
            "-fx-padding: 5 12 5 12;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: #d4af37;" +
            "-fx-border-width: 1.5;" +
            "-fx-border-radius: 8;"
        );
    }

    private void refreshHand() {
        for (CardView cv : cardViews) {
            overlayPane.getChildren().remove(cv);
        }

        cardViews.clear();

        Game game = controller.getGame();
        Player currentPlayer = game.getPlayers().get(0);

        ArrayList<Card> hand = currentPlayer.getHand();
        Card selectedCard = currentPlayer.getSelectedCard();

        double w = overlayPane.getWidth();
        double h = overlayPane.getHeight();

        if (w <= 0 || h <= 0)
            return;

        double cardWidth = 90;
        double spacing = 10;
        int count = hand.size();
        double totalWidth = count * cardWidth + Math.max(0, count - 1) * spacing;

        double cardAreaLeft = w * 0.57;
        double cardAreaRight = w * 0.80;
        double cardAreaCentreX = (cardAreaLeft + cardAreaRight) / 2.0;
        double startX = cardAreaCentreX - totalWidth / 2.0;

        if (startX < cardAreaLeft)
            startX = cardAreaLeft;

        double y = h * 0.84;

        for (int i = 0; i < count; i++) {
            final Card card = hand.get(i);

            CardView cardView = new CardView(card);
            cardView.setSelected(card == selectedCard);
            cardView.setLayoutX(startX + i * (cardWidth + spacing));
            cardView.setLayoutY(y);

            cardView.setOnMouseClicked(e -> {
                if (cpuTurnInProgress)
                    return;

                try {
                    controller.selectCard(card);
                    refreshUI();
                    cardActionLabel.setText(getCardDescription(card));
                    cardActionLabel.setVisible(true);
                } catch (Exception ex) {
                    DialogUtils.showError("Card Selection Error", ex.getMessage());
                }
            });

            cardViews.add(cardView);
            overlayPane.getChildren().add(cardView);
        }
    }

    private void positionElements() {
        double w = overlayPane.getWidth();
        double h = overlayPane.getHeight();
        cardActionLabel.setLayoutX(w * 0.02);   // same left as buttons
        cardActionLabel.setLayoutY(h * 0.78);   // ABOVE buttons nicely
        if (w <= 0 || h <= 0)
            return;

        double labelX = w * 0.02;

        currentPlayerLabel.setLayoutX(labelX);
        currentPlayerLabel.setLayoutY(h * 0.04);

        nextPlayerLabel.setLayoutX(labelX);
        nextPlayerLabel.setLayoutY(h * 0.09);

        turnLabel.setLayoutX(labelX);
        turnLabel.setLayoutY(h * 0.14);

        playerInfoBox.setLayoutX(w * 0.78);
        playerInfoBox.setLayoutY(h * 0.03);

        bottomPlayerNameLabel.setLayoutX(w * 0.470);
        bottomPlayerNameLabel.setLayoutY(h * 0.905);

        leftPlayerNameLabel.setLayoutX(w * 0.055);
        leftPlayerNameLabel.setLayoutY(h * 0.550);

        topPlayerNameLabel.setLayoutX(w * 0.470);
        topPlayerNameLabel.setLayoutY(h * 0.150);

        rightPlayerNameLabel.setLayoutX(w * 0.870);
        rightPlayerNameLabel.setLayoutY(h * 0.545);

        firePitLabel.setLayoutX(w * 0.50 - 22);
        firePitLabel.setLayoutY(h * 0.42);

        double firePitCardWidth = firePitCardView.getFitWidth();
        double firePitCardHeight = firePitCardView.getFitHeight();

        firePitCardView.setLayoutX((w / 2) - (firePitCardWidth / 2));
        firePitCardView.setLayoutY((h / 2) - (firePitCardHeight / 2) + (h * 0.02));

        playButton.setLayoutX(w * 0.02);
        playButton.setLayoutY(h * 0.84);

        discardButton.setLayoutX(w * 0.02);
        discardButton.setLayoutY(h * 0.89);

        deselectButton.setLayoutX(w * 0.02);
        deselectButton.setLayoutY(h * 0.94);
    }

    private void setHumanInputEnabled(boolean enabled) {
        playButton.setDisable(!enabled);
        discardButton.setDisable(!enabled);
        deselectButton.setDisable(!enabled);
    }

    private void styleInfoLabel(Label label) {
        label.setStyle(
            "-fx-text-fill: #1a0d04;" +
            "-fx-font-size: 13px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-color: rgba(245,208,111,0.92);" +
            "-fx-padding: 3 8 3 8;" +
            "-fx-background-radius: 5;" +
            "-fx-border-color: #8B6914;" +
            "-fx-border-width: 1.5;" +
            "-fx-border-radius: 5;"
        );
    }

    private void styleMainButton(Button button, String bg, String hover) {
        String base =
            "-fx-background-color: " + bg + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 13px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 6;" +
            "-fx-border-color: rgba(255,255,255,0.35);" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 6;" +
            "-fx-padding: 7 18 7 18;" +
            "-fx-cursor: hand;";

        String hoverStyle =
            "-fx-background-color: " + hover + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 13px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 6;" +
            "-fx-border-color: rgba(255,255,255,0.55);" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 6;" +
            "-fx-padding: 7 18 7 18;" +
            "-fx-cursor: hand;";

        button.setStyle(base);
        button.setOnMouseEntered(e -> button.setStyle(hoverStyle));
        button.setOnMouseExited(e -> button.setStyle(base));
    }
    private void refreshCpuHands() {
        // Remove old CPU card views
        for (ArrayList<ImageView> hand : cpuCardViews) {
            overlayPane.getChildren().removeAll(hand);
        }
        cpuCardViews.clear();

        Game game = controller.getGame();
        ArrayList<Player> players = game.getPlayers();

        double w = overlayPane.getWidth();
        double h = overlayPane.getHeight();
        if (w <= 0 || h <= 0) return;

        // Card back images for each CPU (players 1, 2, 3)
        String[] backs = {
            "file:assets/BACK_1_EYE_BLACK.png",
            "file:assets/BACK_3_EYE_TEAL.png",
            "file:assets/BACK_5_SCARAB_RED.png"
        };

        // [centerX%, centerY%, rotation, stackOffsetX, stackOffsetY]
        double[][] positions = {
            { 0.14, 0.50,  90, 0, 6  },   // left  player (player 1)
            { 0.56, 0.12, 0, 6, 0  },   // top   player (player 2)
            { 0.86, 0.50, -90, 0, 6  }    // right player (player 3)
        };

        double cardW = 60;
        double cardH = 90;

        for (int p = 0; p < 3; p++) {
            Player cpu = players.get(p + 1);
            int cardCount = cpu.getHand().size();
            ArrayList<ImageView> hand = new ArrayList<>();

            double cx = w * positions[p][0];
            double cy = h * positions[p][1];
            double rot = positions[p][2];
            double offX = positions[p][3];
            double offY = positions[p][4];

            for (int i = 0; i < cardCount; i++) {
                ImageView iv = new ImageView(new javafx.scene.image.Image(backs[p]));
                iv.setFitWidth(cardW);
                iv.setFitHeight(cardH);
                iv.setPreserveRatio(false);
                iv.setRotate(rot);
                // Stack cards with a slight offset so they look like a pile
                iv.setLayoutX(cx - cardW / 2 + i * offX);
                iv.setLayoutY(cy - cardH / 2 + i * offY);
                iv.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 6, 0.3, 1, 1);");
                hand.add(iv);
                overlayPane.getChildren().add(iv);
            }

            cpuCardViews.add(hand);
        }
    }
}