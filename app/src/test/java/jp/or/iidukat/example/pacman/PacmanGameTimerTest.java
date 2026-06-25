package jp.or.iidukat.example.pacman;

import org.junit.Test;

import static jp.or.iidukat.example.pacman.PacmanGame.GameplayMode;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PacmanGameTimerTest {

    // -----------------------------------------------------------------------
    // applyModeState — pure state
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
    // onGameplayModeTimerExpired — pure mode transitions (no side-effects beyond changeGameplayMode)
    // -----------------------------------------------------------------------

    /**
     * Suppresses applyModeEffects so onGameplayModeTimerExpired can be tested
     * without a fully initialised canvasEl.
     */
    private static class NoEffectsGame extends PacmanGame {
        NoEffectsGame() { super(null); }
        @Override void applyModeEffects(GameplayMode mode) {}
    }

    @Test
    public void onGameplayModeTimerExpired_playerDying_transitionsToPlayerDied() {
        NoEffectsGame g = new NoEffectsGame();
        g.timing = new Timing(false);
        g.gameplayMode = GameplayMode.PLAYER_DYING;
        g.onGameplayModeTimerExpired();
        assertEquals(GameplayMode.PLAYER_DIED, g.getGameplayMode());
    }

    @Test
    public void onGameplayModeTimerExpired_newgameStarting_transitionsToNewgameStarted() {
        NoEffectsGame g = new NoEffectsGame();
        g.timing = new Timing(false);
        g.gameplayMode = GameplayMode.NEWGAME_STARTING;
        g.onGameplayModeTimerExpired();
        assertEquals(GameplayMode.NEWGAME_STARTED, g.getGameplayMode());
    }

    @Test
    public void onGameplayModeTimerExpired_gameRestarting_transitionsToGameRestarted() {
        NoEffectsGame g = new NoEffectsGame();
        g.timing = new Timing(false);
        g.gameplayMode = GameplayMode.GAME_RESTARTING;
        g.onGameplayModeTimerExpired();
        assertEquals(GameplayMode.GAME_RESTARTED, g.getGameplayMode());
    }

    @Test
    public void onGameplayModeTimerExpired_levelBeingCompleted_transitionsToLevelCompleted() {
        NoEffectsGame g = new NoEffectsGame();
        g.timing = new Timing(false);
        g.gameplayMode = GameplayMode.LEVEL_BEING_COMPLETED;
        g.onGameplayModeTimerExpired();
        assertEquals(GameplayMode.LEVEL_COMPLETED, g.getGameplayMode());
    }

    @Test
    public void onGameplayModeTimerExpired_levelCompleted_transitionsToTransitionIntoNextScene() {
        NoEffectsGame g = new NoEffectsGame();
        g.timing = new Timing(false);
        g.gameplayMode = GameplayMode.LEVEL_COMPLETED;
        g.onGameplayModeTimerExpired();
        assertEquals(GameplayMode.TRANSITION_INTO_NEXT_SCENE, g.getGameplayMode());
    }

}
