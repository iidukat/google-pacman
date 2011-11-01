package jp.or.iidukat.example.pacman.entity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

public interface Entity extends Comparable<Entity> {

    int getHeight();
    int getWidth();
    float[] getAbsolutePos();
    Entity getParent();
    void setParent(Entity parent);
    boolean isVisible();
    void setVisibility(boolean visibility);
    Appearance getAppearance();
    void draw(Canvas canvas);
    boolean addChild(Entity child);
    boolean removeChild(Entity child);
    void clearChildren();
    
    public interface Appearance {
        int getHeight();
        void setHeight(int height);
        int getWidth();
        void setWidth(int width);
        float[] getAbsolutePos();
        float getTop();
        void setTop(float top);
        float getTopOffset();
        void setTopOffset(float topOffset);
        float getLeft();
        void setLeft(float left);
        float getLeftOffset();
        void setLeftOffset(float leftOffset);
        float getBgPosX();
        void setBgPosX(float bgPosX);
        float getBgPosY();
        void setBgPosY(float bgPosY);
        void prepareBkPos(int x, int y);
        void changeBkPos(int x, int y, boolean correction);
        int getBgColor();
        void setBgColor(int bgColor);
        Paint getPaint();
        void setPaint(Paint paint);
        Rect getSrc();
        void setSrc(Rect src);
        RectF getDest();
        void setDest(RectF dest);
        Appearance getParent();
        boolean isVisible();
        void setVisibility(boolean visibility);
        int getOrder();
        void setOrder(int order);
        Bitmap getSourceImage();
        void drawBitmap(Canvas canvas);
        void drawRectShape(Canvas canvas);
    }
}
