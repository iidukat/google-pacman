package jp.or.iidukat.example.pacman;

import jp.or.iidukat.example.pacman.entity.PacmanCanvas;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

class GameView extends View {

    private static final int CANVAS_WIDTH = 464;
    private static final int CANVAS_HEIGHT = 168;
    
    private int canvasHeight = -1;
    private int canvasWidth = -1;

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
        canvasWidth = w > CANVAS_WIDTH ? w : CANVAS_WIDTH;
        canvasHeight = h > CANVAS_HEIGHT ? h : CANVAS_HEIGHT;
    }

    @Override
    public void onDraw(Canvas canvas) {
        PacmanCanvas canvasEl = game.getCanvasEl();
        canvasEl.setLeft((canvasWidth - CANVAS_WIDTH) / 2);
        canvasEl.setTop((canvasHeight - CANVAS_HEIGHT) / 2);
        canvasEl.draw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
        case MotionEvent.ACTION_DOWN:
            game.handleTouchStart(event);
            break;
        case MotionEvent.ACTION_UP:
            game.handleTouchEnd(event);
            break;
        case MotionEvent.ACTION_MOVE:
            game.handleTouchMove(event);
            break;
        }

        return true;
    }
}
