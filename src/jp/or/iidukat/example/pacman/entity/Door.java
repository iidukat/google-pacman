package jp.or.iidukat.example.pacman.entity;

import android.graphics.Canvas;

public class Door extends BaseEntity {
    
    @Override
    public void draw(Canvas c) {
        Presentation p = getPresentation();
        if (!p.isVisible()) return;
        
        p.drawRectShape(c);
    }
}
