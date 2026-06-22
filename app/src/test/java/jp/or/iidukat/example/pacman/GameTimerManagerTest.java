package jp.or.iidukat.example.pacman;

import org.junit.Test;

import jp.or.iidukat.example.pacman.entity.Ghost;
import jp.or.iidukat.example.pacman.entity.Ghost.GhostMode;

import static jp.or.iidukat.example.pacman.PacmanGame.GameplayMode;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GameTimerManagerTest {

    /**
     * Create a GameTimerManager backed by a mock PacmanGame in ORDINARY_PLAYING mode
     * with gameplayModeTime=0, so handleGameplayModeTimer is a no-op unless overridden.
     */
    private PacmanGame mockGame;
    private GameTimerManager newManager() {
        mockGame = mock(PacmanGame.class);
        mockGame.gameplayMode = GameplayMode.ORDINARY_PLAYING;
        mockGame.gameplayModeTime = 0;
        return new GameTimerManager(mockGame);
    }

    // -----------------------------------------------------------------------
    // Gameplay mode timer — expiry calls changeGameplayMode with correct mode
    // (resulting state is tested in PacmanGameTimerTest.applyModeState_*)
    // -----------------------------------------------------------------------

    @Test
    public void handleTimers_newgameStarting_expiresIntoNewgameStarted() {
        GameTimerManager tm = newManager();
        mockGame.gameplayMode = GameplayMode.NEWGAME_STARTING;
        mockGame.gameplayModeTime = 1;

        tm.handleTimers();

        verify(mockGame).changeGameplayMode(GameplayMode.NEWGAME_STARTED);
    }

    @Test
    public void handleTimers_gameRestarting_expiresIntoGameRestarted() {
        GameTimerManager tm = newManager();
        mockGame.gameplayMode = GameplayMode.GAME_RESTARTING;
        mockGame.gameplayModeTime = 1;

        tm.handleTimers();

        verify(mockGame).changeGameplayMode(GameplayMode.GAME_RESTARTED);
    }

    @Test
    public void handleTimers_levelBeingCompleted_expiresIntoLevelCompleted() {
        GameTimerManager tm = newManager();
        mockGame.gameplayMode = GameplayMode.LEVEL_BEING_COMPLETED;
        mockGame.gameplayModeTime = 1;

        tm.handleTimers();

        verify(mockGame).changeGameplayMode(GameplayMode.LEVEL_COMPLETED);
    }

    @Test
    public void handleTimers_gameplayModeTime_decrementsEachTick() {
        GameTimerManager tm = newManager();
        mockGame.gameplayMode = GameplayMode.GAME_RESTARTING;
        mockGame.gameplayModeTime = 5;

        tm.handleTimers();

        assertEquals(4.0, mockGame.gameplayModeTime, 1e-9);
    }

    @Test
    public void handleTimers_zeroTime_doesNotDecrementOrTransition() {
        GameTimerManager tm = newManager();
        mockGame.gameplayMode = GameplayMode.GAME_RESTARTING;
        mockGame.gameplayModeTime = 0;

        tm.handleTimers();

        assertEquals(0.0, mockGame.gameplayModeTime, 1e-9);
        verify(mockGame, never()).changeGameplayMode(any());
    }

    // -----------------------------------------------------------------------
    // Fruit timer
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
    // Fright mode timer
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
    // Ghost mode switch timer (active when frightModeTime == 0)
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
    // Force pen leave timer
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
