package jp.or.iidukat.example.pacman;

import android.graphics.Bitmap;
import android.graphics.Canvas;

class PacManCanvas {
	Presentation presentation = new Presentation();
	
	PlayField playfield;
	ScoreLabel[] scoreLabels = new ScoreLabel[2];
	Score[] scores = new Score[2];
	Sound sound;
	Lives lives;
	Level level;
	
	CutsceneCanvas cutsceneCanvas;
	
	void draw(Bitmap sourceImage, Canvas c) {
		if (!presentation.visibility) return;
		
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
	
	void reset() {
		playfield = null;
		scoreLabels[0] = null;
		scoreLabels[1] = null;
		scores[0] = null;
		sound = null;
		lives = null;
		level = null;
	}
}
