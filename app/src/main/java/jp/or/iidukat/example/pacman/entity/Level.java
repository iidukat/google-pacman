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
        Appearance a = getAppearance();
        a.setLeft(404);
        a.setTop(152);
        a.setHeight(16);
        a.setWidth(64);
    }
    
    @Override
    void doDraw(Canvas canvas) {
    }

    public void update(int level, LevelConfig[] levelConfigs) {
        clearFruits();
        int left =
            (MAX_COUNT_OF_FRUITS - Math.min(level, MAX_COUNT_OF_FRUITS)) * Fruit.WIDTH_ON_LEVEL
                - Fruit.WIDTH_ON_LEVEL;
        for (int i = level, limit = Math.max(level - MAX_COUNT_OF_FRUITS + 1, 1);
                i >= limit; i--) {
            int fruitLevel =
                (i >= levelConfigs.length)
                    ? levelConfigs[levelConfigs.length - 1].getFruit()
                    : levelConfigs[i].getFruit();
            Fruit fruit =
                new Fruit(
                        getAppearance().getSourceImage(),
                        fruitLevel);
            left += Fruit.WIDTH_ON_LEVEL;
            fruit.initOnLevel(left);
            fruit.setParent(this);
            fruits.add(fruit);
        }
    }
    
    private void clearFruits() {
        fruits.clear();
        clearChildren();
    }
}
