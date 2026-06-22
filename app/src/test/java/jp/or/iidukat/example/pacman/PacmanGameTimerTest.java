package jp.or.iidukat.example.pacman;

import org.junit.Test;

import static jp.or.iidukat.example.pacman.PacmanGame.GameplayMode;
import static org.junit.Assert.assertEquals;

public class PacmanGameTimerTest {

    /**
     * PacmanGame subclass with applyModeEffects as no-op.
     * Avoids Mockito spy() which cannot instrument java.lang.Object on Java 25.
     */
    private static class TimerTestGame extends PacmanGame {
        TimerTestGame() { super(null); }
        @Override
        void applyModeEffects(GameplayMode mode) {}
    }

    private TimerTestGame newGame() {
        TimerTestGame g = new TimerTestGame();
        g.timing = new Timing(false);
        return g;
    }

    // -----------------------------------------------------------------------
    // applyModeState — pure state, no spy needed
    // -----------------------------------------------------------------------

    @Test
    public void applyModeState_playerDying_setsTimerAndMode() {
        PacmanGame g = new PacmanGame(null);
        g.timing = new Timing(false);
        g.applyModeState(GameplayMode.PLAYER_DYING);
        assertEquals(GameplayMode.PLAYER_DYING, g.getGameplayMode());
        assertEquals(g.timing.playerDying, g.getGameplayModeTime(), 1e-9);
    }

    @Test
    public void applyModeState_ghostDied_setsTimer() {
        PacmanGame g = new PacmanGame(null);
        g.timing = new Timing(false);
        g.applyModeState(GameplayMode.GHOST_DIED);
        assertEquals(GameplayMode.GHOST_DIED, g.getGameplayMode());
        assertEquals(g.timing.ghostDied, g.getGameplayModeTime(), 1e-9);
    }

    @Test
    public void applyModeState_newgameStarted_decrementsLives() {
        PacmanGame g = new PacmanGame(null);
        g.timing = new Timing(false);
        g.lives = 3;
        g.applyModeState(GameplayMode.NEWGAME_STARTED);
        assertEquals(2, g.lives);
        assertEquals(g.timing.newgameStarted, g.getGameplayModeTime(), 1e-9);
    }

    @Test
    public void applyModeState_levelBeingCompleted_setsTimer() {
        PacmanGame g = new PacmanGame(null);
        g.timing = new Timing(false);
        g.applyModeState(GameplayMode.LEVEL_BEING_COMPLETED);
        assertEquals(g.timing.levelBeingCompleted, g.getGameplayModeTime(), 1e-9);
    }

    @Test
    public void applyModeState_gameoverAndKillScreen_shareTimer() {
        PacmanGame g = new PacmanGame(null);
        g.timing = new Timing(false);
        g.applyModeState(GameplayMode.GAMEOVER);
        double gameoverTime = g.getGameplayModeTime();
        g.applyModeState(GameplayMode.KILL_SCREEN);
        assertEquals(gameoverTime, g.getGameplayModeTime(), 1e-9);
    }

    // -----------------------------------------------------------------------
    // handleTimers — timer expiry transitions
    // (only modes with no per-tick entity calls: NEWGAME_STARTING, GAME_RESTARTING,
    //  LEVEL_BEING_COMPLETED)
    // -----------------------------------------------------------------------

    @Test
    public void handleTimers_newgameStarting_expiresIntoNewgameStarted() {
        TimerTestGame s = newGame();
        s.gameplayMode = GameplayMode.NEWGAME_STARTING;
        s.gameplayModeTime = 1;

        s.handleTimers();

        assertEquals(GameplayMode.NEWGAME_STARTED, s.getGameplayMode());
        assertEquals(s.timing.newgameStarted, s.getGameplayModeTime(), 1e-9);
    }

    @Test
    public void handleTimers_gameRestarting_expiresIntoGameRestarted() {
        TimerTestGame s = newGame();
        s.gameplayMode = GameplayMode.GAME_RESTARTING;
        s.gameplayModeTime = 1;

        s.handleTimers();

        assertEquals(GameplayMode.GAME_RESTARTED, s.getGameplayMode());
        assertEquals(s.timing.gameRestarted, s.getGameplayModeTime(), 1e-9);
    }

    @Test
    public void handleTimers_levelBeingCompleted_expiresIntoLevelCompleted() {
        TimerTestGame s = newGame();
        s.gameplayMode = GameplayMode.LEVEL_BEING_COMPLETED;
        s.gameplayModeTime = 1;

        s.handleTimers();

        assertEquals(GameplayMode.LEVEL_COMPLETED, s.getGameplayMode());
        assertEquals(s.timing.levelCompleted, s.getGameplayModeTime(), 1e-9);
    }

    @Test
    public void handleTimers_gameplayModeTime_decrementsEachTick() {
        TimerTestGame s = newGame();
        s.gameplayMode = GameplayMode.GAME_RESTARTING;
        s.gameplayModeTime = 5;

        s.handleTimers();

        assertEquals(4.0, s.getGameplayModeTime(), 1e-9);
        assertEquals(GameplayMode.GAME_RESTARTING, s.getGameplayMode());
    }

    @Test
    public void handleTimers_zeroTime_doesNotDecrementOrTransition() {
        TimerTestGame s = newGame();
        s.gameplayMode = GameplayMode.GAME_RESTARTING;
        s.gameplayModeTime = 0;

        s.handleTimers();

        assertEquals(GameplayMode.GAME_RESTARTING, s.getGameplayMode());
        assertEquals(0.0, s.getGameplayModeTime(), 1e-9);
    }
}
