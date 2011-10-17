package jp.or.iidukat.example.pacman.entity;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Sound extends BaseEntity {
    
    public Sound(Bitmap sourceImage) {
        super(sourceImage);
    }
    
    @Override
    public void draw(Canvas c) {
        if (!isVisible()) return;
        
        getPresentation().drawBitmap(getSourceImage(), c);
    }
}
