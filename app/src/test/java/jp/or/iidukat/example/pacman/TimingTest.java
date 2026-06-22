package jp.or.iidukat.example.pacman;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TimingTest {

    private static final int FPS = GameConstants.DEFAULT_FPS;

    @Test
    public void withSound_newgameStarting_usesMusicDuration() {
        Timing t = new Timing(false);
        assertEquals((double) Math.round(2.23 * FPS), t.newgameStarting, 1e-9);
    }

    @Test
    public void noSound_newgameStarting_isShorter() {
        Timing t = new Timing(true);
        assertEquals((double) Math.round(1.0 * FPS), t.newgameStarting, 1e-9);
    }

    @Test
    public void noSound_newgameStarted_isShorter() {
        Timing t = new Timing(true);
        assertEquals((double) Math.round(1.0 * FPS), t.newgameStarted, 1e-9);
    }

    @Test
    public void ghostDied_isOneSecond() {
        Timing t = new Timing(false);
        assertEquals((double) FPS, t.ghostDied, 1e-9);
    }

    @Test
    public void playerDying_isOneSecond() {
        Timing t = new Timing(false);
        assertEquals((double) FPS, t.playerDying, 1e-9);
    }

    @Test
    public void frightBlink_matchesFrightBlinkSecsConstant() {
        Timing t = new Timing(false);
        assertEquals((double) Math.round(Timing.FRIGHT_BLINK_SECS * FPS), t.frightBlink, 1e-9);
    }

    @Test
    public void soundAndNoSound_sharedFields_areEqual() {
        Timing sound = new Timing(false);
        Timing noSound = new Timing(true);
        assertEquals(sound.ghostDied, noSound.ghostDied, 1e-9);
        assertEquals(sound.playerDied, noSound.playerDied, 1e-9);
        assertEquals(sound.gameover, noSound.gameover, 1e-9);
    }
}
