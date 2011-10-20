package jp.or.iidukat.example.pacman.entity;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Door extends BaseEntity {
    
    public Door(Bitmap sourceImage) {
        super(sourceImage);
    }
    
    void init() {
        Presentation p = getPresentation();
        p.setWidth(19);
        p.setHeight(2);
        p.setLeft(279);
        p.setTop(46);
        p.setBgColor(0xffaaa5);
    }
    
    @Override
    public void draw(Canvas c) {
        Presentation p = getPresentation();
        if (!p.isVisible()) return;
        
        p.drawRectShape(c);
    }
}
