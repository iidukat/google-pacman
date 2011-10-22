package jp.or.iidukat.example.pacman.entity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

abstract class BaseEntity implements Entity {
    
    private final Presentation presentation;
    private Entity parent;

    public BaseEntity(Bitmap sourceImage) {
        presentation = new PresentationImpl(sourceImage);
    }
    
    @Override
    public float getTop() {
        return presentation.getTop();
    }

    @Override
    public float getLeft() {
        return presentation.getLeft();
    }
    
    @Override
    public float[] getAbsolutePos() {
    	return presentation.getAbsolutePos();
    }
    
    @Override
    public Entity getParent() {
        return parent;
    }
    
    @Override
    public void setParent(Entity parent) {
        this.parent = parent;
    }
    
    @Override
    public boolean isVisible() {
        return presentation.isVisible();
    }
    
    @Override
    public void setVisibility(boolean visibility) {
        presentation.setVisibility(visibility);
    }
    
    @Override
    public int getDisplayOrder() {
        return presentation.getOrder();
    }
    
    @Override
    public int compareTo(Entity another) {
        int o = getDisplayOrder();
        int ao = another.getDisplayOrder();
        if (o < ao) {
            return -1;
        } else if (o > ao) {
            return 1;
        } else {
            float t = getTop();
            float at = another.getTop();
            if (t < at) {
                return -1;
            } else if (t > at) {
                return 1;
            } else {
                float lt = getLeft();
                float alt = another.getLeft();
                return lt < alt ? -1 : lt > alt ? 1 : 0;
            }
        }
    }
    
    @Override
    public Presentation getPresentation() {
        return presentation;
    }

    public class PresentationImpl implements Presentation {
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
        private final Bitmap sourceImage;
        
        PresentationImpl(Bitmap sourceImage) {
            this.sourceImage = sourceImage;
        }

        @Override
        public void drawBitmap(Canvas c) {
            float top = 0;
            float left = 0;
            Presentation p = this;
            do {
                top += p.getTop();
                left += p.getLeft();
            } while ((p = p.getParent()) != null);

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
        
        @Override
        public void drawRectShape(Canvas c) {
            float top = 0;
            float left = 0;
            Presentation p = this;
            do {
                top += p.getTop();
                left += p.getLeft();
            } while ((p = p.getParent()) != null);

            dest.set(
                    left,
                    top,
                    left + width,
                    top + height);
            
            paint.setColor(bgColor);
            paint.setAlpha(0xff);
            
            c.drawRect(dest, paint);
        }

        @Override
        public int getWidth() {
            return width;
        }

        @Override
        public void setWidth(int width) {
            this.width = width;
        }

        @Override
        public int getHeight() {
            return height;
        }

        @Override
        public void setHeight(int height) {
            this.height = height;
        }

        @Override
        public float getLeft() {
            return left + leftOffset;
        }

        @Override
        public void setLeft(float left) {
            this.left = left;
        }
        
        @Override
        public float getLeftOffset() {
            return leftOffset;
        }
        
        @Override
        public void setLeftOffset(float leftOffset) {
            this.leftOffset = leftOffset;
        }

        @Override
        public float getTop() {
            return top + topOffset;
        }

        @Override
        public void setTop(float top) {
            this.top = top;
        }
        
        @Override
        public float getTopOffset() {
            return topOffset;
        }
        
        @Override
        public void setTopOffset(float topOffset) {
            this.topOffset = topOffset;
        }

        @Override
        public float getBgPosX() {
            return bgPosX;
        }

        @Override
        public void setBgPosX(float bgPosX) {
            this.bgPosX = bgPosX;
        }

        @Override
        public float getBgPosY() {
            return bgPosY;
        }

        @Override
        public void setBgPosY(float bgPosY) {
            this.bgPosY = bgPosY;
        }

        @Override
        public int getBgColor() {
            return bgColor;
        }

        @Override
        public void setBgColor(int bgColor) {
            this.bgColor = bgColor;
        }

        @Override
        public boolean isVisible() {
            return visibility;
        }

        @Override
        public void setVisibility(boolean visibility) {
            this.visibility = visibility;
        }

        @Override
        public Rect getSrc() {
            return src;
        }

        @Override
        public void setSrc(Rect src) {
            this.src = src;
        }

        @Override
        public RectF getDest() {
            return dest;
        }

        @Override
        public void setDest(RectF dest) {
            this.dest = dest;
        }

        @Override
        public Paint getPaint() {
            return paint;
        }

        @Override
        public void setPaint(Paint paint) {
            this.paint = paint;
        }

        @Override
        public int getOrder() {
            return order;
        }
        
        @Override
        public void setOrder(int order) {
            this.order = order;
        }
        
        @Override
        public Presentation getParent() {
            Entity parent = BaseEntity.this.getParent();
            if (parent == null) {
            	return null;
            } else {
            	return parent.getPresentation();
            }
        }

        @Override
        public float[] getAbsolutePos() {
            Presentation b = this;
            float[] c = { 0, 0 };
            do {
                c[0] += b.getTop();
                c[1] += b.getLeft();
            } while ((b = b.getParent()) != null);
            return c;
        }
        
        @Override
        public void prepareBkPos(int x, int y) {
            bgPosX = getCorrectedSpritePos(x);
            bgPosY = getCorrectedSpritePos(y);
        }

        @Override
        public void changeBkPos(int x, int y, boolean f) {
            if (f) {
                bgPosX = getCorrectedSpritePos(x);
                bgPosY = getCorrectedSpritePos(y);
            } else {
                bgPosX = x;
                bgPosY = y;
            }
        }

        private int getCorrectedSpritePos(int p) {
            return p / 8 * 10 + 2;
        }

        @Override
        public Bitmap getSourceImage() {
            return sourceImage;
        }
    }
    
}
