package jp.or.iidukat.example.pacman.entity;

import jp.or.iidukat.example.pacman.Presentation;
import android.graphics.Bitmap;
import android.graphics.Canvas;

public class ScoreLabel {
	private Presentation presentation = new Presentation();
	
	void draw(Bitmap sourceImage, Canvas c) {
		if (!presentation.isVisible()) return;
		
		presentation.drawBitmap(sourceImage, c);
	}

	public Presentation getPresentation() {
		return presentation;
	}
	
}
