package jp.or.iidukat.example.pacman.entity;

import jp.or.iidukat.example.pacman.Direction;
import jp.or.iidukat.example.pacman.Direction.Move;
import jp.or.iidukat.example.pacman.PacmanGame;
import jp.or.iidukat.example.pacman.PacmanGame.GameplayMode;
import jp.or.iidukat.example.pacman.entity.Playfield.PathElement;

import android.graphics.Bitmap;
import android.util.FloatMath;

public class Pacman extends PlayfieldActor {

    private static final InitPosition INIT_POS =
        InitPosition.createPlayerInitPosition(39.5f, 15, Direction.LEFT);

    private float[] posDelta;
    Direction requestedDir = Direction.NONE;
    private float dotEatingSpeed;
    
    Pacman(Bitmap sourceImage, PacmanGame game) {
        super(sourceImage, game);
    }

    @Override
    public void arrange() {
        InitPosition p = getInitPosition();
        this.pos = new float[] { p.y * 8, p.x * 8 };
        this.posDelta = new float[] { 0, 0 };
        this.tilePos = new int[] { (int) p.y * 8, (int) p.x * 8 };
        this.lastActiveDir = this.dir = p.dir;
        this.physicalSpeed = 0;
        this.requestedDir = this.nextDir = Direction.NONE;
        this.changeSpeed(CurrentSpeed.NORMAL);
    }

    @Override
    public void changeSpeed() {
        float s = 0;
        switch (this.currentSpeed) {
        case NORMAL:
            s = this.fullSpeed;
            break;
        case PACMAN_EATING_DOT:
            s = this.dotEatingSpeed;
            break;
        case PASSING_TUNNEL:
            s = this.tunnelSpeed;
            break;
        }
        if (this.physicalSpeed != s) {
            this.physicalSpeed = s;
            this.speedIntervals = game.getSpeedIntervals(this.physicalSpeed);
        }
    }
    
    @Override
    public void move() {
        if (game.getGameplayMode() == GameplayMode.ORDINARY_PLAYING) {
            if (this.requestedDir != Direction.NONE) {
                this.handleInput(this.requestedDir);
                this.requestedDir = Direction.NONE;
            }

            this.step();
        }
    }

    private void handleInput(Direction inputDir) {
        if (this.dir == inputDir.getOpposite()) {
            this.dir = inputDir;
            this.posDelta = new float[] { 0, 0 };
            if (this.currentSpeed != CurrentSpeed.PASSING_TUNNEL) {
                this.changeSpeed(CurrentSpeed.NORMAL);
            }
            if (this.dir != Direction.NONE) {
                this.lastActiveDir = this.dir;
            }
            this.nextDir = Direction.NONE;
        } else if (this.dir != inputDir) {
            if (this.dir == Direction.NONE) {
                if (game.getPathElement((int) this.pos[1], (int) this.pos[0])
                        .allow(inputDir)) {
                    this.dir = inputDir;
                }
            } else {
                PathElement p =
                    game.getPathElement(this.tilePos[1], this.tilePos[0]);
                if (p != null && p.allow(inputDir)) { // if an available direction is entered
                    // determine whether the input of direction is slightly delayed.
                    Move mv = this.dir.getMove();
                    float[] pastPos = new float[] { this.pos[0], this.pos[1] };
                    pastPos[mv.getAxis()] -= mv.getIncrement();
                    int stepCount = 0;
                    if (pastPos[0] == this.tilePos[0] && pastPos[1] == this.tilePos[1]) {
                        stepCount = 1;
                    } else {
                        pastPos[mv.getAxis()] -= mv.getIncrement();
                        if (pastPos[0] == this.tilePos[0] && pastPos[1] == this.tilePos[1]) {
                            stepCount = 2;
                        }
                    }
                    if (stepCount != 0) {
                        // the input of direction is slightly delayed,
                        // correct the location according to the new direction.
                        this.dir = inputDir;
                        this.pos[0] = this.tilePos[0];
                        this.pos[1] = this.tilePos[1];
                        mv = this.dir.getMove();
                        this.pos[mv.getAxis()] += mv.getIncrement() * stepCount;
                        return;
                    }
                }
                // prepare for handling with a precede input of direction
                this.nextDir = inputDir;
                this.posDelta = new float[] { 0, 0 };
            }
        }
    }

    @Override
    boolean supportShortcut() {
        return true;
    }
    
    @Override
    void prepareShortcut() {
        handlePrecedeInput();
    }
    
