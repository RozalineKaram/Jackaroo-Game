package controller;

import engine.Game;
import exception.CannotFieldException;
import exception.GameException;
import exception.IllegalDestroyException;
import exception.InvalidCardException;
import exception.InvalidMarbleException;
import exception.SplitOutOfRangeException;
import model.Colour;
import model.card.Card;
import model.player.Marble;
import model.player.Player;

/**
 * GameController — thin wrapper around the Game engine used by the view layer.
 * All exceptions from the engine are propagated; the view is responsible for
 * catching them and showing appropriate error dialogs.
 */
public class GameController {

    private final Game game;

    public GameController(Game game) {
        this.game = game;
    }

    // ── Accessors ──────────────────────────────────────────────

    public Game getGame() {
        return game;
    }

    public Player getCurrentPlayer() {
        return game.getCurrentPlayer();
    }

    // ── Card / Marble selection ────────────────────────────────

    public void selectCard(Card card) throws InvalidCardException {
        game.selectCard(card);
    }

    public void selectMarble(Marble marble) throws InvalidMarbleException {
        game.selectMarble(marble);
    }

    public void deselectAll() {
        game.deselectAll();
    }

    // ── Split distance for Seven card ──────────────────────────

    public void setSplitDistance(int distance) throws SplitOutOfRangeException {
        game.editSplitDistance(distance);
    }

    // ── Field marble (also used for keyboard shortcut) ─────────

    public void fieldMarble() throws CannotFieldException, IllegalDestroyException {
        game.fieldMarble();
    }

    // ── Turn control ───────────────────────────────────────────

    /**
     * Plays the current (human) player's selected card, then ends their turn.
     */
    public void playCurrentTurnAndEnd() throws GameException {
        game.playPlayerTurn();
        game.endPlayerTurn();
    }

    /**
     * Ends the current turn without playing (used after CPU.play() was called
     * directly, or when skipping).
     */
    public void endCurrentTurn() {
        game.endPlayerTurn();
    }
    public boolean canPlayTurn() {
        return game.canPlayTurn();
    }
    
}