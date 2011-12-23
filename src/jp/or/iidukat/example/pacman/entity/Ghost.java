package jp.or.iidukat.example.pacman.entity;

import jp.or.iidukat.example.pacman.Direction;
import jp.or.iidukat.example.pacman.Direction.Move;
import jp.or.iidukat.example.pacman.PacmanGame;
import jp.or.iidukat.example.pacman.PacmanGame.GameplayMode;
import jp.or.iidukat.example.pacman.entity.Playfield.PathElement;
import android.graphics.Bitmap;
import android.util.FloatMath;

public abstract class Ghost extends PlayfieldActor {

    public static enum GhostMode {
        NONE(0), CHASE(1), SCATTER(2), FRIGHTENED(4), 
        EATEN(8), IN_PEN(16), LEAVING_PEN(32),
        ENTERING_PEN(64), RE_LEAVING_FROM_PEN(128);
        
        private final int mode;
        
        private GhostMode(int mode) {
            this.mode = mode;
        }

        public int getMode() {
            return mode;
        }
    }
    
    static final float LEAVING_PEN_SPEED = 0.8f * 0.4f;
    
    static class MoveInPen {
        final float x;
        final float y;
        final Direction dir;
        final float dest;
        final float speed;
        MoveInPen(float x, float y, Direction dir, float dest, float speed) {
            this.x = x;
            this.y = y;
            this.dir = dir;
            this.dest = dest;
            this.speed = speed;
        }
    }

    GhostMode mode = GhostMode.NONE;
    float[] targetPos;
    float[] scatterPos;
    private boolean followingRoutine;
    private boolean proceedToNextRoutineMove;
    private int routineMoveId;
    private boolean freeToLeavePen;
    private boolean modeChangedWhileInPen;
    private boolean eatenInThisFrightMode;
    private boolean reverseDirectionsNext;
    private int dotCount;

    Ghost(Bitmap sourceImage, PacmanGame game) {
        super(sourceImage, game);
    }

    @Override
    public final void arrange() {
        InitPosition p = getInitPosition();
        this.pos = new float[] {p.y * 8, p.x * 8};
        this.tilePos = new int[] {(int) p.y * 8, (int) p.x * 8};
        this.targetPos = new float[] {p.scatterY * 8, p.scatterX * 8};
        this.scatterPos = new float[] {p.scatterY * 8, p.scatterX * 8};
        this.lastActiveDir = this.dir = p.dir;
        this.physicalSpeed = 0;
        this.nextDir = Direction.NONE;
        this.changeSpeed(CurrentSpeed.NORMAL);
        this.reverseDirectionsNext = this.freeToLeavePen = this.modeChangedWhileInPen = this.eatenInThisFrightMode = false;
    }

    public abstract void updateTargetPos();

    public final void switchGhostMode(GhostMode mode) {
        GhostMode oldMode = this.mode;
        this.mode = mode;
        if (this == game.getClyde()
                && (mode == GhostMode.IN_PEN || oldMode == GhostMode.IN_PEN)) {
            game.updateCruiseElroySpeed();
        }
        switch (oldMode) {
        case LEAVING_PEN:
            game.setGhostExitingPenNow(false);
            break;
        case EATEN:
            if (game.getGhostEyesCount() > 0) game.decrementGhostEyesCount();
            if (game.getGhostEyesCount() == 0) game.playAmbientSound();
            break;
        }
        switch (mode) {
        case FRIGHTENED:
            this.fullSpeed = game.getLevels().getGhostFrightSpeed() * 0.8f;
            this.tunnelSpeed = game.getLevels().getGhostTunnelSpeed() * 0.8f;
            this.followingRoutine = false;
            break;
        case CHASE:
            this.fullSpeed = game.getLevels().getGhostSpeed() * 0.8f;
            this.tunnelSpeed = game.getLevels().getGhostTunnelSpeed() * 0.8f;
            this.followingRoutine = false;
            break;
        case SCATTER:
            this.targetPos = this.scatterPos;
            this.fullSpeed = game.getLevels().getGhostSpeed() * 0.8f;
            this.tunnelSpeed = game.getLevels().getGhostTunnelSpeed() * 0.8f;
            this.followingRoutine = false;
            break;
        case EATEN:
            this.tunnelSpeed = this.fullSpeed = 1.6f;
            this.targetPos =
                    new float[] {
                        Playfield.PEN_ENTRANCE[0],
                        Playfield.PEN_ENTRANCE[1]
                    };
            this.freeToLeavePen = this.followingRoutine = false;
            break;
        case LEAVING_PEN:
            this.followingRoutine = true;
            this.routineMoveId = -1;
            game.setGhostExitingPenNow(true);
            break;
        case IN_PEN:
        case ENTERING_PEN:
        case RE_LEAVING_FROM_PEN:
            this.followingRoutine = true;
            this.routineMoveId = -1;
            break;
        }
        this.changeSpeed();
    }
    
