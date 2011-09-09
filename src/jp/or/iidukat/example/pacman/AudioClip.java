package jp.or.iidukat.example.pacman;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

class AudioClip {
	
	private static final String TAG = "AudioClip";

	private final Context context;
	private final int resId;
	
	private MediaPlayer mPlayer;
	private String name;

	private boolean mPlaying = false;
	private boolean mLoop = false;
	
	AudioClip(Context context, int resId) {
		this.context = context;
		this.resId = resId;
	}
	
	void init() {
		name = context.getResources().getResourceName(resId);
		mPlayer = MediaPlayer.create(context, resId);
		mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				mPlaying = false;
				if (mLoop) {
					play();
				}
			}
		});		
	}
	
	void play() {
		if (mPlaying)
			return;
		
		if (mPlayer != null) {
			mPlaying = true;
			mPlayer.start();
		}
	}
	
	void stop() {
		try {
			mLoop = false;
			if (mPlaying) {
				mPlaying = false;
				mPlayer.pause();
				mPlayer.seekTo(0);
			}
		} catch (Exception e) {
			Log.e(TAG, "stop " + name, e);
		}
	}
	
	void loop() {
		mLoop = true;
		play();
	}
	
	void destroy() {
		if (mPlayer != null) {
			mPlayer.release();
			mPlayer = null;
		}
	}
}
