package jp.or.iidukat.example.pacman.entity;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Sound extends BaseEntity {

    public Sound(Bitmap sourceImage) {
        super(sourceImage);
    }

    public void init() {
        Presentation p = getPresentation();
        p.setLeft(15); // 7 + 8
        p.setTop(124); // 116 + 8
        p.setWidth(12);
        p.setHeight(12);
        p.prepareBkPos(-32, -16);
    }

    public void turnOn() {
        getPresentation().changeBkPos(216, 105, false);
    }

    public void turnOff() {
        getPresentation().changeBkPos(236, 105, false);
    }

    @Override
    void doDraw(Canvas c) {
        getPresentation().drawBitmap(c);
    }
}