    @Override
    public final void changeSpeed() {
        float s = 0;
        switch (this.currentSpeed) {
        case NORMAL:
            s = getNormalSpeed();
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
    
    float getNormalSpeed() {
        return fullSpeed;
    }    

    private void decideNextDir(boolean reversed) {
        int[] currentTilePos = tilePos;
        Move currentMove = dir.getMove();
        int[] newTilePos = new int[] {currentTilePos[0], currentTilePos[1]};
        newTilePos[currentMove.getAxis()] += currentMove.getIncrement() * 8; // anticipate the next tile by the direction of the progress
        PathElement destination = game.getPathElement(newTilePos[1], newTilePos[0]);
        if (reversed && !destination.isIntersection()) {
            // when the ghost has already reversed its direction and its destination is neither a dead end nor a intersection,
            // it return to the current position.
            destination = game.getPathElement(currentTilePos[1], currentTilePos[0]);
        }

        // The destination of the ghost is a dead end or a intersection.
        if (destination.isIntersection()) {
            switch (mode) {
            case SCATTER:
            case CHASE:
            case EATEN:
                if (destination.allowOnlyOpposite(dir)) {
                    // If the opposite direction is available and other directions is not available,
                    // choose the opposite direction.
                    nextDir = dir.getOpposite();
                } else {
                    // If the ghost can choose some directions except the opposite,
                    // the ghost choose the direction in which it is the closest to its target position.
                    float max = 99999999999f;
                    float distance = 0;
                    Direction dirCandidate = Direction.NONE;
                    for (Direction d : Direction.getAllMoves()) {
                        if (destination.allow(d) && dir != d.getOpposite()) {
                            float[] tilePosCandidate =
                                new float[] {(float) newTilePos[0], (float) newTilePos[1]};
                            tilePosCandidate[d.getMove().getAxis()] += d.getMove().getIncrement();
                            distance = getDistance(
                                            tilePosCandidate,
                                            new float[] {targetPos[0], targetPos[1]});
                            if (distance < max) {
                                max = distance;
                                dirCandidate = d;
                            }
                        }
                    }
                    if (dirCandidate != Direction.NONE) {
                        nextDir = dirCandidate;
                    }
                }
                break;
            case FRIGHTENED:
                if (destination.allowOnlyOpposite(dir)) {
                    // If the opposite direction is available and other directions is not available,
                    // choose the opposite direction.
                    this.nextDir = dir.getOpposite();
                } else {
                    // If the ghost can choose some directions except the opposite,
                    // the ghost randomly choose one of them.
                    Direction nDir = Direction.NONE;
                    do {
                        nDir =
                            Direction.getAllMoves().get(
                                (int) FloatMath.floor(
                                        game.rand() * Direction.getAllMoves().size()));
                    } while (!destination.allow(nDir)
                                || nDir == dir.getOpposite());
                    nextDir = nDir;
                }
                break;
          }
        }
    }

    // when the ghost stays in the pen or goes out of the pen, manage its behaviors
    private void switchFollowingRoutine() {
        this.routineMoveId++;
        if (this.routineMoveId == getMovesInPen().length) { // the end of the routine
            if (this.mode == GhostMode.IN_PEN && this.freeToLeavePen
                    && !game.isGhostExitingPenNow()) { // the conditions of going out are met
                if (this.eatenInThisFrightMode) {
                    this.switchGhostMode(GhostMode.RE_LEAVING_FROM_PEN);
                } else {
                    this.switchGhostMode(GhostMode.LEAVING_PEN);
                }
                return;
            } else if (this.mode == GhostMode.LEAVING_PEN
                        || this.mode == GhostMode.RE_LEAVING_FROM_PEN) { // go out of the pen right now
                this.pos =
                    new float[] {
                        Playfield.PEN_ENTRANCE[0],
                        Playfield.PEN_ENTRANCE[1] + 4
                    };
                this.dir = this.modeChangedWhileInPen ? Direction.RIGHT : Direction.LEFT;
                GhostMode mainMode = game.getMainGhostMode();
                if (this.mode == GhostMode.RE_LEAVING_FROM_PEN
                        && mainMode == GhostMode.FRIGHTENED) {
                    mainMode = game.getLastMainGhostMode();
                }
                this.switchGhostMode(mainMode);
                return;
            } else if (this.mode == GhostMode.ENTERING_PEN) { // After the ghost has been eaten, it enters the pen.
                if (this == game.getBlinky() || this.freeToLeavePen) {
                    this.switchGhostMode(GhostMode.RE_LEAVING_FROM_PEN); // Blinky goes out from the pen soon.
                } else {
                    this.eatenInThisFrightMode = true;
                    this.switchGhostMode(GhostMode.IN_PEN);
                }
                return;
            } else { // If the conditions of going out aren't met, repeat the routines.
                this.routineMoveId = 0;
            }
        }

        MoveInPen mv = getMovesInPen()[this.routineMoveId];
        this.pos[0] = mv.y * 8;
        this.pos[1] = mv.x * 8;
        this.dir = mv.dir;
        this.physicalSpeed = 0;
        this.speedIntervals = game.getSpeedIntervals(mv.speed);
        this.proceedToNextRoutineMove = false;
        this.updateAppearance();
    }
    
    // When the ghost stays in the pen or goes out of the pen, manage its behaviors
    // (switch display image and update the display position)
    private void continueFollowingRoutine() {
        
        MoveInPen mv = null;
        MoveInPen[] mvs = getMovesInPen();
        
        if (0 <= this.routineMoveId && this.routineMoveId < mvs.length) {
            mv = mvs[this.routineMoveId];
        }
        
        if (mv != null) {
            if (this.speedIntervals[game.getIntervalTime()]) {
                Move m = this.dir.getMove();
                this.pos[m.getAxis()] += m.getIncrement();
                switch (this.dir) {
                case UP:
                case LEFT:
                    if (this.pos[m.getAxis()] < mv.dest * 8) {
                        this.pos[m.getAxis()] = mv.dest * 8;
                        this.proceedToNextRoutineMove = true;
                    }
                    break;
                case DOWN:
                case RIGHT:
                    if (this.pos[m.getAxis()] > mv.dest * 8) {
                        this.pos[m.getAxis()] = mv.dest * 8;
                        this.proceedToNextRoutineMove = true;
                    }
                    break;
                }
                this.updateAppearance();
            }
        }
    }
    
    abstract MoveInPen[] getMovesInPen();
    
    // When the ghost stays in the pen or goes out of the pen, manage its behaviors
    private void followRoutine() {
        if (this.routineMoveId == -1 || this.proceedToNextRoutineMove) {
            this.switchFollowingRoutine();
        }
        
        this.continueFollowingRoutine();
    }

    @Override
    final void handleAnObjectWhenEncountering() {
        // enter into the pen
        if (this.mode == GhostMode.EATEN
                && this.pos[0] == Playfield.PEN_ENTRANCE[0]
                && this.pos[1] == Playfield.PEN_ENTRANCE[1]) {
            this.switchGhostMode(GhostMode.ENTERING_PEN);
        }
    }

    @Override
    final boolean supportShortcut() {
        return false;
    }
    
    @Override
    final void prepareShortcut() {
    }
    
    @Override
    final void adjustPosOnEnteringTile(int[] tilePos) {
    }
    
    @Override
    final void reverseOnEnteringTile() {
        if (this.reverseDirectionsNext) { // reverse its direction
            this.dir = this.dir.getOpposite();
            this.nextDir = Direction.NONE;
            this.reverseDirectionsNext = false;
            this.decideNextDir(true);
        }
    }
    
    @Override
    final boolean canChangeSpeedInTunnel() {
        return this.mode != GhostMode.EATEN;
    }
    
    @Override
    final void encounterDot(int[] tilePos) {
    }
    
    @Override
    final void decideNextDirOnEnteredTile() {
        decideNextDir(false);
    }
    
    @Override
    final void shortcutCorner() {
    }
    
    // determine the display image of the ghost
    @Override
    final int[] getImagePos() {
        int x = 0;
        int y = 0;
        if (game.getGameplayMode() == GameplayMode.LEVEL_COMPLETED
                || game.getGameplayMode() == GameplayMode.NEWGAME_STARTING
                || game.getGameplayMode() == GameplayMode.PLAYER_DIED) {
            // Immediately after the player dies, the ghost disappears.
            x = 3;
            y = 0;
        } else if (game.getGameplayMode() == GameplayMode.GHOST_DIED
                        && this== game.getGhostBeingEaten()) {
            // Determine the score to be displayed when the ghost has been eaten
            switch (game.getModeScoreMultiplier()) {
            case 2:
                x = 0;
                break;
            case 4:
                x = 1;
                break;
            case 8:
                x = 2;
                break;
            case 16:
                x = 3;
                break;
            }
            y = 11;
            getAppearance().setOrder(111);
        } else if (this.mode == GhostMode.FRIGHTENED
                      || (this.mode == GhostMode.IN_PEN || this.mode == GhostMode.LEAVING_PEN)
                          && game.getMainGhostMode() == GhostMode.FRIGHTENED
                          && !this.eatenInThisFrightMode) {
            // in the frighten mode, however, not eaten.
            x = 0;
            y = 8;
            // blinking before the end of the frighten mode
            if (game.getFrightModeTime() < game.getLevels().getFrightTotalTime() - game.getLevels().getFrightTime()
                    && FloatMath.floor(game.getFrightModeTime() / game.getTiming()[1]) % 2 == 0) {
                x += 2;
            }
    
            x += (int) (Math.floor(game.getGlobalTime() / 16) % 2); // switch display image in frighten mode.
        } else if (this.mode == GhostMode.EATEN || this.mode == GhostMode.ENTERING_PEN) { // eyes only
            Direction ndir = this.nextDir;
            if (ndir != Direction.NONE) {
                ndir = this.dir;
            }
            switch (ndir) {
            case LEFT:
                x = 2;
                break;
            case RIGHT:
                x = 3;
                break;
            case UP:
                x = 0;
                break;
            case DOWN:
                x = 1;
                break;
            }
            y = 10;
        } else { // display the image in the ordinary way.
            Direction ndir = this.nextDir;
            if (ndir == Direction.NONE
                || game.getPathElement(this.tilePos[1], this.tilePos[0]).isTunnel()) {
                ndir = this.dir;
            }
            
            switch (ndir) {
            case LEFT:
                x = 4;
                break;
            case RIGHT:
                x = 6;
                break;
            case UP:
                x = 0;
                break;
            case DOWN:
                x = 2;
                break;
            }
            y = getOrdinaryImageRow();
            if (game.getGameplayMode() != GameplayMode.CUTSCENE) {
                x += (int) (Math.floor(game.getGlobalTime() / 16) % 2);
            }
        }
        return new int[] { y, x };
    }
    
    abstract int getOrdinaryImageRow();

    @Override
    public final void move() {
        if (game.getGameplayMode() == GameplayMode.ORDINARY_PLAYING
                || game.getGameplayMode() == GameplayMode.GHOST_DIED
                    && (this.mode == GhostMode.EATEN
                            || this.mode == GhostMode.ENTERING_PEN)) {
            if (this.followingRoutine) {
                this.followRoutine();
                if (this.mode == GhostMode.ENTERING_PEN) this.followRoutine();
            } else {
                this.step();
                if (this.mode == GhostMode.EATEN) this.step();
            }
        }
    }

    @Override
    public final float getFieldX() {
        return PacmanGame.getFieldX(pos[1]);
    }
    
    @Override
    public final float getFieldY() {
        return PacmanGame.getFieldY(pos[0]);
    }
    
    public final GhostMode getMode() {
        return mode;
    }

    public final void setReverseDirectionsNext(boolean reverseDirectionsNext) {
        this.reverseDirectionsNext = reverseDirectionsNext;
    }

    public final boolean isEatenInThisFrightMode() {
        return eatenInThisFrightMode;
    }

    public final void setEatenInThisFrightMode(boolean eatenInThisFrightMode) {
        this.eatenInThisFrightMode = eatenInThisFrightMode;
    }

    public final void setFreeToLeavePen(boolean freeToLeavePen) {
        this.freeToLeavePen = freeToLeavePen;
    }

    public final void setModeChangedWhileInPen(boolean modeChangedWhileInPen) {
        this.modeChangedWhileInPen = modeChangedWhileInPen;
    }

    public final int getDotCount() {
        return dotCount;
    }

    public final void incrementDotCount() {
        this.dotCount++;
    }
    
    static float getDistance(int[] p1, int[] p2) {
        return FloatMath.sqrt((p2[1] - p1[1]) * (p2[1] - p1[1]) + (p2[0] - p1[0]) * (p2[0] - p1[0]));
    }

    static float getDistance(float[] p1, float[] p2) {
        return FloatMath.sqrt((p2[1] - p1[1]) * (p2[1] - p1[1]) + (p2[0] - p1[0]) * (p2[0] - p1[0]));
    }
    
}
