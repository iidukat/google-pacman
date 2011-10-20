package jp.or.iidukat.example.pacman.entity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

public interface Entity extends Comparable<Entity>{

    int getId();
    void setId(int id);
    float getTop();
    float getLeft();
    float[] getAbsolutePos();
    Entity getParent();
    void setParent(Entity entity);
    boolean isVisible();
    void setVisibility(boolean visibility);
    int getDisplayOrder();
    Presentation getPresentation();
    void draw(Canvas canvas);
    
    public interface Presentation {
        boolean hasBackground();
        void drawBitmap(Canvas c);
        void drawRectShape(Canvas c);
        int getWidth();
        void setWidth(int width);
        int getHeight();
        void setHeight(int height);
        float getLeft();
        void setLeft(float left);
        float getLeftOffset();
        void setLeftOffset(float leftOffset);
        float getTop();
        void setTop(float top);
        float getTopOffset();
        void setTopOffset(float topOffset);
        float getBgPosX();
        void setBgPosX(float bgPosX);
        float getBgPosY();
        void setBgPosY(float bgPosY);
        int getBgColor();
        void setBgColor(int bgColor);
        boolean isVisible();
        void setVisibility(boolean visibility);
        Rect getSrc();
        void setSrc(Rect src);
        RectF getDest();
        void setDest(RectF dest);
        Paint getPaint();
        void setPaint(Paint paint);
        int getOrder();
        void setOrder(int order);
        Presentation getParent();
        float[] getAbsolutePos();
        void prepareBkPos(int x, int y);
        void changeBkPos(int x, int y, boolean f);
        Bitmap getSourceImage();
    }
}
