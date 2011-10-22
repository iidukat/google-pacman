package jp.or.iidukat.example.pacman.entity;

import jp.or.iidukat.example.pacman.CurrentSpeed;
import jp.or.iidukat.example.pacman.Direction;
import jp.or.iidukat.example.pacman.GameplayMode;
import jp.or.iidukat.example.pacman.Move;
import jp.or.iidukat.example.pacman.PacmanGame;
import jp.or.iidukat.example.pacman.PathElement;
import android.util.FloatMath;

public class Pacman extends Actor {

    private static final InitPosition INIT_POS =
        InitPosition.createPlayerInitPosition(39.5f, 15, Direction.LEFT);

    
    Direction requestedDir = Direction.NONE;
    private float dotEatingSpeed;
    
    public Pacman(int b, PacmanGame g) {
        super(b, g);
    }

    // Actorを再配置
    @Override
    public void A() {
        InitPosition b = getInitPosition();
        this.pos = new float[] {b.y * 8, b.x * 8};
        this.posDelta = new float[] {0, 0};
        this.tilePos = new int[] {(int) b.y * 8, (int) b.x * 8};
        this.lastActiveDir = this.dir = b.dir;
        this.physicalSpeed = 0;
        this.requestedDir = this.nextDir = Direction.NONE;
        this.c(CurrentSpeed.NORMAL);
    }
        
    // Actorの速度設定(currentSpeedプロパティを利用)
    @Override
    public void d() {
        float b = 0;
        switch (this.currentSpeed) {
        case NORMAL:
            b = this.fullSpeed;
            break;
        case PACMAN_EATING_DOT:
            b = this.dotEatingSpeed;
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
        
        // プレイヤーがフルーツを食べる
        if (this.pos[0] == PlayField.getV()[0]
                && (this.pos[1] == PlayField.getV()[1]
                    || this.pos[1] == PlayField.getV()[1] + 8))
            g.eatFruit();
    }

    @Override
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
        PathElement pe = g.getPathElement(d[1], d[0]);
        if (this.nextDir != Direction.NONE
                && pe.isIntersection()
                && pe.getAllowedDir().contains(this.nextDir))
                this.t();
    }
    
    // tilePosとposの差分が有意になったとき呼び出される
    @Override
    void p(int[] b) {
        g.setTilesChanged(true);
        if (!g.getPathElement(b[1], b[0]).isPath()) { // プレイヤーがパスでないところへ移動しようとする
            // 最後に正常に移動成功した位置に補正
            this.pos[0] = this.lastGoodTilePos[0];
            this.pos[1] = this.lastGoodTilePos[1];
            b[0] = this.lastGoodTilePos[0];
            b[1] = this.lastGoodTilePos[1];
            this.dir = Direction.NONE;
        } else // モンスターの移動 or プレイヤーがパスであるところへ移動
            this.lastGoodTilePos = new int[] {b[0], b[1]};
    
        // トンネル通過[モンスターが食べられた時以外](currentSpeed:2) or それ以外(currentSpeed:0)
        if (g.getPathElement(b[1], b[0]).isTunnel())
            this.c(CurrentSpeed.PASSING_TUNNEL);
        else
            this.c(CurrentSpeed.NORMAL);
        
        // プレイヤーがエサを食べる
        if (g.getPathElement(b[1], b[0]).getDot() != 0)
            g.dotEaten(b);
        
        this.tilePos[0] = b[0];
        this.tilePos[1] = b[1];
    }

    // 先行入力された方向に対応
    void t() {
        int[] b = this.tilePos;
        float[] c;
        float[] d;
        switch (this.dir) {
        case UP:
            c = new float[] { b[0], b[1] };
            d = new float[] { b[0] + 3.6f, b[1] };
            break;
        case DOWN:
            c = new float[] { b[0] - 4, b[1] };
            d = new float[] { b[0], b[1] };
            break;
        case LEFT:
            c = new float[] { b[0], b[1] };
            d = new float[] { b[0], b[1] + 3.6f };
            break;
        case RIGHT:
            c = new float[] { b[0], b[1] - 4 };
            d = new float[] { b[0], b[1] };
            break;
        default:
            // posDeltaの更新が行われないようにダミーの値をセット
            c = new float[] { this.pos[0] + 1, this.pos[1] + 1 };
            d = new float[] { this.pos[0] - 1, this.pos[1] - 1 };
            break;
        }
        if (this.pos[0] >= c[0]
            && this.pos[0] <= d[0]
            && this.pos[1] >= c[1]
            && this.pos[1] <= d[1]) {
            Move dir = this.nextDir.getMove();
            this.posDelta[dir.getAxis()] += dir.getIncrement();
        }
    }

    // posの値がtilePosと一致(pos が8の倍数)したときに呼び出される
    @Override
    void u() {
        this.n();
        PathElement b =
            g.getPathElement((int) this.pos[1], (int) this.pos[0]);
        if (b.isIntersection()) // 行き止まり/交差点にて
            if (this.nextDir != Direction.NONE && b.getAllowedDir().contains(this.nextDir)) { // nextDirで指定された方向へ移動可能
                if (this.dir != Direction.NONE) this.lastActiveDir = this.dir;
                this.dir = this.nextDir;
                this.nextDir = Direction.NONE;
                // 先行入力された移動方向分を更新(メソッドtを参照)
                this.pos[0] += this.posDelta[0];
                this.pos[1] += this.posDelta[1];
                this.posDelta = new float[] {0, 0};
            } else if (!b.getAllowedDir().contains(this.dir)) { // nextDirもdirも移動不可だったら、停止
                if (this.dir != Direction.NONE) this.lastActiveDir = this.dir;
                this.nextDir = this.dir = Direction.NONE;
                this.c(CurrentSpeed.NORMAL);
            }
    }

