package jp.or.iidukat.example.pacman.entity;

import jp.or.iidukat.example.pacman.Direction;
import jp.or.iidukat.example.pacman.Direction.Move;
import jp.or.iidukat.example.pacman.PacmanGame;
import jp.or.iidukat.example.pacman.PacmanGame.GameplayMode;
import jp.or.iidukat.example.pacman.entity.Playfield.PathElement;
import jp.or.iidukat.example.pacman.entity.Playfield.PathElement.Dot;
import android.graphics.Bitmap;
import android.util.FloatMath;

public abstract class PlayfieldActor extends Actor {

    public static enum CurrentSpeed {
        NONE(-1), NORMAL(0), PACMAN_EATING_DOT(1), PASSING_TUNNEL(2);
        
        private final int mode;
        
        private CurrentSpeed(int mode) {
            this.mode = mode;
        }

        public int getMode() {
            return mode;
        }
    }
    
    static class InitPosition {
        final float x;
        final float y;
        final Direction dir;
        final float scatterX;
        final float scatterY;

        InitPosition(float x, float y, Direction dir) {
            this(x, y, dir, 0, 0);
        }
        
        InitPosition(float x, float y, Direction dir, float scatterX, float scatterY) {
            this.x = x;
            this.y = y;
            this.dir = dir;
            this.scatterX = scatterX;
            this.scatterY = scatterY;
        }
        
        static InitPosition createPlayerInitPosition(float x, float y, Direction dir) {
            return new InitPosition(x, y, dir);
        }
        
        static InitPosition createGhostInitPosition(
                                                float x,
                                                float y,
                                                Direction dir,
                                                float scatterX,
                                                float scatterY) {
            return new InitPosition(x, y, dir, scatterX, scatterY);
        }
    }

    int[] tilePos;
    int[] lastGoodTilePos;
    Direction lastActiveDir = Direction.NONE;
    float physicalSpeed;
    Direction nextDir = Direction.NONE;
    CurrentSpeed currentSpeed = CurrentSpeed.NONE;
    float fullSpeed;
    float tunnelSpeed;
    Boolean[] speedIntervals;

    PlayfieldActor(Bitmap sourceImage, PacmanGame game) {
        super(sourceImage, game);
    }
    
    public abstract void arrange();
    
    abstract InitPosition getInitPosition();
    
    // be invoked when the difference between tilePos and pos is significant.
    final void enteringTile(int[] tilePos) {
        game.setTilesChanged(true);
        adjustPosOnEnteringTile(tilePos);
        reverseOnEnteringTile();

        this.lastGoodTilePos = new int[] { tilePos[0], tilePos[1] };

        // passing the tunnel(currentSpeed:2) or the others(currentSpeed:0)
        if (game.getPathElement(tilePos[1], tilePos[0]).isTunnel()) {
            if (canChangeSpeedInTunnel()) {
                this.changeSpeed(CurrentSpeed.PASSING_TUNNEL);
            }
        } else {
            this.changeSpeed(CurrentSpeed.NORMAL);
        }

        if (game.getPathElement(tilePos[1], tilePos[0]).getDot() != Dot.NONE) {
            encounterDot(tilePos);
        }

        this.tilePos[0] = tilePos[0];
        this.tilePos[1] = tilePos[1];
    }

    abstract void adjustPosOnEnteringTile(int[] tilePos);
    abstract void reverseOnEnteringTile();
    abstract boolean canChangeSpeedInTunnel();
    abstract void encounterDot(int[] tilePos);
    
