package jp.or.iidukat.example.pacman;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.or.iidukat.example.pacman.entity.PlayfieldActor;
import jp.or.iidukat.example.pacman.entity.PlayfieldActor.CurrentSpeed;
import jp.or.iidukat.example.pacman.entity.CutsceneActor;
import jp.or.iidukat.example.pacman.entity.CutsceneBlinky;
import jp.or.iidukat.example.pacman.entity.CutsceneField;
import jp.or.iidukat.example.pacman.entity.CutscenePacman;
import jp.or.iidukat.example.pacman.entity.CutsceneSteak;
import jp.or.iidukat.example.pacman.entity.Entity;
import jp.or.iidukat.example.pacman.entity.Fruit;
import jp.or.iidukat.example.pacman.entity.Ghost;
import jp.or.iidukat.example.pacman.entity.Ghost.GhostMode;
import jp.or.iidukat.example.pacman.entity.Level;
import jp.or.iidukat.example.pacman.entity.Lives;
import jp.or.iidukat.example.pacman.entity.Pacman;
import jp.or.iidukat.example.pacman.entity.PacmanCanvas;
import jp.or.iidukat.example.pacman.entity.Playfield;
import jp.or.iidukat.example.pacman.entity.Playfield.Door;
import jp.or.iidukat.example.pacman.entity.Playfield.PathElement;
import jp.or.iidukat.example.pacman.entity.Playfield.PathElement.Dot;
import jp.or.iidukat.example.pacman.entity.Score;
import jp.or.iidukat.example.pacman.entity.ScoreLabel;
import jp.or.iidukat.example.pacman.entity.Sound;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.MotionEvent;

public class PacmanGame {

    private static final int DEFAULT_KILL_SCREEN_LEVEL = 256;
    
    // After a level restart, when a number of dots eaten by the player has reached the threshold,
    // each ghost leaves from the pen.
    // This is the thresholds of each ghost.
    private static final int[] PEN_LEAVING_FOOD_LIMITS = { 0, 7, 17, 32 };

    // Cutscene Animation
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
    
    public static enum GameplayMode {
        ORDINARY_PLAYING(0), GHOST_DIED(1), PLAYER_DYING(2), PLAYER_DIED(3),
        NEWGAME_STARTING(4), NEWGAME_STARTED(5), GAME_RESTARTING(6), GAME_RESTARTED(7),
        GAMEOVER(8), LEVEL_BEING_COMPLETED(9), LEVEL_COMPLETED(10),
        TRANSITION_INTO_NEXT_SCENE(11), CUTSCENE(13), KILL_SCREEN(14);
        
        private final int mode;
        
        private GameplayMode(int mode) {
            this.mode = mode;
        }

        public int getMode() {
            return mode;
        }

    }

    private final Context context;
    GameView view;
    private Bitmap sourceImage;
    SoundManager soundManager;
    private InputHandler inputHandler;

    private boolean paused;
    private boolean started;
    private long randSeed;

    private PacmanCanvas canvasEl;

    private long score;
    private boolean extraLifeAwarded;
    private int lives = 3;
    private int level = 0;
    private int killScreenLevel = DEFAULT_KILL_SCREEN_LEVEL;
    private LevelConfig levelConfig;
    private long globalTime = 0;

    private int frightModeTime = 0;
    private int intervalTime = 0;
    private double gameplayModeTime = 0;
    private int fruitTime = 0;
    private int forcePenLeaveTime;
    private int ghostModeSwitchPos = 0;
    private double ghostModeTime;
    private boolean ghostExitingPenNow = false;
    private int ghostEyesCount = 0;
    private boolean tilesChanged = false;
    private GameplayMode gameplayMode;
    private double[] timing;
    private boolean alternatePenLeavingScheme;
    private int alternateDotCount;
    private boolean lostLifeOnThisLevel;

    private GhostMode lastMainGhostMode;
    private GhostMode mainGhostMode;

    private double currentPlayerSpeed;
    private double currentDotEatingSpeed;
    private double cruiseElroySpeed;
    private Map<Double, Boolean[]> speedIntervals;

    private int modeScoreMultiplier;
    private boolean fruitShown;
    private Ghost ghostBeingEaten;

    private Cutscene cutscene;
    private int cutsceneId;
    private int cutsceneSequenceId;
    private double cutsceneTime;
    private int debugCutsceneId;
    private boolean debugCutsceneMode;
    private Runnable onDebugCutsceneFinished;
    private TickClock tickClock;

    PacmanGame(Context context) {
        this.context = context;
    }

    public double rand() {
        long b = 4294967296L;
        long c = 134775813L;
        c = c * randSeed + 1;
        return (randSeed = c % b) / (double) b;
    }

    private void seed(long s) {
        this.randSeed = s;
    }

    private void restartActors() {
        getPacman().arrange();

        for (PlayfieldActor ghost : getGhosts())
            ghost.arrange();
    }

