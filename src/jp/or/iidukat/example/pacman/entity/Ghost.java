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
    
    static final float EXIT_PEN_SPEED = 0.8f * 0.4f;
    
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

    // Actorを再配置
    @Override
    public final void arrange() {
        InitPosition b = getInitPosition();
        this.pos = new float[] {b.y * 8, b.x * 8};
        this.tilePos = new int[] {(int) b.y * 8, (int) b.x * 8};
        this.targetPos = new float[] {b.scatterY * 8, b.scatterX * 8};
        this.scatterPos = new float[] {b.scatterY * 8, b.scatterX * 8};
        this.lastActiveDir = this.dir = b.dir;
        this.physicalSpeed = 0;
        this.nextDir = Direction.NONE;
        this.changeSpeed(CurrentSpeed.NORMAL);
        this.reverseDirectionsNext = this.freeToLeavePen = this.modeChangedWhileInPen = this.eatenInThisFrightMode = false;
    }

    public abstract void updateTargetPos();

    // モンスターのモード設定
    public final void switchGhostMode(GhostMode mode) {
        GhostMode c = this.mode;
        this.mode = mode;
        if (this == game.getClyde()
                && (mode == GhostMode.IN_PEN || c == GhostMode.IN_PEN))
            game.updateCruiseElroySpeed();
        switch (c) {
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
    
    // Actorの速度設定(currentSpeedフィールドを利用)
    @Override
    public final void changeSpeed() {
        float b = 0;
        switch (this.currentSpeed) {
        case NORMAL:
            b = getNormalSpeed();
            break;
        case PASSING_TUNNEL:
            b = this.tunnelSpeed;
            break;
        }
        if (this.physicalSpeed != b) {
          this.physicalSpeed = b;
          this.speedIntervals = game.getSpeedIntervals(this.physicalSpeed);
        }
    }
    
    float getNormalSpeed() {
        return fullSpeed;
    }    

    // モンスターが交差点/行き止まり にたどり着いたときの動作. nextDirの決定
    //     b: 反転済みフラグ
    private void decideNextDir(boolean reversed) {
        int[] currentTilePos = tilePos;
        Move currentMove = dir.getMove();
        int[] newTilePos = new int[] {currentTilePos[0], currentTilePos[1]};
        newTilePos[currentMove.getAxis()] += currentMove.getIncrement() * 8; // 進行方向へ1マス先取り
        PathElement destination = game.getPathElement(newTilePos[1], newTilePos[0]);
        if (reversed && !destination.isIntersection())
            destination = game.getPathElement(currentTilePos[1], currentTilePos[0]); // 交差点/行き止まり でなければ現在位置に戻る(反転済みの場合)
        
        if (destination.isIntersection())
            switch (mode) {
            case SCATTER: // Scatter
            case CHASE: // 追跡
            case EATEN: // プレイヤーに食べられる
                if (destination.allowOnlyOpposite(dir))// 反対向きしか通れないなら反対向きを選ぶ
                    nextDir = dir.getOpposite();
                else { // 反対向き以外を選択可能なら、目的地に最も近い方向を選択する
                    float max = 99999999999f;
                    float distance = 0;
                    Direction dirCandidate = Direction.NONE;
                    for (Direction d : Direction.getAllMoves()) {
                        if (destination.allow(d) && dir != d.getOpposite()) {
                            float[] tilePosCandidate = new float[] {(float) newTilePos[0], (float) newTilePos[1]};
                            tilePosCandidate[d.getMove().getAxis()] += d.getMove().getIncrement();
                            distance = getDistance(tilePosCandidate, new float[] {targetPos[0], targetPos[1]});
                            if (distance < max) {
                                max = distance;
                                dirCandidate = d;
                            }
                        }
                    }
                    if (dirCandidate != Direction.NONE) nextDir = dirCandidate;
                }
                break;
            case FRIGHTENED: // ブルーモード
                if (destination.allowOnlyOpposite(dir)) // 反対向きしか通れないなら反対向きを選ぶ
                    this.nextDir = dir.getOpposite();
                else { // 移動可能な方向のうち反対向き以外を選択
                    Direction nDir = Direction.NONE;
                    do nDir = Direction.getAllMoves().get((int) FloatMath.floor(game.rand() * Direction.getAllMoves().size()));
                    while (!destination.allow(nDir)
                                || nDir == dir.getOpposite());
                    nextDir = nDir;
                }
                break;
          }
    }

    // モンスターの巣の中/巣から出る挙動を管理(モンスター個別のモード管理)
    private void switchFollowingRoutine() {
        this.routineMoveId++;
        if (this.routineMoveId == getMovesInPen().length) // ルーチンの最後に到達
            if (this.mode == GhostMode.IN_PEN && this.freeToLeavePen && !game.isGhostExitingPenNow()) { // 外に出る条件が満たされた
                if (this.eatenInThisFrightMode) this.switchGhostMode(GhostMode.RE_LEAVING_FROM_PEN);
                else this.switchGhostMode(GhostMode.LEAVING_PEN);
                return;
            } else if (this.mode == GhostMode.LEAVING_PEN
                        || this.mode == GhostMode.RE_LEAVING_FROM_PEN) { // 将に外に出むとす
                this.pos =
                    new float[] {
                        Playfield.PEN_ENTRANCE[0],
                        Playfield.PEN_ENTRANCE[1] + 4
                    };
                this.dir = this.modeChangedWhileInPen ? Direction.RIGHT : Direction.LEFT;
                GhostMode b = game.getMainGhostMode();
                if (this.mode == GhostMode.RE_LEAVING_FROM_PEN
                        && b == GhostMode.FRIGHTENED)
                    b = game.getLastMainGhostMode();
                this.switchGhostMode(b);
                return;
            } else if (this.mode == GhostMode.ENTERING_PEN) { // 食べられて巣に入る
                if (this instanceof Blinky || this.freeToLeavePen)
                    this.switchGhostMode(GhostMode.RE_LEAVING_FROM_PEN); // アカベエはすぐに巣から出てくる
                else {
                    this.eatenInThisFrightMode = true;
                    this.switchGhostMode(GhostMode.IN_PEN);
                }
                return;
            } else // 外にでる条件が満たされなければ、ルーチンを繰り返す
                this.routineMoveId = 0;
    
        MoveInPen mv = getMovesInPen()[this.routineMoveId];
        this.pos[0] = mv.y * 8;
        this.pos[1] = mv.x * 8;
        this.dir = mv.dir;
        this.physicalSpeed = 0;
        this.speedIntervals = game.getSpeedIntervals(mv.speed);
        this.proceedToNextRoutineMove = false;
        this.updateAppearance();
    }
    
    // モンスターの巣の中/巣から出る挙動を管理(表示画像決定&位置移動)
    private void continueFollowingRoutine() {
        
        MoveInPen b = null;
        MoveInPen[] mvs = getMovesInPen();
        
        if (0 <= this.routineMoveId && this.routineMoveId < mvs.length)
            b = mvs[this.routineMoveId];
        
        if (b != null)
            if (this.speedIntervals[game.getIntervalTime()]) {
                Move c = this.dir.getMove();
                this.pos[c.getAxis()] += c.getIncrement();
                switch (this.dir) {
                case UP:
                case LEFT:
                    if (this.pos[c.getAxis()] < b.dest * 8) {
                        this.pos[c.getAxis()] = b.dest * 8;
                        this.proceedToNextRoutineMove = true;
                    }
                    break;
                case DOWN:
                case RIGHT:
                    if (this.pos[c.getAxis()] > b.dest * 8) {
                        this.pos[c.getAxis()] = b.dest * 8;
                        this.proceedToNextRoutineMove = true;
                    }
                    break;
                }
                this.updateAppearance();
            }
    }
    
    abstract MoveInPen[] getMovesInPen();
    
    // モンスターの巣の中/巣から出る挙動を管理
    private void followRoutine() {
        if (this.routineMoveId == -1 || this.proceedToNextRoutineMove)
            this.switchFollowingRoutine();
        
        this.continueFollowingRoutine();
    }

    @Override
    final void handleAnObjectWhenEncountering() {
        // 巣に入る
        if (this.mode == GhostMode.EATEN
                && this.pos[0] == Playfield.PEN_ENTRANCE[0]
                && this.pos[1] == Playfield.PEN_ENTRANCE[1])
            this.switchGhostMode(GhostMode.ENTERING_PEN);
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
        if (this.reverseDirectionsNext) { // 方向を反転する
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
    final void reverseOnEnteredTile() {
        decideNextDir(false); // モンスターの交差点/行き止まりでの進行方向決定
    }
    
    @Override
    final void shortcutCorner() {
    }
    
    // モンスターの表示画像決定
    @Override
    final int[] getImagePos() {
        int b = 0;
        int c = 0;
        if (game.getGameplayMode() == GameplayMode.LEVEL_COMPLETED
                || game.getGameplayMode() == GameplayMode.NEWGAME_STARTING
                || game.getGameplayMode() == GameplayMode.PLAYER_DIED) {
            // Pacman or Ms.Pacmanが死んだ直後。モンスターの姿は消える 
            b = 3;
            c = 0;
        } else if (game.getGameplayMode() == GameplayMode.GHOST_DIED
                        && this== game.getGhostBeingEaten()) {
            switch (game.getModeScoreMultiplier()) {// モンスターが食べられたときに表示させるスコアを決定
            case 2:
                b = 0;
                break;
            case 4:
                b = 1;
                break;
            case 8:
                b = 2;
                break;
            case 16:
                b = 3;
                break;
            }
            c = 11;
            getAppearance().setOrder(111);
        } else if (this.mode == GhostMode.FRIGHTENED
                      || (this.mode == GhostMode.IN_PEN || this.mode == GhostMode.LEAVING_PEN)
                          && game.getMainGhostMode() == GhostMode.FRIGHTENED
                          && !this.eatenInThisFrightMode) {
            // ブルーモード.ただし、食べられてはいない
            b = 0;
            c = 8;
            // ブルーモード時間切れ間近の青白明滅
            if (game.getFrightModeTime() < game.getLevels().getFrightTotalTime() - game.getLevels().getFrightTime()
                    && FloatMath.floor(game.getFrightModeTime() / game.getTiming()[1]) % 2 == 0)
                b += 2;
    
            b += (int) (Math.floor(game.getGlobalTime() / 16) % 2); // ブルーモードの画像切り替え
        } else if (this.mode == GhostMode.EATEN || this.mode == GhostMode.ENTERING_PEN) { // 食べられて目玉だけ
            Direction ndir = this.nextDir;
            if (ndir != Direction.NONE) ndir = this.dir;
            switch (ndir) {
            case LEFT:
                b = 2;
                break;
            case RIGHT:
                b = 3;
                break;
            case UP:
                b = 0;
                break;
            case DOWN:
                b = 1;
                break;
            }
            c = 10;
        } else { // 通常時の画像表示
            Direction ndir = this.nextDir;
            if (ndir == Direction.NONE
                || game.getPathElement(this.tilePos[1], this.tilePos[0]).isTunnel()) {
                ndir = this.dir;
            }
            
            switch (ndir) {
            case LEFT:
                b = 4;
                break;
            case RIGHT:
                b = 6;
                break;
            case UP:
                b = 0;
                break;
            case DOWN:
                b = 2;
                break;
            }
            c = getOrdinaryImageRow();
            if (game.getGameplayMode() != GameplayMode.CUTSCENE)
                b += (int) (Math.floor(game.getGlobalTime() / 16) % 2);
        }
        return new int[] { c, b };
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
    
    static float getDistance(int[] b, int[] c) {
        return FloatMath.sqrt((c[1] - b[1]) * (c[1] - b[1]) + (c[0] - b[0]) * (c[0] - b[0]));
    }

    static float getDistance(float[] b, float[] c) {
        return FloatMath.sqrt((c[1] - b[1]) * (c[1] - b[1]) + (c[0] - b[0]) * (c[0] - b[0]));
    }
    
}
