package jp.or.iidukat.example.pacman.entity;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class PlayField extends BaseEntity {

    public PlayField(Bitmap sourceImage) {
        super(sourceImage);
    }
    
    private List<Actor> actors = new ArrayList<Actor>();
    private Door door;
    private List<Food> foods = new ArrayList<Food>();
    private Fruit fruit;
    private Ready ready;
    private List<KillScreenTile> killScreenTiles = new ArrayList<KillScreenTile>();
    private GameOver gameover;
    
    public void draw(Canvas c) {
        if (!isVisible())
            return;

        getPresentation().drawBitmap(getSourceImage(), c);

        if (door != null) {
            door.draw(c);
        }

        for (Food f : foods) {
            f.draw(c);
        }

        if (fruit != null) {
            fruit.draw(c);
        }

        for (Actor actor : actors) {
            actor.draw(c);
        }

        for (KillScreenTile tile : killScreenTiles) {
            tile.draw(c);
        }
        
        if (ready != null) {
            ready.draw(c);
        }
        
        if (gameover != null) {
            gameover.draw(c);
        }
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

    public static class Food extends BaseEntity {
        
        private boolean eaten = false;

        public Food(Bitmap sourceImage) {
            super(sourceImage);
        }
        
        @Override
        public void draw(Canvas c) {
            if (eaten || !isVisible())
                return;

            // TODO: 要見直し
            Presentation p = getPresentation();
            if (p.hasBackground()) {
                p.drawBitmap(getSourceImage(), c);
            } else {
                p.drawRectShape(c);
            }
        }

        public boolean isEaten() {
            return eaten;
        }

        public void setEaten(boolean eaten) {
            this.eaten = eaten;
        }
        
    }

    public static class Ready extends BaseEntity {
    
        public Ready(Bitmap sourceImage) {
            super(sourceImage);
        }
        
        @Override
        public void draw(Canvas c) {
            if (!isVisible())
                return;

            getPresentation().drawBitmap(getSourceImage(), c);
        }
    }

    public static class GameOver extends BaseEntity {
        
        public GameOver(Bitmap sourceImage) {
            super(sourceImage);
        }
        
        @Override
        public void draw(Canvas c) {
            if (!isVisible())
                return;

            getPresentation().drawBitmap(getSourceImage(), c);
        }
    }
    
    public static class KillScreenTile extends BaseEntity {
        
        public KillScreenTile(Bitmap sourceImage) {
            super(sourceImage);
        }
        
        @Override
        public void draw(Canvas c) {
            if (!isVisible()) return;

            // TODO: 要見直し
            Presentation p = getPresentation();
            if (p.hasBackground()) {
                p.drawBitmap(getSourceImage(), c);
            } else {
                p.drawRectShape(c);
            }
        }
    }

}