    private void createPlayfield() {
        canvasEl.createPlayfield(this);
    }
    
    private void resetPlayfield() {
        getPlayfieldEl().reset();
    }
    
    private void createReadyElement() {
        getPlayfieldEl().createReadyElement();
    }
    
    private void removeReadyElement() {
        getPlayfieldEl().removeReady();
    }
    
    private void createGameOverElement() {
        getPlayfieldEl().createGameOverElement();
    }

    void handleTouchStart(MotionEvent e) {
        inputHandler.onTouchStart(e);
    }

    void handleTouchMove(MotionEvent e) {
        inputHandler.onTouchMove(e);
    }

    void handleTouchEnd(MotionEvent e) {
        inputHandler.onTouchEnd(e);
    }

    void setDpadDir(Direction dir) {
        inputHandler.setDpadDir(dir);
    }

    private void startGameplay() {
        score = 0;
        extraLifeAwarded = false;
        lives = 3;
        level = 0;
        globalTime = 0;
        newLevel(true);
    }

    private void restartGameplay(boolean newGame) {
        seed(0);
        frightModeTime = 0;
        intervalTime = 0;
        gameplayModeTime = 0;
        fruitTime = 0;
        ghostModeSwitchPos = 0;
        ghostModeTime = levelConfig.getGhostModeSwitchTimes()[0] * GameConstants.DEFAULT_FPS;
        ghostExitingPenNow = false;
        ghostEyesCount = 0;
        tilesChanged = false;
        updateCruiseElroySpeed();
        hideFruit();
        resetForcePenLeaveTime();
        restartActors();
        updateActorPositions();
        switchMainGhostMode(GhostMode.SCATTER, true);
        for (int i = 1; i < 4; i++) {
            getGhosts()[i].switchGhostMode(GhostMode.IN_PEN);
        }
        soundManager.resetDotEatingSound();

        if (newGame && debugCutsceneId != 0) {
            cutsceneId = debugCutsceneId;
            debugCutsceneId = 0;
            changeGameplayMode(GameplayMode.CUTSCENE);
        } else if (newGame) {
            changeGameplayMode(GameplayMode.NEWGAME_STARTING);
        } else {
            changeGameplayMode(GameplayMode.GAME_RESTARTING);
        }
//      changeGameplayMode(GameplayMode.LEVEL_COMPLETED); // for Cutscene debug
    }

    private void newGame() {
        createChrome();
        createPlayfield();
        startGameplay();
    }

    private void killScreen() {
        seed(0);
        canvasEl.setVisibility(true);
        getPlayfieldEl().killScreen();
        changeGameplayMode(GameplayMode.KILL_SCREEN);
    }

    private void newLevel(boolean newGame) {
        level++;
        levelConfig =
            level >= LevelConfig.LEVEL_CONFIGS.length
                ? LevelConfig.LEVEL_CONFIGS[LevelConfig.LEVEL_CONFIGS.length - 1]
                : LevelConfig.LEVEL_CONFIGS[level];
        alternatePenLeavingScheme = false;
        lostLifeOnThisLevel = false;
        updateChrome();
        resetPlayfield();
        restartGameplay(newGame);
        if (level == killScreenLevel) {
            killScreen();
        }
    }

    private void newLife() {
        lostLifeOnThisLevel = true;
        alternatePenLeavingScheme = true;
        alternateDotCount = 0;
        lives--;
        updateChromeLives();

        if (lives == -1) {
            changeGameplayMode(GameplayMode.GAMEOVER);
        } else {
            restartGameplay(false);
        }
    }

