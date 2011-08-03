package jp.or.iidukat.example.pacman;

import android.app.Activity;
import android.media.AudioManager;
import android.os.Bundle;

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
    	view.game.soundPlayer.destroy();
    	super.onDestroy();
    }
}