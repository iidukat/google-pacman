package jp.or.iidukat.example.pacman.entity;

import java.util.ArrayList;
import java.util.List;

import jp.or.iidukat.example.pacman.Presentation;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Score {
	private Presentation presentation = new Presentation();
	private List<Number> numbers = new ArrayList<Number>();
	
	void draw(Bitmap sourceImage, Canvas c) {
		
		presentation.drawRectShape(c);
		
		for (Number n : numbers) {
			n.draw(sourceImage, c);
		}
	}
	
	public Presentation getPresentation() {
		return presentation;
	}
	
	public void addNumber(Number number) {
		numbers.add(number);
	}
	
	public Number getNumber(int index) {
		return numbers.get(index);
	}

	public static class Number {
		private Presentation presentation = new Presentation();
		
		void draw(Bitmap sourceImage, Canvas c) {
			if (!presentation.isVisible()) return;
			
			presentation.drawBitmap(sourceImage, c);
		}

		public Presentation getPresentation() {
			return presentation;
		}
		
	}
}
