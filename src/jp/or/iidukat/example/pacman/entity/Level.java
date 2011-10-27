package jp.or.iidukat.example.pacman.entity;

import java.util.ArrayList;
import java.util.List;

import jp.or.iidukat.example.pacman.PacmanGame.LevelConfig;
import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Level extends BaseEntity {
    private static final int MAX_COUNT_OF_FRUITS = 4;
    private List<Fruit> fruits = new ArrayList<Fruit>();
    
    public Level(Bitmap sourceImage) {
        super(sourceImage, true);
    }
    
    public void init() {
        Presentation p = getPresentation();
        p.setLeft(515);
        p.setTop(74);
        p.setHeight(64);
        p.setWidth(32);
    }
    
    @Override
    void doDraw(Canvas canvas) {
    }

    public void update(int level, LevelConfig[] levelConfigs) {
        clearFruits();
        int top =
            (MAX_COUNT_OF_FRUITS - Math.min(level, MAX_COUNT_OF_FRUITS)) * Fruit.HEIGHT
                - Fruit.HEIGHT;
        for (int i = level, bottom = Math.max(level - MAX_COUNT_OF_FRUITS + 1, 1);
                i >= bottom; i--) {
            int fruitLevel =
                (i >= levelConfigs.length)
                    ? levelConfigs[levelConfigs.length - 1].getFruit()
                    : levelConfigs[i].getFruit();
            Fruit fruit =
                new Fruit(
                        getPresentation().getSourceImage(),
                        fruitLevel);
            top += Fruit.HEIGHT;
            fruit.initOnLevel(top);
            fruit.setParent(this);
            fruits.add(fruit);
        }
    }
    
    private void clearFruits() {
        fruits.clear();
        clearChildren();
    }
}
