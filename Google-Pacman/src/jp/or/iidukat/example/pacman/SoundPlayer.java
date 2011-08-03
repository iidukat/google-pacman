package jp.or.iidukat.example.pacman;

import java.util.HashMap;
import java.util.Map;

import jp.or.iidukat.example.pacman.Pacman10Hp3.Game;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;

public class SoundPlayer {

	private final Context context;
	private final Game game;
	private SoundPool soundPool;
	private SoundPool ambientPool;
	private volatile boolean soundPoolAvailable;
	private volatile boolean ambientPoolAvailable;

	private Map<String, Integer> soundIds;
	private Map<String, Integer> ambientIds;

	private int[] channels = new int[5];
	private int ambientChannel;
	String oldAmbient;

	SoundPlayer(Context context, Game game) {
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
					if (count == 9) {
						if (success) {
							soundPoolAvailable = true;
							game.soundAvailable = soundPoolAvailable && ambientPoolAvailable;
						}
					}

				}
			});
			soundIds = new HashMap<String, Integer>();
			soundIds.put("death_double", Integer.valueOf(soundPool.load(context, R.raw.death_double, 1)));
			soundIds.put("death", Integer.valueOf(soundPool.load(context, R.raw.death, 1)));
			soundIds.put("eating_dot_1", Integer.valueOf(soundPool.load(context, R.raw.eating_dot_1, 1)));
			soundIds.put("eating_dot_2", Integer.valueOf(soundPool.load(context, R.raw.eating_dot_2, 1)));
			soundIds.put("eating_dot_double", Integer.valueOf(soundPool.load(context, R.raw.eating_dot_double, 1)));
			soundIds.put("eating_ghost", Integer.valueOf(soundPool.load(context, R.raw.eating_ghost, 1)));
			soundIds.put("extra_life", Integer.valueOf(soundPool.load(context, R.raw.extra_life, 1)));
			soundIds.put("fruit", Integer.valueOf(soundPool.load(context, R.raw.fruit, 1)));
			soundIds.put("start_music_double", Integer.valueOf(soundPool.load(context, R.raw.start_music_double, 1)));
			soundIds.put("start_music", Integer.valueOf(soundPool.load(context, R.raw.start_music, 1)));
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
					if (count == 7) {
						if (success) {
							ambientPoolAvailable = true;
							game.soundAvailable = soundPoolAvailable && ambientPoolAvailable;							
						}
					}
				}
			});
			ambientIds = new HashMap<String, Integer>();
			ambientIds.put("ambient_1", Integer.valueOf(ambientPool.load(context, R.raw.ambient_1, 1)));
			ambientIds.put("ambient_2", Integer.valueOf(ambientPool.load(context, R.raw.ambient_2, 1)));
			ambientIds.put("ambient_3", Integer.valueOf(ambientPool.load(context, R.raw.ambient_3, 1)));
			ambientIds.put("ambient_4", Integer.valueOf(ambientPool.load(context, R.raw.ambient_4, 1)));
			ambientIds.put("ambient_eyes", Integer.valueOf(ambientPool.load(context, R.raw.ambient_eyes, 1)));
			ambientIds.put("ambient_fright", Integer.valueOf(ambientPool.load(context, R.raw.ambient_fright, 1)));
			ambientIds.put("cutscene", Integer.valueOf(ambientPool.load(context, R.raw.cutscene, 1)));
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
