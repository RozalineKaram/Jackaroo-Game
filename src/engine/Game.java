package engine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import engine.board.Board;
import engine.board.SafeZone;
import exception.CannotDiscardException;
import exception.CannotFieldException;
import exception.GameException;
import exception.IllegalDestroyException;
import exception.InvalidCardException;
import exception.InvalidMarbleException;
import exception.SplitOutOfRangeException;
import model.Colour;
import model.card.Card;
import model.card.Deck;
import model.player.*;

@SuppressWarnings("unused")
public class Game implements GameManager {
    private final Board board;
    private final ArrayList<Player> players;
	private int currentPlayerIndex;
    private final ArrayList<Card> firePit;
    private int turn;

    public Game(String playerName) throws IOException {
        turn = 0;
        currentPlayerIndex = 0;
        firePit = new ArrayList<>();

        ArrayList<Colour> colourOrder = new ArrayList<>();
        
        colourOrder.addAll(Arrays.asList(Colour.values()));
        
        Collections.shuffle(colourOrder);
        
        this.board = new Board(colourOrder, this);
        
        Deck.loadCardPool(this.board, (GameManager)this);
        
        this.players = new ArrayList<>();
        this.players.add(new Player(playerName, colourOrder.get(0)));
        
        for (int i = 1; i < 4; i++) 
            this.players.add(new CPU("CPU " + i, colourOrder.get(i), this.board));
        
        for (int i = 0; i < 4; i++) 
            this.players.get(i).setHand(Deck.drawCards());
        
    }
    
    public Board getBoard() {
        return board;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public ArrayList<Card> getFirePit() {
        return firePit;
    }
    
    public void selectCard(Card card) throws InvalidCardException {
        players.get(currentPlayerIndex).selectCard(card);
    }

    public void selectMarble(Marble marble) throws InvalidMarbleException {
        players.get(currentPlayerIndex).selectMarble(marble);
    }

    public void deselectAll() {
        players.get(currentPlayerIndex).deselectAll();
    }

    public void editSplitDistance(int splitDistance) throws SplitOutOfRangeException {
        if(splitDistance < 1 || splitDistance > 6)
            throw new SplitOutOfRangeException();

        board.setSplitDistance(splitDistance);
    }

    public boolean canPlayTurn() {
        return players.get(currentPlayerIndex).getHand().size() == (4 - turn);
    }

    public void playPlayerTurn() throws GameException {
        players.get(currentPlayerIndex).play();
    }

    public void endPlayerTurn() {
        Player currentPlayer = players.get(currentPlayerIndex);
        Card selected = currentPlayer.getSelectedCard();

        if (selected != null) {
            currentPlayer.getHand().remove(selected);
            firePit.add(selected);
        }

        currentPlayer.deselectAll();

        currentPlayerIndex = (currentPlayerIndex + 1) % 4;

        if (currentPlayerIndex == 0) {
            turn++;

            if (turn == 4) {
                turn = 0;

                if (Deck.getPoolSize() < 16) {
                    Deck.refillPool(firePit);
                    firePit.clear();
                }

                for (Player p : players) {
                    p.setHand(Deck.drawCards());
                }
            }
        }
    }

    public Colour checkWin() {
        for(SafeZone safeZone : board.getSafeZones()) 
            if(safeZone.isFull())
                return safeZone.getColour();
    
        return null;
    }

    @Override
    public void sendHome(Marble marble) {
        for (Player player : players) {
            if (player.getColour() == marble.getColour()) {
                player.regainMarble(marble);
                break;
            }
        }
    }

    @Override
    public void fieldMarble() throws CannotFieldException, IllegalDestroyException {
        Marble marble = players.get(currentPlayerIndex).getOneMarble();
        
        if (marble == null)
        	throw new CannotFieldException("No marbles left in the Home Zone to field.");
        
        board.sendToBase(marble);
        players.get(currentPlayerIndex).getMarbles().remove(marble);
    }
    
    @Override
    public void discardCard(Colour colour) throws CannotDiscardException {
        for (Player player : players) {
            if (player.getColour() == colour) {
                int handSize = player.getHand().size();
                if(handSize == 0)
                    throw new CannotDiscardException("Player has no cards to discard.");
                int randIndex = (int) (Math.random() * handSize);
                this.firePit.add(player.getHand().remove(randIndex));
            }
        }
    }

    @Override
    public void discardCard() throws CannotDiscardException {
        int randIndex = (int) (Math.random() * 4);
        while(randIndex == currentPlayerIndex)
            randIndex = (int) (Math.random() * 4);

        discardCard(players.get(randIndex).getColour());
    }

    @Override
    public Colour getActivePlayerColour() {
        return players.get(currentPlayerIndex).getColour();
    }

    @Override
    public Colour getNextPlayerColour() {
        return players.get((currentPlayerIndex + 1) % 4).getColour();
    }
    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    public int getTurn() {
        return turn;
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public Player getNextPlayer() {
        return players.get((currentPlayerIndex + 1) % players.size());
    }

    public boolean isCurrentPlayerCPU() {
        return getCurrentPlayer() instanceof CPU;
    }
    public Card getTopFirePitCard() {
        if (firePit.isEmpty())
            return null;

        return firePit.get(firePit.size() - 1);
    }

    public int getFirePitSize() {
        return firePit.size();
    }

    public int getPlayerIndexByColour(Colour colour) {
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getColour() == colour)
                return i;
        }

        return -1;
    }
    
}
