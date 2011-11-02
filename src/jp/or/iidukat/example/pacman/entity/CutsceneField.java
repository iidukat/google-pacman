package jp.or.iidukat.example.pacman.entity;

import java.util.ArrayList;
import java.util.List;

import jp.or.iidukat.example.pacman.PacmanGame;
import android.graphics.Bitmap;
import android.graphics.Canvas;

public class CutsceneField extends BaseEntity {

    private CutsceneActor[] actors;
    
    public CutsceneField(Bitmap sourceImage) {
        super(sourceImage, true);
    }
    
    public void init() {
        Appearance a = getAppearance();
        a.setTop(16);
        a.setLeft(0);
        a.setWidth(464);
        a.setHeight(136);
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
                                        getAppearance().getSourceImage(),
                                        game,
                                        cutsceneId);
            actor.init();
            actor.setParent(this);
            cas.add(actor);
        }
       actors = cas.toArray(new CutsceneActor[0]);
    }
    
    @Override
    void doDraw(Canvas canvas) {
        getAppearance().drawRectShape(canvas);
    }
    
    public CutsceneActor[] getActors() {
        return actors;
    }
    
}