    private void switchMainGhostMode(GhostMode ghostMode,
                                    boolean justRestartGame) {
        Ghost[] ghosts = getGhosts();
        if (ghostMode == GhostMode.FRIGHTENED
                && levelConfig.getFrightTime() == 0) {
            for (Ghost ghost : ghosts) {
                ghost.setReverseDirectionsNext(true); // If frightTime is 0, a frightened ghost only reverse its direction.
            }
        } else {
            GhostMode oldMainGhostMode = mainGhostMode;
            if (ghostMode == GhostMode.FRIGHTENED
                    && mainGhostMode != GhostMode.FRIGHTENED) {
                lastMainGhostMode = mainGhostMode;
            }
            mainGhostMode = ghostMode;
            if (ghostMode == GhostMode.FRIGHTENED
                || oldMainGhostMode == GhostMode.FRIGHTENED) {
                playAmbientSound();
            }
            switch (ghostMode) {
            case CHASE:
            case SCATTER:
                currentPlayerSpeed = levelConfig.getPlayerSpeed() * 0.8f;
                currentDotEatingSpeed = levelConfig.getDotEatingSpeed() * 0.8f;
                break;
            case FRIGHTENED:
                currentPlayerSpeed = levelConfig.getPlayerFrightSpeed() * 0.8f;
                currentDotEatingSpeed = levelConfig.getDotEatingFrightSpeed() * 0.8f;
                frightModeTime = levelConfig.getFrightTotalTime();
                modeScoreMultiplier = 1;
                break;
            }
            for (Ghost ghost : ghosts) {
                if (ghostMode != GhostMode.ENTERING_PEN && !justRestartGame) {
                    ghost.setModeChangedWhileInPen(true);
                }
                if (ghostMode == GhostMode.FRIGHTENED) {
                    ghost.setEatenInThisFrightMode(false);
                }
                if (ghost.getMode() != GhostMode.EATEN
                        && ghost.getMode() != GhostMode.IN_PEN
                        && ghost.getMode() != GhostMode.LEAVING_PEN
                        && ghost.getMode() != GhostMode.RE_LEAVING_FROM_PEN
                        && ghost.getMode() != GhostMode.ENTERING_PEN || justRestartGame) {

                    // If it is not immediately after restart the game (justRestartGmae:false),
                    // a ghost reverse its direction 
                    // when its mode change from other than FRIGHTENED (CHASE or SCATTER) to another mode.
                    if (!justRestartGame && ghost.getMode() != GhostMode.FRIGHTENED
                            && ghost.getMode() != ghostMode) {
                        ghost.setReverseDirectionsNext(true);
                    }

                    // If it is not immediately after restart the game
                    // and a mode of each ghost is any of EATEN, IN_PEN, LEAVING_PEN, RE_LEAVING_FROM_PEN, or ENTERING_PEN,
                    // it is not updated.
                    ghost.switchGhostMode(ghostMode);
                }
            }

            Pacman pacman = getPacman();
            pacman.setFullSpeed(currentPlayerSpeed);
            pacman.setDotEatingSpeed(currentDotEatingSpeed);
            pacman.setTunnelSpeed(currentPlayerSpeed);
            pacman.changeSpeed();
        }
    }

    private void figureOutPenLeaving() {
        Ghost pinky = getPinky();
        Ghost inky = getInky();
        Ghost clyde = getClyde();
        if (alternatePenLeavingScheme) {
            // By using a number of dots eaten after a level restart,
            // manage the timing of the ghosts leaving from the pen.
            alternateDotCount++;
            if (alternateDotCount == PEN_LEAVING_FOOD_LIMITS[1]) {
                pinky.setFreeToLeavePen(true);
            } else if (alternateDotCount == PEN_LEAVING_FOOD_LIMITS[2]) {
                inky.setFreeToLeavePen(true);
            } else if (alternateDotCount == PEN_LEAVING_FOOD_LIMITS[3]) {
                if (clyde.getMode() == GhostMode.IN_PEN) {
                    alternatePenLeavingScheme = false;
                }
            }
        } else if (pinky.getMode() == GhostMode.IN_PEN
                || pinky.getMode() == GhostMode.EATEN) {
            pinky.incrementDotCount();
            if (pinky.getDotCount() >= levelConfig.getPenLeavingLimits()[1]) {
                pinky.setFreeToLeavePen(true);
            }
        } else if (inky.getMode() == GhostMode.IN_PEN
                || inky.getMode() == GhostMode.EATEN) {
            inky.incrementDotCount();
            if (inky.getDotCount() >= levelConfig.getPenLeavingLimits()[2]) {
                inky.setFreeToLeavePen(true);
            }
        } else if (clyde.getMode() == GhostMode.IN_PEN
                || clyde.getMode() == GhostMode.EATEN) {
            clyde.incrementDotCount();
            if (clyde.getDotCount() >= levelConfig.getPenLeavingLimits()[3]) {
                clyde.setFreeToLeavePen(true);
            }
        }
    }

    private void resetForcePenLeaveTime() {
        forcePenLeaveTime = levelConfig.getPenForceTime() * GameConstants.DEFAULT_FPS;
    }

    public void dotEaten(int[] dotPos) {
        getPlayfieldEl().decrementDotsRemaining();
        getPlayfieldEl().incrementDotsEaten();
        getPacman().changeSpeed(CurrentSpeed.PACMAN_EATING_DOT);
        soundManager.playDotEatingSound(gameplayMode == GameplayMode.ORDINARY_PLAYING);
        if (getPathElement(dotPos[1], dotPos[0]).getDot() == Dot.ENERGIZER) { // when eating an energizer
            switchMainGhostMode(GhostMode.FRIGHTENED, false);
            addToScore(50);
        } else { // when eating a normal food
            addToScore(10);
        }

        getPlayfieldEl().clearDot(dotPos[1], dotPos[0]);
        updateCruiseElroySpeed();
        resetForcePenLeaveTime();
        figureOutPenLeaving();
        if (getPlayfieldEl().getDotsEaten() == 70
                || getPlayfieldEl().getDotsEaten() == 170) {
            showFruit();
        }
        if (getPlayfieldEl().getDotsRemaining() == 0) {
            finishLevel();
        }
        playAmbientSound();
    }

    public PathElement getPathElement(int x, int y) {
        return getPlayfieldEl().getPathElement(x, y);
    }