    // be invoked when pos's value is equal to tilePos (pos is a multiple of 8)
    final void enteredTile() {
        warpIfPossible();
        handleAnObjectWhenEncountering();
        decideNextDirOnEnteredTile(); 
        PathElement p =
            game.getPathElement((int) this.pos[1], (int) this.pos[0]);
        if (p.isIntersection()) // at either dead end or intersection
            if (this.nextDir != Direction.NONE
                    && p.allow(this.nextDir)) { // enable to move towards the direction which nextDir shows
                if (this.dir != Direction.NONE) {
                    this.lastActiveDir = this.dir;
                }
                this.dir = this.nextDir;
                this.nextDir = Direction.NONE;
                if (supportShortcut()) {
                    shortcutCorner();
                }
            } else if (!p.allow(this.dir)) { // stop if neither nextDir nor dir shows a movable direction.
                if (this.dir != Direction.NONE) {
                    this.lastActiveDir = this.dir;
                }
                this.nextDir = this.dir = Direction.NONE;
                changeSpeed(CurrentSpeed.NORMAL);
            }
    }
    
    abstract void decideNextDirOnEnteredTile(); 
    abstract void shortcutCorner();

    final void warpIfPossible() {
        if (this.pos[0] == Playfield.TUNNEL_POS[0].getY() * 8
                && this.pos[1] == Playfield.TUNNEL_POS[0].getX() * 8) { // warp from left to right
            this.pos[0] = Playfield.TUNNEL_POS[1].getY() * 8;
            this.pos[1] = (Playfield.TUNNEL_POS[1].getX() - 1) * 8;
        } else if (this.pos[0] == Playfield.TUNNEL_POS[1].getY() * 8
                    && this.pos[1] == Playfield.TUNNEL_POS[1].getX() * 8) { // warp from right to left
            this.pos[0] = Playfield.TUNNEL_POS[0].getY() * 8;
            this.pos[1] = (Playfield.TUNNEL_POS[0].getX() + 1) * 8;
        }
    }
    
    abstract void handleAnObjectWhenEncountering();
    
    public final void changeSpeed(CurrentSpeed speed) {
        this.currentSpeed = speed;
        this.changeSpeed();
    }
    
    public abstract void changeSpeed();
    
    // move when the actor doesn't follow routine
    final void step() {
        if (this.dir == Direction.NONE
            || !this.speedIntervals[game.getIntervalTime()]) {
            return;
        }

        Move mv = this.dir.getMove();
        this.pos[mv.getAxis()] += mv.getIncrement();
        
        float imaginaryTileY = this.pos[0] / 8;
        float imaginaryTileX = this.pos[1] / 8;
        int[] nextTile = { Math.round(imaginaryTileY) * 8,
                            Math.round(imaginaryTileX) * 8 };
        if (nextTile[0] != this.tilePos[0]
                || nextTile[1] != this.tilePos[1]) { // the actor is entering into a tile.
            enteringTile(nextTile);
        } else {
            float[] tile = { FloatMath.floor(imaginaryTileY) * 8,
                                FloatMath.floor(imaginaryTileX) * 8 };
            if (this.pos[1] == tile[1]
                    && this.pos[0] == tile[0]) { // the actor has entered into a tile.
                enteredTile(); 
            }
        }
        
        if (supportShortcut()) {
            PathElement path = game.getPathElement(nextTile[1], nextTile[0]);
            if (this.nextDir != Direction.NONE
                    && path.isIntersection()
                    && path.allow(this.nextDir)) {
                prepareShortcut();
            }
        }
        
        this.updateAppearance();
    }
    
    abstract boolean supportShortcut();
    abstract void prepareShortcut();

    @Override
    final boolean canAppear() {
        return game.getGameplayMode() != GameplayMode.GAMEOVER
                && game.getGameplayMode() != GameplayMode.KILL_SCREEN;
    }
    
    public final int[] getTilePos() {
        return tilePos;
    }

    public final void setFullSpeed(float fullSpeed) {
        this.fullSpeed = fullSpeed;
    }

    public final void setTunnelSpeed(float tunnelSpeed) {
        this.tunnelSpeed = tunnelSpeed;
    }

    public final void resetDisplayOrder() {
        getAppearance().setOrder(DEFAULT_DISPLAY_ORDER);
    }
    
}
