package view;

import java.util.ArrayList;
import java.util.List;

import controller.GameController;
import engine.Game;
import engine.board.Cell;
import engine.board.SafeZone;
import javafx.animation.FadeTransition;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import model.Colour;
import model.player.Marble;
import model.player.Player;

public class BoardView {

    private GameController controller;
    private Runnable refreshCallback;

    private final StackPane root;
    private final Pane overlayPane;
    private final ImageView boardImageView;

    private final ArrayList<Button> trackCells;
    private final ArrayList<Button> safeZoneCells;
    private final ArrayList<Button> marbleCells;
    private final ArrayList<Button> dynamicMarbleButtons;

    // Floating trap notification label
    private Label trapNotificationLabel;

    public BoardView() {
        root = new StackPane();
        overlayPane = new Pane();
        boardImageView = new ImageView();
        trackCells = new ArrayList<>();
        safeZoneCells = new ArrayList<>();
        marbleCells = new ArrayList<>();
        dynamicMarbleButtons = new ArrayList<>();

        try {
            boardImageView.setImage(new Image("file:assets/board.png"));
        } catch (Exception e) {
            System.out.println("Board image not found.");
        }

        root.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
        root.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        boardImageView.setPreserveRatio(false);
        boardImageView.setSmooth(true);
        boardImageView.setCache(true);
        boardImageView.fitWidthProperty().bind(root.widthProperty());
        boardImageView.fitHeightProperty().bind(root.heightProperty());

        overlayPane.prefWidthProperty().bind(root.widthProperty());
        overlayPane.prefHeightProperty().bind(root.heightProperty());
        overlayPane.setPickOnBounds(false);

        // Trap notification label (hidden by default)
        trapNotificationLabel = new Label();
        trapNotificationLabel.setVisible(false);
        trapNotificationLabel.setStyle(
            "-fx-text-fill: white;" +
            "-fx-background-color: rgba(180,0,0,0.88);" +
            "-fx-font-size: 16px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 10 18 10 18;" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: #ff6666;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 10;"
        );

        createTrackCells();
        createSafeZoneCells();
        createMarbleCells();

        root.getChildren().addAll(boardImageView, overlayPane);
        overlayPane.getChildren().add(trapNotificationLabel);

        root.widthProperty().addListener((obs, o, n)  -> { layoutTrackCells(); layoutSafeZoneCells(); layoutMarbleCells(); positionTrapLabel(); });
        root.heightProperty().addListener((obs, o, n) -> { layoutTrackCells(); layoutSafeZoneCells(); layoutMarbleCells(); positionTrapLabel(); });
    }

    // ════════════════════════════════════════════════════════════
    //  TRAP NOTIFICATION
    // ════════════════════════════════════════════════════════════

    public void showTrapNotification(String marbleColour) {
        trapNotificationLabel.setText("💥  " + marbleColour + " marble hit a TRAP and was destroyed!");
        trapNotificationLabel.setVisible(true);
        positionTrapLabel();

        FadeTransition fade = new FadeTransition(Duration.millis(3000), trapNotificationLabel);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        fade.setDelay(Duration.millis(1500));
        fade.setOnFinished(e -> trapNotificationLabel.setVisible(false));
        fade.play();
    }

    private void positionTrapLabel() {
        double w = root.getWidth();
        double h = root.getHeight();
        if (w <= 0 || h <= 0) return;
        trapNotificationLabel.setLayoutX(w * 0.25);
        trapNotificationLabel.setLayoutY(h * 0.47);
    }

    // ════════════════════════════════════════════════════════════
    //  TRACK CELLS
    // ════════════════════════════════════════════════════════════

    private void createTrackCells() {
        for (int i = 0; i < 100; i++) {
            Button cell = new Button(String.valueOf(i));
            cell.setFocusTraversable(false);
            cell.setMnemonicParsing(false);
            cell.setStyle(buildTrackCellStyle(42));
            trackCells.add(cell);
        }
        overlayPane.getChildren().addAll(trackCells);
    }

