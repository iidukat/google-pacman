package jp.or.iidukat.example.pacman.entity;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Fruit extends BaseEntity {
    
    private final int level;
    
    public Fruit(Bitmap sourceImage, int level) {
        super(sourceImage);
        this.level = level;
    }
    
    void initOnPlayfield(int[] pos) {
        Presentation p = getPresentation();
        p.setWidth(32);
        p.setHeight(16);
        p.setLeft(Playfield.getPlayfieldX(pos[1]));
        p.setTop(Playfield.getPlayfieldY(pos[0]));
        p.setLeftOffset(-8);
        p.setTopOffset(-4);
        p.prepareBkPos(-32, -16);
    }
    
    void initOnLevel(int top) {
        int[] fs = getSprite();
        Presentation p = getPresentation();
        p.prepareBkPos(fs[0], fs[1]);
        p.setWidth(32);
        p.setHeight(16);
        p.setTop(top);
    }
    
    public void hide() {
        getPresentation().changeBkPos(32, 16, true);
    }

    public void show() {
        int[] b = getSprite();
        getPresentation().changeBkPos(b[0], b[1], true);
    }

    public void eaten() {
        int[] c = getScoreSprite();
        getPresentation().changeBkPos(c[0], c[1], true);
    }
    
    private int[] getSprite() {
        int c = level <= 4 ? 128 : 160;
        int b = 128 + 16 * ((level - 1) % 4);
        return new int[] { c, b };
    }

    private int[] getScoreSprite() {
        int c = 128;
        int b = 16 * (level - 1);
        return new int[] { c, b };
    }    
    
    @Override
    public void draw(Canvas c) {
        if (!isVisible()) return;
    
        getPresentation().drawBitmap(c);
    }
}
