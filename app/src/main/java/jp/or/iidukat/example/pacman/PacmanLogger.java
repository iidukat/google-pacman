package jp.or.iidukat.example.pacman;

import android.util.Log;

import java.util.function.Supplier;

class PacmanLogger {

    interface Backend {
        boolean isLoggable(String tag, int level);
        void log(int level, String tag, String msg);
    }

    private static final Backend ANDROID = new Backend() {
        @Override
        public boolean isLoggable(String tag, int level) {
            return Log.isLoggable(tag, level);
        }

        @Override
        public void log(int level, String tag, String msg) {
            Log.println(level, tag, msg);
        }
    };

    private final String tag;
    private final Backend backend;

    PacmanLogger(String tag) {
        this(tag, ANDROID);
    }

    PacmanLogger(String tag, Backend backend) {
        this.tag = tag;
        this.backend = backend;
    }

    void d(Supplier<String> msg) {
        if (backend.isLoggable(tag, Log.DEBUG)) {
            backend.log(Log.DEBUG, tag, msg.get());
        }
    }

    void w(Supplier<String> msg) {
        if (backend.isLoggable(tag, Log.WARN)) {
            backend.log(Log.WARN, tag, msg.get());
        }
    }
}
