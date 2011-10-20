package jp.or.iidukat.example.pacman.entity;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class CutsceneCanvas extends BaseEntity {

    private List<CutsceneActor> actors = new ArrayList<CutsceneActor>();
    
    public CutsceneCanvas(Bitmap sourceImage) {
        super(sourceImage);
    }
    
    public void init() {
        Presentation p = getPresentation();
        p.setLeft(45);
        p.setWidth(464);
        p.setHeight(136);
    }

    public static float getFieldX(float b) {
        return b + -32;
    }

    public static float getFieldY(float b) {
        return b + 0;
    }
    
    @Override
    public void draw(Canvas c) {
        if (!isVisible()) return;
        
        getPresentation().drawRectShape(c);
        
        for (CutsceneActor actor : actors) {
            actor.draw(c);
        }
    }

    public void addActor(CutsceneActor actor) {
        actor.setParent(this);
        actors.add(actor);
    }
}
