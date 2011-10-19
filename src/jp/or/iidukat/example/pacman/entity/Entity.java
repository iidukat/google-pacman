package jp.or.iidukat.example.pacman.entity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

public interface Entity extends Comparable<Entity>{

    float getTop();
    float getLeft();
    boolean isVisible();
    void setVisibility(boolean visibility);
    int getDisplayOrder();
    void draw(Canvas canvas);
    
    public static class Presentation {
        private int id;
        private int width;
        private int height;
        private float left;
        private float top;
        private float leftOffset;
        private float topOffset;
        private float bgPosX = Float.NaN;
        private float bgPosY = Float.NaN;
        private int bgColor;
        private boolean visibility = true;
        private Rect src = new Rect();
        private RectF dest = new RectF();
        private Paint paint = new Paint();
        private int order;
        private Presentation parent;
        private Bitmap sourceImage;
        
        Presentation() {
        }
        
        Presentation(Bitmap sourceImage) {
            this.sourceImage = sourceImage;
        }

        public boolean hasBackground() {
            return !Float.isNaN(bgPosX) && !Float.isNaN(bgPosY);
        }
        
        public void drawBitmap(Canvas c) {
            float top = 0;
            float left = 0;
            Presentation p = this;
            do {
                top += p.getTop();
                left += p.getLeft();
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
                top += p.getTop();
                left += p.getLeft();
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

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
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
            return left + leftOffset;
        }

        public void setLeft(float left) {
            this.left = left;
        }
        
        public float getLeftOffset() {
            return leftOffset;
        }
        
        public void setLeftOffset(float leftOffset) {
            this.leftOffset = leftOffset;
        }

        public float getTop() {
            return top + topOffset;
        }

        public void setTop(float top) {
            this.top = top;
        }
        
        public float getTopOffset() {
            return topOffset;
        }
        
        public void setTopOffset(float topOffset) {
            this.topOffset = topOffset;
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

        public int getOrder() {
            return order;
        }
        
        public void setOrder(int order) {
            this.order = order;
        }
        
        public Presentation getParent() {
            return parent;
        }

        public void setParent(Presentation parent) {
            this.parent = parent;
        }
    }
}
