package jp.or.iidukat.example.pacman.entity;

import jp.or.iidukat.example.pacman.PacmanGame.GameplayMode;
import android.graphics.Bitmap;
import android.graphics.Canvas;

public class ScoreLabel extends BaseEntity {

    public ScoreLabel(Bitmap sourceImage) {
        super(sourceImage);
    }

    public void init() {
        Presentation p = getPresentation();
        p.setLeft(-2);
        p.setTop(0);
        p.setWidth(48);
        p.setHeight(8);
        p.prepareBkPos(160, 56);
    }

    public void update(GameplayMode gameplayMode, long globalTime, float interval) {
        if (gameplayMode != GameplayMode.CUTSCENE) {
            if (globalTime % (interval * 2) == 0)
                setVisibility(true);
            else if (globalTime % (interval * 2) == interval)
                setVisibility(false);
        }
    }
    
    @Override
    void doDraw(Canvas c) {
        getPresentation().drawBitmap(c);
    }
}
