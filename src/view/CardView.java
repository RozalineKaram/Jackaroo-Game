package view;

import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import model.card.Card;

public class CardView extends StackPane {

    private Card card;
    private ImageView imageView;
    private Rectangle selectionBorder;
    private static final boolean DEBUG = true;
    public CardView(Card card) {
        this.card = card;

        Image image = AssetManager.getCardImage(card);
        imageView = new ImageView();

        if (image != null) {
            imageView.setImage(image);
        }

        imageView.setFitWidth(90);
        imageView.setFitHeight(130);
        imageView.setPreserveRatio(false);

        selectionBorder = new Rectangle(96, 136);
        selectionBorder.setArcWidth(15);
        selectionBorder.setArcHeight(15);
        selectionBorder.setFill(Color.TRANSPARENT);
        selectionBorder.setStroke(Color.TRANSPARENT);
        selectionBorder.setStrokeWidth(5);

        setPadding(new Insets(3));
        getChildren().addAll(selectionBorder, imageView);
    }

    public Card getCard() {
        return card;
    }

    public void setSelected(boolean selected) {
        if (selected) {
            selectionBorder.setStroke(Color.web("#C8960C"));
        } else {
            selectionBorder.setStroke(Color.TRANSPARENT);
        }
    }
}