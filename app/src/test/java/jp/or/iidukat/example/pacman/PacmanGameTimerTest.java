package jp.or.iidukat.example.pacman;

import org.junit.Test;

import static jp.or.iidukat.example.pacman.PacmanGame.GameplayMode;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;

public class PacmanGameTimerTest {

    private PacmanGame newGame() {
        PacmanGame g = spy(new PacmanGame(null));
        doNothing().when(g).applyModeEffects(any());
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

    @Test
    public void applyModeState_playerDied_setsTimer() {
        PacmanGame g = new PacmanGame(null);
        g.timing = new Timing(false);
        g.applyModeState(GameplayMode.PLAYER_DIED);
        assertEquals(GameplayMode.PLAYER_DIED, g.getGameplayMode());
        assertEquals(g.timing.playerDied, g.getGameplayModeTime(), 1e-9);
    }

    @Test
    public void applyModeState_gameRestarting_setsTimer() {
        PacmanGame g = new PacmanGame(null);
        g.timing = new Timing(false);
        g.applyModeState(GameplayMode.GAME_RESTARTING);
        assertEquals(g.timing.gameRestarting, g.getGameplayModeTime(), 1e-9);
    }

    @Test
    public void applyModeState_gameRestarted_setsTimer() {
        PacmanGame g = new PacmanGame(null);
        g.timing = new Timing(false);
        g.applyModeState(GameplayMode.GAME_RESTARTED);
        assertEquals(g.timing.gameRestarted, g.getGameplayModeTime(), 1e-9);
    }

    @Test
    public void applyModeState_newgameStarting_withSound_setsTimer() {
        PacmanGame g = new PacmanGame(null);
        g.timing = new Timing(false);
        g.applyModeState(GameplayMode.NEWGAME_STARTING);
        assertEquals(g.timing.newgameStarting, g.getGameplayModeTime(), 1e-9);
    }

    @Test
    public void applyModeState_newgameStarting_noSound_setsShorterTimer() {
        PacmanGame withSound = new PacmanGame(null);
        withSound.timing = new Timing(false);
        withSound.applyModeState(GameplayMode.NEWGAME_STARTING);

        PacmanGame noSound = new PacmanGame(null);
        noSound.timing = new Timing(true);
        noSound.applyModeState(GameplayMode.NEWGAME_STARTING);

        assertEquals(noSound.timing.newgameStarting, noSound.getGameplayModeTime(), 1e-9);
        // No-sound skips the start_music track; the timer is shorter as a result.
        assertTrue(noSound.getGameplayModeTime() < withSound.getGameplayModeTime());
    }

    @Test
    public void applyModeState_levelCompleted_setsTimer() {
        PacmanGame g = new PacmanGame(null);
        g.timing = new Timing(false);
        g.applyModeState(GameplayMode.LEVEL_COMPLETED);
        assertEquals(g.timing.levelCompleted, g.getGameplayModeTime(), 1e-9);
    }

    @Test
    public void applyModeState_transitionIntoNextScene_setsTimer() {
        PacmanGame g = new PacmanGame(null);
        g.timing = new Timing(false);
        g.applyModeState(GameplayMode.TRANSITION_INTO_NEXT_SCENE);
        assertEquals(g.timing.transition, g.getGameplayModeTime(), 1e-9);
    }

    @Test
    public void applyModeState_ordinaryPlaying_doesNotChangeTimer() {
        PacmanGame g = new PacmanGame(null);
        g.timing = new Timing(false);
        g.gameplayModeTime = 42.0;
        g.applyModeState(GameplayMode.ORDINARY_PLAYING);
        assertEquals(GameplayMode.ORDINARY_PLAYING, g.getGameplayMode());
        assertEquals(42.0, g.getGameplayModeTime(), 1e-9);
    }

    @Test
    public void applyModeState_cutscene_doesNotChangeTimer() {
        PacmanGame g = new PacmanGame(null);
        g.timing = new Timing(false);
        g.gameplayModeTime = 42.0;
        g.applyModeState(GameplayMode.CUTSCENE);
        assertEquals(GameplayMode.CUTSCENE, g.getGameplayMode());
        assertEquals(42.0, g.getGameplayModeTime(), 1e-9);
    }

    // -----------------------------------------------------------------------
    // handleTimers — timer expiry transitions
    // (only modes with no per-tick entity calls: NEWGAME_STARTING, GAME_RESTARTING,
    //  LEVEL_BEING_COMPLETED)
    // -----------------------------------------------------------------------

    @Test
    public void handleTimers_newgameStarting_expiresIntoNewgameStarted() {
        PacmanGame s = newGame();
        s.gameplayMode = GameplayMode.NEWGAME_STARTING;
        s.gameplayModeTime = 1;

        s.handleTimers();

        assertEquals(GameplayMode.NEWGAME_STARTED, s.getGameplayMode());
        assertEquals(s.timing.newgameStarted, s.getGameplayModeTime(), 1e-9);
    }

    @Test
    public void handleTimers_gameRestarting_expiresIntoGameRestarted() {
        PacmanGame s = newGame();
        s.gameplayMode = GameplayMode.GAME_RESTARTING;
        s.gameplayModeTime = 1;

        s.handleTimers();

        assertEquals(GameplayMode.GAME_RESTARTED, s.getGameplayMode());
        assertEquals(s.timing.gameRestarted, s.getGameplayModeTime(), 1e-9);
    }

    @Test
    public void handleTimers_levelBeingCompleted_expiresIntoLevelCompleted() {
        PacmanGame s = newGame();
        s.gameplayMode = GameplayMode.LEVEL_BEING_COMPLETED;
        s.gameplayModeTime = 1;

        s.handleTimers();

        assertEquals(GameplayMode.LEVEL_COMPLETED, s.getGameplayMode());
        assertEquals(s.timing.levelCompleted, s.getGameplayModeTime(), 1e-9);
    }

    @Test
    public void handleTimers_gameplayModeTime_decrementsEachTick() {
        PacmanGame s = newGame();
        s.gameplayMode = GameplayMode.GAME_RESTARTING;
        s.gameplayModeTime = 5;

        s.handleTimers();

        assertEquals(4.0, s.getGameplayModeTime(), 1e-9);
        assertEquals(GameplayMode.GAME_RESTARTING, s.getGameplayMode());
    }

    @Test
    public void handleTimers_zeroTime_doesNotDecrementOrTransition() {
        PacmanGame s = newGame();
        s.gameplayMode = GameplayMode.GAME_RESTARTING;
        s.gameplayModeTime = 0;

        s.handleTimers();

        assertEquals(GameplayMode.GAME_RESTARTING, s.getGameplayMode());
        assertEquals(0.0, s.getGameplayModeTime(), 1e-9);
    }
}
