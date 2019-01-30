package jp.or.iidukat.example.pacman.entity;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Score extends BaseEntity {

    private static final int DIGITS = 10;
    private List<Number> numbers = new ArrayList<Number>();

    public Score(Bitmap sourceImage) {
        super(sourceImage, true);
    }

    public void init() {
        Appearance a = getAppearance();
        a.setLeft(40);
        a.setTop(3);
        a.setWidth(80);
        a.setHeight(8);
        initScoreNumbers();
    }

    private void initScoreNumbers() {
        for (int i = 0; i < DIGITS; i++) {
            Score.Number n =
                new Score.Number(getAppearance().getSourceImage());
            n.init(i * Number.WIDTH);
            n.setParent(this);
            numbers.add(n);
        }
    }

    public void update(long score) {
        String s = String.valueOf(score);
        if (s.length() > DIGITS) {
            s = s.substring(s.length() - DIGITS);
        }
        for (int i = 0; i < DIGITS; i++) {
            Score.Number n = getNumber(i);
            String c = null;
            if (i < s.length()) {
                c = s.substring(i, i + 1);
            }
            if (c != null) {
                n.update(Integer.parseInt(c, 10));
            } else {
                n.updateToBlank();
            }
        }
    }

    private Number getNumber(int index) {
        return numbers.get(index);
    }

    @Override
    void doDraw(Canvas canvas) {
        getAppearance().drawRectShape(canvas);
    }

    public static class Number extends BaseEntity {

        static final int WIDTH = 8;
        static final int HEIGHT = 8;
        
        public Number(Bitmap sourceImage) {
            super(sourceImage);
        }

        void init(int left) {
            Appearance a = getAppearance();
            a.setTop(0);
            a.setLeft(left);
            a.setWidth(WIDTH);
            a.setHeight(HEIGHT);
            a.prepareBkPos(48, 0);
        }

        void update(int n) {
            getAppearance().changeBkPos(8 + 8 * n, 144, true);
        }
        
        void updateToBlank() {
            getAppearance().changeBkPos(48, 0, true);
        }

        @Override
        void doDraw(Canvas canvas) {
            getAppearance().drawBitmap(canvas);
        }
    }
}
