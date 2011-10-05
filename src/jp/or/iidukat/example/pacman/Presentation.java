package jp.or.iidukat.example.pacman;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

public class Presentation {
    private String id;
    private int index;
    private int width;
    private int height;
    private float left;
    private float top;
    private float bgPosX = Float.NaN;
    private float bgPosY = Float.NaN;
    private int bgColor;
    private boolean visibility = true;
    private Rect src = new Rect();
    private RectF dest = new RectF();
    private Paint paint = new Paint();
    private Presentation parent;
    
    public boolean hasBackground() {
        return !Float.isNaN(bgPosX) && !Float.isNaN(bgPosY);
    }
    
    public void drawBitmap(Bitmap sourceImage, Canvas c) {
        float top = 0;
        float left = 0;
        Presentation p = this;
        do {
            top += p.top;
            left += p.left;
        } while ((p = p.parent) != null);

        // TODO: floatをintに変更して問題ないかどうか検討すること
        src.set(
            Math.round(bgPosX),
            Math.round(bgPosY),
            Math.round(bgPosX + width),
            Math.round(bgPosY + height));
        dest.set(
                left,
                top,
                left + width,
                top + height);
        c.drawBitmap(sourceImage, src, dest, null);
    }
    
    public void drawRectShape(Canvas c) {
        float top = 0;
        float left = 0;
        Presentation p = this;
        do {
            top += p.top;
            left += p.left;
        } while ((p = p.parent) != null);

        dest.set(
                left,
                top,
                left + width,
                top + height);
        
        paint.setColor(bgColor);
        paint.setAlpha(0xff);
        
        c.drawRect(dest, paint);
    }

    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public float getLeft() {
        return left;
    }

    public void setLeft(float left) {
        this.left = left;
    }

    public float getTop() {
        return top;
    }

    public void setTop(float top) {
        this.top = top;
    }

    public float getBgPosX() {
        return bgPosX;
    }

    public void setBgPosX(float bgPosX) {
        this.bgPosX = bgPosX;
    }

    public float getBgPosY() {
        return bgPosY;
    }

    public void setBgPosY(float bgPosY) {
        this.bgPosY = bgPosY;
    }

    public int getBgColor() {
        return bgColor;
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
    }

    public boolean isVisible() {
        return visibility;
    }

    public void setVisibility(boolean visibility) {
        this.visibility = visibility;
    }

    public Rect getSrc() {
        return src;
    }

    public void setSrc(Rect src) {
        this.src = src;
    }

    public RectF getDest() {
        return dest;
    }

    public void setDest(RectF dest) {
        this.dest = dest;
    }

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    public Presentation getParent() {
        return parent;
    }

    public void setParent(Presentation parent) {
        this.parent = parent;
    }
    
    

}
