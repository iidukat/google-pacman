package jp.or.iidukat.example.pacman.entity;

import jp.or.iidukat.example.pacman.Presentation;
import android.graphics.Bitmap;
import android.graphics.Canvas;

public class PacManCanvas {
	private Presentation presentation = new Presentation();
	
	private PlayField playfield;
	private ScoreLabel[] scoreLabels = new ScoreLabel[2];
	private Score[] scores = new Score[2];
	private Sound sound;
	private Lives lives;
	private Level level;
	
	private CutsceneCanvas cutsceneCanvas;
	
	public void draw(Bitmap sourceImage, Canvas c) {
		if (!presentation.isVisible()) return;
		
//		presentation.drawRectShape(c);
		
		if (playfield != null) {
			playfield.draw(sourceImage, c);
		}
		
		for (ScoreLabel sl : scoreLabels) {
			if (sl != null) {
				sl.draw(sourceImage, c);
			}
		}
		
		for (Score s : scores) {
			if (s != null) {
				s.draw(sourceImage, c);
			}
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

	public ScoreLabel[] getScoreLabels() {
		return scoreLabels;
	}

	public Score[] getScores() {
		return scores;
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
		scoreLabels[0] = null;
		scoreLabels[1] = null;
		scores[0] = null;
		sound = null;
		lives = null;
		level = null;
	}
}