    private String buildTrackCellStyle(double size) {
        double radius   = size;
        double fontSize = Math.max(12, size * 0.34);
        return "-fx-background-color: white;"
             + "-fx-text-fill: #4a3312;"
             + "-fx-font-weight: bold;"
             + "-fx-font-size: " + fontSize + "px;"
             + "-fx-background-radius: " + radius + ";"
             + "-fx-border-color: linear-gradient(#f9e39a, #a87312);"
             + "-fx-border-width: 3px;"
             + "-fx-border-radius: " + radius + ";"
             + "-fx-padding: 0;";
    }

    private void layoutTrackCells() {
        double w = root.getWidth();
        double h = root.getHeight();
        if (w <= 0 || h <= 0 || trackCells.isEmpty()) return;

        double left   = w * 0.19;
        double right  = w * 0.81;
        double top    = h * 0.20;
        double bottom = h * 0.82;

        double rectWidth  = right - left;
        double rectHeight = bottom - top;
        double perimeter  = (2 * rectWidth) + (2 * rectHeight);
        double cellSize   = Math.max(22, Math.min(28, Math.min(rectWidth / 14.0, rectHeight / 10.0)));

        for (int i = 0; i < trackCells.size(); i++) {
            Button cell = trackCells.get(i);
            cell.setPrefSize(cellSize, cellSize);
            cell.setMinSize(cellSize, cellSize);
            cell.setMaxSize(cellSize, cellSize);
            cell.setStyle(buildTrackCellStyle(cellSize));

            double distance = (i * perimeter) / 100.0;
            double[] point  = getPointOnClockwiseRectangle(distance, left, top, right, bottom);
            cell.setLayoutX(point[0] - cellSize / 2.0);
            cell.setLayoutY(point[1] - cellSize / 2.0);
        }
    }

    private double[] getPointOnClockwiseRectangle(double distance,
                                                   double left, double top,
                                                   double right, double bottom) {
        double width  = right - left;
        double height = bottom - top;

        double s1 = width / 2.0;
        double s2 = height;
        double s3 = width;
        double s4 = height;
        double s5 = width / 2.0;

        double x, y;
        if (distance < s1) {
            double t = distance / s1;
            x = left + (width / 2.0) * (1.0 - t);
            y = bottom;
        } else if (distance < s1 + s2) {
            double t = (distance - s1) / s2;
            x = left;
            y = bottom - height * t;
        } else if (distance < s1 + s2 + s3) {
            double t = (distance - s1 - s2) / s3;
            x = left + width * t;
            y = top;
        } else if (distance < s1 + s2 + s3 + s4) {
            double t = (distance - s1 - s2 - s3) / s4;
            x = right;
            y = top + height * t;
        } else {
            double t = (distance - s1 - s2 - s3 - s4) / s5;
            x = right - (width / 2.0) * t;
            y = bottom;
        }
        return new double[]{ x, y };
    }

    // ════════════════════════════════════════════════════════════
    //  SAFE ZONE CELLS
    // ════════════════════════════════════════════════════════════

    private void createSafeZoneCells() {
        for (int i = 0; i < 16; i++) {
            Button cell = new Button("");
            cell.setFocusTraversable(false);
            cell.setMnemonicParsing(false);
            cell.setStyle(buildTrackCellStyle(28));
            safeZoneCells.add(cell);
        }
        overlayPane.getChildren().addAll(safeZoneCells);
    }

