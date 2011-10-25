package jp.or.iidukat.example.pacman.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

abstract class BaseEntity implements Entity {
    
    private final Presentation presentation;
    private final List<Entity> drawQueue;
    private Entity parent;

    public BaseEntity(Bitmap sourceImage) {
        this(sourceImage, false);
    }

    public BaseEntity(Bitmap sourceImage, boolean parent) {
        presentation = new PresentationImpl(sourceImage);
        drawQueue = parent ? new ArrayList<Entity>() : null;
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
        parent.addToDrawQueue(this);
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
    public void draw(Canvas canvas) {
        if (!isVisible()) {
            return;
        }
        
        doDraw(canvas);
        
        if (drawQueue != null) {
            for (Entity entity : drawQueue) {
                entity.draw(canvas);
            }
        }
    }
    
    abstract void doDraw(Canvas canvas);
    
    @Override
    public boolean addToDrawQueue(Entity entity) {
        boolean added = drawQueue.add(entity);
        if (added) {
            Collections.sort(drawQueue);
        }
        return added;
    }
    
    @Override
    public boolean removeFromDrawQueue(Entity entity) {
        return drawQueue.remove(entity);
    }
    
    @Override
    public void clearDrawQueue() {
        drawQueue.clear();
    }
    
    @Override
    public int compareTo(Entity another) {
        int o = getPresentation().getOrder();
        int ao = another.getPresentation().getOrder();
        if (o < ao) {
            return -1;
        } else if (o > ao) {
            return 1;
        } else {
            return 0;
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
        private float bgPosX;
        private float bgPosY;
        private int bgColor;
        private boolean visibility = true;
        private Rect src = new Rect();
        private RectF dest = new RectF();
        private Paint paint = new Paint();
        private int order;
        private final Bitmap sourceImage;
        
        private final float[] parentSize = new float[] { 0, 0 };
        private final float[] adjustedSize = new float[] { 0, 0 };
        private final float[] adjustedPos = new float[] { 0, 0 };
        private final float[] adjustedBgPos = new float[] { 0, 0 };
        
        PresentationImpl(Bitmap sourceImage) {
            this.sourceImage = sourceImage;
        }

        @Override
        public void drawBitmap(Canvas c) {

            if (!adjust()) {
                return;
            }

            src.set(
                Math.round(adjustedBgPos[1]),
                Math.round(adjustedBgPos[0]),
                Math.round(adjustedBgPos[1] + adjustedSize[1]),
                Math.round(adjustedBgPos[0] + adjustedSize[0]));
            dest.set(
                    adjustedPos[1],
                    adjustedPos[0],
                    adjustedPos[1] + adjustedSize[1],
                    adjustedPos[0] + adjustedSize[0]);
            c.drawBitmap(sourceImage, src, dest, null);
        }
        
        @Override
        public void drawRectShape(Canvas c) {
            if (!adjust()) {
                return;
            }
            
            dest.set(
                    adjustedPos[1],
                    adjustedPos[0],
                    adjustedPos[1] + adjustedSize[1],
                    adjustedPos[0] + adjustedSize[0]);
            
            paint.setColor(bgColor);
            paint.setAlpha(0xff);
            
            c.drawRect(dest, paint);
        }
        
        private boolean adjust() {
            adjustedPos[0] = getTop();
            adjustedPos[1] = getLeft();
            adjustedSize[0] = height;
            adjustedSize[1] = width;
            adjustedBgPos[0] = bgPosY;
            adjustedBgPos[1] = bgPosX; 

            Presentation p = getParent();
            if (p == null) {
                return true;
            }
            parentSize[0] = p.getHeight();
            parentSize[1] = p.getWidth();

            for (int i = 0; i < 2; i++) {
                if (!auxAdjust(i)) {
                    return false;
                }
            }
            
            do {
                adjustedPos[0] += p.getTop();
                adjustedPos[1] += p.getLeft();
            } while (p != p.getParent()
                        && (p = p.getParent()) != null);
            
            return true;
        }
        
        private boolean auxAdjust(int axis) {
            if (0 <= adjustedPos[axis]
                     && adjustedPos[axis] <= parentSize[axis] - adjustedSize[axis]) {
            } else if (- adjustedSize[axis] <= adjustedPos[axis] && adjustedPos[axis] < 0 ) {
                adjustedSize[axis] += adjustedPos[axis];
                adjustedBgPos[axis] -= adjustedPos[axis];
                adjustedPos[axis] = 0;
            } else if (parentSize[axis] - adjustedSize[axis] < adjustedPos[axis]
                        && adjustedPos[axis] < parentSize[axis]) {
                adjustedSize[axis] = parentSize[axis] - adjustedPos[axis];
            } else {
                return false;
            }
            return true;
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
            if (this.order != order) {
                this.order = order;
                BaseEntity parent = (BaseEntity) BaseEntity.this.getParent();
                if (parent != null && parent.drawQueue.contains(BaseEntity.this)) {
                    Collections.sort(parent.drawQueue);
                }
            }
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
            } while (b != b.getParent()
                        && (b = b.getParent()) != null);
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
