package jp.or.iidukat.example.pacman.entity;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Sound extends BaseEntity {

    public Sound(Bitmap sourceImage) {
        super(sourceImage);
    }

    public void init() {
        Appearance a = getAppearance();
        a.setLeft(0);
        a.setTop(156);
        a.setWidth(12);
        a.setHeight(12);
        a.prepareBkPos(-32, -16);
    }

    public void turnOn() {
        getAppearance().changeBkPos(216, 105, false);
    }

    public void turnOff() {
        getAppearance().changeBkPos(236, 105, false);
    }

    @Override
    void doDraw(Canvas canvas) {
        getAppearance().drawBitmap(canvas);
    }
}
