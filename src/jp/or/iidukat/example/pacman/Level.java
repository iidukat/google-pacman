package jp.or.iidukat.example.pacman;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Canvas;

class Level {
	Presentation presentation = new Presentation();
	List<Fruit> fruits = new ArrayList<Fruit>();
	
	void draw(Bitmap sourceImage, Canvas c) {
		if (!presentation.visibility) return;
		
//		presentation.drawRectShape(c); 
		
		for (Fruit f : fruits) {
			f.draw(sourceImage, c);
		}
	}
}
