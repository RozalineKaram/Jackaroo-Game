package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.TextField;
public class DialogUtils {

    public static void showError(String title, String message) {
        showPopup(title, message);
    }

    public static void showInfo(String title, String message) {
        showPopup(title, message);
    }

    private static void showPopup(String title, String message) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle(title);

        Label label = new Label(message == null ? "No message available." : message);
        label.setWrapText(true);

        Button closeButton = new Button("OK");
        closeButton.setOnAction(e -> popup.close());

        VBox root = new VBox(15, label, closeButton);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));

        Scene scene = new Scene(root, 380, 180);
        popup.setScene(scene);
        popup.showAndWait();
    }
    public static int showSplitDialog() {
        final int[] selectedValue = {3};

        javafx.stage.Stage popup = new javafx.stage.Stage();
        popup.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        popup.setTitle("Seven Split Distance");
        popup.setResizable(false);

        javafx.scene.layout.VBox box = new javafx.scene.layout.VBox(16);
        box.setAlignment(javafx.geometry.Pos.CENTER);
        box.setPadding(new javafx.geometry.Insets(24));
        box.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #3b2208, #120805);" +
            "-fx-border-color: #d4af37;" +
            "-fx-border-width: 4;" +
            "-fx-background-radius: 14;" +
            "-fx-border-radius: 14;"
        );

        javafx.scene.control.Label title = new javafx.scene.control.Label("⚱ Seven Split");
        title.setStyle(
            "-fx-text-fill: #f5d06f;" +
            "-fx-font-size: 22px;" +
            "-fx-font-weight: bold;"
        );

        javafx.scene.control.Label message = new javafx.scene.control.Label(
            "Choose the distance for the first marble.\nThe second marble will move the remaining steps."
        );
        message.setWrapText(true);
        message.setMaxWidth(360);
        message.setAlignment(javafx.geometry.Pos.CENTER);
        message.setStyle(
            "-fx-text-fill: #fff1c7;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;"
        );

        javafx.scene.control.ComboBox<Integer> comboBox = new javafx.scene.control.ComboBox<>();
        comboBox.getItems().addAll(1, 2, 3, 4, 5, 6);
        comboBox.setValue(3);
        comboBox.setStyle(
            "-fx-background-color: #f5d06f;" +
            "-fx-border-color: #d4af37;" +
            "-fx-border-width: 2;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 14px;"
        );

        javafx.scene.control.Label hint = new javafx.scene.control.Label("Example: 3 means 3 + 4 steps");
        hint.setStyle(
            "-fx-text-fill: #d4af37;" +
            "-fx-font-size: 12px;" +
            "-fx-font-style: italic;"
        );

        javafx.scene.control.Button confirmButton = new javafx.scene.control.Button("Confirm");
        confirmButton.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #f5d06f, #b98222);" +
            "-fx-text-fill: #1a0d04;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 18;" +
            "-fx-border-radius: 18;" +
            "-fx-border-color: #fff1a8;" +
            "-fx-border-width: 1.5;" +
            "-fx-padding: 8 28;" +
            "-fx-cursor: hand;"
        );

        confirmButton.setOnAction(e -> {
            selectedValue[0] = comboBox.getValue();
            popup.close();
        });

        box.getChildren().addAll(title, message, comboBox, hint, confirmButton);

        javafx.scene.Scene scene = new javafx.scene.Scene(box, 430, 260);
        popup.setScene(scene);
        popup.showAndWait();

        return selectedValue[0];
    }
}