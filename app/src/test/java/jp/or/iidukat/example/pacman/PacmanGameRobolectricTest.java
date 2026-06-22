package jp.or.iidukat.example.pacman;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertNotNull;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 33)
public class PacmanGameRobolectricTest {

    @Test
    public void init_withContext_initializesTiming() {
        Context context = ApplicationProvider.getApplicationContext();
        PacmanGame game = new PacmanGame(context);
        game.init();
        assertNotNull(game.timing);
    }
}