    private void layoutSafeZoneCells() {
        double w = root.getWidth();
        double h = root.getHeight();
        if (w <= 0 || h <= 0 || safeZoneCells.isEmpty()) return;

        double left   = w * 0.19;
        double right  = w * 0.81;
        double top    = h * 0.20;
        double bottom = h * 0.82;

        double cellSize = Math.max(22, Math.min(28, Math.min((right - left) / 14.0, (bottom - top) / 10.0)));
        double gap      = cellSize + 2;

        // cell 25 — LEFT wall, safe zone goes RIGHT (inward)
        double y25 = bottom - ((bottom - top) * 25.0 / 50.0);
        for (int i = 0; i < 4; i++) {
            setSafeCell(safeZoneCells.get(i), cellSize,
                left + (i + 1) * gap - cellSize / 2.0,
                y25 - cellSize / 2.0);
        }

        // cell 75 — RIGHT wall, safe zone goes LEFT (inward)
        double y75 = top + ((bottom - top) * 25.0 / 50.0);
        for (int i = 0; i < 4; i++) {
            setSafeCell(safeZoneCells.get(4 + i), cellSize,
                right - (i + 1) * gap - cellSize / 2.0,
                y75 - cellSize / 2.0);
        }

        // cell 50 — TOP wall, safe zone goes DOWN (inward)
        double x50 = (left + right) / 2.0;
        for (int i = 0; i < 4; i++) {
            setSafeCell(safeZoneCells.get(8 + i), cellSize,
                x50 - cellSize / 2.0,
                top + (i + 1) * gap - cellSize / 2.0);
        }

        // cell 0 — BOTTOM wall, safe zone goes UP (inward)
        double x0 = (left + right) / 2.0;
        for (int i = 0; i < 4; i++) {
            setSafeCell(safeZoneCells.get(12 + i), cellSize,
                x0 - cellSize / 2.0,
                bottom - (i + 1) * gap - cellSize / 2.0);
        }
    }

    private void setSafeCell(Button cell, double size, double x, double y) {
        cell.setPrefSize(size, size);
        cell.setMinSize(size, size);
        cell.setMaxSize(size, size);
        cell.setStyle(buildTrackCellStyle(size));
        cell.setLayoutX(x);
        cell.setLayoutY(y);
    }

    public List<Button> getSafeZoneCells() { return safeZoneCells; }

    // ════════════════════════════════════════════════════════════
    //  STATIC HOME-ZONE MARBLE CELLS
    // ════════════════════════════════════════════════════════════

    private void createMarbleCells() {
        String[] colors = {
            "#cc0000","#cc0000","#cc0000","#cc0000",
            "#009900","#009900","#009900","#009900",
            "#ccaa00","#ccaa00","#ccaa00","#ccaa00",
            "#0055cc","#0055cc","#0055cc","#0055cc"
        };
        for (int i = 0; i < 16; i++) {
            Button cell = new Button("");
            cell.setFocusTraversable(false);
            cell.setMnemonicParsing(false);
            cell.setStyle(buildMarbleStyle(28, colors[i], false));
            marbleCells.add(cell);
        }
        overlayPane.getChildren().addAll(marbleCells);
    }

    private void layoutMarbleCells() {
        double w = root.getWidth();
        double h = root.getHeight();
        if (w <= 0 || h <= 0 || marbleCells.isEmpty()) return;

        double cellSize = Math.max(22, Math.min(28, Math.min((w * 0.62) / 14.0, (h * 0.62) / 10.0)));
        double gap = cellSize + 4;

        placeMarble2x2(0,  w * 0.095, h * 0.62,  cellSize, gap);   // CPU1 left
        placeMarble2x2(4,  w * 0.905, h * 0.62,  cellSize, gap);   // CPU3 right
        placeMarble2x2(8,  w * 0.37,  h * 0.07,  cellSize, gap);   // CPU2 top
        placeMarble2x2(12, w * 0.37,  h * 0.865, cellSize, gap);   // Player bottom
    }

    private void placeMarble2x2(int startIdx, double cx, double cy, double size, double gap) {
        String[] colors = {
            "#cc0000","#cc0000","#cc0000","#cc0000",
            "#009900","#009900","#009900","#009900",
            "#ccaa00","#ccaa00","#ccaa00","#ccaa00",
            "#0055cc","#0055cc","#0055cc","#0055cc"
        };
        int[] col = { 0, 1, 0, 1 };
        int[] row = { 0, 0, 1, 1 };
        for (int i = 0; i < 4; i++) {
            Button cell = marbleCells.get(startIdx + i);
            cell.setPrefSize(size, size);
            cell.setMinSize(size, size);
            cell.setMaxSize(size, size);
            cell.setStyle(buildMarbleStyle(size, colors[startIdx + i], false));
            cell.setLayoutX(cx - gap / 2.0 + col[i] * gap - size / 2.0);
            cell.setLayoutY(cy + row[i] * gap - size / 2.0);
        }
    }

