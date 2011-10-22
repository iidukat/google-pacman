package jp.or.iidukat.example.pacman.entity;

import jp.or.iidukat.example.pacman.CurrentSpeed;
import jp.or.iidukat.example.pacman.Direction;
import jp.or.iidukat.example.pacman.GameplayMode;
import jp.or.iidukat.example.pacman.Move;
import jp.or.iidukat.example.pacman.PacmanGame;
import jp.or.iidukat.example.pacman.PathElement;
import android.util.FloatMath;

public abstract class Ghost extends Actor {

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
    boolean eatenInThisFrightMode;
    boolean reverseDirectionsNext;
    private int dotCount;

    public Ghost(int b, PacmanGame g) {
        super(b, g);
    }

    // Actorを再配置
    @Override
    public void A() {
        InitPosition b = getInitPosition();
        this.pos = new float[] {b.y * 8, b.x * 8};
        this.posDelta = new float[] {0, 0};
        this.tilePos = new int[] {(int) b.y * 8, (int) b.x * 8};
        this.targetPos = new float[] {b.scatterY * 8, b.scatterX * 8};
        this.scatterPos = new float[] {b.scatterY * 8, b.scatterX * 8};
        this.lastActiveDir = this.dir = b.dir;
        this.physicalSpeed = 0;
        this.nextDir = Direction.NONE;
        this.c(CurrentSpeed.NORMAL);
        this.reverseDirectionsNext = this.freeToLeavePen = this.modeChangedWhileInPen = this.eatenInThisFrightMode = false;
    }

    public abstract void B();

    // モンスターのモード設定
    public void a(GhostMode b) {
        GhostMode c = this.mode;
        this.mode = b;
        if (this.id == 4
                && (b == GhostMode.IN_PEN || c == GhostMode.IN_PEN))
            g.updateCruiseElroySpeed();
        switch (c) {
        case LEAVING_PEN:
            g.setGhostExitingPenNow(false);
            break;
        case EATEN:
            if (g.getGhostEyesCount() > 0) g.decrementGhostEyesCount();
            if (g.getGhostEyesCount() == 0) g.playAmbientSound();
            break;
        }
        switch (b) {
        case FRIGHTENED:
            this.fullSpeed = g.getLevels().getGhostFrightSpeed() * 0.8f;
            this.tunnelSpeed = g.getLevels().getGhostTunnelSpeed() * 0.8f;
            this.followingRoutine = false;
            break;
        case CHASE:
            this.fullSpeed = g.getLevels().getGhostSpeed() * 0.8f;
            this.tunnelSpeed = g.getLevels().getGhostTunnelSpeed() * 0.8f;
            this.followingRoutine = false;
            break;
        case SCATTER:
            this.targetPos = this.scatterPos;
            this.fullSpeed = g.getLevels().getGhostSpeed() * 0.8f;
            this.tunnelSpeed = g.getLevels().getGhostTunnelSpeed() * 0.8f;
            this.followingRoutine = false;
            break;
        case EATEN:
            this.tunnelSpeed = this.fullSpeed = 1.6f;
            this.targetPos = new float[] {s[0], s[1]};
            this.freeToLeavePen = this.followingRoutine = false;
            break;
        case LEAVING_PEN:
            this.followingRoutine = true;
            this.routineMoveId = -1;
            g.setGhostExitingPenNow(true);
            break;
        case IN_PEN:
        case ENTERING_PEN:
        case RE_LEAVING_FROM_PEN:
            this.followingRoutine = true;
            this.routineMoveId = -1;
            break;
        }
        this.d();
    }

