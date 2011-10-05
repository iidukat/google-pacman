package jp.or.iidukat.example.pacman.entity;

import jp.or.iidukat.example.pacman.Presentation;
import android.graphics.Canvas;

public class Door {
    private Presentation presentation = new Presentation();
    
    void draw(Canvas c) {
        if (!presentation.isVisible()) return;
        
        presentation.drawRectShape(c);
    }

    public Presentation getPresentation() {
        return presentation;
    }
}
