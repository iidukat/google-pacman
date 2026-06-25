package jp.or.iidukat.example.pacman;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import jp.or.iidukat.example.pacman.entity.CutsceneActor;
import jp.or.iidukat.example.pacman.entity.CutsceneBlinky;
import jp.or.iidukat.example.pacman.entity.CutscenePacman;
import jp.or.iidukat.example.pacman.entity.CutsceneSteak;

class CutsceneController {

    private static class Cutscene {
        private final Class<?>[] actors;
        private final double[] sequenceTimes;

        Cutscene(Class<?>[] actors, double[] sequenceTimes) {
            this.actors = actors;
            this.sequenceTimes = sequenceTimes;
        }
    }

    private static final Map<Integer, Cutscene> CUTSCENES;
    static {
        Map<Integer, Cutscene> css = new HashMap<Integer, Cutscene>();
        css.put(
            Integer.valueOf(1),
            new Cutscene(
                new Class<?>[] { CutscenePacman.class, CutsceneBlinky.class },
                new double[] { 5.5f, 0.1f, 9 }));
        css.put(
            Integer.valueOf(2),
            new Cutscene(
                new Class<?>[] {
                    CutscenePacman.class,
                    CutsceneBlinky.class,
                    CutsceneSteak.class
                },
                new double[] { 2.7f, 1, 1.3f, 1, 2.5f }));
        css.put(
            Integer.valueOf(3),
            new Cutscene(
                new Class<?>[] { CutscenePacman.class, CutsceneBlinky.class },
                new double[] { 5.3f, 5.3f }));
        CUTSCENES = Collections.unmodifiableMap(css);
    }

    private final PacmanGame game;

    private Cutscene cutscene;
    int cutsceneId;
    private int cutsceneSequenceId;
    private double cutsceneTime;
    private int debugCutsceneId;
    private boolean debugCutsceneMode;
    private Runnable onDebugCutsceneFinished;

    CutsceneController(PacmanGame game) {
        this.game = game;
    }

    int getCutsceneId() { return cutsceneId; }

    void setCutsceneId(int id) { cutsceneId = id; }

    int getCutsceneSequenceId() { return cutsceneSequenceId; }

    double getCutsceneTime() { return cutsceneTime; }

    boolean hasDebugCutscene() { return debugCutsceneId != 0; }

    void consumeDebugCutscene() {
        cutsceneId = debugCutsceneId;
        debugCutsceneId = 0;
    }

    void setDebugCutsceneId(int id) {
        debugCutsceneId = id;
        debugCutsceneMode = true;
    }

    void setOnFinished(Runnable callback) {
        onDebugCutsceneFinished = callback;
    }

    void start() {
        game.getPlayfieldEl().setVisibility(false);

        game.getCanvasEl().setVisibility(true);
        game.getCanvasEl().showChrome(false);
        game.getCanvasEl().createCutsceneField();

        cutscene = CUTSCENES.get(Integer.valueOf(cutsceneId));
        cutsceneSequenceId = -1;
        game.getGameTimerManager().setFrightModeTime(game.getLevelConfig().getFrightTotalTime());
        createActors();

        nextSequence();
        game.getSoundManager().stopAll();
        game.getSoundManager().playCutsceneAmbient();
    }

    void stop() {
        game.getSoundManager().stopCutsceneAmbient();
        game.getPlayfieldEl().setVisibility(true);
        game.getCanvasEl().removeCutsceneField();
        game.getCanvasEl().showChrome(true);
        if (debugCutsceneMode) {
            debugCutsceneMode = false;
            if (onDebugCutsceneFinished != null) {
                onDebugCutsceneFinished.run();
            }
        } else {
            game.newLevel(false);
        }
    }

    void advance() {
        CutsceneActor[] cutsceneActors = game.getCanvasEl().getCutsceneField().getActors();
        for (CutsceneActor actor : cutsceneActors) {
            actor.move();
        }
        cutsceneTime--;
    }

    void check() {
        if (cutsceneTime <= 0) {
            nextSequence();
        }
    }

    private void nextSequence() {
        cutsceneSequenceId++;
        if (cutscene.sequenceTimes.length == cutsceneSequenceId) {
            stop();
        } else {
            cutsceneTime = cutscene.sequenceTimes[cutsceneSequenceId] * GameConstants.DEFAULT_FPS;
            CutsceneActor[] cutsceneActors = game.getCanvasEl().getCutsceneField().getActors();
            for (int i = 0; i < cutsceneActors.length; i++) {
                CutsceneActor actor = cutsceneActors[i];
                actor.setupSequence();
                actor.updateAppearance();
            }
        }
    }

    private void createActors() {
        game.getCanvasEl().getCutsceneField().createActors(game, cutsceneId, cutscene.actors);
    }
}
