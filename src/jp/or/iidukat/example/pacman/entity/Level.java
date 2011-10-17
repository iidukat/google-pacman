package jp.or.iidukat.example.pacman.entity;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;

public class Level extends BaseEntity {
    private List<Fruit> fruits = new ArrayList<Fruit>();
    
    @Override
    public void draw(Canvas c) {
        if (!isVisible()) return;
        
//        presentation.drawRectShape(c); 
        
        for (Fruit f : fruits) {
            f.draw(c);
        }
    }

    public void addFruit(Fruit fruit) {
        fruits.add(fruit);
    }
    
    public void clearFruits() {
        fruits.clear();
    }
}
