package jp.or.iidukat.example.pacman.entity;

import java.util.ArrayList;
import java.util.List;

import jp.or.iidukat.example.pacman.PacmanGame;
import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Score extends BaseEntity {
    private final int scoreDigits;
    private List<Number> numbers = new ArrayList<Number>();

    public Score(Bitmap sourceImage, int scoreDigits) {
        super(sourceImage);
        this.scoreDigits = scoreDigits;
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
        for (int b = 0; b < scoreDigits; b++) {
            Score.Number c = new Score.Number(getPresentation()
                    .getSourceImage());
            c.init(b * 8);
            c.setParent(this);
            numbers.add(c);
        }
    }

    public void update(long score) {
        String c = String.valueOf(score);
        if (c.length() > scoreDigits)
            c = c.substring(c.length() - scoreDigits);
        for (int d = 0; d < scoreDigits; d++) {
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
            PacmanGame.prepareElement(p, 48, 0);
        }

        void update(int n) {
            PacmanGame.changeElementBkPos(
                                        getPresentation(),
                                        8 + 8 * n,
                                        144,
                                        true);
        }
        
        void updateToBlank() {
            PacmanGame.changeElementBkPos(
                                        getPresentation(),
                                        48,
                                        0,
                                        true);
        }

        @Override
        public void draw(Canvas c) {
            if (!isVisible())
                return;

            getPresentation().drawBitmap(c);
        }
    }
}
