package jp.or.iidukat.example.pacman;

import java.util.ArrayList;
import java.util.List;

import jp.or.iidukat.example.pacman.Pacman10Hp3.E;
import android.graphics.Bitmap;
import android.graphics.Canvas;

public class CutsceneCanvas {
	Presentation presentation = new Presentation();
	
	List<E> actors = new ArrayList<E>();
	
	void draw(Bitmap sourceImage, Canvas c) {
		if (!presentation.visibility) return;
		
		presentation.drawRectShape(c);
		
		for (E actor : actors) {
			actor.draw(sourceImage, c);
		}
	}
}
