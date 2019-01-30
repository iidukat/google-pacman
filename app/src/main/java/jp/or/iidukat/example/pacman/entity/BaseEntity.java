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
    public final double[] getAbsolutePos() {
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
        private double top;
        private double topOffset;
        private double left;
        private double leftOffset;
        private double bgPosX;
        private double bgPosY;
        private int bgColor;
        private Paint paint = new Paint();
        private Rect src = new Rect();
        private RectF dest = new RectF();
        private boolean visibility = true;
        private int order;
        private final Bitmap sourceImage;
        
        private final double[] parentSize = new double[] { 0, 0 };
        private final double[] adjustedSize = new double[] { 0, 0 };
        private final double[] adjustedPos = new double[] { 0, 0 };
        private final double[] adjustedBgPos = new double[] { 0, 0 };
        
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
        public double[] getAbsolutePos() {
            Appearance a = this;
            double[] pos = { 0, 0 };
            do {
                pos[0] += a.getTop();
                pos[1] += a.getLeft();
            } while (a != a.getParent()
                        && (a = a.getParent()) != null);
            return pos;
        }

        @Override
        public double getTop() {
            return top + topOffset;
        }

        @Override
        public void setTop(double top) {
            this.top = top;
        }
        
        @Override
        public double getTopOffset() {
            return topOffset;
        }
        
        @Override
        public void setTopOffset(double topOffset) {
            this.topOffset = topOffset;
        }

        @Override
        public double getLeft() {
            return left + leftOffset;
        }

        @Override
        public void setLeft(double left) {
            this.left = left;
        }
        
        @Override
        public double getLeftOffset() {
            return leftOffset;
        }
        
        @Override
        public void setLeftOffset(double leftOffset) {
            this.leftOffset = leftOffset;
        }

        @Override
        public double getBgPosX() {
            return bgPosX;
        }

        @Override
        public void setBgPosX(double bgPosX) {
            this.bgPosX = bgPosX;
        }

        @Override
        public double getBgPosY() {
            return bgPosY;
        }

        @Override
        public void setBgPosY(double bgPosY) {
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
                    (int) Math.round(adjustedBgPos[1]),
                    (int) Math.round(adjustedBgPos[0]),
                    (int) Math.round(adjustedBgPos[1] + adjustedSize[1]),
                    (int) Math.round(adjustedBgPos[0] + adjustedSize[0]));
            dest.set(
                    (float) adjustedPos[1],
                    (float) adjustedPos[0],
                    (float) (adjustedPos[1] + adjustedSize[1]),
                    (float) (adjustedPos[0] + adjustedSize[0]));
            canvas.drawBitmap(sourceImage, src, dest, null);
        }
        
        @Override
        public void drawRectShape(Canvas canvas) {
            if (!adjust()) {
                return;
            }
            
            dest.set(
                    (float) adjustedPos[1],
                    (float) adjustedPos[0],
                    (float) (adjustedPos[1] + adjustedSize[1]),
                    (float) (adjustedPos[0] + adjustedSize[0]));
            
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
