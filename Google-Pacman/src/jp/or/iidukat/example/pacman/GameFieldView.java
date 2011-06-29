package jp.or.iidukat.example.pacman;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


public class GameFieldView extends SurfaceView implements SurfaceHolder.Callback {

	private ExecutorService executor = Executors.newSingleThreadScheduledExecutor();
	private Runnable tickTask;
	
	public GameFieldView(Context context, AttributeSet attrs) {
		super(context, attrs);

        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        tickTask = new TickTask(holder, context, new Handler() {
            @Override
            public void handleMessage(Message m) {
            }
        });

        setFocusable(true); // make sure we get key events
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
								int height) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Future<?> f = executor.submit(tickTask);
    }

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		
		// TODO Auto-generated method stub
		
	}
	
	private class TickTask implements Runnable {
		private volatile boolean running = true;
		
	    private Bitmap sourceImage;
	    private Bitmap backgroundImage;
	    
	    private Drawable pacman;

	    private int mCanvasHeight = -1;

	    private int mCanvasWidth = -1;

	    private SurfaceHolder mSurfaceHolder;
	    private Context mContext;
	    private Handler mHandler;


		TickTask(SurfaceHolder surfaceHolder, Context context, Handler handler) {
			mSurfaceHolder = surfaceHolder;
			mContext = context;
			mHandler = handler;
			
	        sourceImage = BitmapFactory.decodeResource(context.getResources(),
	        										R.drawable.pacman_sprite);
	    	backgroundImage = Bitmap.createBitmap(sourceImage, 322, 2, 464, 136);
		}
		
		public void run() {
			while (running) {
				try {
					// TODO don't like this hardcoding
					TimeUnit.MILLISECONDS.sleep(20);
					
					draw();
//					model.updateBubbles();

				} catch (InterruptedException ie) {
					running = false;
				}
			}
		}
		
		public void safeStop() {
			running = false;
//			interrupt();
		}
		
		private void draw() {
			// TODO thread safety - the SurfaceView could go away while we are drawing
			
			Canvas c = null;
			try {
				// NOTE: in the LunarLander they don't have any synchronization here,
				// so I guess this is OK. It will return null if the holder is not ready
				c = mSurfaceHolder.lockCanvas();
				synchronized (mSurfaceHolder) {
					if (c != null) {
						doDraw(c);
					}
				}
			} finally {
				if (c != null) {
					mSurfaceHolder.unlockCanvasAndPost(c);
				}
			}
		}
		
		private void doDraw(Canvas c) {
			
			if (mCanvasWidth < 0 || mCanvasHeight < 0) {
				int w = c.getWidth();
				int h = c.getHeight();
				int width = w > h ? w : h;
				int height = w > h ? h : w;
				mCanvasWidth = width > 464 ? width : 464; 
				mCanvasHeight = height > 136 ? height : 136;
			}
          

			// TODO: ディスプレイのサイズから倍率を決定する
			c.drawBitmap(backgroundImage, (mCanvasWidth - 464) / 2, (mCanvasHeight - 136) / 3, null);
		}
	}
}