    private void handlePrecedeInput() {
        float[] a;
        float[] b;
        switch (this.dir) {
        case UP:
            a = new float[] { this.tilePos[0], this.tilePos[1] };
            b = new float[] { this.tilePos[0] + 3.6f, this.tilePos[1] };
            break;
        case DOWN:
            a = new float[] { this.tilePos[0] - 4, this.tilePos[1] };
            b = new float[] { this.tilePos[0], this.tilePos[1] };
            break;
        case LEFT:
            a = new float[] { this.tilePos[0], this.tilePos[1] };
            b = new float[] { this.tilePos[0], this.tilePos[1] + 3.6f };
            break;
        case RIGHT:
            a = new float[] { this.tilePos[0], this.tilePos[1] - 4 };
            b = new float[] { this.tilePos[0], this.tilePos[1] };
            break;
        default:
            // set dummy values so that posDelta is prevented from being updated
            a = new float[] { 1, 1 };
            b = new float[] { -1, -1 };
            break;
        }
        if (this.pos[0] >= a[0]
            && this.pos[0] <= b[0]
            && this.pos[1] >= a[1]
            && this.pos[1] <= b[1]) {
            Move mv = this.nextDir.getMove();
            this.posDelta[mv.getAxis()] += mv.getIncrement();
        }
    }
    

    @Override
    void adjustPosOnEnteringTile(int[] tilePos) {
        if (!game.getPathElement(tilePos[1], tilePos[0]).isPath()) { // try moving to where is not a path
            // correct the position to where Pacman has actually moved to the last
            this.pos[0] = this.lastGoodTilePos[0];
            this.pos[1] = this.lastGoodTilePos[1];
            tilePos[0] = this.lastGoodTilePos[0];
            tilePos[1] = this.lastGoodTilePos[1];
            this.dir = Direction.NONE;
        } 
    }
    
    @Override
    void reverseOnEnteringTile() {
    }
    
    @Override
    boolean canChangeSpeedInTunnel() {
        return true;
    }
    
    @Override
    void encounterDot(int[] tilePos) {
        game.dotEaten(tilePos);
    }
    
    @Override
    void decideNextDirOnEnteredTile() {
    }
    
    @Override
    void shortcutCorner() {
        // update the position according to the precede input (see handlePrecedeInput method)
        this.pos[0] += this.posDelta[0];
        this.pos[1] += this.posDelta[1];
        this.posDelta = new float[] { 0, 0 };
    }
    
    @Override
    void handleAnObjectWhenEncountering() {
        // eat a fruit
        if (this.pos[0] == Playfield.FRUIT_POSITION[0]
                && (this.pos[1] == Playfield.FRUIT_POSITION[1]
                    || this.pos[1] == Playfield.FRUIT_POSITION[1] + 8)) {
            game.eatFruit();
        }
    }
    
    // determine the display image of Pacman
    @Override
    int[] getImagePos() {
        int x = 0;
        int y = 0;
        Direction d = this.dir;
        if (d == Direction.NONE) {
            d = this.lastActiveDir;
        }
        if (game.getGameplayMode() == GameplayMode.GHOST_DIED) { // eat a ghost. no image.
            x = 3;
            y = 0;
        } else if (game.getGameplayMode() == GameplayMode.LEVEL_BEING_COMPLETED
                    || game.getGameplayMode() == GameplayMode.LEVEL_COMPLETED) { // complete a level. Pacman is rounded.
            x = 2;
            y = 0;
        } else if (game.getGameplayMode() == GameplayMode.NEWGAME_STARTING
                    || game.getGameplayMode() == GameplayMode.NEWGAME_STARTED
                    || game.getGameplayMode() == GameplayMode.GAME_RESTARTED) { // Immediately after game starts
            x = 2;
            y = 0;
        } else if (game.getGameplayMode() == GameplayMode.PLAYER_DIED) {
            int t = 20 - (int) FloatMath.floor(game.getGameplayModeTime() / game.getTiming()[4] * 21);
            x = t - 1;
            switch (x) {
            case -1:
                x = 0;
                break;
            case 11:
                x = 10;
                break;
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
                x = 11;
                break;
            }
            y = 12;
        } else { // display the image in the ordinary way.
            switch (d) {
            case LEFT:
                y = 0;
                break;
            case RIGHT:
                y = 1;
                break;
            case UP:
                y = 2;
                break;
            case DOWN:
                y = 3;
                break;
            }
            if (game.getGameplayMode() != GameplayMode.PLAYER_DYING) {
                x = (int) (Math.floor(game.getGlobalTime() * 0.3) % 4);
            }
            if (x == 3 && this.dir == Direction.NONE) {
                x = 0;
            }
            if (x == 2) {
                x = 0;
            }
            if (x == 3) {
                x = 2;
                y = 0;
            }
        }
        return new int[] { y, x };
    }
    
    @Override
    InitPosition getInitPosition() {
        return INIT_POS;
    }

    @Override
    public float getFieldX() {
        return PacmanGame.getFieldX(pos[1] + posDelta[1]);
    }
    
    @Override
    public float getFieldY() {
        return PacmanGame.getFieldY(pos[0] + posDelta[0]);
    }
    
    public void setRequestedDir(Direction requestedDir) {
        this.requestedDir = requestedDir;
    }

    public void setDotEatingSpeed(float dotEatingSpeed) {
        this.dotEatingSpeed = dotEatingSpeed;
    }

}
