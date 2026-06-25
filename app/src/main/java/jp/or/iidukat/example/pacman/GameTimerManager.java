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

    int getFrightModeTime() {
        return frightModeTime;
    }

    void setFrightModeTime(int time) {
        frightModeTime = time;
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
        if (game.getGameplayMode() == GameplayMode.ORDINARY_PLAYING) {
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
        if (game.getGameplayModeTime() != 0) {
            game.decrementGameplayModeTime();
            switch (game.getGameplayMode()) {
            case PLAYER_DYING:
            case PLAYER_DIED:
                game.getPacman().updateAppearance();
                Ghost[] ghosts = game.getGhosts();
                for (PlayfieldActor actor : ghosts) {
                    actor.updateAppearance();
                }
                break;
            case LEVEL_COMPLETED:
                game.getPlayfieldEl().blink(game.getGameplayModeTime(), game.getTiming().levelCompleted);
                break;
            }

            if (game.getGameplayModeTime() <= 0) {
                game.resetGameplayModeTime();
                game.onGameplayModeTimerExpired();
            }
        }
    }
}