    // Pacman, Ms.Pacman表示画像決定(アニメーション対応)
    @Override
    int[] getImagePos() {
        int b = 0;
        int c = 0;
        Direction d = this.dir;
        if (d == Direction.NONE) d = this.lastActiveDir;
        if (g.getGameplayMode() == GameplayMode.GHOST_DIED) { // モンスターを食べたとき。画像なし
            b = 3;
            c = 0;
        } else if (g.getGameplayMode() == GameplayMode.LEVEL_BEING_COMPLETED
                            || g.getGameplayMode() == GameplayMode.LEVEL_COMPLETED) { // レベルクリア。Pacmanは丸まる
            b = 2;
            c = 0;
        } else if (g.getGameplayMode() == GameplayMode.NEWGAME_STARTING
                        || g.getGameplayMode() == GameplayMode.NEWGAME_STARTED
                        || g.getGameplayMode() == GameplayMode.GAME_RESTARTED) { // ゲーム開始直後の表示画像決定
            b = 2;
            c = 0;
        } else if (g.getGameplayMode() == GameplayMode.PLAYER_DIED) { // プレイヤーが死んだ時の画像決定.
            int t = 20 - (int) FloatMath.floor(g.getGameplayModeTime() / g.getTiming()[4] * 21);
            b = t - 1;
            switch (b) {
            case -1:
                b = 0;
                break;
            case 11:
                b = 10;
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
                  b = 11;
                  break;
            }
            c = 12;
        } else { // 通常時のプレイヤー画像決定
            switch (d) {
            case LEFT:
                c = 0;
                break;
            case RIGHT:
                c = 1;
                break;
            case UP:
                c = 2;
                break;
            case DOWN:
                c = 3;
                break;
            }
            if (g.getGameplayMode() != GameplayMode.PLAYER_DYING) b = (int) (Math.floor(g.getGlobalTime() * 0.3) % 4);
            if (b == 3 && this.dir == Direction.NONE) b = 0;
            if (b == 2) b = 0;
            if (b == 3) {
                b = 2;
                c = 0;
            }
        }
        return new int[] { c, b };
    }

    // 位置, 速度の決定
    void z(Direction b) {
        if (this.dir == b.getOpposite()) {
            this.dir = b;
            this.posDelta = new float[] {0, 0};
            if (this.currentSpeed != CurrentSpeed.PASSING_TUNNEL) this.c(CurrentSpeed.NORMAL);
            if (this.dir != Direction.NONE) this.lastActiveDir = this.dir;
            this.nextDir = Direction.NONE;
        } else if (this.dir != b)
            if (this.dir == Direction.NONE) {
                if (g.getPathElement((int) this.pos[1], (int) this.pos[0])
                        .getAllowedDir().contains(b))
                    this.dir = b;
            } else {
                PathElement p = g.getPathElement(this.tilePos[1], this.tilePos[0]);
                if (p != null && p.getAllowedDir().contains(b)) { // 移動可能な方向が入力された場合
                    //     遅延ぎみに方向入力されたかどうか判定
                    Move c = this.dir.getMove();
                    float[] d = new float[] {this.pos[0], this.pos[1]};
                    d[c.getAxis()] -= c.getIncrement();
                    int f = 0;
                    if (d[0] == this.tilePos[0] && d[1] == this.tilePos[1]) {
                        f = 1;
                    } else {
                        d[c.getAxis()] -= c.getIncrement();
                        if (d[0] == this.tilePos[0] && d[1] == this.tilePos[1]) {
                            f = 2;
                        }
                    }
                    if (f != 0) { // 遅延ぎみに方向入力された場合、新しい移動方向に応じて位置を補正
                        this.dir = b;
                        this.pos[0] = this.tilePos[0];
                        this.pos[1] = this.tilePos[1];
                        c = this.dir.getMove();
                        this.pos[c.getAxis()] += c.getIncrement() * f;
                        return;
                    }
                }
                // 移動方向の先行入力対応
                this.nextDir = b;
                this.posDelta = new float[] {0, 0};
            }
    }

    @Override
    InitPosition getInitPosition() {
        return INIT_POS;
    }

    @Override
    public void move() {
        if (g.getGameplayMode() == GameplayMode.ORDINARY_PLAYING) {
            if (this.requestedDir != Direction.NONE) {
                this.z(this.requestedDir);
                this.requestedDir = Direction.NONE;
            }
            
            this.e();
        }
    }

    public Direction getRequestedDir() {
        return requestedDir;
    }
    
    public void setRequestedDir(Direction requestedDir) {
        this.requestedDir = requestedDir;
    }

    public float getDotEatingSpeed() {
        return dotEatingSpeed;
    }

    public void setDotEatingSpeed(float dotEatingSpeed) {
        this.dotEatingSpeed = dotEatingSpeed;
    }

}
