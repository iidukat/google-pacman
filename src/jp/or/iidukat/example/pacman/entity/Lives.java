package jp.or.iidukat.example.pacman.entity;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Lives extends BaseEntity {
    private List<Life> lives = new ArrayList<Life>();

    public Lives(Bitmap sourceImage) {
        super(sourceImage, true);
    }

    public void init() {
        Presentation p = getPresentation();
        p.setLeft(523);
        p.setTop(0);
        p.setHeight(80);
        p.setWidth(16);
    }

    public void update(int liveCount) {
        clearLives();
        for (int b = 0; b < liveCount; b++) {
            Lives.Life life = new Lives.Life(getPresentation().getSourceImage());
            life.init(b * 15);
            life.setParent(this);
            lives.add(life);
        }
    }
    
    private void clearLives() {
        lives.clear();
        clearDrawQueue();
    }

    @Override
    void doDraw(Canvas c) {
    }

    public static class Life extends BaseEntity {

        public Life(Bitmap sourceImage) {
            super(sourceImage);
        }

        void init(int top) {
            Presentation p = getPresentation();
            p.setWidth(16);
            p.setHeight(12);
            p.setTop(top); // margin-bottom: 3px
            p.prepareBkPos(64, 129);
        }
        
        @Override
        void doDraw(Canvas c) {
            getPresentation().drawBitmap(c);
        }
    }
}
