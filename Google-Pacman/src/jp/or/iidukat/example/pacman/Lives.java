package jp.or.iidukat.example.pacman;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Canvas;

class Lives {
	Presentation presentation = new Presentation();
	List<Life> lives = new ArrayList<Life>();

	void draw(Bitmap sourceImage, Canvas c) {
		if (!presentation.visibility)
			return;

//		presentation.drawRectShape(c);

		for (Life life : lives) {
			life.draw(sourceImage, c);
		}
	}

	static class Life {
		Presentation presentation = new Presentation();

		void draw(Bitmap sourceImage, Canvas c) {
			if (!presentation.visibility)
				return;

			presentation.drawBitmap(sourceImage, c);
		}
	}
}
