package jp.or.iidukat.example.pacman.entity;

import jp.or.iidukat.example.pacman.PacmanGame;
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
        PacmanGame.prepareElement(getPresentation(), -32, -16);
    }

    public void turnOn() {
        PacmanGame.changeElementBkPos(getPresentation(), 216, 105, false);
    }

    public void turnOff() {
        PacmanGame.changeElementBkPos(getPresentation(), 236, 105, false);
    }

    @Override
    public void draw(Canvas c) {
        if (!isVisible())
            return;

        getPresentation().drawBitmap(c);
    }
}
