package jp.or.iidukat.example.pacman;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TickClockTest {

    @Test
    public void init_tickMultiplier_isOne_forDefaultFps() {
        TickClock clock = new TickClock();
        clock.init();
        assertEquals(1, clock.tickMultiplier);
    }

    @Test
    public void init_tickIntervalMs_matchesDefaultFps() {
        TickClock clock = new TickClock();
        clock.init();
        long expected = Math.round(1000.0 / GameConstants.DEFAULT_FPS);
        assertEquals(expected, clock.getTickIntervalMs());
    }

    @Test
    public void advance_returnsZero_whenNoLatencyAccumulated() {
        TickClock clock = new TickClock();
        clock.init();
        // Advance by exactly one tick interval — no accumulated latency.
        long now = System.currentTimeMillis();
        clock.advance(now); // consumes the initial lastTime offset
        long next = now + Math.round(1000.0 / GameConstants.DEFAULT_FPS);
        int extra = clock.advance(next);
        assertEquals(0, extra);
    }

    @Test
    public void advance_returnsPositive_whenLatencyExceedsOneInterval() {
        TickClock clock = new TickClock();
        clock.init();
        long now = System.currentTimeMillis();
        clock.advance(now); // resets lastTime; first call may accumulate negative delta
        // Arrive 20 intervals late — large enough to dominate any initial negative delta.
        long late = now + clock.getTickIntervalMs() * 20;
        int extra = clock.advance(late);
        assertTrue("Expected latency multiplier > 0 but got " + extra, extra > 0);
    }
}
