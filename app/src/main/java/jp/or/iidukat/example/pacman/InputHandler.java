package jp.or.iidukat.example.pacman;

import android.view.MotionEvent;

import jp.or.iidukat.example.pacman.entity.Entity;
import jp.or.iidukat.example.pacman.entity.Pacman;

class InputHandler {

    private final PacmanGame game;

    private double touchDX;
    private double touchDY;
    private double touchStartX;
    private double touchStartY;
    private boolean touchCanceld = true;
    private Direction dpadDir = Direction.NONE;

    InputHandler(PacmanGame game) {
        this.game = game;
    }

    void onTouchStart(MotionEvent e) {
        touchDX = 0;
        touchDY = 0;
        if (e.getPointerCount() == 1) {
            touchCanceld = false;
            touchStartX = e.getX(0);
            touchStartY = e.getY(0);
        }
    }

    void onTouchMove(MotionEvent e) {
        if (touchCanceld) {
            return;
        }

        if (e.getPointerCount() > 1) {
            cancelTouch();
        } else {
            touchDX = e.getX(0) - touchStartX;
            touchDY = e.getY(0) - touchStartY;
        }
    }

    void onTouchEnd(MotionEvent e) {
        if (touchCanceld) {
            return;
        }

        double absDx = Math.abs(touchDX);
        double absDy = Math.abs(touchDY);
        Pacman pacman = game.getPacman();
        if (absDx < 8 && absDy < 8) {
            canvasClicked(touchStartX, touchStartY);
        } else if (absDx > 15 && absDy < absDx * 2 / 3) {
            pacman.setRequestedDir(touchDX > 0 ? Direction.RIGHT : Direction.LEFT);
        } else if (absDy > 15 && absDx < absDy * 2 / 3) {
            pacman.setRequestedDir(touchDY > 0 ? Direction.DOWN : Direction.UP);
        }
        cancelTouch();
    }

    private void cancelTouch() {
        touchStartX = Double.NaN;
        touchStartY = Double.NaN;
        touchCanceld = true;
    }

    private void canvasClicked(double x, double y) {
        if (handleSoundIconClick(x, y)) {
            return;
        }

        double[] offset = game.getCanvasEl().getAbsolutePos();
        double cx = x - offset[1];
        double cy = y - offset[0];

        Pacman pacman = game.getPacman();
        double px = pacman.getFieldX() + 48;
        double py = pacman.getFieldY() + 32;
        double xdiff = Math.abs(cx - px);
        double ydiff = Math.abs(cy - py);
        if (xdiff > 8 && ydiff < xdiff) {
            pacman.setRequestedDir(cx > px ? Direction.RIGHT : Direction.LEFT);
        } else if (ydiff > 8 && xdiff < ydiff) {
            pacman.setRequestedDir(cy > py ? Direction.DOWN : Direction.UP);
        }
    }

    private boolean handleSoundIconClick(double x, double y) {
        if (!game.soundManager.isAvailable() || !game.getSoundEl().isVisible()) {
            return false;
        }

        Entity soundEl = game.getSoundEl();
        double[] pos = soundEl.getAbsolutePos();
        if (pos[1] <= x && x <= pos[1] + soundEl.getWidth()) {
            if (pos[0] <= y && y <= pos[0] + soundEl.getHeight()) {
                game.toggleSound();
                return true;
            }
        }

        return false;
    }

    void setDpadDir(Direction dir) {
        this.dpadDir = dir;
    }

    void processDpadInput() {
        if (dpadDir != Direction.NONE) {
            game.getPacman().setRequestedDir(dpadDir);
        }
    }
}
