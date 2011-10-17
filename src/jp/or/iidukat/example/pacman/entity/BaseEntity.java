package jp.or.iidukat.example.pacman.entity;

import android.graphics.Bitmap;

abstract class BaseEntity implements Entity {
    
    private final Presentation presentation;

    public BaseEntity() {
        presentation = new Presentation();
    }
    
    public BaseEntity(Bitmap sourceImage) {
        presentation = new Presentation(sourceImage);
    }
    
    @Override
    public float getTop() {
        return presentation.getTop();
    }

    @Override
    public float getLeft() {
        return presentation.getLeft();
    }
    
    // height
    // width
    
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
    
    public Presentation getPresentation() {
        return presentation;
    }

}
