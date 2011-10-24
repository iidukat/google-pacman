package jp.or.iidukat.example.pacman.entity;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Score extends BaseEntity {

    private static final int DIGITS = 10;
    private List<Number> numbers = new ArrayList<Number>();

    public Score(Bitmap sourceImage) {
        super(sourceImage);
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
        for (int b = 0; b < DIGITS; b++) {
            Score.Number c = new Score.Number(getPresentation()
                    .getSourceImage());
            c.init(b * 8);
            c.setParent(this);
            numbers.add(c);
        }
    }

    public void update(long score) {
        String c = String.valueOf(score);
        if (c.length() > DIGITS)
            c = c.substring(c.length() - DIGITS);
        for (int d = 0; d < DIGITS; d++) {
            Score.Number f = getNumber(d);
            String h = null;
            if (d < c.length())
                h = c.substring(d, d + 1);
            if (h != null)
                f.update(Integer.parseInt(h, 10));
            else
                f.updateToBlank();
        }
    }

    @Override
    public void draw(Canvas c) {

        if (!isVisible())
            return;

        getPresentation().drawRectShape(c);

        for (Number n : numbers) {
            n.draw(c);
        }
    }

    public Number getNumber(int index) {
        return numbers.get(index);
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
        public void draw(Canvas c) {
            if (!isVisible())
                return;

            getPresentation().drawBitmap(c);
        }
    }
}
