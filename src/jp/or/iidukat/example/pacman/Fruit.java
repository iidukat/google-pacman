package jp.or.iidukat.example.pacman;

import android.graphics.Bitmap;
import android.graphics.Canvas;

class Fruit {

	Presentation presentation = new Presentation();
	
	void draw(Bitmap sourceImage, Canvas c) {
		if (!presentation.visibility) return;
	
		presentation.drawBitmap(sourceImage, c);
	}
}