    public int getDotsRemaining() {
        return getPlayfieldEl().getDotsRemaining();
    }

    private void hideFruit() {
        fruitShown = false;
        getFruitEl().hide();
    }

    private void showFruit() {
        fruitShown = true;
        getFruitEl().show();
        fruitTime =
            (int) timing[15]
                + (int) ((timing[16] - timing[15]) * rand());
    }

    public void eatFruit() {
        if (fruitShown) {
            soundManager.playTrack("fruit", 0);
            fruitShown = false;
            getFruitEl().eaten();
            fruitTime = (int) timing[14];
            addToScore(levelConfig.getFruitScore());
        }
    }

    private void updateActorTargetPositions() {
        Ghost[] ghosts = getGhosts();
        for (Ghost ghost : ghosts) {
            ghost.updateTargetPos();
        }
    }

    private void moveActors() {
        getPacman().move();
        Ghost[] ghosts = getGhosts();
        for (PlayfieldActor actor : ghosts) {
            actor.move();
        }
    }

    private void ghostDies(int index) {
        soundManager.playTrack("eating_ghost", 0);
        addToScore(200 * modeScoreMultiplier);
        modeScoreMultiplier *= 2;
        ghostBeingEaten = getPlayfieldEl().getGhosts()[index];
        changeGameplayMode(GameplayMode.GHOST_DIED);
    }

    private void pacmanDies() {
        changeGameplayMode(GameplayMode.PLAYER_DYING);
    }

    private void detectCollisions() {
        tilesChanged = false;
        Pacman pacman = getPacman();
        Ghost[] ghosts = getGhosts();
        for (int i = 0; i < 4; i++) {
            if (ghosts[i].getTilePos()[0] == pacman.getTilePos()[0]
                    && ghosts[i].getTilePos()[1] == pacman.getTilePos()[1]) {
                if (ghosts[i].getMode() == GhostMode.FRIGHTENED) {
                    ghostDies(i);
                    return;
                } else if (ghosts[i].getMode() != GhostMode.EATEN
                        && ghosts[i].getMode() != GhostMode.IN_PEN
                        && ghosts[i].getMode() != GhostMode.LEAVING_PEN
                        && ghosts[i].getMode() != GhostMode.RE_LEAVING_FROM_PEN
                        && ghosts[i].getMode() != GhostMode.ENTERING_PEN) {
                    pacmanDies();
                }
            }
        }
    }

    public void updateCruiseElroySpeed() {
        double speed = levelConfig.getGhostSpeed() * 0.8f;
        if (!lostLifeOnThisLevel || getClyde().getMode() != GhostMode.IN_PEN) {
            LevelConfig c = levelConfig;
            if (getPlayfieldEl().getDotsRemaining() < c.getElroyDotsLeftPart2()) {
                speed = c.getElroySpeedPart2() * 0.8f;
            } else if (getPlayfieldEl().getDotsRemaining() < c.getElroyDotsLeftPart1()) {
                speed = c.getElroySpeedPart1() * 0.8f;
            }
        }
        if (speed != cruiseElroySpeed) {
            cruiseElroySpeed = speed;
            getBlinky().changeSpeed(); // update the speed of Blinky.
        }
    }

    // argument:speed
    // return value: an array of boolean using intervalTime as its index.
    //               an element of this array represents whether it is necessary to move the character in a intervalTime.
    // ex) speed: 0.64 -> return value: [false, true, false, true, false, true, ...]
    public Boolean[] getSpeedIntervals(double speed) {
        Double key = Double.valueOf(speed);
        if (!speedIntervals.containsKey(key)) {
            double distance = 0;
            double lastPos = 0;
            List<Boolean> movabilityTimeTable = new ArrayList<Boolean>();
            for (int i = 0; i < GameConstants.DEFAULT_FPS; i++) {
                distance += speed;
                double pos = Math.floor(distance);
                if (pos > lastPos) {
                    movabilityTimeTable.add(true);
                    lastPos = pos;
                } else {
                    movabilityTimeTable.add(false);
                }
            }
            speedIntervals.put(key, movabilityTimeTable.toArray(new Boolean[0]));
        }
        return speedIntervals.get(key);
    }

    private void finishLevel() {
        changeGameplayMode(GameplayMode.LEVEL_BEING_COMPLETED);
    }

