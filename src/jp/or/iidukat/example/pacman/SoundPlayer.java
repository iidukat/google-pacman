package jp.or.iidukat.example.pacman;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;

public class SoundPlayer {
	
	private static final int[] soundResources = {
		R.raw.death,
		R.raw.eating_dot_1,
		R.raw.eating_dot_2,
		R.raw.eating_ghost,
		R.raw.extra_life,
		R.raw.fruit,
		R.raw.start_music,
	};
	
	private static final int[] ambientResources = {
		R.raw.ambient_1,
		R.raw.ambient_2,
		R.raw.ambient_3,
		R.raw.ambient_4,
		R.raw.ambient_eyes,
		R.raw.ambient_fright,
	};
	
	private final Context context;
	private final PacmanGame game;
	private SoundPool soundPool;
	private SoundPool ambientPool;
	private volatile boolean soundPoolAvailable;
	private volatile boolean ambientPoolAvailable;

	private Map<String, Integer> soundIds;
	private Map<String, Integer> ambientIds;

	private int[] channels = new int[5];
	private int ambientChannel;
	String oldAmbient;

	SoundPlayer(Context context, PacmanGame game) {
		this.context = context;
		this.game = game;
	}

	void init() {
		{
			soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
			soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
				private int count = 0;
				private boolean success = true;
				@Override
				public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
					count++;
					success = success && (status == 0);
					if (count == soundResources.length) {
						if (success) {
							soundPoolAvailable = true;
							game.soundAvailable = soundPoolAvailable && ambientPoolAvailable;
						}
					}

				}
			});
			soundIds = new HashMap<String, Integer>();
			for (int res : soundResources) {
				soundIds.put(
						context.getResources().getResourceEntryName(res),
						Integer.valueOf(soundPool.load(context, res, 1)));
			}
		}

		{
			ambientPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
			ambientPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
				private int count = 0;
				private boolean success = true;
				@Override
				public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
					count++;
					success = success && (status == 0);
					if (count == ambientResources.length) {
						if (success) {
							ambientPoolAvailable = true;
							game.soundAvailable = soundPoolAvailable && ambientPoolAvailable;							
						}
					}
				}
			});
			ambientIds = new HashMap<String, Integer>();
			for (int res : ambientResources) {
				ambientIds.put(
						context.getResources().getResourceEntryName(res),
						Integer.valueOf(ambientPool.load(context, res, 1)));
			}
		}
	}
	
	void destroy() {
		soundPool.release();
		for (int i = 0; i < channels.length; i++) {
			channels[i] = 0; 
		}

		ambientPool.release();
		ambientChannel = 0;
	}

	void playTrack(String track, int channel) throws SoundPlayerException {

		if (channel >= channels.length) {
			throw new IllegalArgumentException("channel is too large. : " + channel);
		}

		Integer id = soundIds.get(track);
		if (id != null) {
			channels[channel] =
				Integer.valueOf(soundPool.play(id.intValue(), 1, 1, 0, 0, 1));
		} else {
			throw new SoundPlayerException("playing " + track + " is failed.");
		}
	}

	void stopChannel(int channel) throws SoundPlayerException {
		if (channel >= channels.length) {
			throw new IllegalArgumentException("channel is too large. : " + channel);
		}

		int id = channels[channel];
		if (id != 0) {
			soundPool.stop(id);
			channels[channel] = 0;
		}
	}

	void playAmbientTrack(String track) throws SoundPlayerException {

		Integer id = ambientIds.get(track);
		if (id != null) {
			ambientChannel =
				Integer.valueOf(ambientPool.play(id.intValue(), 1, 1, 0, -1, 1));
		} else {
			throw new SoundPlayerException("playing ambient " + track + " is failed.");
		}
	}

	void stopAmbientTrack() throws SoundPlayerException {
		if (ambientChannel != 0) {
			ambientPool.stop(ambientChannel);
			ambientChannel = 0;
		}
		oldAmbient = null;
	}

}