    // Actorの速度設定(currentSpeedプロパティを利用)
    @Override
    public void d() {
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
          this.speedIntervals = g.getSpeedIntervals(this.physicalSpeed);
        }
    }
    
    float getNormalSpeed() {
        return this.fullSpeed;
    }

    // モンスターが交差点/行き止まり にたどり着いたときの動作. nextDirの決定
    //     b: 反転済みフラグ
    void i(boolean b) {
        int[] c = this.tilePos;
        Move d = this.dir.getMove();
        int[] f = new int[] {c[0], c[1]};
        f[d.getAxis()] += d.getIncrement() * 8; // 進行方向へ1マス先取り
        PathElement h = g.getPathElement(f[1], f[0]);
        if (b && !h.isIntersection())
            h = g.getPathElement(c[1], c[0]); // 交差点/行き止まり でなければ現在位置に戻る(反転済みの場合)
        
        if (h.isIntersection())
            switch (this.mode) {
            case SCATTER: // Scatter
            case CHASE: // 追跡
            case EATEN: // プレイヤーに食べられる
                if (!h.getAllowedDir().contains(this.dir)
                        && h.getAllowedDir().contains(this.dir.getOpposite())
                        && h.getAllowedDir().size() == 1)// 反対向きしか通れないなら反対向きを選ぶ
                    this.nextDir = this.dir.getOpposite();
                else { // 反対向き以外を選択可能なら、目的地に最も近い方向を選択する
                    float max = 99999999999f;
                    float distance = 0;
                    Direction nDir = Direction.NONE;
                    for (Direction k : Direction.getAllMoves()) {
                        if (h.getAllowedDir().contains(k) && this.dir != k.getOpposite()) {
                            d = k.getMove();
                            float[] x = new float[] {(float) f[0], (float) f[1]};
                            x[d.getAxis()] += d.getIncrement();
                            distance = PlayField.getDistance(x, new float[] {this.targetPos[0], this.targetPos[1]});
                            if (distance < max) {
                                max = distance;
                                nDir = k;
                            }
                        }
                    }
                    if (nDir != Direction.NONE) this.nextDir = nDir;
                }
                break;
            case FRIGHTENED: // ブルーモード
                if (!h.getAllowedDir().contains(this.dir)
                        && h.getAllowedDir().contains(this.dir.getOpposite())
                        && h.getAllowedDir().size() == 1) // 反対向きしか通れないなら反対向きを選ぶ
                    this.nextDir = this.dir.getOpposite();
                else { // 移動可能な方向のうち反対向き以外を選択
                    Direction nDir = Direction.NONE;
                    do nDir = Direction.getAllMoves().get((int) FloatMath.floor(g.rand() * Direction.getAllMoves().size()));
                    while (!h.getAllowedDir().contains(nDir)
                                || nDir == this.dir.getOpposite());
                    this.nextDir = nDir;
                }
                break;
          }
    }

    // モンスターの巣の中/巣から出る挙動を管理(モンスター個別のモード管理)
    void v() {
        this.routineMoveId++;
        if (this.routineMoveId == getMovesInPen().length) // ルーチンの最後に到達
            if (this.mode == GhostMode.IN_PEN && this.freeToLeavePen && !g.isGhostExitingPenNow()) { // 外に出る条件が満たされた
                if (this.eatenInThisFrightMode) this.a(GhostMode.RE_LEAVING_FROM_PEN);
                else this.a(GhostMode.LEAVING_PEN);
                return;
            } else if (this.mode == GhostMode.LEAVING_PEN
                        || this.mode == GhostMode.RE_LEAVING_FROM_PEN) { // 将に外に出むとす
                this.pos = new float[] { s[0], s[1] + 4 };
                this.dir = this.modeChangedWhileInPen ? Direction.RIGHT : Direction.LEFT;
                GhostMode b = g.getMainGhostMode();
                if (this.mode == GhostMode.RE_LEAVING_FROM_PEN
                        && b == GhostMode.FRIGHTENED)
                    b = g.getLastMainGhostMode();
                this.a(b);
                return;
            } else if (this.mode == GhostMode.ENTERING_PEN) { // 食べられて巣に入る
                if (this instanceof Blinky || this.freeToLeavePen)
                    this.a(GhostMode.RE_LEAVING_FROM_PEN); // アカベエはすぐに巣から出てくる
                else {
                    this.eatenInThisFrightMode = true;
                    this.a(GhostMode.IN_PEN);
                }
                return;
            } else // 外にでる条件が満たされなければ、ルーチンを繰り返す
                this.routineMoveId = 0;
    
        MoveInPen mv = getMovesInPen()[this.routineMoveId];
        this.pos[0] = mv.y * 8;
        this.pos[1] = mv.x * 8;
        this.dir = mv.dir;
        this.physicalSpeed = 0;
        this.speedIntervals = g.getSpeedIntervals(mv.speed);
        this.proceedToNextRoutineMove = false;
        this.b();
    }
    
    // モンスターの巣の中/巣から出る挙動を管理(表示画像決定&位置移動)
    void m() {
        
        MoveInPen b = null;
        MoveInPen[] mvs = getMovesInPen();
        
        if (0 <= this.routineMoveId && this.routineMoveId < mvs.length)
            b = mvs[this.routineMoveId];
        
        if (b != null)
            if (this.speedIntervals[g.getIntervalTime()]) {
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
                this.b();
            }
    }
    
    MoveInPen[] getMovesInPen() {
        return new MoveInPen[0];
    }
    
    // モンスターの巣の中/巣から出る挙動を管理
    void j() {
        if (this.routineMoveId == -1 || this.proceedToNextRoutineMove)
            this.v();
        
        this.m();
    }

    @Override
    void n() {
        if (this.pos[0] == PlayField.getQ()[0].getY() * 8
                && this.pos[1] == PlayField.getQ()[0].getX() * 8) { // 画面左から右へワープ
            this.pos[0] = PlayField.getQ()[1].getY() * 8;
            this.pos[1] = (PlayField.getQ()[1].getX() - 1) * 8;
        } else if (this.pos[0] == PlayField.getQ()[1].getY() * 8
                    && this.pos[1] == PlayField.getQ()[1].getX() * 8) { // 画面右から左へワープ
            this.pos[0] = PlayField.getQ()[0].getY() * 8;
            this.pos[1] = (PlayField.getQ()[0].getX() + 1) * 8;
        }
        // モンスターが巣に入る
        if (this.mode == GhostMode.EATEN
                && this.pos[0] == s[0]
                && this.pos[1] == s[1])
            this.a(GhostMode.ENTERING_PEN);
    }

    void o() {
        float b = this.pos[0] / 8;
        float c = this.pos[1] / 8;
        int[] d = { Math.round(b) * 8, Math.round(c) * 8};
        if (d[0] != this.tilePos[0] || d[1] != this.tilePos[1]) // tileが切り替わる
            this.p(d); // tilePosの更新
        else {
            float[] tPoses =
                new float[] {
                        FloatMath.floor(b) * 8,
                        FloatMath.floor(c) * 8
                };
            if (this.pos[1] == tPoses[1] && this.pos[0] == tPoses[0])
                this.u(); // posの値がtilePosと一致(pos が8の倍数)
        }
    }
    
    // tilePosとposの差分が有意になったとき呼び出される
    @Override
    void p(int[] b) {
        g.setTilesChanged(true);
        if (this.reverseDirectionsNext) { // 方向を反転する(この判定がtrueになるのはモンスターのみ)
            this.dir = this.dir.getOpposite();
            this.nextDir = Direction.NONE;
            this.reverseDirectionsNext = false;
            this.i(true);
        }

        // モンスターの移動 or プレイヤーがパスであるところへ移動
        this.lastGoodTilePos = new int[] {b[0], b[1]};
    
        // トンネル通過[モンスターが食べられた時以外](currentSpeed:2) or それ以外(currentSpeed:0)
        if (g.getPathElement(b[1], b[0]).isTunnel()
                && this.mode != GhostMode.EATEN)
            this.c(CurrentSpeed.PASSING_TUNNEL);
        else
            this.c(CurrentSpeed.NORMAL);
        
        this.tilePos[0] = b[0];
        this.tilePos[1] = b[1];
    }
    
    // posの値がtilePosと一致(pos が8の倍数)したときに呼び出される
    @Override
    void u() {
        this.n();
        this.i(false); // モンスターの交差点/行き止まりでの進行方向決定
        PathElement b =
            g.getPathElement((int) this.pos[1], (int) this.pos[0]);
        if (b.isIntersection()) // 行き止まり/交差点にて
            if (this.nextDir != Direction.NONE && b.getAllowedDir().contains(this.nextDir)) { // nextDirで指定された方向へ移動可能
                if (this.dir != Direction.NONE) this.lastActiveDir = this.dir;
                this.dir = this.nextDir;
                this.nextDir = Direction.NONE;
            } else if (!b.getAllowedDir().contains(this.dir)) { // nextDirもdirも移動不可だったら、停止
                if (this.dir != Direction.NONE) this.lastActiveDir = this.dir;
                this.nextDir = this.dir = Direction.NONE;
                this.c(CurrentSpeed.NORMAL);
            }
    }
    
    // モンスターの表示画像決定
    @Override
    int[] getImagePos() {
        int b = 0;
        int c = 0;
        if (g.getGameplayMode() == GameplayMode.LEVEL_COMPLETED
                || g.getGameplayMode() == GameplayMode.NEWGAME_STARTING
                || g.getGameplayMode() == GameplayMode.PLAYER_DIED) {
            // Pacman or Ms.Pacmanが死んだ直後。モンスターの姿は消える 
            b = 3;
            c = 0;
        } else if (g.getGameplayMode() == GameplayMode.GHOST_DIED
                        && this.id == g.getGhostBeingEatenId()) {
            switch (g.getModeScoreMultiplier()) {// モンスターが食べられたときに表示させるスコアを決定
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
//              this.el.className = "pcm-ac pcm-n"
        } else if (this.mode == GhostMode.FRIGHTENED
                      || (this.mode == GhostMode.IN_PEN || this.mode == GhostMode.LEAVING_PEN)
                          && g.getMainGhostMode() == GhostMode.FRIGHTENED
                          && !this.eatenInThisFrightMode) {
            // ブルーモード.ただし、食べられてはいない
            b = 0;
            c = 8;
            // ブルーモード時間切れ間近の青白明滅
            if (g.getFrightModeTime() < g.getLevels().getFrightTotalTime() - g.getLevels().getFrightTime()
                    && FloatMath.floor(g.getFrightModeTime() / g.getTiming()[1]) % 2 == 0)
                b += 2;
    
            b += (int) (Math.floor(g.getGlobalTime() / 16) % 2); // ブルーモードの画像切り替え
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
                || g.getPathElement(this.tilePos[1], this.tilePos[0]).isTunnel()) {
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
            if (this.speed > 0 || g.getGameplayMode() != GameplayMode.CUTSCENE)
                b += (int) (Math.floor(g.getGlobalTime() / 16) % 2);
        }
        return new int[] { c, b };
    }
    
    abstract int getOrdinaryImageRow();

    @Override
    public void move() {
        if (g.getGameplayMode() == GameplayMode.ORDINARY_PLAYING
                || g.getGameplayMode() == GameplayMode.GHOST_DIED
                    && (this.mode == GhostMode.EATEN
                            || this.mode == GhostMode.ENTERING_PEN)) {
            if (this.followingRoutine) {
                this.j();
                if (this.mode == GhostMode.ENTERING_PEN) this.j();
            } else {
                this.e();
                if (this.mode == GhostMode.EATEN) this.e();
            }
        }
    }

    public GhostMode getMode() {
        return mode;
    }

    public void setMode(GhostMode mode) {
        this.mode = mode;
    }

    public boolean isReverseDirectionsNext() {
        return reverseDirectionsNext;
    }

    public void setReverseDirectionsNext(boolean reverseDirectionsNext) {
        this.reverseDirectionsNext = reverseDirectionsNext;
    }

    public boolean isEatenInThisFrightMode() {
        return eatenInThisFrightMode;
    }

    public void setEatenInThisFrightMode(boolean eatenInThisFrightMode) {
        this.eatenInThisFrightMode = eatenInThisFrightMode;
    }

    public boolean isFreeToLeavePen() {
        return freeToLeavePen;
    }

    public void setFreeToLeavePen(boolean freeToLeavePen) {
        this.freeToLeavePen = freeToLeavePen;
    }

    public boolean isModeChangedWhileInPen() {
        return modeChangedWhileInPen;
    }

    public void setModeChangedWhileInPen(boolean modeChangedWhileInPen) {
        this.modeChangedWhileInPen = modeChangedWhileInPen;
    }

    public int getDotCount() {
        return dotCount;
    }

    public void setDotCount(int dotCount) {
        this.dotCount = dotCount;
    }
    
    public void incrementDotCount() {
        this.dotCount++;
    }
}