    private void changeGameplayMode(GameplayMode mode) {
        gameplayMode = mode;
        if (mode != GameplayMode.CUTSCENE) {
            getPacman().updateAppearance();

            Ghost[] ghosts = getGhosts();
            for (PlayfieldActor actor : ghosts) {
                actor.updateAppearance();
            }
        }

        switch (mode) {
        case ORDINARY_PLAYING:
            playAmbientSound();
            break;
        case PLAYER_DYING:
            soundManager.stopAll();
            gameplayModeTime = timing[3];
            break;
        case PLAYER_DIED:
            soundManager.playTrack("death", 0);
            gameplayModeTime = timing[4];
            break;
        case GAME_RESTARTING:
            canvasEl.setVisibility(false);
            gameplayModeTime = timing[5];
            break;
        case GAME_RESTARTED:
            soundManager.stopAll();
            canvasEl.setVisibility(true);
            getDoorEl().setVisibility(true);
            createReadyElement();
            gameplayModeTime = timing[6];
            break;
        case NEWGAME_STARTING:
            getDoorEl().setVisibility(true);
            createReadyElement();
            gameplayModeTime = timing[7];
            soundManager.stopAll();
            soundManager.playTrack("start_music", 0, true);
            break;
        case NEWGAME_STARTED:
            lives--;
            updateChromeLives();
            gameplayModeTime = timing[8];
            break;
        case GAMEOVER:
        case KILL_SCREEN:
            removeReadyElement();
            soundManager.stopAll();
            createGameOverElement();
            gameplayModeTime = timing[9];
            break;
        case LEVEL_BEING_COMPLETED:
            soundManager.stopAll();
            gameplayModeTime = timing[10];
            break;
        case LEVEL_COMPLETED:
            getDoorEl().setVisibility(false);
            gameplayModeTime = timing[11];
            break;
        case TRANSITION_INTO_NEXT_SCENE:
            canvasEl.setVisibility(false);
            gameplayModeTime = timing[12];
            break;
        case GHOST_DIED:
            gameplayModeTime = timing[2];
            break;
        case CUTSCENE:
            startCutscene();
            break;
        }
    }

    void toggleSound() {
        if (soundManager.isPacManSound()) {
            soundManager.stopAll();
            soundManager.setPacManSound(false);
        } else {
            soundManager.setPacManSound(true);
            playAmbientSound();
        }
    }

    private void updateSoundIcon() {
        Sound soundEl = getSoundEl();
        if (soundManager.isAvailable()) {
            soundEl.setVisibility(true);
            if (soundManager.isPacManSound()) {
                soundEl.turnOn();
            } else {
                soundEl.turnOff();
            }
        } else {
            soundEl.setVisibility(false);
        }
    }

    private void startCutscene() {
        getPlayfieldEl().setVisibility(false);
        
        canvasEl.setVisibility(true);
        canvasEl.showChrome(false);
        canvasEl.createCutsceneField();
        
        cutscene = CUTSCENES.get(Integer.valueOf(cutsceneId));
        cutsceneSequenceId = -1;
        frightModeTime = levelConfig.getFrightTotalTime();
        createCutsceneActors();
        
        cutsceneNextSequence();
        soundManager.stopAll();
        soundManager.playCutsceneAmbient();
    }
    
    private void createCutsceneActors() {
        getCutsceneFieldEl().createActors(this, cutsceneId, cutscene.actors);
    }

    private void stopCutscene() {
        soundManager.stopCutsceneAmbient();
        getPlayfieldEl().setVisibility(true);
        canvasEl.removeCutsceneField();
        canvasEl.showChrome(true);
        if (debugCutsceneMode) {
            debugCutsceneMode = false;
            if (onDebugCutsceneFinished != null) {
                onDebugCutsceneFinished.run();
            }
        } else {
            newLevel(false);
        }
    }

    private void cutsceneNextSequence() {
        cutsceneSequenceId++;
        if (cutscene.sequenceTimes.length == cutsceneSequenceId) {
            stopCutscene();
        } else {
            cutsceneTime = cutscene.sequenceTimes[cutsceneSequenceId] * GameConstants.DEFAULT_FPS;
            CutsceneActor[] cutsceneActors = getCutsceneActors();
            for (int i = 0; i < cutsceneActors.length; i++) {
                CutsceneActor actor = cutsceneActors[i];
                actor.setupSequence();
                actor.updateAppearance();
            }
        }
    }

    private void checkCutscene() {
        if (cutsceneTime <= 0) {
            cutsceneNextSequence();
        }
    }

    private void advanceCutscene() {
        CutsceneActor[] cutsceneActors = getCutsceneActors();
        for (CutsceneActor actor : cutsceneActors) {
            actor.move();
        }
        cutsceneTime--;
    }

    private void updateActorPositions() {
        getPacman().updateElPos();
        Ghost[] ghosts = getGhosts();
        for (PlayfieldActor actor : ghosts) {
            actor.updateElPos();
        }
    }

    private void blinkEnergizers() {
        getPlayfieldEl().blinkEnergizers(gameplayMode, globalTime, timing[0]);
    }

    private void blinkScoreLabels() {
        getScoreLabelEl().update(gameplayMode, globalTime, timing[17]);
    }

    private void finishFrightMode() {
        switchMainGhostMode(lastMainGhostMode, false);
    }

