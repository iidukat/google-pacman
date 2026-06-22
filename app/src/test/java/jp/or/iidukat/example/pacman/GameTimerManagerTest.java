package jp.or.iidukat.example.pacman;

import org.junit.Test;

import jp.or.iidukat.example.pacman.entity.Ghost;
import jp.or.iidukat.example.pacman.entity.Ghost.GhostMode;

import static jp.or.iidukat.example.pacman.PacmanGame.GameplayMode;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    /**
     * Create a GameTimerManager backed by a mock PacmanGame in ORDINARY_PLAYING mode
     * with gameplayModeTime=0, so handleGameplayModeTimer is always a no-op in these tests.
     */
    private PacmanGame mockGame;
    private GameTimerManager newManager() {
        mockGame = mock(PacmanGame.class);
        mockGame.gameplayMode = GameplayMode.ORDINARY_PLAYING;
        mockGame.gameplayModeTime = 0;
        return new GameTimerManager(mockGame);
    }

    // -----------------------------------------------------------------------
    // Gameplay mode timer — expiry transitions
    // (tested via StubGame to exercise applyModeState properly)
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

    // -----------------------------------------------------------------------
    // Fruit timer — direct GameTimerManager call
    // -----------------------------------------------------------------------

    @Test
    public void fruitTimer_decrementsEachTick() {
        GameTimerManager tm = newManager();
        tm.fruitTime = 5;

        tm.handleTimers();

        assertEquals(4, tm.fruitTime);
    }

    @Test
    public void fruitTimer_callsHideFruit_onExpiry() {
        GameTimerManager tm = newManager();
        tm.fruitTime = 1;

        tm.handleTimers();

        assertEquals(0, tm.fruitTime);
        verify(mockGame).hideFruit();
    }

    @Test
    public void fruitTimer_noop_whenZero() {
        GameTimerManager tm = newManager();
        tm.fruitTime = 0;

        tm.handleTimers();

        assertEquals(0, tm.fruitTime);
        verify(mockGame, never()).hideFruit();
    }

    // -----------------------------------------------------------------------
    // Fright mode timer — direct GameTimerManager call
    // -----------------------------------------------------------------------

    @Test
    public void frightModeTimer_decrementsEachTick() {
        GameTimerManager tm = newManager();
        tm.frightModeTime = 5;

        tm.handleTimers();

        assertEquals(4, tm.frightModeTime);
    }

    @Test
    public void frightModeTimer_callsFinishFrightMode_onExpiry() {
        GameTimerManager tm = newManager();
        tm.frightModeTime = 1;

        tm.handleTimers();

        assertEquals(0, tm.frightModeTime);
        verify(mockGame).finishFrightMode();
    }

    // -----------------------------------------------------------------------
    // Ghost mode switch timer — direct GameTimerManager call
    // frightModeTime must be 0 for this branch to activate
    // -----------------------------------------------------------------------

    @Test
    public void ghostModeTimer_decrementsEachTick() {
        GameTimerManager tm = newManager();
        tm.ghostModeTime = 5;

        tm.handleTimers();

        assertEquals(4.0, tm.ghostModeTime, 1e-9);
    }

    @Test
    public void ghostModeTimer_switchesToChase_whenScatterExpires() {
        LevelConfig lc = mock(LevelConfig.class);
        when(lc.getGhostModeSwitchTimes()).thenReturn(new double[]{1.0, 2.0});
        GameTimerManager tm = newManager();
        when(mockGame.getLevelConfig()).thenReturn(lc);
        when(mockGame.getMainGhostMode()).thenReturn(GhostMode.SCATTER);
        tm.ghostModeSwitchPos = 0;
        tm.ghostModeTime = 1;

        tm.handleTimers();

        assertEquals(1, tm.ghostModeSwitchPos);
        assertEquals(2.0 * GameConstants.DEFAULT_FPS, tm.ghostModeTime, 1e-9);
        verify(mockGame).switchMainGhostMode(GhostMode.CHASE, false);
    }

    @Test
    public void ghostModeTimer_switchesToScatter_whenChaseExpires() {
        LevelConfig lc = mock(LevelConfig.class);
        when(lc.getGhostModeSwitchTimes()).thenReturn(new double[]{1.0, 2.0, 3.0});
        GameTimerManager tm = newManager();
        when(mockGame.getLevelConfig()).thenReturn(lc);
        when(mockGame.getMainGhostMode()).thenReturn(GhostMode.CHASE);
        tm.ghostModeSwitchPos = 1;
        tm.ghostModeTime = 1;

        tm.handleTimers();

        assertEquals(2, tm.ghostModeSwitchPos);
        assertEquals(3.0 * GameConstants.DEFAULT_FPS, tm.ghostModeTime, 1e-9);
        verify(mockGame).switchMainGhostMode(GhostMode.SCATTER, false);
    }

    @Test
    public void ghostModeTimer_noSwitch_whenAllSwitchesExhausted() {
        LevelConfig lc = mock(LevelConfig.class);
        when(lc.getGhostModeSwitchTimes()).thenReturn(new double[]{1.0});
        GameTimerManager tm = newManager();
        when(mockGame.getLevelConfig()).thenReturn(lc);
        tm.ghostModeSwitchPos = 0;
        tm.ghostModeTime = 1;

        tm.handleTimers();

        assertEquals(1, tm.ghostModeSwitchPos);
        assertEquals(0.0, tm.ghostModeTime, 1e-9);
        verify(mockGame, never()).switchMainGhostMode(GhostMode.CHASE, false);
        verify(mockGame, never()).switchMainGhostMode(GhostMode.SCATTER, false);
    }

    // -----------------------------------------------------------------------
    // Force pen leave timer — direct GameTimerManager call
    // -----------------------------------------------------------------------

    @Test
    public void forcePenLeaveTimer_decrementsEachTick() {
        GameTimerManager tm = newManager();
        tm.forcePenLeaveTime = 5;

        tm.handleTimers();

        assertEquals(4, tm.forcePenLeaveTime);
    }

    @Test
    public void forcePenLeaveTimer_releasesFirstPennedGhost_onExpiry() {
        LevelConfig lc = mock(LevelConfig.class);
        when(lc.getPenForceTime()).thenReturn(5);
        Ghost blinky = mock(Ghost.class);
        Ghost pinky = mock(Ghost.class);
        Ghost inky = mock(Ghost.class);
        Ghost clyde = mock(Ghost.class);
        when(pinky.getMode()).thenReturn(GhostMode.IN_PEN);
        GameTimerManager tm = newManager();
        when(mockGame.getLevelConfig()).thenReturn(lc);
        when(mockGame.getGhosts()).thenReturn(new Ghost[]{blinky, pinky, inky, clyde});
        tm.forcePenLeaveTime = 1;

        tm.handleTimers();

        verify(pinky).setFreeToLeavePen(true);
        verify(inky, never()).setFreeToLeavePen(true);
        assertEquals(5 * GameConstants.DEFAULT_FPS, tm.forcePenLeaveTime);
    }
}
