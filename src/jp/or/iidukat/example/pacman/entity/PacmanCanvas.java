package jp.or.iidukat.example.pacman.entity;

import jp.or.iidukat.example.pacman.Presentation;
import android.graphics.Bitmap;
import android.graphics.Canvas;

public class PacmanCanvas {
    private Presentation presentation = new Presentation();
    
    private PlayField playfield;
    private ScoreLabel scoreLabel;
    private Score score;
    private Sound sound;
    private Lives lives;
    private Level level;
    
    private CutsceneCanvas cutsceneCanvas;
    
    public void draw(Bitmap sourceImage, Canvas c) {
        if (!presentation.isVisible()) return;
        
//        presentation.drawRectShape(c);
        
        if (playfield != null) {
            playfield.draw(sourceImage, c);
        }
        

        if (scoreLabel != null) {
            scoreLabel.draw(sourceImage, c);
        }
        
        if (score != null) {
            score.draw(sourceImage, c);
        }
        
        if (sound != null) {
            sound.draw(sourceImage, c);
        }
        
        if (lives != null) {
            lives.draw(sourceImage, c);
        }
        
        if (level != null) {
            level.draw(sourceImage, c);
        }
        
        if (cutsceneCanvas != null) {
            cutsceneCanvas.draw(sourceImage, c);
        }
    }

    public Presentation getPresentation() {
        return presentation;
    }

    public PlayField getPlayfield() {
        return playfield;
    }

    public ScoreLabel getScoreLabel() {
        return scoreLabel;
    }

    public void setScoreLabel(ScoreLabel scoreLabel) {
        this.scoreLabel = scoreLabel;
    }

    public Score getScore() {
        return score;
    }
    
    public void setScore(Score score) {
        this.score = score;
    }
    
    public void setPlayfield(PlayField playfield) {
        this.playfield = playfield;
    }

    public Sound getSound() {
        return sound;
    }

    public void setSound(Sound sound) {
        this.sound = sound;
    }

    public Lives getLives() {
        return lives;
    }

    public void setLives(Lives lives) {
        this.lives = lives;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public CutsceneCanvas getCutsceneCanvas() {
        return cutsceneCanvas;
    }

    public void setCutsceneCanvas(CutsceneCanvas cutsceneCanvas) {
        this.cutsceneCanvas = cutsceneCanvas;
    }

    public void reset() {
        playfield = null;
        scoreLabel = null;
        score = null;
        sound = null;
        lives = null;
        level = null;
    }
}
