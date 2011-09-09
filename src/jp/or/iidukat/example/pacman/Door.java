package jp.or.iidukat.example.pacman;

import android.graphics.Canvas;

class Door {
	Presentation presentation = new Presentation();
	
	void draw(Canvas c) {
		if (!presentation.visibility) return;
		
		presentation.drawRectShape(c);
	}

}
