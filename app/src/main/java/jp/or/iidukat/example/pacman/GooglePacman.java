package jp.or.iidukat.example.pacman;

import android.app.Activity;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class GooglePacman extends Activity implements OnClickListener {

    private static final int CUTSCENE_COUNT = 3;

    private PacmanGame game;
    private GameView gameView;
    private int nextCutsceneId = 1;
    private boolean inGameView = false;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        initGame();
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
        View cutsceneButton = findViewById(R.id.cutscene_button);
        cutsceneButton.setOnClickListener(this);
        View exitButton = findViewById(R.id.exit_button);
        exitButton.setOnClickListener(this);

        if (!BuildConfig.DEBUG) {
            killScreenButton.setVisibility(View.GONE);
            cutsceneButton.setVisibility(View.GONE);
        }
    }
    
    private void initGameView(PacmanGame game) {
        gameView = new GameView(this);
        game.view = gameView;
        gameView.game = game;
    }
    
    private PacmanGame initGame() {
        game = new PacmanGame(this);
        game.init();
        if (BuildConfig.DEBUG) {
            game.setOnDebugCutsceneFinished(this::returnToMenu);
        }
        return game;
    }

    private void transitionToGameView() {
        inGameView = true;
        setContentView(gameView);
        gameView.setFocusable(true);
    }

    private void returnToMenu() {
        inGameView = false;
        game.pause();
        initGame();
        game.resume();
        initGameView(game);
        initMainView();
    }

    @Override
    public void onBackPressed() {
        if (inGameView) {
            returnToMenu();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.new_game_button) {
            transitionToGameView();
            game.startNewGame();
        } else if (id == R.id.killscreen_button) {
            transitionToGameView();
            game.showKillScreen();
        } else if (id == R.id.cutscene_button) {
            transitionToGameView();
            game.showCutscene(nextCutsceneId);
            nextCutsceneId = nextCutsceneId % CUTSCENE_COUNT + 1;
        } else if (id == R.id.exit_button) {
            finish();
        }
        
    }
    
}