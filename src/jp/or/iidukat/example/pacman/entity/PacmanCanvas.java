package jp.or.iidukat.example.pacman.entity;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class PacmanCanvas extends BaseEntity {
    
    private PlayField playfield;
    private ScoreLabel scoreLabel;
    private Score score;
    private Sound sound;
    private Lives lives;
    private Level level;
    
    private CutsceneCanvas cutsceneCanvas;
    
    public PacmanCanvas(Bitmap sourceImage) {
        super(sourceImage);
    }

    public void init() {
        Presentation p = getPresentation();
        p.setWidth(554);
        p.setHeight(136);
        p.setBgColor(0x000000);
    }
    
    @Override
    public void draw(Canvas c) {
        if (!isVisible()) return;
        
//        presentation.drawRectShape(c);
        
        if (playfield != null) {
            playfield.draw(c);
        }
        

        if (scoreLabel != null) {
            scoreLabel.draw(c);
        }
        
        if (score != null) {
            score.draw(c);
        }
        
        if (sound != null) {
            sound.draw(c);
        }
        
        if (lives != null) {
            lives.draw(c);
        }
        
        if (level != null) {
            level.draw(c);
        }
        
        if (cutsceneCanvas != null) {
            cutsceneCanvas.draw(c);
        }
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

    public void setTop(float top) {
        getPresentation().setTop(top);
    }
    
    public void setLeft(float left) {
        getPresentation().setLeft(left);
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
