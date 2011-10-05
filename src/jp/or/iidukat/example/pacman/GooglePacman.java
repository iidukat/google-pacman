package jp.or.iidukat.example.pacman;

import android.app.Activity;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class GooglePacman extends Activity {
    /** Called when the activity is first created. */
    
    private GameFieldView view;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//      setContentView(R.layout.main);
        view = new GameFieldView(this);
        setContentView(view);
        
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }
    
    @Override
    public void onDestroy() {
        view.game.destroy();
        super.onDestroy();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.game_start:
            view.game.setDefaultKillScreenLevel();
            view.game.start();
            return true;
        case R.id.kill_screen:
            view.game.setKillScreenLevel(1);
            view.game.start();
            return true;
        }
        return false;
    }
}