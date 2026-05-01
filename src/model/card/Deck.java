package model.card;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import engine.GameManager;
import engine.board.BoardManager;
import model.card.standard.Ace;
import model.card.standard.Five;
import model.card.standard.Four;
import model.card.standard.Jack;
import model.card.standard.King;
import model.card.standard.Queen;
import model.card.standard.Seven;
import model.card.standard.Standard;
import model.card.standard.Suit;
import model.card.standard.Ten;
import model.card.wild.Burner;
import model.card.wild.Saver;

public class Deck {
	private static final String CARDS_FILE = "Cards.csv";
	private static ArrayList<Card> cardsPool = new ArrayList<Card>();

    @SuppressWarnings("resource")
    public static void loadCardPool(BoardManager boardManager, GameManager gameManager) throws IOException {
    	if (cardsPool == null) {
    	    cardsPool = new ArrayList<Card>();
    	} else {
    	    cardsPool.clear();
    	}
        BufferedReader br = new BufferedReader(new FileReader(CARDS_FILE));
        String nextLine = br.readLine();

        while (nextLine != null) {

            nextLine = nextLine.trim();

            // skip empty lines
            if (nextLine.length() == 0) {
                nextLine = br.readLine();
                continue;
            }

            String[] data = nextLine.split(",");

            // basic validation
            if (data.length < 4) {
                throw new IOException("Invalid line in Cards.csv: " + nextLine);
            }

            int code = Integer.parseInt(data[0].trim());
            int frequency = Integer.parseInt(data[1].trim());
            String name = data[2].trim();
            String description = data[3].trim();

            Card card = null;

            // Wild cards
            if (code == 14) {
                card = new Burner(name, description, boardManager, gameManager);
            } else if (code == 15) {
                card = new Saver(name, description, boardManager, gameManager);
            } else {
                // Standard cards need rank and suit
                if (data.length < 6) {
                    throw new IOException("Invalid standard card line in Cards.csv: " + nextLine);
                }

                int rank = Integer.parseInt(data[4].trim());
                Suit suit = Suit.valueOf(data[5].trim());

                switch (rank) {
                case 1:
                    card = new Ace(name, description, suit, boardManager, gameManager);
                    break;
                case 4:
                    card = new Four(name, description, suit, boardManager, gameManager);
                    break;
                case 5:
                    card = new Five(name, description, suit, boardManager, gameManager);
                    break;
                case 7:
                    card = new Seven(name, description, suit, boardManager, gameManager);
                    break;
                case 10:
                    card = new Ten(name, description, suit, boardManager, gameManager);
                    break;
                case 11:
                    card = new Jack(name, description, suit, boardManager, gameManager);
                    break;
                case 12:
                    card = new Queen(name, description, suit, boardManager, gameManager);
                    break;
                case 13:
                    card = new King(name, description, suit, boardManager, gameManager);
                    break;
                default:
                    throw new IOException("Unsupported rank in Cards.csv: " + rank);
                }
            }

            for (int i = 0; i < frequency; i++) {
                cardsPool.add(card);
            }

            nextLine = br.readLine();
        }

        br.close();
    }
    public static ArrayList<Card> drawCards() {
        Collections.shuffle(cardsPool);
        ArrayList<Card> cards = new ArrayList<>(cardsPool.subList(0, 4));
        cardsPool.subList(0, 4).clear();
        return cards;
    }
    
    public static int getPoolSize() {
		return cardsPool.size();
	}

    public static void refillPool(ArrayList<Card> cards) {
        cardsPool.addAll(cards);
    }

}

