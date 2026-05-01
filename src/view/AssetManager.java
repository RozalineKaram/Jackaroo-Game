package view;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;

import javafx.scene.image.Image;
import model.card.Card;
import model.card.standard.Standard;
import model.card.wild.Burner;
import model.card.wild.Saver;
import model.card.standard.Suit;

public class AssetManager {

    private static final String ASSETS_PATH = "assets/";
    private static HashMap<String, Image> cache = new HashMap<String, Image>();

    public static Image getBoardImage() {
        return loadImage("board.png");
    }

    public static Image getCardBackImage() {
        return loadImage("back.png");
    }

    public static Image getCardImage(Card card) {
        String fileName = getCardFileName(card);
        Image image = loadImage(fileName);

        if (image == null) {
            return getCardBackImage();
        }

        return image;
    }

    private static String getCardFileName(Card card) {

        if (card instanceof model.card.wild.Burner) {
            return "BURNER.png";
        }

        if (card instanceof model.card.wild.Saver) {
            return "SAVER.png";
        }

        if (card instanceof model.card.standard.Standard) {
            model.card.standard.Standard s = (model.card.standard.Standard) card;

            String rankStr = rankToString(s.getRank());
            String suitStr = suitToString(s.getSuit());

            return rankStr + suitStr + ".png";
        }

        return "back.png";
    }

    private static String rankToString(int rank) {
        switch (rank) {
        case 1:
            return "A";
        case 10:
            return "10";
        case 11:
            return "J";
        case 12:
            return "Q";
        case 13:
            return "K";
        default:
            return String.valueOf(rank);
        }
    }

    private static String suitToString(model.card.standard.Suit suit) {
        if (suit == model.card.standard.Suit.SPADE) return "S";
        if (suit == model.card.standard.Suit.HEART) return "H";
        if (suit == model.card.standard.Suit.DIAMOND) return "D";
        return "C";
    }

    private static Image loadImage(String fileName) {
        try {
            if (cache.containsKey(fileName)) {
                return cache.get(fileName);
            }

            Image image = new Image(new FileInputStream(ASSETS_PATH + fileName));
            cache.put(fileName, image);
            return image;
        } catch (FileNotFoundException e) {
            System.out.println("Missing image: " + fileName);
            return null;
        }
    }
}