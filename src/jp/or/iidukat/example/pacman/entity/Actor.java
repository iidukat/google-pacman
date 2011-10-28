package jp.or.iidukat.example.pacman.entity;

import jp.or.iidukat.example.pacman.Direction;
import jp.or.iidukat.example.pacman.Direction.Move;
import jp.or.iidukat.example.pacman.PacmanGame;
import jp.or.iidukat.example.pacman.PacmanGame.GameplayMode;
import jp.or.iidukat.example.pacman.entity.Playfield.PathElement;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.FloatMath;

public abstract class Actor extends BaseEntity {

    static final int DEFAULT_DISPLAY_ORDER = 110;
    
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

    final PacmanGame game;
    float[] pos;
    int[] tilePos;
    int[] lastGoodTilePos;
    private float[] elPos;
    private int[] elBackgroundPos;
    Direction dir = Direction.NONE;
    Direction lastActiveDir = Direction.NONE;
    float speed;
    float physicalSpeed;
    Direction nextDir = Direction.NONE;
    CurrentSpeed currentSpeed = CurrentSpeed.NONE;
    float fullSpeed;
    float tunnelSpeed;
    Boolean[] speedIntervals;

    public Actor(Bitmap sourceImage, PacmanGame game) {
        super(sourceImage);
        this.game = game;
    }
    
    // Actorを再配置
    public abstract void arrange();
    
    abstract InitPosition getInitPosition();
    
    public void init() {
        Presentation p = getPresentation();
        p.setWidth(16);
        p.setHeight(16);
        p.setTopOffset(-4);
        p.setLeftOffset(-4);
        p.prepareBkPos(0, 0);
        p.setOrder(DEFAULT_DISPLAY_ORDER);
        
        this.elPos = new float[] {0, 0};
        this.elBackgroundPos = new int[] {0, 0};
    }
    
    // tilePosとposの差分が有意になったとき呼び出される
    abstract void enteringTile(int[] b);
    // posの値がtilePosと一致(pos が8の倍数)したときに呼び出される
    abstract void enteredTile();
    
    abstract void lookForSomething();
    
    // Actorの速度設定変更
    public void changeSpeed(CurrentSpeed b) {
        this.currentSpeed = b;
        this.changeSpeed();
    }
    
    // Actorの速度設定(currentSpeedプロパティを利用)
    public abstract void changeSpeed();
    
    // Actorの移動(ルーチン以外)
    void step() {
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
                || nextTile[1] != this.tilePos[1]) { // tileが切り替わる
            enteringTile(nextTile);
        } else {
            float[] tile = { FloatMath.floor(imaginaryTileY) * 8,
                                FloatMath.floor(imaginaryTileX) * 8 };
            if (this.pos[1] == tile[1]
                    && this.pos[0] == tile[0]) { // tileが切り替わった直後
                enteredTile(); 
            }
        }
        
        if (supportShortcut()) {
            PathElement path = game.getPathElement(nextTile[1], nextTile[0]);
            if (this.nextDir != Direction.NONE
                    && path.isIntersection()
                    && path.allow(this.nextDir)) {
                shortcutCorner();
            }
        }
        
        this.updatePresentation();
    }
    
    abstract boolean supportShortcut();
    abstract void shortcutCorner();

    public abstract void move();
    
    // Actor表示画像切り替え(アニメーション対応)&表示位置更新
    public void updatePresentation() {
        this.updateElPos(); //位置移動 
        int[] b = { 0, 0 };
        b = game.getGameplayMode() == GameplayMode.GAMEOVER
            || game.getGameplayMode() == GameplayMode.KILL_SCREEN
                ? new int[] { 0, 3 }
                : getImagePos();
        if (this.elBackgroundPos[0] != b[0] || this.elBackgroundPos[1] != b[1]) {
            this.elBackgroundPos[0] = b[0];
            this.elBackgroundPos[1] = b[1];
            b[0] *= 16;
            b[1] *= 16;
            getPresentation().changeBkPos(b[1], b[0], true);
        }
    }
    
    // 表示位置更新
    public void updateElPos() {
        float b = getFieldX();
        float c = getFieldY();
        if (this.elPos[0] != c || this.elPos[1] != b) {
            this.elPos[0] = c;
            this.elPos[1] = b;
            Presentation el = getPresentation();
            el.setLeft(b);
            el.setTop(c);
        }
    }
    
    abstract int[] getImagePos();
    
    @Override
    void doDraw(Canvas canvas) {
        getPresentation().drawBitmap(canvas);
    }

    public int[] getTilePos() {
        return tilePos;
    }

    public abstract float getFieldX();
    public abstract float getFieldY();
    
    public float[] getPos() {
        return pos;
    }

    public void setPos(float[] pos) {
        this.pos = pos;
    }

    public float[] getElPos() {
        return elPos;
    }

    public void setElPos(float[] elPos) {
        this.elPos = elPos;
    }

    public int[] getElBackgroundPos() {
        return elBackgroundPos;
    }

    public void setElBackgroundPos(int[] elBackgroundPos) {
        this.elBackgroundPos = elBackgroundPos;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }
    
    public float getFullSpeed() {
        return fullSpeed;
    }

    public void setFullSpeed(float fullSpeed) {
        this.fullSpeed = fullSpeed;
    }

    public float getTunnelSpeed() {
        return tunnelSpeed;
    }

    public void setTunnelSpeed(float tunnelSpeed) {
        this.tunnelSpeed = tunnelSpeed;
    }

    public Direction getDir() {
        return dir;
    }

    public void setDir(Direction dir) {
        this.dir = dir;
    }
    
    public void resetDisplayOrder() {
        getPresentation().setOrder(DEFAULT_DISPLAY_ORDER);
    }
}
