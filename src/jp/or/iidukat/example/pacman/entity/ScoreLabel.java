package jp.or.iidukat.example.pacman.entity;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class ScoreLabel extends BaseEntity {

    public ScoreLabel(Bitmap sourceImage) {
        super(sourceImage);
    }

    public void init() {
        Presentation p = getPresentation();
        p.setLeft(-2);
        p.setTop(0);
        p.setWidth(48);
        p.setHeight(8);
        p.prepareBkPos(160, 56);
    }

    @Override
    public void draw(Canvas c) {
        if (!isVisible())
            return;

        getPresentation().drawBitmap(c);
    }
}
