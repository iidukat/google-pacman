package jp.or.iidukat.example.pacman.entity;

import static jp.or.iidukat.example.pacman.Direction.Move;

import jp.or.iidukat.example.pacman.Direction;
import jp.or.iidukat.example.pacman.PacmanGame;
import jp.or.iidukat.example.pacman.entity.CutsceneActor.Cutscene.StartPoint;
import android.graphics.Bitmap;
import android.graphics.Canvas;

public abstract class CutsceneActor extends BaseEntity {

    static final int DEFAULT_DISPLAY_ORDER = 110;
    
    final PacmanGame g;
    private float[] pos;
    private float[] posDelta;
    private float[] elPos;
    private int[] elBackgroundPos;
    private Direction dir = Direction.NONE;
    private float speed;

    CutsceneActor(Bitmap sourceImage, PacmanGame g) {
        super(sourceImage);
        this.g = g;
    }

    public void init() {
        Presentation p = getPresentation();
        p.setWidth(16);
        p.setHeight(16);
        p.setLeftOffset(-4);
        p.setTopOffset(-4);
        p.prepareBkPos(0, 0);
        p.setOrder(DEFAULT_DISPLAY_ORDER);
        
        elBackgroundPos = new int[] { 0, 0 };
        elPos = new float[] { 0, 0 };
        float[] start = getStartPoint(getCutscene());
        pos = new float[] { start[0] * 8, start[1] * 8 };
        posDelta = new float[] { 0, 0 };
    }

    float[] getStartPoint(Cutscene cutscene) {
        StartPoint p = cutscene.startPoint;
        return new float[] { p.y, p.x };
    }

    @Override
    void doDraw(Canvas c) {
        getPresentation().drawBitmap(c);
    }

    public void move() {
        Move d = dir.getMove();
        pos[d.getAxis()] += d.getIncrement() * speed;
        b();
    }

    // Actor表示画像切り替え(アニメーション対応)&位置移動
    public void b() {
        this.k(); // 位置移動
        int[] b = { 0, 0 };
        b = getImagePos();
        if (this.elBackgroundPos[0] != b[0] || this.elBackgroundPos[1] != b[1]) {
            this.elBackgroundPos[0] = b[0];
            this.elBackgroundPos[1] = b[1];
            b[0] *= 16;
            b[1] *= 16;
            getPresentation().changeBkPos(b[1], b[0], true);
        }
    }

    // 位置移動
    public void k() {
        float b = CutsceneField.getFieldX(this.pos[1] + this.posDelta[1]);
        float c = CutsceneField.getFieldY(this.pos[0] + this.posDelta[0]);
        if (this.elPos[0] != c || this.elPos[1] != b) {
            this.elPos[0] = c;
            this.elPos[1] = b;
            Presentation el = getPresentation();
            el.setLeft(b);
            el.setTop(c);
        }
    }

    int[] getImagePos() {
        return new int[] { 0, 3 };
    }

    public float[] getPos() {
        return pos;
    }

    public void setPos(float[] pos) {
        this.pos = pos;
    }

    public float[] getPosDelta() {
        return posDelta;
    }

    public void setPosDelta(float[] posDelta) {
        this.posDelta = posDelta;
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

    public Direction getDir() {
        return dir;
    }

    public void setDir(Direction dir) {
        this.dir = dir;
    }

    abstract Cutscene getCutscene();

    public void setupSequence() {
        Cutscene c = getCutscene();
        setMove(c);
        setMode(c);
    }

    private void setMove(Cutscene cutscene) {
        dir = cutscene.sequences[g.getCutsceneSequenceId()].move.dir;
        speed = cutscene.sequences[g.getCutsceneSequenceId()].move.speed;
    }

    abstract void setMode(Cutscene cutscene);

    static class Cutscene {
        final StartPoint startPoint;
        final Sequence[] sequences;

        Cutscene(StartPoint startPoint, Sequence[] sequences) {
            this.startPoint = startPoint;
            this.sequences = sequences;
        }

        static class StartPoint {
            final float x;
            final float y;

            StartPoint(float x, float y) {
                this.x = x;
                this.y = y;
            }
        }

        static class Sequence {
            final Move move;

            Sequence(Move move) {
                this.move = move;
            }
        }

        static class Move {
            final Direction dir;
            final float speed;

            Move(Direction dir, float speed) {
                this.dir = dir;
                this.speed = speed;
            }
        }

    }
}