    private void handleGameplayModeTimer() {
        if (gameplayModeTime != 0) {
            gameplayModeTime--;
            switch (gameplayMode) {
            case PLAYER_DYING:
            case PLAYER_DIED:
                getPacman().updateAppearance();
                Ghost[] ghosts = getGhosts();
                for (PlayfieldActor actor : ghosts) {
                    actor.updateAppearance();
                }
                break;
            case LEVEL_COMPLETED:
                getPlayfieldEl().blink(gameplayModeTime, timing[11]);
                break;
            }

            if (gameplayModeTime <= 0) {
                gameplayModeTime = 0;
                Ghost[] ghosts = getGhosts();
                switch (gameplayMode) {
                case GHOST_DIED:
                    changeGameplayMode(GameplayMode.ORDINARY_PLAYING);
                    ghostEyesCount++;
                    playAmbientSound();
                    ghostBeingEaten.resetDisplayOrder();
                    ghostBeingEaten.switchGhostMode(GhostMode.EATEN);
                    // If there is no ghost frightened, finish fright mode.
                    boolean frightenedGhostExists = false;
                    for (Ghost ghost : ghosts) {
                        if (ghost.getMode() == GhostMode.FRIGHTENED
                            || (ghost.getMode() == GhostMode.IN_PEN
                                    || ghost.getMode() == GhostMode.RE_LEAVING_FROM_PEN)
                                && !ghost.isEatenInThisFrightMode()) {
                            frightenedGhostExists = true;
                            break;
                        }
                    }
                    if (!frightenedGhostExists) {
                        finishFrightMode();
                    }
                    break;
                case PLAYER_DYING:
                    changeGameplayMode(GameplayMode.PLAYER_DIED);
                    break;
                case PLAYER_DIED:
                    newLife();
                    break;
                case NEWGAME_STARTING:
                    changeGameplayMode(GameplayMode.NEWGAME_STARTED);
                    break;
                case GAME_RESTARTING:
                    changeGameplayMode(GameplayMode.GAME_RESTARTED);
                    break;
                case GAME_RESTARTED:
                case NEWGAME_STARTED:
                    getPlayfieldEl().removeReady();
                    changeGameplayMode(GameplayMode.ORDINARY_PLAYING);
                    break;
                case GAMEOVER:
                    getPlayfieldEl().removeGameover();
                    break;
                case LEVEL_BEING_COMPLETED:
                    changeGameplayMode(GameplayMode.LEVEL_COMPLETED);
                    break;
                case LEVEL_COMPLETED:
                    changeGameplayMode(GameplayMode.TRANSITION_INTO_NEXT_SCENE);
                    break;
                case TRANSITION_INTO_NEXT_SCENE:
                    if (levelConfig.getCutsceneId() != 0) {
                        cutsceneId = levelConfig.getCutsceneId();
                        changeGameplayMode(GameplayMode.CUTSCENE);
                    } else {
                        // canvasEl.style.visibility = "";
                        canvasEl.setVisibility(true);
                        newLevel(false);
                    }
                    break;
                }
            }
        }
    }

    private void handleFruitTimer() {
        if (fruitTime != 0) {
            fruitTime--;
            if (fruitTime <= 0)
                hideFruit();
        }
    }

    private void handleGhostModeTimer() {
        if (frightModeTime != 0) {
            frightModeTime--;
            if (frightModeTime <= 0) {
                frightModeTime = 0;
                finishFrightMode();
            }
        } else if (ghostModeTime > 0) {
            ghostModeTime--;
            if (ghostModeTime <= 0) {
                ghostModeTime = 0;
                ghostModeSwitchPos++;
                if (ghostModeSwitchPos < levelConfig.getGhostModeSwitchTimes().length) {
                    ghostModeTime = levelConfig.getGhostModeSwitchTimes()[ghostModeSwitchPos] * GameConstants.DEFAULT_FPS;
                    switch (mainGhostMode) {
                    case SCATTER:
                        switchMainGhostMode(GhostMode.CHASE, false);
                        break;
                    case CHASE:
                        switchMainGhostMode(GhostMode.SCATTER, false);
                        break;
                    }
                }
            }
        }
    }

    private void handleForcePenLeaveTimer() {
        if (forcePenLeaveTime != 0) {
            forcePenLeaveTime--;
            if (forcePenLeaveTime <= 0) {
                Ghost[] ghosts = getGhosts();
                for (int i = 1; i <= 3; i++) {
                    if (ghosts[i].getMode() == GhostMode.IN_PEN) {
                        ghosts[i].setFreeToLeavePen(true);
                        break;
                    }
                }

                resetForcePenLeaveTime();
            }
        }
    }

    private void handleTimers() {
        if (gameplayMode == GameplayMode.ORDINARY_PLAYING) {
            handleForcePenLeaveTimer();
            handleFruitTimer();
            handleGhostModeTimer();
        }
        handleGameplayModeTimer();
    }

