package jp.or.iidukat.example.pacman.entity;

import java.util.ArrayList;
import java.util.List;

import jp.or.iidukat.example.pacman.PacmanGame;
import android.graphics.Bitmap;
import android.graphics.Canvas;

public class CutsceneField extends BaseEntity {

    private CutsceneActor[] actors;
    
    public CutsceneField(Bitmap sourceImage) {
        super(sourceImage);
    }
    
    public void init() {
        Presentation p = getPresentation();
        p.setLeft(45);
        p.setWidth(464);
        p.setHeight(136);
    }

    public void createActors(
                        PacmanGame game,
                        int cutsceneId,
                        Class<?>[] actorTypes) {
        List<CutsceneActor> cas = new ArrayList<CutsceneActor>();
        for (Class<?> actorType : actorTypes) {
            CutsceneActor actor =
                CutsceneActorFactory.getInstance()
                                    .create(
                                        actorType,
                                        getPresentation().getSourceImage(),
                                        game,
                                        cutsceneId);
            actor.init();
            actor.setParent(this);
            cas.add(actor);
        }
       actors = cas.toArray(new CutsceneActor[0]);
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
    
    public CutsceneActor[] getActors() {
        return actors;
    }
}
