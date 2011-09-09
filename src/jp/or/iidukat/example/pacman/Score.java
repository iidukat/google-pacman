package jp.or.iidukat.example.pacman;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Canvas;

class Score {
	Presentation presentation = new Presentation();
	List<Number> numbers = new ArrayList<Number>();
	
	void draw(Bitmap sourceImage, Canvas c) {
		
		presentation.drawRectShape(c);
		
		for (Number n : numbers) {
			n.draw(sourceImage, c);
		}
	}
	
	static class Number {
		Presentation presentation = new Presentation();
		
		void draw(Bitmap sourceImage, Canvas c) {
			if (!presentation.visibility) return;
			
			presentation.drawBitmap(sourceImage, c);
		}
	}
}
