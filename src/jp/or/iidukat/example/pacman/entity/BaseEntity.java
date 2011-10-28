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
    
    private final Appearance appearance;
    private final List<Entity> children;
    private Entity parent;

    BaseEntity(Bitmap sourceImage) {
        this(sourceImage, false);
    }

    BaseEntity(Bitmap sourceImage, boolean parent) {
        appearance = new AppearanceImpl(sourceImage);
        children = parent ? new ArrayList<Entity>() : null;
    }
    
    @Override
    public final int getHeight() {
        return appearance.getHeight();
    }
    
    @Override
    public final int getWidth() {
        return appearance.getWidth();
    }
    
    @Override
    public final float[] getAbsolutePos() {
        return appearance.getAbsolutePos();
    }
    
    @Override
    public final Entity getParent() {
        return parent;
    }
    
    @Override
    public final void setParent(Entity parent) {
        this.parent = parent;
        this.parent.addChild(this);
    }
    
    @Override
    public final boolean isVisible() {
        return appearance.isVisible();
    }
    
    @Override
    public final void setVisibility(boolean visibility) {
        appearance.setVisibility(visibility);
    }
    
    @Override
    public final Appearance getAppearance() {
        return appearance;
    }
    
    @Override
    public final void draw(Canvas canvas) {
        if (!isVisible()) {
            return;
        }
        
        doDraw(canvas);
        
        if (children != null) {
            for (Entity child : children) {
                child.draw(canvas);
            }
        }
    }
    
    abstract void doDraw(Canvas canvas);
    
    @Override
    public final boolean addChild(Entity child) {
        boolean added = children.add(child);
        if (added) {
            Collections.sort(children);
        }
        return added;
    }
    
    @Override
    public final boolean removeChild(Entity child) {
        return children.remove(child);
    }
    
    @Override
    public final void clearChildren() {
        children.clear();
    }
    
    @Override
    public final int compareTo(Entity another) {
        int o = getAppearance().getOrder();
        int ao = another.getAppearance().getOrder();
        if (o < ao) {
            return -1;
        } else if (o > ao) {
            return 1;
        } else {
            return 0;
        }
    }
    
    private class AppearanceImpl implements Appearance {
        private int height;
        private int width;
        private float top;
        private float topOffset;
        private float left;
        private float leftOffset;
        private float bgPosX;
        private float bgPosY;
        private int bgColor;
        private Paint paint = new Paint();
        private Rect src = new Rect();
        private RectF dest = new RectF();
        private boolean visibility = true;
        private int order;
        private final Bitmap sourceImage;
        
        private final float[] parentSize = new float[] { 0, 0 };
        private final float[] adjustedSize = new float[] { 0, 0 };
        private final float[] adjustedPos = new float[] { 0, 0 };
        private final float[] adjustedBgPos = new float[] { 0, 0 };
        
        AppearanceImpl(Bitmap sourceImage) {
            this.sourceImage = sourceImage;
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
        public int getWidth() {
            return width;
        }

        @Override
        public void setWidth(int width) {
            this.width = width;
        }

        @Override
        public float[] getAbsolutePos() {
            Appearance a = this;
            float[] pos = { 0, 0 };
            do {
                pos[0] += a.getTop();
                pos[1] += a.getLeft();
            } while (a != a.getParent()
                        && (a = a.getParent()) != null);
            return pos;
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
        public void prepareBkPos(int x, int y) {
            bgPosX = getCorrectedSpritePos(x);
            bgPosY = getCorrectedSpritePos(y);
        }

        @Override
        public void changeBkPos(int x, int y, boolean correction) {
            if (correction) {
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
        public int getBgColor() {
            return bgColor;
        }

        @Override
        public void setBgColor(int bgColor) {
            this.bgColor = bgColor;
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
        public Appearance getParent() {
            Entity parent = BaseEntity.this.parent;
            if (parent == null) {
            	return null;
            } else {
            	return parent.getAppearance();
            }
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
        public int getOrder() {
            return order;
        }
        
        @Override
        public void setOrder(int order) {
            if (this.order != order) {
                this.order = order;
                BaseEntity parent = (BaseEntity) BaseEntity.this.parent;
                if (parent != null && parent.children.contains(BaseEntity.this)) {
                    Collections.sort(parent.children);
                }
            }
        }
        
        @Override
        public Bitmap getSourceImage() {
            return sourceImage;
        }

        @Override
        public void drawBitmap(Canvas canvas) {

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
            canvas.drawBitmap(sourceImage, src, dest, null);
        }
        
        @Override
        public void drawRectShape(Canvas canvas) {
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
            
            canvas.drawRect(dest, paint);
        }
        
        private boolean adjust() {
            adjustedPos[0] = getTop();
            adjustedPos[1] = getLeft();
            adjustedSize[0] = height;
            adjustedSize[1] = width;
            adjustedBgPos[0] = bgPosY;
            adjustedBgPos[1] = bgPosX; 

            Appearance a = getParent();
            if (a == null) {
                return true;
            }
            parentSize[0] = a.getHeight();
            parentSize[1] = a.getWidth();

            for (int i = 0; i < 2; i++) {
                if (!auxAdjust(i)) {
                    return false;
                }
            }
            
            do {
                adjustedPos[0] += a.getTop();
                adjustedPos[1] += a.getLeft();
            } while (a != a.getParent()
                        && (a = a.getParent()) != null);
            
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
        
    }
    
}
