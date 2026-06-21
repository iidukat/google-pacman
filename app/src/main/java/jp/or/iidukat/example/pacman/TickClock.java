package jp.or.iidukat.example.pacman;

import java.util.Date;

class TickClock {

    private double tickInterval;
    private double lastTimeDelta;
    private long lastTime;
    private long pausedTime;
    private int fpsChoice;
    private boolean canDecreaseFps;
    private int lastTimeSlownessCount;
    int tickMultiplier;

    void init() {
        fpsChoice = 0;
        canDecreaseFps = true;
        applyFps();
    }

    private void applyFps() {
        int fps = GameConstants.FPS_OPTIONS[fpsChoice];
        tickInterval = 1000.0 / fps;
        tickMultiplier = GameConstants.DEFAULT_FPS / fps;
        lastTime = new Date().getTime();
        lastTimeDelta = 0;
        lastTimeSlownessCount = 0;
    }

    void onPaused(long now) {
        pausedTime = now;
    }

    void onResumed() {
        lastTime += new Date().getTime() - pausedTime;
    }

    // Returns the number of extra game steps to run to compensate for accumulated latency.
    int advance(long now) {
        lastTimeDelta += now - lastTime - tickInterval;
        if (lastTimeDelta > 100) {
            lastTimeDelta = 100;
        }
        if (canDecreaseFps && lastTimeDelta > 50) {
            // Count consecutive slow ticks; reduce fps after 20 in a row.
            lastTimeSlownessCount++;
            if (lastTimeSlownessCount == 20) {
                decreaseFps();
            }
        }
        int latencyMultiplier = 0;
        if (lastTimeDelta > tickInterval) {
            latencyMultiplier = (int) Math.floor(lastTimeDelta / tickInterval);
            lastTimeDelta -= tickInterval * latencyMultiplier;
        }
        lastTime = now;
        return latencyMultiplier;
    }

    long getTickIntervalMs() {
        return Math.round(tickInterval);
    }

    private void decreaseFps() {
        if (fpsChoice < GameConstants.FPS_OPTIONS.length - 1) {
            fpsChoice++;
            applyFps();
            if (fpsChoice == GameConstants.FPS_OPTIONS.length - 1) {
                canDecreaseFps = false;
            }
        }
    }
}
