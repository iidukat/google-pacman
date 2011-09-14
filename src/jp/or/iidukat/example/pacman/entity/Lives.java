package jp.or.iidukat.example.pacman.entity;

import java.util.ArrayList;
import java.util.List;

import jp.or.iidukat.example.pacman.Presentation;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Lives {
	private Presentation presentation = new Presentation();
	private List<Life> lives = new ArrayList<Life>();

	void draw(Bitmap sourceImage, Canvas c) {
		if (!presentation.isVisible())
			return;

//		presentation.drawRectShape(c);

		for (Life life : lives) {
			life.draw(sourceImage, c);
		}
	}
	
	public Presentation getPresentation() {
		return presentation;
	}
	
	public void addLife(Life life) {
		lives.add(life);
	}
	
	public void clearLives() {
		lives.clear();
	}
	
	public static class Life {
		private Presentation presentation = new Presentation();

		void draw(Bitmap sourceImage, Canvas c) {
			if (!presentation.isVisible())
				return;

			presentation.drawBitmap(sourceImage, c);
		}

		public Presentation getPresentation() {
			return presentation;
		}
		
	}
}
