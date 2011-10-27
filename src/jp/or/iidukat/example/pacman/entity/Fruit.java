package jp.or.iidukat.example.pacman.entity;

import jp.or.iidukat.example.pacman.PacmanGame;
import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Fruit extends BaseEntity {
    
    static final int WIDTH = 32;
    static final int HEIGHT = 16;
    
    private final int level;
    
    public Fruit(Bitmap sourceImage, int level) {
        super(sourceImage);
        this.level = level;
    }
    
    void initOnPlayfield(int x, int y) {
        Presentation p = getPresentation();
        p.setWidth(WIDTH);
        p.setHeight(HEIGHT);
        p.setLeft(PacmanGame.getFieldX(x));
        p.setTop(PacmanGame.getFieldY(y));
        p.setLeftOffset(-8);
        p.setTopOffset(-4);
        p.prepareBkPos(-32, -16);
        p.setOrder(105);
    }
    
    void initOnLevel(int top) {
        int[] bgPos = getSprite();
        Presentation p = getPresentation();
        p.prepareBkPos(bgPos[0], bgPos[1]);
        p.setWidth(WIDTH);
        p.setHeight(HEIGHT);
        p.setTop(top);
        p.setOrder(105);
    }
    
    public void hide() {
        getPresentation().changeBkPos(32, 16, true);
    }

    public void show() {
        int[] bgPos = getSprite();
        getPresentation().changeBkPos(bgPos[0], bgPos[1], true);
    }

    public void eaten() {
        int[] bgPos = getScoreSprite();
        getPresentation().changeBkPos(bgPos[0], bgPos[1], true);
    }
    
    private int[] getSprite() {
        int bgPosX = level <= 4 ? 128 : 160;
        int bgPosY = 128 + 16 * ((level - 1) % 4);
        return new int[] { bgPosX, bgPosY };
    }

    private int[] getScoreSprite() {
        int bgPosX = 128;
        int bgPosY = 16 * (level - 1);
        return new int[] { bgPosX, bgPosY };
    }    
    
    @Override
    void doDraw(Canvas canvas) {
        getPresentation().drawBitmap(canvas);
    }
}
