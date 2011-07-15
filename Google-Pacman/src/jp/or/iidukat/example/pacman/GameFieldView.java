package jp.or.iidukat.example.pacman;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class GameFieldView extends View {

//    private long mMoveDelay = 600;
    private long lastMove;
    private long lastClick;

    private Bitmap sourceImage;
    
    private int canvasHeight = -1;
    private int canvasWidth = -1;

    private Pacman10Hp3.Game game;
    
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

	public GameFieldView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initGameFieldView();
		
		// TODO: ユーザのアクションによりゲームが開始されるように修正する
		this.game = new Pacman10Hp3.Game(this);
		this.game.init();
		
	}

	private void initGameFieldView() {
        setFocusable(true); 
        sourceImage =
        	BitmapFactory.decodeResource(
			        			getResources(),
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
		// ゲームプレイフィールド
		Rect src = new Rect(322, 2, 786, 138);
		Rect dest = new Rect(
				        (canvasWidth - 464) / 2,
						(canvasHeight - 136) / 2,
						(canvasWidth + 464) / 2,
						(canvasHeight + 136) / 2);
		canvas.drawBitmap(sourceImage, src, dest, null);
		
		// Actor
		
		// エサ、パワーエサ
		
		// モンスターの巣のドア
		
		// スコア
		
		// プレーヤー残機
		
		// レベル
		
		// サウンドON/OFF
		
	}

	@Override
    public boolean onTouchEvent(MotionEvent event) {

		if (System.currentTimeMillis() - lastClick > 500) {
			lastClick = System.currentTimeMillis();

        }
        return true;
	}
}
