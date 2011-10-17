package jp.or.iidukat.example.pacman.entity;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;

public class CutsceneCanvas extends BaseEntity {

    private List<CutsceneActor> actors = new ArrayList<CutsceneActor>();
    
    @Override
    public void draw(Canvas c) {
        if (!isVisible()) return;
        
        getPresentation().drawRectShape(c);
        
        for (CutsceneActor actor : actors) {
            actor.draw(c);
        }
    }

    public void addActor(CutsceneActor actor) {
        actor.getPresentation().setParent(getPresentation());
        actors.add(actor);
    }
}
