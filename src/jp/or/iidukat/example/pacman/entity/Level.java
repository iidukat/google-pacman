package jp.or.iidukat.example.pacman.entity;

import java.util.ArrayList;
import java.util.List;

import jp.or.iidukat.example.pacman.Presentation;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Level {
	private Presentation presentation = new Presentation();
	private List<Fruit> fruits = new ArrayList<Fruit>();
	
	void draw(Bitmap sourceImage, Canvas c) {
		if (!presentation.isVisible()) return;
		
//		presentation.drawRectShape(c); 
		
		for (Fruit f : fruits) {
			f.draw(sourceImage, c);
		}
	}

	public Presentation getPresentation() {
		return presentation;
	}
	
	public void addFruit(Fruit fruit) {
		fruits.add(fruit);
	}
	
	public void clearFruits() {
		fruits.clear();
	}
}
