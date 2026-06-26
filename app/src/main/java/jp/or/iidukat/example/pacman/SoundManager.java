package jp.or.iidukat.example.pacman;

import android.content.Context;
import android.util.Log;

class SoundManager {

    private static final PacmanLogger LOG = new PacmanLogger("PacmanSound");

    private final Context context;
    private SoundPlayer soundPlayer;
    private boolean pacManSound = true;
    private String oldAmbient;
    private int dotEatingChannel;
    private int dotEatingSoundPart = 1;

    SoundManager(Context context) {
        this.context = context;
    }

    // Call on every Activity.onResume() to (re)create the SoundPlayer.
    void reinit() {
        soundPlayer = new SoundPlayer(context);
        soundPlayer.init();
    }

    void destroy() {
        soundPlayer.destroy();
    }

    boolean isAvailable() {
        return soundPlayer != null && soundPlayer.isAvailable();
    }

    boolean isPacManSound() {
        return pacManSound;
    }

    void setPacManSound(boolean val) {
        this.pacManSound = val;
    }

    void playTrack(String track, int channel) {
        playTrack(track, channel, false);
    }

    void playTrack(String track, int channel, boolean noBlank) {
        if (!pacManSound || soundPlayer == null) return;
        if (!noBlank) {
            soundPlayer.stopChannel(channel);
        }
        soundPlayer.playTrack(track, channel);
    }

    void stopChannel(int channel) {
        if (soundPlayer != null) soundPlayer.stopChannel(channel);
    }

    void stopAmbient() {
        LOG.d(() -> "stopAmbient: oldAmbient=" + oldAmbient);
        if (soundPlayer != null) soundPlayer.stopAmbient();
        oldAmbient = null;
    }

    void stopAll() {
        LOG.d(() -> "stopAll: oldAmbient=" + oldAmbient + "\n" + Log.getStackTraceString(new Throwable()));
        stopAmbient();
        for (int i = 0; i < 5; i++) {
            stopChannel(i);
        }
    }

    void playAmbient(String track) {
        if (track == null) {
            LOG.d(() -> "playAmbient: skip (track=null) oldAmbient=" + oldAmbient);
            return;
        }
        if (!pacManSound || soundPlayer == null) {
            LOG.d(() -> "playAmbient: skip (pacManSound=" + pacManSound + " soundPlayerNull=" + (soundPlayer == null) + ") track=" + track);
            return;
        }
        if (track.equals(oldAmbient)) {
            LOG.d(() -> "playAmbient: skip (dedup) track=" + track);
            return;
        }
        LOG.d(() -> "playAmbient: PLAY track=" + track + " (was " + oldAmbient + ")");
        soundPlayer.playAmbient(track);
        oldAmbient = track;
    }

    // Resume the last ambient track after an Activity pause without dedup check.
    void resumeAmbient() {
        LOG.d(() -> "resumeAmbient: oldAmbient=" + oldAmbient);
        if (pacManSound && soundPlayer != null && oldAmbient != null) {
            soundPlayer.playAmbient(oldAmbient);
        }
    }

    void playCutsceneAmbient() {
        if (pacManSound && soundPlayer != null) soundPlayer.playCutsceneAmbient();
    }

    void stopCutsceneAmbient() {
        if (soundPlayer != null) soundPlayer.stopCutsceneAmbient();
    }

    void playDotEatingSound(boolean isOrdinaryPlaying) {
        if (!pacManSound || !isOrdinaryPlaying) return;
        String track = dotEatingSoundPart == 1 ? "eating_dot_1" : "eating_dot_2";
        playTrack(track, 1 + dotEatingChannel, true);
        dotEatingChannel = (dotEatingChannel + 1) % 2;
        dotEatingSoundPart = 3 - dotEatingSoundPart;
    }

    void resetDotEatingSound() {
        dotEatingChannel = 0;
        dotEatingSoundPart = 1;
    }
}