    public List<Button> getMarbleCells()  { return marbleCells; }
    public List<Button> getTrackCells()   { return trackCells; }
    public StackPane    getRoot()         { return root; }
    public Pane         getOverlayPane()  { return overlayPane; }

    // ════════════════════════════════════════════════════════════
    //  DYNAMIC MARBLE RENDERING
    // ════════════════════════════════════════════════════════════

    public void refreshBoardFromEngine(Game game) {
        for (Button b : dynamicMarbleButtons) {
            overlayPane.getChildren().remove(b);
        }
        dynamicMarbleButtons.clear();

        hideStaticHomeMarbles();

        drawTrackMarbles(game);
        drawSafeZoneMarbles(game);
        drawHomeZoneMarbles(game);
    }

    private void hideStaticHomeMarbles() {
        for (Button b : marbleCells) {
            b.setVisible(false);
            b.setManaged(false);
        }
    }

    private void drawTrackMarbles(Game game) {
        ArrayList<Cell> track = game.getBoard().getTrack();
        for (int i = 0; i < track.size(); i++) {
            Marble marble = track.get(i).getMarble();
            if (marble != null) {
                Button cell = trackCells.get(i);
                addDynamicMarble(marble, cell.getLayoutX(), cell.getLayoutY(), cell.getWidth());
            }
        }
    }

    private void drawSafeZoneMarbles(Game game) {
        ArrayList<SafeZone> safeZones = game.getBoard().getSafeZones();

        for (SafeZone sz : safeZones) {
            int ownerIndex = game.getPlayerIndexByColour(sz.getColour());
            int visualStart = getSafeZoneVisualStartIndexByPlayerIndex(ownerIndex);

            for (int ci = 0; ci < sz.getCells().size(); ci++) {
                Marble marble = sz.getCells().get(ci).getMarble();
                if (marble != null) {
                    Button safeCell = safeZoneCells.get(visualStart + ci);
                    addDynamicMarble(marble, safeCell.getLayoutX(), safeCell.getLayoutY(), safeCell.getWidth());
                }
            }
        }
    }

    private void drawHomeZoneMarbles(Game game) {
        ArrayList<Player> players = game.getPlayers();

        for (int playerIndex = 0; playerIndex < players.size(); playerIndex++) {
            Player player = players.get(playerIndex);
            ArrayList<Marble> homeMarbles = player.getMarbles();

            for (int i = 0; i < homeMarbles.size(); i++) {
                double[] pt = getHomeMarblePointByPlayerIndex(playerIndex, i);
                addDynamicMarble(homeMarbles.get(i), pt[0], pt[1], pt[2]);
            }
        }
    }
    private double[] getHomeMarblePointByPlayerIndex(int playerIndex, int index) {
        double w = root.getWidth();
        double h = root.getHeight();

        double size = Math.max(18, Math.min(24, Math.min((w * 0.62) / 14.0, (h * 0.62) / 10.0)));
        double gap = size + 4;

        int col = index % 2;
        int row = index / 2;

        double cx;
        double cy;

        if (playerIndex == 0) {          // human bottom
            cx = w * 0.37;
            cy = h * 0.865;
        } else if (playerIndex == 1) {   // CPU 1 left
            cx = w * 0.095;
            cy = h * 0.62;
        } else if (playerIndex == 2) {   // CPU 2 top
            cx = w * 0.37;
            cy = h * 0.07;
        } else {                         // CPU 3 right
            cx = w * 0.905;
            cy = h * 0.62;
        }

        double x = cx - gap / 2.0 + col * gap - size / 2.0;
        double y = cy + row * gap - size / 2.0;

        return new double[] { x, y, size };
    }

