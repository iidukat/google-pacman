package jp.or.iidukat.example.pacman;

import android.app.Activity;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class GooglePacman extends Activity implements OnClickListener {

    private PacmanGame game;
    private GameView gameView;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        PacmanGame game = initGame();
        initGameView(game);
        initMainView();
        
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    @Override
    public void onResume() {
        super.onResume();
        game.resume();
    }
    
    @Override
    public void onPause() {
        game.pause();
        super.onPause();
    }
    
    private void initMainView() {
        setContentView(R.layout.main);
        
        View newGameButton = findViewById(R.id.new_game_button);
        newGameButton.setOnClickListener(this);
        View killScreenButton = findViewById(R.id.killscreen_button);
        killScreenButton.setOnClickListener(this);
        View exitButton = findViewById(R.id.exit_button);
        exitButton.setOnClickListener(this);
    }
    
    private void initGameView(PacmanGame game) {
        gameView = new GameView(this);
        game.view = gameView;
        gameView.game = game;
    }
    
    private PacmanGame initGame() {
        game = new PacmanGame(this);
        game.init();
        return game;
    }
    
    private void transitionToGameView() {
        setContentView(gameView);
        gameView.setFocusable(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.new_game_button:
            transitionToGameView();
            game.startNewGame();
            break;
        case R.id.killscreen_button:
            transitionToGameView();
            game.showKillScreen();
            break;
        case R.id.exit_button:
            finish();
            break;
        }
        
    }
    
}