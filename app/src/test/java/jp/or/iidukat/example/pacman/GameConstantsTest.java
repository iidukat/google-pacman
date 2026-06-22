package jp.or.iidukat.example.pacman;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GameConstantsTest {

    @Test
    public void defaultFps_is90() {
        assertEquals(90, GameConstants.DEFAULT_FPS);
    }

    @Test
    public void fpsOptions_firstEntry_matchesDefaultFps() {
        assertEquals(GameConstants.DEFAULT_FPS, GameConstants.FPS_OPTIONS[0]);
    }

    @Test
    public void fpsOptions_areInDescendingOrder() {
        int[] opts = GameConstants.FPS_OPTIONS;
        for (int i = 0; i < opts.length - 1; i++) {
            assertTrue("FPS_OPTIONS[" + i + "] should be > FPS_OPTIONS[" + (i + 1) + "]",
                    opts[i] > opts[i + 1]);
        }
    }
}
