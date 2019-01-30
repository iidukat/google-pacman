package jp.or.iidukat.example.pacman;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.util.Log;

class SoundPlayer {

    private static final String TAG = "SoundPlayer";
    private static final int[] SOUND_RESOURCES = {
        R.raw.death,
        R.raw.eating_dot_1,
        R.raw.eating_dot_2,
        R.raw.eating_ghost,
        R.raw.extra_life,
        R.raw.fruit,
        R.raw.start_music,
    };
    private static final int SOUND_CHANNEL_COUNT = 5;
    private static final int[] AMBIENT_RESOURCES = {
        R.raw.ambient_1,
        R.raw.ambient_2,
        R.raw.ambient_3,
        R.raw.ambient_4,
        R.raw.ambient_eyes,
        R.raw.ambient_fright,
    };
    private static final int AMBIENT_CHANNEL_COUNT = 1;
    private static final int CUTSCENE_RESOURCE_ID = R.raw.cutscene;
    
    private final Context context;
    private SoundPoolManager soundManager;
    private boolean soundAvailable;
    private SoundPoolManager ambientManager;
    private boolean ambientAvailable;
    private AudioClip cutsceneAudioClip;
    private boolean cutsceneAmbientAvailable;
    private String queuedAmbient;
    
    SoundPlayer(Context context) {
        this.context = context;
    }

    void init() {
        cutsceneAudioClip = new AudioClip(context);
        soundManager =
            new SoundPoolManager(
                            context,
                            SOUND_RESOURCES,
                            SOUND_CHANNEL_COUNT);
        ambientManager =
            new SoundPoolManager(
                            context,
                            AMBIENT_RESOURCES,
                            AMBIENT_CHANNEL_COUNT);

        cutsceneAmbientAvailable = cutsceneAudioClip.init();
        if (cutsceneAmbientAvailable) {
            soundManager.init(new SoundPoolManager.AvailabilityNotifier() {
                @Override
                public void notifyAvailability() {
                    soundAvailable = true;
                    if (isAvailable()) {
                        playQueuedAmbient();
                    }
                }
            });
            ambientManager.init(new SoundPoolManager.AvailabilityNotifier() {
                @Override
                public void notifyAvailability() {
                    ambientAvailable = true;
                    if (isAvailable()) {
                        playQueuedAmbient();
                    }
                }
            });
        }
    }

    void playTrack(String track, int channel) {
        if (isAvailable()) {
            soundManager.playTrack(track, channel, false);
        }
    }

    void stopChannel(int channel) {
        if (isAvailable()) {
            soundManager.stopChannel(channel);
        }
    }

    void playAmbient(String track) {
        if (isAvailable()) {
            ambientManager.playTrack(track, 0, true);
            queuedAmbient = null;
        } else {
            queuedAmbient = track;
        }
    }
    
    private void playQueuedAmbient() {
        if (queuedAmbient != null) {
            ambientManager.playTrack(queuedAmbient, 0, true);
            queuedAmbient = null;
        }
    }

    void stopAmbient() {
       queuedAmbient = null;
        if (isAvailable()) {
            ambientManager.stopChannel(0);
        }
    }

    void playCutsceneAmbient() {
        if (isAvailable()) {
            try {
                cutsceneAudioClip.loop();
            } catch (IllegalStateException e) {
                Log.e(TAG, "playing cutscene ambient track is failed.");
                cutsceneAmbientAvailable = false;
            }
        }
    }

    void stopCutsceneAmbient() {
        if (isAvailable()) {
            try {
                cutsceneAudioClip.stop();
            } catch (IllegalStateException e) {
                Log.e(TAG, "stopping cutscene ambient track is failed.");
                cutsceneAmbientAvailable = false;
            }
        }
    }

    boolean isAvailable() {
        return soundAvailable
                    && ambientAvailable
                    && cutsceneAmbientAvailable;
    }

    void destroy() {
        soundManager.destroy();
        ambientManager.destroy();
        cutsceneAudioClip.destroy();
    }

    private static class SoundPoolManager {
        
        private static interface AvailabilityNotifier {
            void notifyAvailability();
        }

        private final Context context;
        private final int[] soundResources;
        private final int[] channels;
        private SoundPool soundPool;
        private final Map<String, Integer> soundIds;

        SoundPoolManager(
                        Context context,
                        int[] soundResources,
                        int channelCount) {
            this.context = context;
            this.soundResources = soundResources;
            this.channels = new int[channelCount];
            this.soundIds = new HashMap<String, Integer>();
        }

        void init(final AvailabilityNotifier notifier) {
            this.soundPool =
                new SoundPool(
                        channels.length,
                        AudioManager.STREAM_MUSIC,
                        0);
            soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
                private int count = 0;
                private boolean success = true;
                @Override
                public void onLoadComplete(
                                    SoundPool soundPool,
                                    int sampleId,
                                    int status) {
                    count++;
                    success = success && (status == 0);
                    if (count == soundResources.length) {
                        if (success) {
                            notifier.notifyAvailability();
                        }
                    }
                }
            });
            for (int res : soundResources) {
                soundIds.put(
                        context.getResources().getResourceEntryName(res),
                        Integer.valueOf(soundPool.load(context, res, 1)));
            }
        }

        void destroy() {
            if (soundPool != null) {
                soundPool.release();
                soundPool = null;
            }
            
            soundIds.clear();
            for (int i = 0; i < channels.length; i++) {
                channels[i] = 0;
            }
        }

        void playTrack(String track, int channel, boolean repeat) {
            if (soundPool == null) {
                return;
            }
            
            Integer id = soundIds.get(track);
            if (id == null) {
                throw new IllegalArgumentException(
                                "invalid track name. : " + track);
            }
            if (channel >= channels.length) {
                throw new IllegalArgumentException(
                                "channel is too large. : " + channel);
            }

            channels[channel] =
                    soundPool.play(
                                id.intValue(),
                                1,
                                1,
                                0,
                                (repeat ? -1 : 0),
                                1);
        }

        void stopChannel(int channel) {
            if (soundPool == null) {
                return;
            }
            
            if (channel >= channels.length) {
                throw new IllegalArgumentException(
                                "channel is too large. : " + channel);
            }

            int id = channels[channel];
            if (id != 0) {
                soundPool.stop(id);
                channels[channel] = 0;
            }
        }
    }

    private static class AudioClip {

        private final Context context;
        private MediaPlayer mPlayer;
        private boolean mPlaying = false;
        private boolean mLoop = false;

        AudioClip(Context context) {
            this.context = context;
        }

        boolean init() {
            mPlayer = MediaPlayer.create(context, CUTSCENE_RESOURCE_ID);
            if (mPlayer == null) {
                return false;
            }
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mPlaying = false;
                    if (mLoop) {
                        play();
                    }
                }
            });
            return true;
        }

        void play() {
            if (mPlaying) {
                return;
            }

            if (mPlayer != null) {
                mPlayer.start();
                mPlaying = true;
            }
        }

        void stop() {
            mLoop = false;
            if (mPlaying) {
                if (mPlayer != null) {
                    mPlayer.pause();
                    mPlayer.seekTo(0);
                }
                mPlaying = false;
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

}
