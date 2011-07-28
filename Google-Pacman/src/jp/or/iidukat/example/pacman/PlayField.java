package jp.or.iidukat.example.pacman;

import java.util.ArrayList;
import java.util.List;

import jp.or.iidukat.example.pacman.Pacman10Hp3.E;
import android.graphics.Bitmap;
import android.graphics.Canvas;

class PlayField {

	Presentation presentation = new Presentation();

	List<E> actors = new ArrayList<E>();
	Door door;
	List<Food> foods = new ArrayList<Food>();
	Fruit fruit;
	Ready ready;
	GameOver gameover;

	void draw(Bitmap sourceImage, Canvas c) {
		if (!presentation.visibility)
			return;

		presentation.drawBitmap(sourceImage, c);

		if (door != null) {
			door.draw(c);
		}

		for (Food f : foods) {
			f.draw(sourceImage, c);
		}

		if (fruit != null) {
			fruit.draw(sourceImage, c);
		}

		for (E actor : actors) {
			actor.draw(sourceImage, c);
		}

		if (ready != null) {
			ready.draw(sourceImage, c);
		}

		if (gameover != null) {
			gameover.draw(sourceImage, c);
		}
	}

	static class Food {
		boolean eaten = false;
		Presentation presentation = new Presentation();

		void draw(Bitmap sourceImage, Canvas c) {
			if (eaten || !presentation.visibility)
				return;

			// TODO: 要見直し
			if (presentation.hasBackground()) {
				presentation.drawBitmap(sourceImage, c);
			} else {
				// TODO: margin処理を実装する
				presentation.left += 3;
				presentation.top += 3;
				presentation.drawRectShape(c);
				presentation.left -= 3;
				presentation.top -= 3;
			}
		}
	}

	static class Ready {
		Presentation presentation = new Presentation();

		void draw(Bitmap sourceImage, Canvas c) {
			if (!presentation.visibility)
				return;

			presentation.drawBitmap(sourceImage, c);
		}
	}

	static class GameOver {
		Presentation presentation = new Presentation();

		void draw(Bitmap sourceImage, Canvas c) {
			if (!presentation.visibility)
				return;

			presentation.drawBitmap(sourceImage, c);
		}
	}
}
