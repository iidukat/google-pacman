package jp.or.iidukat.example.pacman.entity;

import java.util.ArrayList;
import java.util.List;

import jp.or.iidukat.example.pacman.Presentation;
import android.graphics.Bitmap;
import android.graphics.Canvas;

public class CutsceneCanvas {
    private Presentation presentation = new Presentation();
    
    private List<CutsceneActor> actors = new ArrayList<CutsceneActor>();
    
    void draw(Bitmap sourceImage, Canvas c) {
        if (!presentation.isVisible()) return;
        
        presentation.drawRectShape(c);
        
        for (CutsceneActor actor : actors) {
            actor.draw(sourceImage, c);
        }
    }

    public Presentation getPresentation() {
        return presentation;
    }
    
    public void addActor(CutsceneActor actor) {
        actor.getEl().setParent(presentation);
        actors.add(actor);
    }
}
