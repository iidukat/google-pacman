package jp.or.iidukat.example.pacman;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class GameFieldView extends View {

    private Bitmap sourceImage;

    private int canvasHeight = -1;
    private int canvasWidth = -1;

    PacmanGame game;

    final RefreshHandler redrawHandler = new RefreshHandler();

    class RefreshHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            game.tick();
            // TODO: 必要な領域だけinvalidateするように変更する
            GameFieldView.this.invalidate();
        }

        public void sleep(long delayMillis) {
            this.removeMessages(0);
            sendMessageDelayed(obtainMessage(0), delayMillis);
        }
    }

    public GameFieldView(Context context) {
        super(context);
        initGameFieldView();

        // TODO: ユーザのアクションによりゲームが開始されるように修正する
        this.game = new PacmanGame(this);
        this.game.init();
    }

    public GameFieldView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initGameFieldView();

        // TODO: ユーザのアクションによりゲームが開始されるように修正する
        this.game = new PacmanGame(this);
        this.game.init();
    }

    private void initGameFieldView() {
        setFocusable(true);
        sourceImage = BitmapFactory.decodeResource(getResources(),
                R.drawable.pacman_sprite);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        int width = w > h ? w : h;
        int height = w > h ? h : w;
        canvasWidth = width > 464 ? width : 464;
        canvasHeight = height > 136 ? height : 136;
    }

    @Override
    public void onDraw(Canvas canvas) {
        game.getCanvasEl().getPresentation().setTop((canvasHeight - 136) / 2);
        game.getCanvasEl().draw(sourceImage, canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        // // タッチイベントをログにダンプする
        // dumpEvent(event);

        // ここでタッチイベントを処理
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

        // イベントが処理されたことを知らせる
        return true;
    }

//    private void dumpEvent(MotionEvent event) {
//        String[] names = { "DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE",
//                "POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?", };
//        StringBuilder sb = new StringBuilder();
//        int action = event.getAction();
//        int actionCode = action & MotionEvent.ACTION_MASK;
//        sb.append("event ACTION_").append(names[actionCode]);
//        if (actionCode == MotionEvent.ACTION_POINTER_DOWN
//                || actionCode == MotionEvent.ACTION_POINTER_UP) {
//            sb.append("(pid ").append(
//                    action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
//            sb.append(")");
//        }
//        sb.append("[");
//        for (int i = 0; i < event.getPointerCount(); i++) {
//            sb.append("#").append(i);
//            sb.append("(pid ").append(event.getPointerId(i));
//            sb.append(")=").append((int) event.getX(i));
//            sb.append(",").append((int) event.getY(i));
//            if (i + 1 < event.getPointerCount()) {
//                sb.append(";");
//            }
//        }
//        sb.append("]");
//        // Log.d(TAG, sb.toString());
//    }

}
