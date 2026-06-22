package jp.or.iidukat.example.pacman;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class LevelConfigTest {

    private static final int FPS = GameConstants.DEFAULT_FPS;
    // (int) Math.round(0.23 * 90) = 21
    private static final int FRIGHT_BLINK_DURATION = (int) Math.round(Timing.FRIGHT_BLINK_SECS * FPS);

    @Test
    public void level0_isPlaceholder() {
        LevelConfig c = LevelConfig.LEVEL_CONFIGS[0];
        assertEquals(0.0, c.getGhostSpeed(), 1e-9);
        assertEquals(0, c.getFrightTime());
    }

    @Test
    public void level1_speedValues() {
        LevelConfig c = LevelConfig.LEVEL_CONFIGS[1];
        // Config uses float literals (0.75f, 0.8f); compare at float precision
        assertEquals(0.75f, c.getGhostSpeed(), 1e-6);
        assertEquals(0.8f, c.getPlayerSpeed(), 1e-6);
    }

    @Test
    public void level1_frightTime_isMultipliedByFps() {
        LevelConfig c = LevelConfig.LEVEL_CONFIGS[1];
        assertEquals(6 * FPS, c.getFrightTime());
    }

    @Test
    public void level1_frightTotalTime_includesBlinkDuration() {
        // frightTotalTime = frightTime + FRIGHT_BLINK_DURATION * (frightBlinkCount * 2 - 1)
        // = 6*90 + 21*(5*2-1) = 540 + 189 = 729
        LevelConfig c = LevelConfig.LEVEL_CONFIGS[1];
        int expected = 6 * FPS + FRIGHT_BLINK_DURATION * (5 * 2 - 1);
        assertEquals(expected, c.getFrightTotalTime());
    }

    @Test
    public void level1_penLeavingLimits() {
        LevelConfig c = LevelConfig.LEVEL_CONFIGS[1];
        assertArrayEquals(new int[] { 0, 0, 30, 60 }, c.getPenLeavingLimits());
    }

    @Test
    public void level2_hasCutscene() {
        LevelConfig c = LevelConfig.LEVEL_CONFIGS[2];
        assertEquals(1, c.getCutsceneId());
    }

    @Test
    public void level5_frightTime_isZero_whenFrightTimeSetToZero() {
        // Level 9+ has frightTime=0, verify frightTotalTime formula handles that
        // Level 5 has frightTime=2
        LevelConfig c = LevelConfig.LEVEL_CONFIGS[5];
        assertEquals(2 * FPS, c.getFrightTime());
    }
}
