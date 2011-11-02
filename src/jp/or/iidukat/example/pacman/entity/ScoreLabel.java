package jp.or.iidukat.example.pacman.entity;

import jp.or.iidukat.example.pacman.PacmanGame.GameplayMode;
import android.graphics.Bitmap;
import android.graphics.Canvas;

public class ScoreLabel extends BaseEntity {

    public ScoreLabel(Bitmap sourceImage) {
        super(sourceImage);
    }

    public void init() {
        Appearance a = getAppearance();
        a.setLeft(-8);
        a.setTop(3);
        a.setWidth(48);
        a.setHeight(8);
        a.prepareBkPos(160, 56);
    }

    public void update(
                    GameplayMode gameplayMode,
                    long globalTime,
                    float interval) {
        if (gameplayMode != GameplayMode.CUTSCENE) {
            if (globalTime % (interval * 2) == 0) {
                setVisibility(true);
            } else if (globalTime % (interval * 2) == interval) {
                setVisibility(false);
            }
        }
    }
    
    @Override
    void doDraw(Canvas canvas) {
        getAppearance().drawBitmap(canvas);
    }
}
