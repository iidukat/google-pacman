package jp.or.iidukat.example.pacman.entity;

import jp.or.iidukat.example.pacman.PacmanGame;
import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Fruit extends BaseEntity {
    
    static final int WIDTH = 32;
    static final int HEIGHT = 16;
    static final int WIDTH_ON_LEVEL = 16;
    
    private final int level;
    
    public Fruit(Bitmap sourceImage, int level) {
        super(sourceImage);
        this.level = level;
    }
    
    void initOnPlayfield(int x, int y) {
        Appearance a = getAppearance();
        a.setWidth(WIDTH);
        a.setHeight(HEIGHT);
        a.setLeft(PacmanGame.getFieldX(x));
        a.setTop(PacmanGame.getFieldY(y));
        a.setLeftOffset(-8);
        a.setTopOffset(-4);
        a.prepareBkPos(-32, -16);
        a.setOrder(105);
    }
    
    void initOnLevel(int left) {
        int[] bgPos = getLevelSprite();
        Appearance a = getAppearance();
        a.prepareBkPos(bgPos[0], bgPos[1]);
        a.setWidth(WIDTH_ON_LEVEL);
        a.setHeight(HEIGHT);
        a.setTop(0);
        a.setLeft(left);
        a.setOrder(105);
    }
    
    public void hide() {
        getAppearance().changeBkPos(32, 16, true);
    }

    public void show() {
        int[] bgPos = getSprite();
        getAppearance().changeBkPos(bgPos[0], bgPos[1], true);
    }

    public void eaten() {
        int[] bgPos = getScoreSprite();
        getAppearance().changeBkPos(bgPos[0], bgPos[1], true);
    }
    
    private int[] getSprite() {
        int bgPosX = level <= 4 ? 128 : 160;
        int bgPosY = 128 + 16 * ((level - 1) % 4);
        return new int[] { bgPosX, bgPosY };
    }

    private int[] getLevelSprite() {
        int bgPosX = level <= 4 ? 136 : 168;
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
        getAppearance().drawBitmap(canvas);
    }
}