    void tick() {
        long now = new Date().getTime();
        if (paused) {
            tickClock.onPaused(now);
            return;
        }

        int latencyMultiplyer = tickClock.advance(now);
        if (gameplayMode == GameplayMode.CUTSCENE) { // Cutscene
            for (int i = 0; i < tickClock.tickMultiplier + latencyMultiplyer; i++) {
                // run multiple time depending on the tickMultiplier and latency
                advanceCutscene();
                intervalTime = (intervalTime + 1) % GameConstants.DEFAULT_FPS;
                globalTime++;
            }
            checkCutscene();
            blinkScoreLabels();
        } else {
            updateSoundIcon();

            for (int i = 0; i < tickClock.tickMultiplier + latencyMultiplyer; i++) {
                // run multiple time depending on the tickMultiplier and latency
                inputHandler.processDpadInput();
                moveActors();
                if (gameplayMode == GameplayMode.ORDINARY_PLAYING) {
                    if (tilesChanged) {
                        detectCollisions();
                        updateActorTargetPositions();
                    }
                }

                globalTime++;
                intervalTime = (intervalTime + 1) % GameConstants.DEFAULT_FPS;
                blinkEnergizers();
                blinkScoreLabels();
                handleTimers();
            }
        }
        setTimeout();
    }

    private void extraLife() {
        soundManager.playTrack("extra_life", 0);
        extraLifeAwarded = true;
        lives++;
        if (lives > 5) {
            lives = 5;
        }
        updateChromeLives();
    }

    private void addToScore(int s) {
        score += s;
        if (!extraLifeAwarded && score > 10000) {
            extraLife();
        }
        updateChromeScore();
    }

    private void updateChrome() {
        updateChromeLevel();
        updateChromeLives();
        updateChromeScore();
    }

    private void updateChromeLives() {
        getLivesEl().update(lives);
    }

    private void updateChromeLevel() {
        getLevelEl().update(level, LevelConfig.LEVEL_CONFIGS);
    }

    private void updateChromeScore() {
        getScoreEl().update(score);
    }

    private void createChrome() {
        canvasEl.reset();
        canvasEl.createScoreLabel();
        canvasEl.createScore();
        canvasEl.createLives();
        canvasEl.createLevel();
        canvasEl.createSoundIcon();
        updateSoundIcon();
    }

    public void playAmbientSound() {
        String ambient = null;
        if (gameplayMode == GameplayMode.ORDINARY_PLAYING
                || gameplayMode == GameplayMode.GHOST_DIED) {
            Playfield playfieldEl = getPlayfieldEl();
            ambient = ghostEyesCount != 0
                    ? "ambient_eyes"
                    : mainGhostMode == GhostMode.FRIGHTENED
                        ? "ambient_fright"
                        : playfieldEl.getDotsEaten() > 241
                            ? "ambient_4"
                            : playfieldEl.getDotsEaten() > 207
                                ? "ambient_3"
                                : playfieldEl.getDotsEaten() > 138
                                    ? "ambient_2" : "ambient_1";
        }
        soundManager.playAmbient(ambient);
    }

    private void initTiming() {
        timing = new double[GameConstants.EVENT_TIME_TABLE.length];
        for (int i = 0; i < GameConstants.EVENT_TIME_TABLE.length; i++) {
            double sec = !soundManager.isPacManSound() && (i == 7 || i == 8)
                    ? 1 : GameConstants.EVENT_TIME_TABLE[i];
            timing[i] = Math.round(sec * GameConstants.DEFAULT_FPS);
        }
    }

    private void setTimeout() {
        view.redrawHandler.sleep(tickClock.getTickIntervalMs());
    }

    private void createCanvasElement() {
        canvasEl = new PacmanCanvas(sourceImage);
        canvasEl.init();
    }

    private void start() {
        started = true;
        setTimeout();
        newGame();
    }

    void startNewGame() {
        setDefaultKillScreenLevel();
        start();
    }

    void showKillScreen() {
        setKillScreenLevel(1);
        start();
    }

    void showCutscene(int id) {
        debugCutsceneId = id;
        debugCutsceneMode = true;
        setDefaultKillScreenLevel();
        start();
    }

    void setOnDebugCutsceneFinished(Runnable callback) {
        this.onDebugCutsceneFinished = callback;
    }

    private void setKillScreenLevel(int level) {
        this.killScreenLevel = level;
    }

    private void setDefaultKillScreenLevel() {
        this.killScreenLevel = DEFAULT_KILL_SCREEN_LEVEL;
    }

    void init() {
        sourceImage =
            BitmapFactory.decodeResource(
                context.getResources(),
                R.drawable.pacman_sprite);
        createCanvasElement();
        speedIntervals = new HashMap<Double, Boolean[]>();
        soundManager = new SoundManager(context);
        inputHandler = new InputHandler(this);
        tickClock = new TickClock();
        tickClock.init();
        initTiming();
    }

    void resume() {
        soundManager.reinit();
        if (started && paused) {
            tickClock.onResumed();
            paused = false;
            soundManager.resumeAmbient();
            tick();
        }
    }

