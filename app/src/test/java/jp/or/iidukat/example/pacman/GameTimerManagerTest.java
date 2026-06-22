package jp.or.iidukat.example.pacman;

import org.junit.Test;

import static jp.or.iidukat.example.pacman.PacmanGame.GameplayMode;
import static org.junit.Assert.assertEquals;

public class GameTimerManagerTest {

    private static class StubGame extends PacmanGame {
        StubGame() { super(null); }

        @Override
        void applyModeEffects(GameplayMode mode) {}
    }

    private StubGame newGame() {
        StubGame g = new StubGame();
        g.timing = new Timing(false);
        return g;
    }

    // -----------------------------------------------------------------------
    // handleTimers — gameplay mode timer expiry transitions
    // (only modes with no per-tick entity calls: NEWGAME_STARTING, GAME_RESTARTING,
    //  LEVEL_BEING_COMPLETED)
    // -----------------------------------------------------------------------

    @Test
    public void handleTimers_newgameStarting_expiresIntoNewgameStarted() {
        StubGame s = newGame();
        s.gameplayMode = GameplayMode.NEWGAME_STARTING;
        s.gameplayModeTime = 1;

        s.handleTimers();

        assertEquals(GameplayMode.NEWGAME_STARTED, s.getGameplayMode());
        assertEquals(s.timing.newgameStarted, s.getGameplayModeTime(), 1e-9);
    }

    @Test
    public void handleTimers_gameRestarting_expiresIntoGameRestarted() {
        StubGame s = newGame();
        s.gameplayMode = GameplayMode.GAME_RESTARTING;
        s.gameplayModeTime = 1;

        s.handleTimers();

        assertEquals(GameplayMode.GAME_RESTARTED, s.getGameplayMode());
        assertEquals(s.timing.gameRestarted, s.getGameplayModeTime(), 1e-9);
    }

    @Test
    public void handleTimers_levelBeingCompleted_expiresIntoLevelCompleted() {
        StubGame s = newGame();
        s.gameplayMode = GameplayMode.LEVEL_BEING_COMPLETED;
        s.gameplayModeTime = 1;

        s.handleTimers();

        assertEquals(GameplayMode.LEVEL_COMPLETED, s.getGameplayMode());
        assertEquals(s.timing.levelCompleted, s.getGameplayModeTime(), 1e-9);
    }

    @Test
    public void handleTimers_gameplayModeTime_decrementsEachTick() {
        StubGame s = newGame();
        s.gameplayMode = GameplayMode.GAME_RESTARTING;
        s.gameplayModeTime = 5;

        s.handleTimers();

        assertEquals(4.0, s.getGameplayModeTime(), 1e-9);
        assertEquals(GameplayMode.GAME_RESTARTING, s.getGameplayMode());
    }

    @Test
    public void handleTimers_zeroTime_doesNotDecrementOrTransition() {
        StubGame s = newGame();
        s.gameplayMode = GameplayMode.GAME_RESTARTING;
        s.gameplayModeTime = 0;

        s.handleTimers();

        assertEquals(GameplayMode.GAME_RESTARTING, s.getGameplayMode());
        assertEquals(0.0, s.getGameplayModeTime(), 1e-9);
    }
}
