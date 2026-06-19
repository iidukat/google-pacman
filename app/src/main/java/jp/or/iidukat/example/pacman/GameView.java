package jp.or.iidukat.example.pacman;

import jp.or.iidukat.example.pacman.entity.PacmanCanvas;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

class GameView extends View {

    private static final int GAME_WIDTH = 464;
    private static final int GAME_HEIGHT = 168;
    private static final float MARGIN = 0.9f;

    private float scale = 1f;
    private float offsetX = 0f;
    private float offsetY = 0f;
    private final Matrix touchTransform = new Matrix();

    PacmanGame game;

    final RefreshHandler redrawHandler = new RefreshHandler();

    class RefreshHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            game.tick();
            GameView.this.invalidate();
        }

        public void sleep(long delayMillis) {
            this.removeMessages(0);
            sendMessageDelayed(obtainMessage(0), delayMillis);
        }
    }

    public GameView(Context context) {
        super(context);
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        scale = Math.min((float) w / GAME_WIDTH, (float) h / GAME_HEIGHT) * MARGIN;
        offsetX = (w - GAME_WIDTH * scale) / 2f;
        offsetY = (h - GAME_HEIGHT * scale) / 2f;
        touchTransform.setTranslate(-offsetX, -offsetY);
        touchTransform.postScale(1f / scale, 1f / scale);
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(offsetX, offsetY);
        canvas.scale(scale, scale);
        PacmanCanvas canvasEl = game.getCanvasEl();
        canvasEl.setLeft(0);
        canvasEl.setTop(0);
        canvasEl.draw(canvas);
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        MotionEvent transformed = MotionEvent.obtain(event);
        transformed.transform(touchTransform);

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
        case MotionEvent.ACTION_DOWN:
            game.handleTouchStart(transformed);
            break;
        case MotionEvent.ACTION_UP:
            game.handleTouchEnd(transformed);
            break;
        case MotionEvent.ACTION_MOVE:
            game.handleTouchMove(transformed);
            break;
        }
        transformed.recycle();
        return true;
    }
}
