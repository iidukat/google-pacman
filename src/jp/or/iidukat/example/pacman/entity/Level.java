package jp.or.iidukat.example.pacman.entity;

import java.util.ArrayList;
import java.util.List;

import jp.or.iidukat.example.pacman.PacmanGame.LevelConfig;
import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Level extends BaseEntity {
    private List<Fruit> fruits = new ArrayList<Fruit>();
    
    public Level(Bitmap sourceImage) {
        super(sourceImage);
    }
    
    public void init() {
        Presentation p = getPresentation();
        p.setLeft(515);
        p.setTop(74);
        p.setHeight(64);
        p.setWidth(32);
    }
    
    @Override
    public void draw(Canvas c) {
        if (!isVisible()) return;
        
//        presentation.drawRectShape(c); 
        
        for (Fruit f : fruits) {
            f.draw(c);
        }
    }

    public void update(int level, LevelConfig[] z) {
        fruits.clear();
        int top = (4 - Math.min(level, 4)) * 16 - 16;
        for (int b = level; b >= Math.max(level - 4 + 1, 1); b--) {
            int c = b >= z.length ? z[z.length - 1].getFruit() : z[b].getFruit();
            Fruit d = new Fruit(getPresentation().getSourceImage(), c);
            top += 16;
            d.initOnLevel(top);
            d.setParent(this);
            fruits.add(d);
        }
    }
}
