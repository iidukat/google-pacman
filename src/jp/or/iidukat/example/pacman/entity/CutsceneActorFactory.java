package jp.or.iidukat.example.pacman.entity;

import jp.or.iidukat.example.pacman.PacmanGame;

public class CutsceneActorFactory {

    private static final CutsceneActorFactory INSTANCE = new CutsceneActorFactory();
    
    public static CutsceneActorFactory getInstance() {
        return INSTANCE;
    }
    
    private CutsceneActorFactory() {
    }
    
    public CutsceneActor create(
                            Class<?> type,
                            PacmanGame g,
                            int cutsceneId) {
        
        if (CutscenePacman.class.equals(type)) {
            return new CutscenePacman(g);
        } else if (CutsceneBlinky.class.equals(type)) {
            return new CutsceneBlinky(g);
        } else if (CutsceneSteak.class.equals(type)) {
            return new CutsceneSteak(g);
        }
        
        throw new IllegalArgumentException("Illegal type: " + type);
    }
}
