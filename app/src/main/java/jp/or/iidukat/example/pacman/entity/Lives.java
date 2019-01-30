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
        a.setLeft(384);
        a.setTop(0);
        a.setHeight(16);
        a.setWidth(80);
    }

    public void update(int liveCount) {
        clearLives();
        for (int i = 0; i < liveCount; i++) {
            Lives.Life life =
                new Lives.Life(getAppearance().getSourceImage());
            life.init(getWidth() - (i + 1) * Life.HEIGHT);
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
        static final int WIDTH = 16;
        static final int HEIGHT = 16;
        
        public Life(Bitmap sourceImage) {
            super(sourceImage);
        }

        void init(int left) {
            Appearance a = getAppearance();
            a.setWidth(WIDTH);
            a.setHeight(HEIGHT);
            a.setLeft(left);
            a.prepareBkPos(64, 129);
        }
        
        @Override
        void doDraw(Canvas canvas) {
            getAppearance().drawBitmap(canvas);
        }
    }
}
