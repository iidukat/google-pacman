package jp.or.iidukat.example.pacman;

import jp.or.iidukat.example.pacman.PacmanGame.GameplayMode;
import jp.or.iidukat.example.pacman.entity.Ghost;
import jp.or.iidukat.example.pacman.entity.Ghost.GhostMode;
import jp.or.iidukat.example.pacman.entity.PlayfieldActor;

class GameTimerManager {

    private final PacmanGame game;

    int fruitTime;
    int frightModeTime;
    double ghostModeTime;
    int ghostModeSwitchPos;
    int forcePenLeaveTime;

    GameTimerManager(PacmanGame game) {
        this.game = game;
    }

    void restartAll(LevelConfig levelConfig) {
        frightModeTime = 0;
        fruitTime = 0;
        ghostModeSwitchPos = 0;
        ghostModeTime = levelConfig.getGhostModeSwitchTimes()[0] * GameConstants.DEFAULT_FPS;
        resetForcePenLeaveTime();
    }

    void resetForcePenLeaveTime() {
        forcePenLeaveTime = game.getLevelConfig().getPenForceTime() * GameConstants.DEFAULT_FPS;
    }

    void handleTimers() {
        if (game.gameplayMode == GameplayMode.ORDINARY_PLAYING) {
            handleForcePenLeaveTimer();
            handleFruitTimer();
            handleGhostModeTimer();
        }
        handleGameplayModeTimer();
    }

    private void handleFruitTimer() {
        if (fruitTime != 0) {
            fruitTime--;
            if (fruitTime <= 0)
                game.hideFruit();
        }
    }

    private void handleGhostModeTimer() {
        if (frightModeTime != 0) {
            frightModeTime--;
            if (frightModeTime <= 0) {
                frightModeTime = 0;
                game.finishFrightMode();
            }
        } else if (ghostModeTime > 0) {
            ghostModeTime--;
            if (ghostModeTime <= 0) {
                ghostModeTime = 0;
                ghostModeSwitchPos++;
                if (ghostModeSwitchPos < game.getLevelConfig().getGhostModeSwitchTimes().length) {
                    ghostModeTime = game.getLevelConfig().getGhostModeSwitchTimes()[ghostModeSwitchPos] * GameConstants.DEFAULT_FPS;
                    switch (game.getMainGhostMode()) {
                    case SCATTER:
                        game.switchMainGhostMode(GhostMode.CHASE, false);
                        break;
                    case CHASE:
                        game.switchMainGhostMode(GhostMode.SCATTER, false);
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
                Ghost[] ghosts = game.getGhosts();
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

    private void handleGameplayModeTimer() {
        if (game.gameplayModeTime != 0) {
            game.gameplayModeTime--;
            switch (game.gameplayMode) {
            case PLAYER_DYING:
            case PLAYER_DIED:
                game.getPacman().updateAppearance();
                Ghost[] ghosts = game.getGhosts();
                for (PlayfieldActor actor : ghosts) {
                    actor.updateAppearance();
                }
                break;
            case LEVEL_COMPLETED:
                game.getPlayfieldEl().blink(game.gameplayModeTime, game.timing.levelCompleted);
                break;
            }

            if (game.gameplayModeTime <= 0) {
                game.gameplayModeTime = 0;
                Ghost[] ghosts = game.getGhosts();
                switch (game.gameplayMode) {
                case GHOST_DIED:
                    game.changeGameplayMode(GameplayMode.ORDINARY_PLAYING);
                    game.incrementGhostEyesCount();
                    game.playAmbientSound();
                    game.getGhostBeingEaten().resetDisplayOrder();
                    game.getGhostBeingEaten().switchGhostMode(GhostMode.EATEN);
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
                        game.finishFrightMode();
                    }
                    break;
                case PLAYER_DYING:
                    game.changeGameplayMode(GameplayMode.PLAYER_DIED);
                    break;
                case PLAYER_DIED:
                    game.newLife();
                    break;
                case NEWGAME_STARTING:
                    game.changeGameplayMode(GameplayMode.NEWGAME_STARTED);
                    break;
                case GAME_RESTARTING:
                    game.changeGameplayMode(GameplayMode.GAME_RESTARTED);
                    break;
                case GAME_RESTARTED:
                case NEWGAME_STARTED:
                    game.getPlayfieldEl().removeReady();
                    game.changeGameplayMode(GameplayMode.ORDINARY_PLAYING);
                    break;
                case GAMEOVER:
                    game.getPlayfieldEl().removeGameover();
                    break;
                case LEVEL_BEING_COMPLETED:
                    game.changeGameplayMode(GameplayMode.LEVEL_COMPLETED);
                    break;
                case LEVEL_COMPLETED:
                    game.changeGameplayMode(GameplayMode.TRANSITION_INTO_NEXT_SCENE);
                    break;
                case TRANSITION_INTO_NEXT_SCENE:
                    if (game.getLevelConfig().getCutsceneId() != 0) {
                        game.cutsceneId = game.getLevelConfig().getCutsceneId();
                        game.changeGameplayMode(GameplayMode.CUTSCENE);
                    } else {
                        // canvasEl.style.visibility = "";
                        game.canvasEl.setVisibility(true);
                        game.newLevel(false);
                    }
                    break;
                }
            }
        }
    }
}
