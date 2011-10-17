package jp.or.iidukat.example.pacman.entity;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Score extends BaseEntity {
    private List<Number> numbers = new ArrayList<Number>();
    
    @Override
    public void draw(Canvas c) {
        
        getPresentation().drawRectShape(c);
        
        for (Number n : numbers) {
            n.draw(c);
        }
    }
    
    public void addNumber(Number number) {
        numbers.add(number);
    }
    
    public Number getNumber(int index) {
        return numbers.get(index);
    }

    public static class Number extends BaseEntity {
        
        public Number(Bitmap sourceImage) {
            super(sourceImage);
        }
        
        @Override
        public void draw(Canvas c) {
            if (!isVisible()) return;
            
            getPresentation().drawBitmap(c);
        }
    }
}
