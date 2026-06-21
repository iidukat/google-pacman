package jp.or.iidukat.example.pacman;

class GameConstants {
    static final int DEFAULT_FPS = 90;
    static final int[] FPS_OPTIONS = { 90, 45, 30 };

    static final double[] EVENT_TIME_TABLE = {
        0.16f,
        0.23f,
        1,
        1,
        2.23f,
        0.3f,
        1.9f,
        2.23f,
        1.9f,
        5,
        1.9f,
        1.18f,
        0.3f,
        0.5f,
        1.9f,
        9,
        10,
        0.26f
    };

    static final double FRIGHT_BLINK_SECS = EVENT_TIME_TABLE[1];
}