    private void addDynamicMarble(Marble marble, double x, double y, double baseSize) {
        double size = Math.max(24, Math.min(30, baseSize * 0.9));
        Button btn  = new Button("");
        btn.setFocusTraversable(false);
        btn.setMnemonicParsing(false);
        btn.setPrefSize(size, size);
        btn.setMinSize(size, size);
        btn.setMaxSize(size, size);
        btn.setStyle(buildMarbleStyle(size, getColourHex(marble.getColour()), isSelectedMarble(marble)));
        btn.setLayoutX(x + (baseSize - size) / 2.0);
        btn.setLayoutY(y + (baseSize - size) / 2.0);
        btn.setUserData(marble);

        btn.setOnAction(e -> {
            if (controller == null)
                return;

            try {
                if (!controller.getGame().getBoard().getActionableMarbles().contains(marble)) {
                    DialogUtils.showError("Invalid Selection", "This marble cannot be selected for the current action.");
                    return;
                }

                controller.selectMarble(marble);

                if (refreshCallback != null)
                    refreshCallback.run();

            } catch (Exception ex) {
                DialogUtils.showError("Invalid Selection", ex.getMessage());
            }
        });

        dynamicMarbleButtons.add(btn);
        overlayPane.getChildren().add(btn);
    }

    // ════════════════════════════════════════════════════════════
    //  MARBLE STYLE
    // ════════════════════════════════════════════════════════════

    private String buildMarbleStyle(double size, String colourHex, boolean selected) {
        if (selected) {
            return "-fx-background-color: " + colourHex + ";"
                 + "-fx-background-radius: 50%;"
                 + "-fx-border-radius: 50%;"
                 + "-fx-border-color: #d4af37;"
                 + "-fx-border-width: 1.5;"
                 + "-fx-effect: dropshadow(gaussian, #d4af37, 10, 0.6, 0, 0);"
                 + "-fx-cursor: hand;";
        } else {
            return "-fx-background-color: " + colourHex + ";"
                 + "-fx-background-radius: 50%;"
                 + "-fx-border-radius: 50%;"
                 + "-fx-border-color: #2b1a0b;"
                 + "-fx-border-width: 1.5;"
                 + "-fx-cursor: hand;";
        }
    }

    // ════════════════════════════════════════════════════════════
    //  HELPERS
    // ════════════════════════════════════════════════════════════

    public void setController(GameController controller, Runnable refreshCallback) {
        this.controller     = controller;
        this.refreshCallback = refreshCallback;
    }

    private boolean isSelectedMarble(Marble marble) {
        if (controller == null || controller.getCurrentPlayer() == null) return false;
        return controller.getCurrentPlayer().getSelectedMarbles().contains(marble);
    }

    private String getColourHex(Colour colour) {
        if (colour == Colour.RED)    return "#cc0000";
        if (colour == Colour.GREEN)  return "#009900";
        if (colour == Colour.YELLOW) return "#ccaa00";
        if (colour == Colour.BLUE)   return "#0055cc";
        return "#444444";
    }

    private int getSafeZoneVisualStartIndexByPlayerIndex(int playerIndex) {
        // Board visual order:
        // player 0 = bottom/base 0, player 1 = left/base 25,
        // player 2 = top/base 50, player 3 = right/base 75.
        if (playerIndex == 0) return 12;
        if (playerIndex == 1) return 0;
        if (playerIndex == 2) return 8;
        if (playerIndex == 3) return 4;
        return 12;
    }

    private int getSafeZoneVisualStartIndex(Colour colour) {
        if (colour == Colour.RED)    return 0;
        if (colour == Colour.GREEN)  return 4;
        if (colour == Colour.YELLOW) return 8;
        if (colour == Colour.BLUE)   return 12;
        return 0;
    }

    private double[] getHomeMarblePoint(Colour colour, int index) {
        double w = root.getWidth();
        double h = root.getHeight();
        double size = Math.max(18, Math.min(24, Math.min((w * 0.62) / 14.0, (h * 0.62) / 10.0)));
        double gap  = size + 4;
        int col = index % 2;
        int row = index / 2;
        double cx, cy;
        if      (colour == Colour.RED)    { cx = w * 0.095; cy = h * 0.62;  }
        else if (colour == Colour.GREEN)  { cx = w * 0.905; cy = h * 0.62;  }
        else if (colour == Colour.YELLOW) { cx = w * 0.37;  cy = h * 0.07;  }
        else                              { cx = w * 0.37;  cy = h * 0.865; }
        double x = cx - gap / 2.0 + col * gap - size / 2.0;
        double y = cy + row * gap - size / 2.0;
        return new double[]{ x, y, size };
    }
}