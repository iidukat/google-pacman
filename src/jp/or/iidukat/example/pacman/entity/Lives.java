package jp.or.iidukat.example.pacman.entity;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Lives extends BaseEntity {
    private List<Life> lives = new ArrayList<Life>();

    @Override
    public void draw(Canvas c) {
        if (!isVisible())
            return;

//        presentation.drawRectShape(c);

        for (Life life : lives) {
            life.draw(c);
        }
    }
    
    public void addLife(Life life) {
        lives.add(life);
    }
    
    public void clearLives() {
        lives.clear();
    }
    
    public static class Life extends BaseEntity {
        
        public Life(Bitmap sourceImage) {
            super(sourceImage);
        }

        @Override
        public void draw(Canvas c) {
            if (!isVisible())
                return;

            getPresentation().drawBitmap(c);
        }
    }
}