    void pause() {
        paused = true;
        soundManager.destroy();
    }
    
    public PacmanCanvas getCanvasEl() {
        return canvasEl;
    }

    private ScoreLabel getScoreLabelEl() {
        if (canvasEl == null) {
            return null;
        }
        return canvasEl.getScoreLabel();
    }

    private Score getScoreEl() {
        if (canvasEl == null) {
            return null;
        }
        return canvasEl.getScore();
    }

    Sound getSoundEl() {
        if (canvasEl == null) {
            return null;
        }
        return canvasEl.getSound();
    }

    private Lives getLivesEl() {
        if (canvasEl == null) {
            return null;
        }
        return canvasEl.getLives();
    }
    
    private Level getLevelEl() {
        if (canvasEl == null) {
            return null;
        }
        return canvasEl.getLevel();
    }

    public Playfield getPlayfieldEl() {
        if (canvasEl == null) {
            return null;
        }
        return canvasEl.getPlayfield();
    }

    public Pacman getPacman() {
        if (canvasEl == null) {
            return null;
        }
        
        Playfield playfieldEl = canvasEl.getPlayfield();
        if (playfieldEl == null) {
            return null;
        }
        
        return playfieldEl.getPacman();
    }

    private Ghost[] getGhosts() {
        if (canvasEl == null) {
            return null;
        }

        Playfield playfieldEl = canvasEl.getPlayfield();
        if (playfieldEl == null) {
            return null;
        }
        return playfieldEl.getGhosts();
    }
    
    public Ghost getBlinky() {
        return getPlayfieldEl().getBlinky();
    }
    
    public Ghost getPinky() {
        return getPlayfieldEl().getPinky();
    }
    
    public Ghost getInky() {
        return getPlayfieldEl().getInky();
    }

    public Ghost getClyde() {
        return getPlayfieldEl().getClyde();
    }
    
    private Fruit getFruitEl() {
        Playfield playfieldEl = canvasEl.getPlayfield();
        if (playfieldEl == null) {
            return null;
        }
        return playfieldEl.getFruit();
    }

    private Door getDoorEl() {
        Playfield playfieldEl = canvasEl.getPlayfield();
        if (playfieldEl == null) {
            return null;
        }
        return playfieldEl.getDoor();
    }

    private CutsceneField getCutsceneFieldEl() {
        if (canvasEl == null) {
            return null;
        }
        return canvasEl.getCutsceneField();
    }
    
    private CutsceneActor[] getCutsceneActors() {
        if (canvasEl == null) {
            return null;
        }
        CutsceneField cutsceneFieldEl = canvasEl.getCutsceneField();
        if (cutsceneFieldEl == null) {
            return null;
        }
        return cutsceneFieldEl.getActors();
    }

    public boolean isPacManSound() {
        return soundManager.isPacManSound();
    }

    public void setPacManSound(boolean val) {
        soundManager.setPacManSound(val);
    }

    public LevelConfig getLevelConfig() {
        return levelConfig;
    }

    public long getGlobalTime() {
        return globalTime;
    }

    public int getFrightModeTime() {
        return frightModeTime;
    }

    public int getIntervalTime() {
        return intervalTime;
    }

    public double getGameplayModeTime() {
        return gameplayModeTime;
    }

    public double[] getTiming() {
        return timing;
    }

    public GameplayMode getGameplayMode() {
        return gameplayMode;
    }

    public GhostMode getMainGhostMode() {
        return mainGhostMode;
    }

    public GhostMode getLastMainGhostMode() {
        return lastMainGhostMode;
    }

    public Ghost getGhostBeingEaten() {
        return ghostBeingEaten;
    }

    public int getModeScoreMultiplier() {
        return modeScoreMultiplier;
    }

    public boolean isLostLifeOnThisLevel() {
        return lostLifeOnThisLevel;
    }

    public boolean isGhostExitingPenNow() {
        return ghostExitingPenNow;
    }

    public boolean setGhostExitingPenNow(boolean ghostExitingPenNow) {
        return this.ghostExitingPenNow = ghostExitingPenNow;
    }

    public int getGhostEyesCount() {
        return ghostEyesCount;
    }

    public void incrementGhostEyesCount() {
        ghostEyesCount++;
    }

    public void decrementGhostEyesCount() {
        ghostEyesCount--;
    }

    public boolean isTilesChanged() {
        return tilesChanged;
    }

    public int getCutsceneId() {
        return cutsceneId;
    }

    public int getCutsceneSequenceId() {
        return cutsceneSequenceId;
    }

    public double getCutsceneTime() {
        return cutsceneTime;
    }

    public void setTilesChanged(boolean tilesChanged) {
        this.tilesChanged = tilesChanged;
    }

    public double getCruiseElroySpeed() {
        return cruiseElroySpeed;
    }
    
    public static double getFieldX(double x) {
        return x + -32;
    }

    public static double getFieldY(double y) {
        return y + 0;
    }
}
