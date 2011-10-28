package jp.or.iidukat.example.pacman.entity;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Lives extends BaseEntity {
    private final List<Life> lives = new ArrayList<Life>();

    public Lives(Bitmap sourceImage) {
        super(sourceImage, true);
    }

    public void init() {
        Appearance a = getAppearance();
        a.setLeft(523);
        a.setTop(0);
        a.setHeight(80);
        a.setWidth(16);
    }

    public void update(int liveCount) {
        clearLives();
        for (int i = 0; i < liveCount; i++) {
            Lives.Life life =
                new Lives.Life(getAppearance().getSourceImage());
            life.init(i * 15);
            life.setParent(this);
            lives.add(life);
        }
    }
    
    private void clearLives() {
        lives.clear();
        clearChildren();
    }

    @Override
    void doDraw(Canvas canvas) {
    }

    public static class Life extends BaseEntity {

        public Life(Bitmap sourceImage) {
            super(sourceImage);
        }

        void init(int top) {
            Appearance a = getAppearance();
            a.setWidth(16);
            a.setHeight(12);
            a.setTop(top); // margin-bottom: 3px
            a.prepareBkPos(64, 129);
        }
        
        @Override
        void doDraw(Canvas canvas) {
            getAppearance().drawBitmap(canvas);
        }
    }
}
