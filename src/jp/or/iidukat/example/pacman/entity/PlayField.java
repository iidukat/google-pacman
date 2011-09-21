package jp.or.iidukat.example.pacman.entity;

import java.util.ArrayList;
import java.util.List;

import jp.or.iidukat.example.pacman.Presentation;
import android.graphics.Bitmap;
import android.graphics.Canvas;

public class PlayField {

	private Presentation presentation = new Presentation();

	private List<Actor> actors = new ArrayList<Actor>();
	private Door door;
	private List<Food> foods = new ArrayList<Food>();
	private Fruit fruit;
	private Ready ready;
	private List<KillScreenTile> killScreenTiles = new ArrayList<KillScreenTile>();
	private GameOver gameover;
	
	void draw(Bitmap sourceImage, Canvas c) {
		if (!presentation.isVisible())
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

		for (Actor actor : actors) {
			actor.draw(sourceImage, c);
		}

		for (KillScreenTile tile : killScreenTiles) {
			tile.draw(sourceImage, c);
		}
		
		if (ready != null) {
			ready.draw(sourceImage, c);
		}
		
		if (gameover != null) {
			gameover.draw(sourceImage, c);
		}
	}
	
	public Presentation getPresentation() {
		return presentation;
	}
	
	public void addActor(Actor actor) {
		actors.add(actor);
	}
	
	public Door getDoor() {
		return door;
	}
	
	public void setDoor(Door door) {
		this.door = door;
	}

	public Fruit getFruit() {
		return fruit;
	}

	public void setFruit(Fruit fruit) {
		this.fruit = fruit;
	}
	
	public Ready getReady() {
		return ready;
	}

	public void setReady(Ready ready) {
		this.ready = ready;
	}

	public List<Food> getFoods() {
		return foods;
	}

	public void addFood(Food food) {
		foods.add(food);
	}
	
	public void clearFoods() {
		foods.clear();
	}
	
	public void addKillScreenTile(KillScreenTile tile) {
		killScreenTiles.add(tile);
	}
	
	public GameOver getGameover() {
		return gameover;
	}

	public void setGameover(GameOver gameover) {
		this.gameover = gameover;
	}

	public static class Food {
		private boolean eaten = false;
		private Presentation presentation = new Presentation();

		void draw(Bitmap sourceImage, Canvas c) {
			if (eaten || !presentation.isVisible())
				return;

			// TODO: 要見直し
			if (presentation.hasBackground()) {
				presentation.drawBitmap(sourceImage, c);
			} else {
				presentation.drawRectShape(c);
			}
		}

		public Presentation getPresentation() {
			return presentation;
		}

		public boolean isEaten() {
			return eaten;
		}

		public void setEaten(boolean eaten) {
			this.eaten = eaten;
		}
		
	}

	public static class Ready {
		Presentation presentation = new Presentation();

		void draw(Bitmap sourceImage, Canvas c) {
			if (!presentation.isVisible())
				return;

			presentation.drawBitmap(sourceImage, c);
		}

		public Presentation getPresentation() {
			return presentation;
		}
	}

	public static class GameOver {
		Presentation presentation = new Presentation();

		void draw(Bitmap sourceImage, Canvas c) {
			if (!presentation.isVisible())
				return;

			presentation.drawBitmap(sourceImage, c);
		}

		public Presentation getPresentation() {
			return presentation;
		}
	}
	
	public static class KillScreenTile {
		private Presentation presentation = new Presentation();
		
		void draw(Bitmap sourceImage, Canvas c) {
			if (!presentation.isVisible()) return;

			// TODO: 要見直し
			if (presentation.hasBackground()) {
				presentation.drawBitmap(sourceImage, c);
			} else {
				presentation.drawRectShape(c);
			}
		}

		public Presentation getPresentation() {
			return presentation;
		}
		
	}

}
