package multiplayer.minesweeper.sessionutils;

/**
 * Definition of the logic behind the start of a game. This class implement a Strategy design pattern and
 * splits the management of a session, handled inside the SessionsManager class and when a session must
 * be terminated in order to start a game.
 * A game could be started only when the required amount of
 * players is connected to a session, as soon a player connects or after the expiration of a timer.
 */
public interface StartStrategy {
    boolean checkCondition();
}
