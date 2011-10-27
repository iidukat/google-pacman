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
        Presentation p = getPresentation();
        p.setLeft(18);
        p.setTop(16);
        p.setWidth(8);
        p.setHeight(56);
        initScoreNumbers();
    }

    private void initScoreNumbers() {
        for (int i = 0; i < DIGITS; i++) {
            Score.Number n =
                new Score.Number(getPresentation().getSourceImage());
            n.init(i * 8);
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
        getPresentation().drawRectShape(canvas);
    }

    public static class Number extends BaseEntity {

        public Number(Bitmap sourceImage) {
            super(sourceImage);
        }

        void init(int top) {
            Presentation p = getPresentation();
            p.setTop(top);
            p.setLeft(0);
            p.setWidth(8);
            p.setHeight(8);
            p.prepareBkPos(48, 0);
        }

        void update(int n) {
            getPresentation().changeBkPos(8 + 8 * n, 144, true);
        }
        
        void updateToBlank() {
            getPresentation().changeBkPos(48, 0, true);
        }

        @Override
        void doDraw(Canvas canvas) {
            getPresentation().drawBitmap(canvas);
        }
    }
}
