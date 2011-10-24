package jp.or.iidukat.example.pacman.entity;

import jp.or.iidukat.example.pacman.PacmanGame;
import android.graphics.Bitmap;

public class CutsceneActorFactory {

    private static final CutsceneActorFactory INSTANCE = new CutsceneActorFactory();
    
    public static CutsceneActorFactory getInstance() {
        return INSTANCE;
    }
    
    private CutsceneActorFactory() {
    }
    
    public CutsceneActor create(
                            Class<?> type,
                            Bitmap sourceImage,
                            PacmanGame g,
                            int cutsceneId) {
        
        if (CutscenePacman.class.equals(type)) {
            return new CutscenePacman(sourceImage, g);
        } else if (CutsceneBlinky.class.equals(type)) {
            return new CutsceneBlinky(sourceImage, g);
        } else if (CutsceneSteak.class.equals(type)) {
            return new CutsceneSteak(sourceImage, g);
        }
        
        throw new IllegalArgumentException("Illegal type: " + type);
    }
}
