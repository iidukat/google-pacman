package jp.or.iidukat.example.pacman.entity;

import jp.or.iidukat.example.pacman.PacmanGame;
import android.graphics.Bitmap;

class CutsceneActorFactory {

    private static final CutsceneActorFactory INSTANCE = new CutsceneActorFactory();
    
    static CutsceneActorFactory getInstance() {
        return INSTANCE;
    }
    
    private CutsceneActorFactory() {
    }
    
    CutsceneActor create(
                    Class<?> type,
                    Bitmap sourceImage,
                    PacmanGame game,
                    int cutsceneId) {
        
        if (CutscenePacman.class.equals(type)) {
            return new CutscenePacman(sourceImage, game);
        } else if (CutsceneBlinky.class.equals(type)) {
            return new CutsceneBlinky(sourceImage, game);
        } else if (CutsceneSteak.class.equals(type)) {
            return new CutsceneSteak(sourceImage, game);
        }
        
        throw new IllegalArgumentException("Illegal type: " + type);
    }
}
