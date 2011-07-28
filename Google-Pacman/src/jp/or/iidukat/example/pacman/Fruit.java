package jp.or.iidukat.example.pacman;

import android.graphics.Bitmap;
import android.graphics.Canvas;

class Fruit {

	private boolean onPlayField = false;
	
	Fruit() {
		this(false);
	}
	
	Fruit(boolean onPlayField) {
		this.onPlayField = onPlayField;
	}

	Presentation presentation = new Presentation();
	
	void draw(Bitmap sourceImage, Canvas c) {
		if (!presentation.visibility) return;
	
		// TODO: margin処理をきちんと実装する
		if (onPlayField) {
			this.presentation.top -=4;
			this.presentation.left -=8;
		}
		presentation.drawBitmap(sourceImage, c);
		if (onPlayField) {
			this.presentation.top +=4;
			this.presentation.left +=8;			
		}

	}
}
