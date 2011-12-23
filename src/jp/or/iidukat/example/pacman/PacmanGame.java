package jp.or.iidukat.example.pacman;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.or.iidukat.example.pacman.entity.PlayfieldActor;
import jp.or.iidukat.example.pacman.entity.PlayfieldActor.CurrentSpeed;
import jp.or.iidukat.example.pacman.entity.CutsceneActor;
import jp.or.iidukat.example.pacman.entity.CutsceneBlinky;
import jp.or.iidukat.example.pacman.entity.CutsceneField;
import jp.or.iidukat.example.pacman.entity.CutscenePacman;
import jp.or.iidukat.example.pacman.entity.CutsceneSteak;
import jp.or.iidukat.example.pacman.entity.Entity;
import jp.or.iidukat.example.pacman.entity.Fruit;
import jp.or.iidukat.example.pacman.entity.Ghost;
import jp.or.iidukat.example.pacman.entity.Ghost.GhostMode;
import jp.or.iidukat.example.pacman.entity.Level;
import jp.or.iidukat.example.pacman.entity.Lives;
import jp.or.iidukat.example.pacman.entity.Pacman;
import jp.or.iidukat.example.pacman.entity.PacmanCanvas;
import jp.or.iidukat.example.pacman.entity.Playfield;
import jp.or.iidukat.example.pacman.entity.Playfield.Door;
import jp.or.iidukat.example.pacman.entity.Playfield.PathElement;
import jp.or.iidukat.example.pacman.entity.Playfield.PathElement.Dot;
import jp.or.iidukat.example.pacman.entity.Score;
import jp.or.iidukat.example.pacman.entity.ScoreLabel;
import jp.or.iidukat.example.pacman.entity.Sound;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.FloatMath;
import android.view.MotionEvent;

public class PacmanGame {

    private static final int DEFAULT_KILL_SCREEN_LEVEL = 256;
    
    // After a level restart, when a number of dots eaten by the player has reached the threshold,
    // each ghost leaves from the pen.
    // This is the thresholds of each ghost.
    private static final int[] PEN_LEAVING_FOOD_LIMITS = { 0, 7, 17, 32 };

    private static final float[] EVENT_TIME_TABLE = {
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

    private static final int[] FPS_OPTIONS = { 90, 45, 30, }; // fps option
    private static final int DEFAULT_FPS = FPS_OPTIONS[0]; // default fps

    public static class LevelConfig {
        private final float ghostSpeed;
        private final float ghostTunnelSpeed;
        private final float playerSpeed;
        private final float dotEatingSpeed;
        private final float ghostFrightSpeed;
        private final float playerFrightSpeed;
        private final float dotEatingFrightSpeed;
        private final int elroyDotsLeftPart1;
        private final float elroySpeedPart1;
        private final int elroyDotsLeftPart2;
        private final float elroySpeedPart2;
        private final int frightTime;
        private int frightTotalTime;
        private final int frightBlinkCount;
        private final int fruit;
        private final int fruitScore;
        private final float[] ghostModeSwitchTimes;
        private final int penForceTime;
        private final float[] penLeavingLimits;
        private final int cutsceneId;

        private static class Builder {
            private float ghostSpeed;
            private float ghostTunnelSpeed;
            private float playerSpeed;
            private float dotEatingSpeed;
            private float ghostFrightSpeed;
            private float playerFrightSpeed;
            private float dotEatingFrightSpeed;
            private int elroyDotsLeftPart1;
            private float elroySpeedPart1;
            private int elroyDotsLeftPart2;
            private float elroySpeedPart2;
            private int frightTime;
            private int frightBlinkCount;
            private int fruit;
            private int fruitScore;
            private float[] ghostModeSwitchTimes;
            private int penForceTime;
            private float[] penLeavingLimits;
            private int cutsceneId;

            Builder ghostSpeed(float val) {
                this.ghostSpeed = val;
                return this;
            }

            Builder ghostTunnelSpeed(float val) {
                this.ghostTunnelSpeed = val;
                return this;
            }

            Builder playerSpeed(float val) {
                this.playerSpeed = val;
                return this;
            }

            Builder dotEatingSpeed(float val) {
                this.dotEatingSpeed = val;
                return this;
            }

            Builder ghostFrightSpeed(float val) {
                this.ghostFrightSpeed = val;
                return this;
            }

            Builder playerFrightSpeed(float val) {
                this.playerFrightSpeed = val;
                return this;
            }

            Builder dotEatingFrightSpeed(float val) {
                this.dotEatingFrightSpeed = val;
                return this;
            }

            Builder elroyDotsLeftPart1(int val) {
                this.elroyDotsLeftPart1 = val;
                return this;
            }

            Builder elroySpeedPart1(float val) {
                this.elroySpeedPart1 = val;
                return this;
            }

            Builder elroyDotsLeftPart2(int val) {
                this.elroyDotsLeftPart2 = val;
                return this;
            }

            Builder elroySpeedPart2(float val) {
                this.elroySpeedPart2 = val;
                return this;
            }

            Builder frightTime(int val) {
                this.frightTime = val;
                return this;
            }

            Builder frightBlinkCount(int val) {
                this.frightBlinkCount = val;
                return this;
            }

            Builder fruit(int val) {
                this.fruit = val;
                return this;
            }

            Builder fruitScore(int val) {
                this.fruitScore = val;
                return this;
            }

            Builder ghostModeSwitchTimes(float[] val) {
                this.ghostModeSwitchTimes = val;
                return this;
            }

            Builder penForceTime(int val) {
                this.penForceTime = val;
                return this;
            }

            Builder penLeavingLimits(float[] val) {
                this.penLeavingLimits = val;
                return this;
            }

            Builder cutsceneId(int val) {
                this.cutsceneId = val;
                return this;
            }

            LevelConfig build() {
                return new LevelConfig(this);
            }
        }

        private LevelConfig(Builder builder) {
            this.ghostSpeed = builder.ghostSpeed;
            this.ghostTunnelSpeed = builder.ghostTunnelSpeed;
            this.playerSpeed = builder.playerSpeed;
            this.dotEatingSpeed = builder.dotEatingSpeed;
            this.ghostFrightSpeed = builder.ghostFrightSpeed;
            this.playerFrightSpeed = builder.playerFrightSpeed;
            this.dotEatingFrightSpeed = builder.dotEatingFrightSpeed;
            this.elroyDotsLeftPart1 = builder.elroyDotsLeftPart1;
            this.elroySpeedPart1 = builder.elroySpeedPart1;
            this.elroyDotsLeftPart2 = builder.elroyDotsLeftPart2;
            this.elroySpeedPart2 = builder.elroySpeedPart2;
            this.frightBlinkCount = builder.frightBlinkCount;
            this.fruit = builder.fruit;
            this.fruitScore = builder.fruitScore;
            this.ghostModeSwitchTimes = builder.ghostModeSwitchTimes;
            this.penForceTime = builder.penForceTime;
            this.penLeavingLimits = builder.penLeavingLimits;
            this.cutsceneId = builder.cutsceneId;
            
            this.frightTime = builder.frightTime * DEFAULT_FPS;
        }

        public float getGhostSpeed() {
            return ghostSpeed;
        }

        public float getGhostTunnelSpeed() {
            return ghostTunnelSpeed;
        }

        public float getGhostFrightSpeed() {
            return ghostFrightSpeed;
        }

        public int getElroyDotsLeftPart1() {
            return elroyDotsLeftPart1;
        }

        public int getElroyDotsLeftPart2() {
            return elroyDotsLeftPart2;
        }

        public int getFrightTime() {
            return frightTime;
        }

        public int getFrightTotalTime() {
            return frightTotalTime;
        }
        
        public int getFruit() {
            return fruit;
        }

    }

    // Configurations of each level.
    private static final LevelConfig[] LEVEL_CONFIGS = {
        new LevelConfig.Builder().build(),
        new LevelConfig.Builder()
                .ghostSpeed(0.75f)
                .ghostTunnelSpeed(0.4f)
                .playerSpeed(0.8f)
                .dotEatingSpeed(0.71f)
                .ghostFrightSpeed(0.5f)
                .playerFrightSpeed(0.9f)
                .dotEatingFrightSpeed(0.79f)
                .elroyDotsLeftPart1(20)
                .elroySpeedPart1(0.8f)
                .elroyDotsLeftPart2(10)
                .elroySpeedPart2(0.85f)
                .frightTime(6)
                .frightBlinkCount(5)
                .fruit(1)
                .fruitScore(100)
                .ghostModeSwitchTimes(new float[] { 7, 20, 7, 20, 5, 20, 5, 1, })
                .penForceTime(4)
                .penLeavingLimits(new float[] { 0, 0, 30, 60, })
                .build(),
        new LevelConfig.Builder()
                .ghostSpeed(0.85f)
                .ghostTunnelSpeed(0.45f)
                .playerSpeed(0.9f)
                .dotEatingSpeed(0.79f)
                .ghostFrightSpeed(0.55f)
                .playerFrightSpeed(0.95f)
                .dotEatingFrightSpeed(0.83f)
                .elroyDotsLeftPart1(30)
                .elroySpeedPart1(0.9f)
                .elroyDotsLeftPart2(15)
                .elroySpeedPart2(0.95f)
                .frightTime(5)
                .frightBlinkCount(5)
                .fruit(2)
                .fruitScore(300)
                .ghostModeSwitchTimes(new float[] { 7, 20, 7, 20, 5, 1033, 1f / 60, 1, })
                .penForceTime(4)
                .penLeavingLimits(new float[] { 0, 0, 0, 50, })
                .cutsceneId(1)
                .build(),
        new LevelConfig.Builder()
                .ghostSpeed(0.85f)
                .ghostTunnelSpeed(0.45f)
                .playerSpeed(0.9f)
                .dotEatingSpeed(0.79f)
                .ghostFrightSpeed(0.55f)
                .playerFrightSpeed(0.95f)
                .dotEatingFrightSpeed(0.83f)
                .elroyDotsLeftPart1(40)
                .elroySpeedPart1(0.9f)
                .elroyDotsLeftPart2(20)
                .elroySpeedPart2(0.95f)
                .frightTime(4)
                .frightBlinkCount(5)
                .fruit(3)
                .fruitScore(500)
                .ghostModeSwitchTimes(new float[] { 7, 20, 7, 20, 5, 1033, 1f / 60, 1, })
                .penForceTime(4)
                .penLeavingLimits(new float[] { 0, 0, 0, 0, })
                .build(),
        new LevelConfig.Builder()
                .ghostSpeed(0.85f)
                .ghostTunnelSpeed(0.45f)
                .playerSpeed(0.9f)
                .dotEatingSpeed(0.79f)
                .ghostFrightSpeed(0.55f)
                .playerFrightSpeed(0.95f)
                .dotEatingFrightSpeed(0.83f)
                .elroyDotsLeftPart1(40)
                .elroySpeedPart1(0.9f)
                .elroyDotsLeftPart2(20)
                .elroySpeedPart2(0.95f)
                .frightTime(3)
                .frightBlinkCount(5)
                .fruit(3)
                .fruitScore(500)
                .ghostModeSwitchTimes(new float[] { 7, 20, 7, 20, 5, 1033, 1f / 60, 1, })
                .penForceTime(4)
                .penLeavingLimits(new float[] { 0, 0, 0, 0, })
                .build(),
        new LevelConfig.Builder()
                .ghostSpeed(0.95f)
                .ghostTunnelSpeed(0.5f)
                .playerSpeed(1)
                .dotEatingSpeed(0.87f)
                .ghostFrightSpeed(0.6f)
                .playerFrightSpeed(1)
                .dotEatingFrightSpeed(0.87f)
                .elroyDotsLeftPart1(40)
                .elroySpeedPart1(1)
                .elroyDotsLeftPart2(20)
                .elroySpeedPart2(1.05f)
                .frightTime(2)
                .frightBlinkCount(5)
                .fruit(4)
                .fruitScore(700)
                .ghostModeSwitchTimes(new float[] { 5, 20, 5, 20, 5, 1037, 1f / 60, 1, })
                .penForceTime(3)
                .penLeavingLimits(new float[] { 0, 0, 0, 0, })
                .cutsceneId(2)
                .build(),
        new LevelConfig.Builder()
                .ghostSpeed(0.95f)
                .ghostTunnelSpeed(0.5f)
                .playerSpeed(1)
                .dotEatingSpeed(0.87f)
                .ghostFrightSpeed(0.6f)
                .playerFrightSpeed(1)
                .dotEatingFrightSpeed(0.87f)
                .elroyDotsLeftPart1(50)
                .elroySpeedPart1(1)
                .elroyDotsLeftPart2(25)
                .elroySpeedPart2(1.05f)
                .frightTime(5)
                .frightBlinkCount(5)
                .fruit(4)
                .fruitScore(700)
                .ghostModeSwitchTimes(new float[] { 5, 20, 5, 20, 5, 1037, 1f / 60, 1, })
                .penForceTime(3)
                .penLeavingLimits(new float[] { 0, 0, 0, 0, })
                .build(),
        new LevelConfig.Builder()
                .ghostSpeed(0.95f)
                .ghostTunnelSpeed(0.5f)
                .playerSpeed(1)
                .dotEatingSpeed(0.87f)
                .ghostFrightSpeed(0.6f)
                .playerFrightSpeed(1)
                .dotEatingFrightSpeed(0.87f)
                .elroyDotsLeftPart1(50)
                .elroySpeedPart1(1)
                .elroyDotsLeftPart2(25)
                .elroySpeedPart2(1.05f)
                .frightTime(2)
                .frightBlinkCount(5)
                .fruit(5)
                .fruitScore(1000)
                .ghostModeSwitchTimes(new float[] { 5, 20, 5, 20, 5, 1037, 1f / 60, 1, })
                .penForceTime(3)
                .penLeavingLimits(new float[] { 0, 0, 0, 0, })
                .build(),
        new LevelConfig.Builder()
                .ghostSpeed(0.95f)
                .ghostTunnelSpeed(0.5f)
                .playerSpeed(1)
                .dotEatingSpeed(0.87f)
                .ghostFrightSpeed(0.6f)
                .playerFrightSpeed(1)
                .dotEatingFrightSpeed(0.87f)
                .elroyDotsLeftPart1(50)
                .elroySpeedPart1(1)
                .elroyDotsLeftPart2(25)
                .elroySpeedPart2(1.05f)
                .frightTime(2)
                .frightBlinkCount(5)
                .fruit(5)
                .fruitScore(1000)
                .ghostModeSwitchTimes(new float[] { 5, 20, 5, 20, 5, 1037, 1f / 60, 1, })
                .penForceTime(3)
                .penLeavingLimits(new float[] { 0, 0, 0, 0, })
                .build(),
        new LevelConfig.Builder()
                .ghostSpeed(0.95f)
                .ghostTunnelSpeed(0.5f)
                .playerSpeed(1)
                .dotEatingSpeed(0.87f)
                .ghostFrightSpeed(0.6f)
                .playerFrightSpeed(1)
                .dotEatingFrightSpeed(0.87f)
                .elroyDotsLeftPart1(60)
                .elroySpeedPart1(1)
                .elroyDotsLeftPart2(30)
                .elroySpeedPart2(1.05f)
                .frightTime(1)
                .frightBlinkCount(3)
                .fruit(6)
                .fruitScore(2000)
                .ghostModeSwitchTimes(new float[] { 5, 20, 5, 20, 5, 1037, 1f / 60, 1, })
                .penForceTime(3)
                .penLeavingLimits(new float[] { 0, 0, 0, 0, })
                .cutsceneId(3)
                .build(),
        new LevelConfig.Builder()
                .ghostSpeed(0.95f)
                .ghostTunnelSpeed(0.5f)
                .playerSpeed(1)
                .dotEatingSpeed(0.87f)
                .ghostFrightSpeed(0.6f)
                .playerFrightSpeed(1)
                .dotEatingFrightSpeed(0.87f)
                .elroyDotsLeftPart1(60)
                .elroySpeedPart1(1)
                .elroyDotsLeftPart2(30)
                .elroySpeedPart2(1.05f)
                .frightTime(5)
                .frightBlinkCount(5)
                .fruit(6)
                .fruitScore(2000)
                .ghostModeSwitchTimes(new float[] { 5, 20, 5, 20, 5, 1037, 1f / 60, 1, })
                .penForceTime(3)
                .penLeavingLimits(new float[] { 0, 0, 0, 0, })
                .build(),
        new LevelConfig.Builder()
                .ghostSpeed(0.95f)
                .ghostTunnelSpeed(0.5f)
                .playerSpeed(1)
                .dotEatingSpeed(0.87f)
                .ghostFrightSpeed(0.6f)
                .playerFrightSpeed(1)
                .dotEatingFrightSpeed(0.87f)
                .elroyDotsLeftPart1(60)
                .elroySpeedPart1(1)
                .elroyDotsLeftPart2(30)
                .elroySpeedPart2(1.05f)
                .frightTime(2)
                .frightBlinkCount(5)
                .fruit(7)
                .fruitScore(3000)
                .ghostModeSwitchTimes(new float[] { 5, 20, 5, 20, 5, 1037, 1f / 60, 1, })
                .penForceTime(3)
                .penLeavingLimits(new float[] { 0, 0, 0, 0, })
                .build(),
        new LevelConfig.Builder()
                .ghostSpeed(0.95f)
                .ghostTunnelSpeed(0.5f)
                .playerSpeed(1)
                .dotEatingSpeed(0.87f)
                .ghostFrightSpeed(0.6f)
                .playerFrightSpeed(1)
                .dotEatingFrightSpeed(0.87f)
                .elroyDotsLeftPart1(80)
                .elroySpeedPart1(1)
                .elroyDotsLeftPart2(40)
                .elroySpeedPart2(1.05f)
                .frightTime(1)
                .frightBlinkCount(3)
                .fruit(7)
                .fruitScore(3000)
                .ghostModeSwitchTimes(new float[] { 5, 20, 5, 20, 5, 1037, 1f / 60, 1, })
                .penForceTime(3)
                .penLeavingLimits(new float[] { 0, 0, 0, 0, })
                .build(),
        new LevelConfig.Builder()
                .ghostSpeed(0.95f)
                .ghostTunnelSpeed(0.5f)
                .playerSpeed(1)
                .dotEatingSpeed(0.87f)
                .ghostFrightSpeed(0.6f)
                .playerFrightSpeed(1)
                .dotEatingFrightSpeed(0.87f)
                .elroyDotsLeftPart1(80)
                .elroySpeedPart1(1)
                .elroyDotsLeftPart2(40)
                .elroySpeedPart2(1.05f)
                .frightTime(1)
                .frightBlinkCount(3)
                .fruit(8)
                .fruitScore(5000)
                .ghostModeSwitchTimes(new float[] { 5, 20, 5, 20, 5, 1037, 1f / 60, 1, })
                .penForceTime(3)
                .penLeavingLimits(new float[] { 0, 0, 0, 0, })
                .cutsceneId(3)
                .build(),
        new LevelConfig.Builder()
                .ghostSpeed(0.95f)
                .ghostTunnelSpeed(0.5f)
                .playerSpeed(1)
                .dotEatingSpeed(0.87f)
                .ghostFrightSpeed(0.6f)
                .playerFrightSpeed(1)
                .dotEatingFrightSpeed(0.87f)
                .elroyDotsLeftPart1(80)
                .elroySpeedPart1(1)
                .elroyDotsLeftPart2(40)
                .elroySpeedPart2(1.05f)
                .frightTime(3)
                .frightBlinkCount(5)
                .fruit(8)
                .fruitScore(5000)
                .ghostModeSwitchTimes(new float[] { 5, 20, 5, 20, 5, 1037, 1f / 60, 1, })
                .penForceTime(3)
                .penLeavingLimits(new float[] { 0, 0, 0, 0, })
                .build(),
        new LevelConfig.Builder()
                .ghostSpeed(0.95f)
                .ghostTunnelSpeed(0.5f)
                .playerSpeed(1)
                .dotEatingSpeed(0.87f)
                .ghostFrightSpeed(0.6f)
                .playerFrightSpeed(1)
                .dotEatingFrightSpeed(0.87f)
                .elroyDotsLeftPart1(100)
                .elroySpeedPart1(1)
                .elroyDotsLeftPart2(50)
                .elroySpeedPart2(1.05f)
                .frightTime(1)
                .frightBlinkCount(3)
                .fruit(8)
                .fruitScore(5000)
                .ghostModeSwitchTimes(new float[] { 5, 20, 5, 20, 5, 1037, 1f / 60, 1, })
                .penForceTime(3)
                .penLeavingLimits(new float[] { 0, 0, 0, 0, })
                .build(),
        new LevelConfig.Builder()
                .ghostSpeed(0.95f)
                .ghostTunnelSpeed(0.5f)
                .playerSpeed(1)
                .dotEatingSpeed(0.87f)
                .ghostFrightSpeed(0.6f)
                .playerFrightSpeed(1)
                .dotEatingFrightSpeed(0.87f)
                .elroyDotsLeftPart1(100)
                .elroySpeedPart1(1)
                .elroyDotsLeftPart2(50)
                .elroySpeedPart2(1.05f)
                .frightTime(1)
                .frightBlinkCount(3)
                .fruit(8)
                .fruitScore(5000)
                .ghostModeSwitchTimes(new float[] { 5, 20, 5, 20, 5, 1037, 1f / 60, 1, })
                .penForceTime(3)
                .penLeavingLimits(new float[] { 0, 0, 0, 0, })
                .build(),
        new LevelConfig.Builder()
                .ghostSpeed(0.95f)
                .ghostTunnelSpeed(0.5f)
                .playerSpeed(1)
                .dotEatingSpeed(0.87f)
                .ghostFrightSpeed(0.6f)
                .playerFrightSpeed(1)
                .dotEatingFrightSpeed(0.87f)
                .elroyDotsLeftPart1(100)
                .elroySpeedPart1(1)
                .elroyDotsLeftPart2(50)
                .elroySpeedPart2(1.05f)
                .frightTime(0)
                .frightBlinkCount(0)
                .fruit(8)
                .fruitScore(5000)
                .ghostModeSwitchTimes(new float[] { 5, 20, 5, 20, 5, 1037, 1f / 60, 1, })
                .penForceTime(3)
                .penLeavingLimits(new float[] { 0, 0, 0, 0, })
                .cutsceneId(3)
                .build(),
        new LevelConfig.Builder()
                .ghostSpeed(0.95f)
                .ghostTunnelSpeed(0.5f)
                .playerSpeed(1)
                .dotEatingSpeed(0.87f)
                .ghostFrightSpeed(0.6f)
                .playerFrightSpeed(1)
                .dotEatingFrightSpeed(0.87f)
                .elroyDotsLeftPart1(100)
                .elroySpeedPart1(1)
                .elroyDotsLeftPart2(50)
                .elroySpeedPart2(1.05f)
                .frightTime(1)
                .frightBlinkCount(3)
                .fruit(8)
                .fruitScore(5000)
                .ghostModeSwitchTimes(new float[] { 5, 20, 5, 20, 5, 1037, 1f / 60, 1, })
                .penForceTime(3)
                .penLeavingLimits(new float[] { 0, 0, 0, 0, })
                .build(),
        new LevelConfig.Builder()
                .ghostSpeed(0.95f)
                .ghostTunnelSpeed(0.5f)
                .playerSpeed(1)
                .dotEatingSpeed(0.87f)
                .ghostFrightSpeed(0.6f)
                .playerFrightSpeed(1)
                .dotEatingFrightSpeed(0.87f)
                .elroyDotsLeftPart1(120)
                .elroySpeedPart1(1)
                .elroyDotsLeftPart2(60)
                .elroySpeedPart2(1.05f)
                .frightTime(0)
                .frightBlinkCount(0)
                .fruit(8)
                .fruitScore(5000)
                .ghostModeSwitchTimes(new float[] { 5, 20, 5, 20, 5, 1037, 1f / 60, 1, })
                .penForceTime(3)
                .penLeavingLimits(new float[] { 0, 0, 0, 0, })
                .build(),
        new LevelConfig.Builder()
                .ghostSpeed(0.95f)
                .ghostTunnelSpeed(0.5f)
                .playerSpeed(1)
                .dotEatingSpeed(0.87f)
                .ghostFrightSpeed(0.6f)
                .playerFrightSpeed(1)
                .dotEatingFrightSpeed(0.87f)
                .elroyDotsLeftPart1(120)
                .elroySpeedPart1(1)
                .elroyDotsLeftPart2(60)
                .elroySpeedPart2(1.05f)
                .frightTime(0)
                .frightBlinkCount(0)
                .fruit(8)
                .fruitScore(5000)
                .ghostModeSwitchTimes(new float[] { 5, 20, 5, 20, 5, 1037, 1f / 60, 1, })
                .penForceTime(3)
                .penLeavingLimits(new float[] { 0, 0, 0, 0, })
                .build(),
        new LevelConfig.Builder()
                .ghostSpeed(0.95f)
                .ghostTunnelSpeed(0.5f)
                .playerSpeed(0.9f)
                .dotEatingSpeed(0.79f)
                .ghostFrightSpeed(0.75f)
                .playerFrightSpeed(0.9f)
                .dotEatingFrightSpeed(0.79f)
                .elroyDotsLeftPart1(120)
                .elroySpeedPart1(1)
                .elroyDotsLeftPart2(60)
                .elroySpeedPart2(1.05f)
                .frightTime(0)
                .frightBlinkCount(0)
                .fruit(8)
                .fruitScore(5000)
                .ghostModeSwitchTimes(new float[] { 5, 20, 5, 20, 5, 1037, 1f / 60, 1, })
                .penForceTime(3)
                .penLeavingLimits(new float[] { 0, 0, 0, 0, })
                .build(),
    };
    

    // Cutscene Animation
    private static class Cutscene {
        private final Class<?>[] actors;
        private final float[] sequenceTimes;

        Cutscene(Class<?>[] actors, float[] sequenceTimes) {
            this.actors = actors;
            this.sequenceTimes = sequenceTimes;
        }
    }

    private static final Map<Integer, Cutscene> CUTSCENES;
    static {
        Map<Integer, Cutscene> css = new HashMap<Integer, Cutscene>();
        css.put(
            Integer.valueOf(1),
            new Cutscene(
                new Class<?>[] { CutscenePacman.class, CutsceneBlinky.class },
                new float[] { 5.5f, 0.1f, 9 }));
        css.put(
            Integer.valueOf(2),
            new Cutscene(
                new Class<?>[] {
                    CutscenePacman.class,
                    CutsceneBlinky.class,
                    CutsceneSteak.class
                },
                new float[] { 2.7f, 1, 1.3f, 1, 2.5f }));
        css.put(
            Integer.valueOf(3),
            new Cutscene(
                new Class<?>[] { CutscenePacman.class, CutsceneBlinky.class },
                new float[] { 5.3f, 5.3f }));
        CUTSCENES = Collections.unmodifiableMap(css);
    }
    
    public static enum GameplayMode {
        ORDINARY_PLAYING(0), GHOST_DIED(1), PLAYER_DYING(2), PLAYER_DIED(3),
        NEWGAME_STARTING(4), NEWGAME_STARTED(5), GAME_RESTARTING(6), GAME_RESTARTED(7),
        GAMEOVER(8), LEVEL_BEING_COMPLETED(9), LEVEL_COMPLETED(10),
        TRANSITION_INTO_NEXT_SCENE(11), CUTSCENE(13), KILL_SCREEN(14);
        
        private final int mode;
        
        private GameplayMode(int mode) {
            this.mode = mode;
        }

        public int getMode() {
            return mode;
        }

    }

    private final Context context;
    GameView view;
    private Bitmap sourceImage;
    private SoundPlayer soundPlayer;
    
    private boolean paused;
    private boolean started;
    private long randSeed;

    private PacmanCanvas canvasEl;

    private float touchDX;
    private float touchDY;
    private float touchStartX;
    private float touchStartY;
    private boolean touchCanceld = true;

    private long score;
    private boolean extraLifeAwarded;
    private int lives = 3;
    private int level = 0;
    private int killScreenLevel = DEFAULT_KILL_SCREEN_LEVEL;
    private LevelConfig levelConfig;
    private long globalTime = 0;

    private int frightModeTime = 0;
    private int intervalTime = 0;
    private float gameplayModeTime = 0;
    private int fruitTime = 0;
    private int forcePenLeaveTime;
    private int ghostModeSwitchPos = 0;
    private float ghostModeTime;
    private boolean ghostExitingPenNow = false;
    private int ghostEyesCount = 0;
    private boolean tilesChanged = false;
    private int dotEatingChannel;
    private int dotEatingSoundPart;
    private GameplayMode gameplayMode;
    private float[] timing;
    private boolean alternatePenLeavingScheme;
    private int alternateDotCount;
    private boolean lostLifeOnThisLevel;

    private GhostMode lastMainGhostMode;
    private GhostMode mainGhostMode;

    private float currentPlayerSpeed;
    private float currentDotEatingSpeed;
    private float cruiseElroySpeed;
    private Map<Float, Boolean[]> speedIntervals;

    private int modeScoreMultiplier;
    private boolean fruitShown;
    private Ghost ghostBeingEaten;

    private boolean pacManSound = true;

    private Cutscene cutscene;
    private int cutsceneId;
    private int cutsceneSequenceId;
    private float cutsceneTime;

    private float tickInterval;
    private float lastTimeDelta;
    private long lastTime;
    private long pausedTime;
    private int fpsChoice;
    private int fps;
    private boolean canDecreaseFps;
    private int lastTimeSlownessCount;
    private int tickMultiplier;

    PacmanGame(Context context) {
        this.context = context;
    }

    public float rand() {
        long b = 4294967296L;
        long c = 134775813L;
        c = c * randSeed + 1;
        return (randSeed = c % b) / (float) b;
    }

    private void seed(long s) {
        this.randSeed = s;
    }

    private void restartActors() {
        getPacman().arrange();

        for (PlayfieldActor ghost : getGhosts())
            ghost.arrange();
    }

    private void createPlayfield() {
        canvasEl.createPlayfield(this);
    }
    
    private void resetPlayfield() {
        getPlayfieldEl().reset();
    }
    
    private void createReadyElement() {
        getPlayfieldEl().createReadyElement();
    }
    
    private void removeReadyElement() {
        getPlayfieldEl().removeReady();
    }
    
    private void createGameOverElement() {
        getPlayfieldEl().createGameOverElement();
    }

    private void canvasClicked(float x, float y) {
        if (handleSoundIconClick(x, y)) {
            return;
        }

        float[] offset = canvasEl.getAbsolutePos();
        float cx = x - offset[1];
        float cy = y - offset[0];
        
        Pacman pacman = getPacman();
        float px = pacman.getFieldX() + 48;
        float py = pacman.getFieldY() + 32;
        float xdiff = Math.abs(cx - px);
        float ydiff = Math.abs(cy - py);
        if (xdiff > 8 && ydiff < xdiff) {
            pacman.setRequestedDir(cx > px ? Direction.RIGHT : Direction.LEFT);
        } else if (ydiff > 8 && xdiff < ydiff) {
            pacman.setRequestedDir(cy > py ? Direction.DOWN : Direction.UP);
        }
    }

    private boolean handleSoundIconClick(float x, float y) {
        if (!soundPlayer.isAvailable() || !getSoundEl().isVisible()) {
            return false;
        }

        Entity soundEl = getSoundEl();
        float[] pos = soundEl.getAbsolutePos();
        if (pos[1] <= x && x <= pos[1] + soundEl.getWidth()) {
            if (pos[0] <= y && y <= pos[0] + soundEl.getHeight()) {
                toggleSound();
                return true;
            }
        }

        return false;
    }

    void handleTouchStart(MotionEvent e) {
        touchDX = 0;
        touchDY = 0;
        if (e.getPointerCount() == 1) {
            touchCanceld = false;
            touchStartX = e.getX(0);
            touchStartY = e.getY(0);
        }
    }

    void handleTouchMove(MotionEvent e) {
        if (touchCanceld) {
            return;
        }

        if (e.getPointerCount() > 1) {
            cancelTouch();
        } else {
            touchDX = e.getX(0) - touchStartX;
            touchDY = e.getY(0) - touchStartY;
        }
    }

    void handleTouchEnd(MotionEvent e) {
        if (touchCanceld) {
            return;
        }

        float absDx = Math.abs(touchDX);
        float absDy = Math.abs(touchDY);
        Pacman pacman = getPacman();
        if (absDx < 8 && absDy < 8) {
            canvasClicked(touchStartX, touchStartY);
        } else if (absDx > 15 && absDy < absDx * 2 / 3) {
            pacman.setRequestedDir(touchDX > 0 ? Direction.RIGHT : Direction.LEFT);
        } else if (absDy > 15 && absDx < absDy * 2 / 3) {
            pacman.setRequestedDir(touchDY > 0 ? Direction.DOWN : Direction.UP);
        }
        cancelTouch();
    }

    private void cancelTouch() {
        touchStartX = Float.NaN;
        touchStartY = Float.NaN;
        touchCanceld = true;
    }

    private void startGameplay() {
        score = 0;
        extraLifeAwarded = false;
        lives = 3;
        level = 0;
        globalTime = 0;
        newLevel(true);
    }

    private void restartGameplay(boolean newGame) {
        seed(0);
        frightModeTime = 0;
        intervalTime = 0;
        gameplayModeTime = 0;
        fruitTime = 0;
        ghostModeSwitchPos = 0;
        ghostModeTime = levelConfig.ghostModeSwitchTimes[0] * DEFAULT_FPS;
        ghostExitingPenNow = false;
        ghostEyesCount = 0;
        tilesChanged = false;
        updateCruiseElroySpeed();
        hideFruit();
        resetForcePenLeaveTime();
        restartActors();
        updateActorPositions();
        switchMainGhostMode(GhostMode.SCATTER, true);
        for (int i = 1; i < 4; i++) {
            getGhosts()[i].switchGhostMode(GhostMode.IN_PEN);
        }
        dotEatingChannel = 0;
        dotEatingSoundPart = 1;

        if (newGame) {
            changeGameplayMode(GameplayMode.NEWGAME_STARTING);
        } else {
            changeGameplayMode(GameplayMode.GAME_RESTARTING);
        }
//      changeGameplayMode(GameplayMode.LEVEL_COMPLETED); // for Cutscene debug
    }

    private void newGame() {
        createChrome();
        createPlayfield();
        startGameplay();
    }

    private void killScreen() {
        seed(0);
        canvasEl.setVisibility(true);
        getPlayfieldEl().killScreen();
        changeGameplayMode(GameplayMode.KILL_SCREEN);
    }

    private void newLevel(boolean newGame) {
        level++;
        levelConfig =
            level >= LEVEL_CONFIGS.length
                ? LEVEL_CONFIGS[LEVEL_CONFIGS.length - 1]
                : LEVEL_CONFIGS[level];
        levelConfig.frightTotalTime =
            levelConfig.frightTime
            + ((int) timing[1]) * (levelConfig.frightBlinkCount * 2 - 1);
        alternatePenLeavingScheme = false;
        lostLifeOnThisLevel = false;
        updateChrome();
        resetPlayfield();
        restartGameplay(newGame);
        if (level == killScreenLevel) {
            killScreen();
        }
    }

    private void newLife() {
        lostLifeOnThisLevel = true;
        alternatePenLeavingScheme = true;
        alternateDotCount = 0;
        lives--;
        updateChromeLives();

        if (lives == -1) {
            changeGameplayMode(GameplayMode.GAMEOVER);
        } else {
            restartGameplay(false);
        }
    }

    private void switchMainGhostMode(GhostMode ghostMode,
                                    boolean justRestartGame) {
        Ghost[] ghosts = getGhosts();
        if (ghostMode == GhostMode.FRIGHTENED
                && levelConfig.frightTime == 0) {
            for (Ghost ghost : ghosts) {
                ghost.setReverseDirectionsNext(true); // If frightTime is 0, a frightened ghost only reverse its direction.
            }
        } else {
            GhostMode oldMainGhostMode = mainGhostMode;
            if (ghostMode == GhostMode.FRIGHTENED
                    && mainGhostMode != GhostMode.FRIGHTENED) {
                lastMainGhostMode = mainGhostMode;
            }
            mainGhostMode = ghostMode;
            if (ghostMode == GhostMode.FRIGHTENED
                || oldMainGhostMode == GhostMode.FRIGHTENED) {
                playAmbientSound();
            }
            switch (ghostMode) {
            case CHASE:
            case SCATTER:
                currentPlayerSpeed = levelConfig.playerSpeed * 0.8f;
                currentDotEatingSpeed = levelConfig.dotEatingSpeed * 0.8f;
                break;
            case FRIGHTENED:
                currentPlayerSpeed = levelConfig.playerFrightSpeed * 0.8f;
                currentDotEatingSpeed = levelConfig.dotEatingFrightSpeed * 0.8f;
                frightModeTime = levelConfig.frightTotalTime;
                modeScoreMultiplier = 1;
                break;
            }
            for (Ghost ghost : ghosts) {
                if (ghostMode != GhostMode.ENTERING_PEN && !justRestartGame) {
                    ghost.setModeChangedWhileInPen(true);
                }
                if (ghostMode == GhostMode.FRIGHTENED) {
                    ghost.setEatenInThisFrightMode(false);
                }
                if (ghost.getMode() != GhostMode.EATEN
                        && ghost.getMode() != GhostMode.IN_PEN
                        && ghost.getMode() != GhostMode.LEAVING_PEN
                        && ghost.getMode() != GhostMode.RE_LEAVING_FROM_PEN
                        && ghost.getMode() != GhostMode.ENTERING_PEN || justRestartGame) {

                    // If it is not immediately after restart the game (justRestartGmae:false),
                    // a ghost reverse its direction 
                    // when its mode change from other than FRIGHTENED (CHASE or SCATTER) to another mode.
                    if (!justRestartGame && ghost.getMode() != GhostMode.FRIGHTENED
                            && ghost.getMode() != ghostMode) {
                        ghost.setReverseDirectionsNext(true);
                    }

                    // If it is not immediately after restart the game
                    // and a mode of each ghost is any of EATEN, IN_PEN, LEAVING_PEN, RE_LEAVING_FROM_PEN, or ENTERING_PEN,
                    // it is not updated.
                    ghost.switchGhostMode(ghostMode);
                }
            }

            Pacman pacman = getPacman();
            pacman.setFullSpeed(currentPlayerSpeed);
            pacman.setDotEatingSpeed(currentDotEatingSpeed);
            pacman.setTunnelSpeed(currentPlayerSpeed);
            pacman.changeSpeed();
        }
    }

    private void figureOutPenLeaving() {
        Ghost pinky = getPinky();
        Ghost inky = getInky();
        Ghost clyde = getClyde();
        if (alternatePenLeavingScheme) {
            // By using a number of dots eaten after a level restart,
            // manage the timing of the ghosts leaving from the pen.
            alternateDotCount++;
            if (alternateDotCount == PEN_LEAVING_FOOD_LIMITS[1]) {
                pinky.setFreeToLeavePen(true);
            } else if (alternateDotCount == PEN_LEAVING_FOOD_LIMITS[2]) {
                inky.setFreeToLeavePen(true);
            } else if (alternateDotCount == PEN_LEAVING_FOOD_LIMITS[3]) {
                if (clyde.getMode() == GhostMode.IN_PEN) {
                    alternatePenLeavingScheme = false;
                }
            }
        } else if (pinky.getMode() == GhostMode.IN_PEN
                || pinky.getMode() == GhostMode.EATEN) {
            pinky.incrementDotCount();
            if (pinky.getDotCount() >= levelConfig.penLeavingLimits[1]) {
                pinky.setFreeToLeavePen(true);
            }
        } else if (inky.getMode() == GhostMode.IN_PEN
                || inky.getMode() == GhostMode.EATEN) {
            inky.incrementDotCount();
            if (inky.getDotCount() >= levelConfig.penLeavingLimits[2]) {
                inky.setFreeToLeavePen(true);
            }
        } else if (clyde.getMode() == GhostMode.IN_PEN
                || clyde.getMode() == GhostMode.EATEN) {
            clyde.incrementDotCount();
            if (clyde.getDotCount() >= levelConfig.penLeavingLimits[3]) {
                clyde.setFreeToLeavePen(true);
            }
        }
    }

    private void resetForcePenLeaveTime() {
        forcePenLeaveTime = levelConfig.penForceTime * DEFAULT_FPS;
    }

    public void dotEaten(int[] dotPos) {
        getPlayfieldEl().decrementDotsRemaining();
        getPlayfieldEl().incrementDotsEaten();
        getPacman().changeSpeed(CurrentSpeed.PACMAN_EATING_DOT);
        playDotEatingSound();
        if (getPathElement(dotPos[1], dotPos[0]).getDot() == Dot.ENERGIZER) { // when eating an energizer
            switchMainGhostMode(GhostMode.FRIGHTENED, false);
            addToScore(50);
        } else { // when eating a normal food
            addToScore(10);
        }

        getPlayfieldEl().clearDot(dotPos[1], dotPos[0]);
        updateCruiseElroySpeed();
        resetForcePenLeaveTime();
        figureOutPenLeaving();
        if (getPlayfieldEl().getDotsEaten() == 70
                || getPlayfieldEl().getDotsEaten() == 170) {
            showFruit();
        }
        if (getPlayfieldEl().getDotsRemaining() == 0) {
            finishLevel();
        }
        playAmbientSound();
    }

    public PathElement getPathElement(int x, int y) {
        return getPlayfieldEl().getPathElement(x, y);
    }

    public int getDotsRemaining() {
        return getPlayfieldEl().getDotsRemaining();
    }

    private void hideFruit() {
        fruitShown = false;
        getFruitEl().hide();
    }

    private void showFruit() {
        fruitShown = true;
        getFruitEl().show();
        fruitTime =
            (int) timing[15]
                + (int) ((timing[16] - timing[15]) * rand());
    }

    public void eatFruit() {
        if (fruitShown) {
            playSound("fruit", 0);
            fruitShown = false;
            getFruitEl().eaten();
            fruitTime = (int) timing[14];
            addToScore(levelConfig.fruitScore);
        }
    }

    private void updateActorTargetPositions() {
        Ghost[] ghosts = getGhosts();
        for (Ghost ghost : ghosts) {
            ghost.updateTargetPos();
        }
    }

    private void moveActors() {
        getPacman().move();
        Ghost[] ghosts = getGhosts();
        for (PlayfieldActor actor : ghosts) {
            actor.move();
        }
    }

    private void ghostDies(int index) {
        playSound("eating_ghost", 0);
        addToScore(200 * modeScoreMultiplier);
        modeScoreMultiplier *= 2;
        ghostBeingEaten = getPlayfieldEl().getGhosts()[index];
        changeGameplayMode(GameplayMode.GHOST_DIED);
    }

    private void pacmanDies() {
        changeGameplayMode(GameplayMode.PLAYER_DYING);
    }

    private void detectCollisions() {
        tilesChanged = false;
        Pacman pacman = getPacman();
        Ghost[] ghosts = getGhosts();
        for (int i = 0; i < 4; i++) {
            if (ghosts[i].getTilePos()[0] == pacman.getTilePos()[0]
                    && ghosts[i].getTilePos()[1] == pacman.getTilePos()[1]) {
                if (ghosts[i].getMode() == GhostMode.FRIGHTENED) {
                    ghostDies(i);
                    return;
                } else if (ghosts[i].getMode() != GhostMode.EATEN
                        && ghosts[i].getMode() != GhostMode.IN_PEN
                        && ghosts[i].getMode() != GhostMode.LEAVING_PEN
                        && ghosts[i].getMode() != GhostMode.RE_LEAVING_FROM_PEN
                        && ghosts[i].getMode() != GhostMode.ENTERING_PEN) {
                    pacmanDies();
                }
            }
        }
    }

    public void updateCruiseElroySpeed() {
        float speed = levelConfig.ghostSpeed * 0.8f;
        if (!lostLifeOnThisLevel || getClyde().getMode() != GhostMode.IN_PEN) {
            LevelConfig c = levelConfig;
            if (getPlayfieldEl().getDotsRemaining() < c.elroyDotsLeftPart2) {
                speed = c.elroySpeedPart2 * 0.8f;
            } else if (getPlayfieldEl().getDotsRemaining() < c.elroyDotsLeftPart1) {
                speed = c.elroySpeedPart1 * 0.8f;
            }
        }
        if (speed != cruiseElroySpeed) {
            cruiseElroySpeed = speed;
            getBlinky().changeSpeed(); // update the speed of Blinky.
        }
    }

    // argument:speed
    // return value: an array of boolean using intervalTime as its index.
    //               an element of this array represents whether it is necessary to move the character in a intervalTime.
    // ex) speed: 0.64 -> return value: [false, true, false, true, false, true, ...]
    public Boolean[] getSpeedIntervals(float speed) {
        Float key = Float.valueOf(speed);
        if (!speedIntervals.containsKey(key)) {
            float distance = 0;
            float lastPos = 0;
            List<Boolean> movabilityTimeTable = new ArrayList<Boolean>();
            for (int i = 0; i < DEFAULT_FPS; i++) {
                distance += speed;
                float pos = FloatMath.floor(distance);
                if (pos > lastPos) {
                    movabilityTimeTable.add(true);
                    lastPos = pos;
                } else {
                    movabilityTimeTable.add(false);
                }
            }
            speedIntervals.put(key, movabilityTimeTable.toArray(new Boolean[0]));
        }
        return speedIntervals.get(key);
    }

    private void finishLevel() {
        changeGameplayMode(GameplayMode.LEVEL_BEING_COMPLETED);
    }

    private void changeGameplayMode(GameplayMode mode) {
        gameplayMode = mode;
        if (mode != GameplayMode.CUTSCENE) {
            getPacman().updateAppearance();

            Ghost[] ghosts = getGhosts();
            for (PlayfieldActor actor : ghosts) {
                actor.updateAppearance();
            }
        }

        switch (mode) {
        case ORDINARY_PLAYING:
            playAmbientSound();
            break;
        case PLAYER_DYING:
            stopAllAudio();
            gameplayModeTime = timing[3];
            break;
        case PLAYER_DIED:
            playSound("death", 0);
            gameplayModeTime = timing[4];
            break;
        case GAME_RESTARTING:
            canvasEl.setVisibility(false);
            gameplayModeTime = timing[5];
            break;
        case GAME_RESTARTED:
            stopAllAudio();
            canvasEl.setVisibility(true);
            getDoorEl().setVisibility(true);
            createReadyElement();
            gameplayModeTime = timing[6];
            break;
        case NEWGAME_STARTING:
            getDoorEl().setVisibility(true);
            createReadyElement();
            gameplayModeTime = timing[7];
            stopAllAudio();
            playSound("start_music", 0, true);
            break;
        case NEWGAME_STARTED:
            lives--;
            updateChromeLives();
            gameplayModeTime = timing[8];
            break;
        case GAMEOVER:
        case KILL_SCREEN:
            removeReadyElement();
            stopAllAudio();
            createGameOverElement();
            gameplayModeTime = timing[9];
            break;
        case LEVEL_BEING_COMPLETED:
            stopAllAudio();
            gameplayModeTime = timing[10];
            break;
        case LEVEL_COMPLETED:
            getDoorEl().setVisibility(false);
            gameplayModeTime = timing[11];
            break;
        case TRANSITION_INTO_NEXT_SCENE:
            canvasEl.setVisibility(false);
            gameplayModeTime = timing[12];
            break;
        case GHOST_DIED:
            gameplayModeTime = timing[2];
            break;
        case CUTSCENE:
            startCutscene();
            break;
        }
    }

    private void toggleSound() {
        if (pacManSound) {
            stopAllAudio();
            pacManSound = false;
        } else {
            pacManSound = true;
            playAmbientSound();
        }
    }

    private void updateSoundIcon() {
        Sound soundEl = getSoundEl();
        if (soundPlayer.isAvailable()) {
            soundEl.setVisibility(true);
            if (pacManSound) {
                soundEl.turnOn();
            } else {
                soundEl.turnOff();
            }
        } else {
            soundEl.setVisibility(false);
        }
    }

    private void startCutscene() {
        getPlayfieldEl().setVisibility(false);
        
        canvasEl.setVisibility(true);
        canvasEl.showChrome(false);
        canvasEl.createCutsceneField();
        
        cutscene = CUTSCENES.get(Integer.valueOf(cutsceneId));
        cutsceneSequenceId = -1;
        frightModeTime = levelConfig.frightTotalTime;
        createCutsceneActors();
        
        cutsceneNextSequence();
        stopAllAudio();
        playCutsceneAmbient();
    }
    
    private void createCutsceneActors() {
        getCutsceneFieldEl().createActors(this, cutsceneId, cutscene.actors);
    }

    private void stopCutscene() {
        stopCutsceneAmbient();
        getPlayfieldEl().setVisibility(true);
        canvasEl.removeCutsceneField();
        canvasEl.showChrome(true);
        newLevel(false);
    }

    private void cutsceneNextSequence() {
        cutsceneSequenceId++;
        if (cutscene.sequenceTimes.length == cutsceneSequenceId) {
            stopCutscene();
        } else {
            cutsceneTime = cutscene.sequenceTimes[cutsceneSequenceId] * DEFAULT_FPS;
            CutsceneActor[] cutsceneActors = getCutsceneActors();
            for (int i = 0; i < cutsceneActors.length; i++) {
                CutsceneActor actor = cutsceneActors[i];
                actor.setupSequence();
                actor.updateAppearance();
            }
        }
    }

    private void checkCutscene() {
        if (cutsceneTime <= 0) {
            cutsceneNextSequence();
        }
    }

    private void advanceCutscene() {
        CutsceneActor[] cutsceneActors = getCutsceneActors();
        for (CutsceneActor actor : cutsceneActors) {
            actor.move();
        }
        cutsceneTime--;
    }

    private void updateActorPositions() {
        getPacman().updateElPos();
        Ghost[] ghosts = getGhosts();
        for (PlayfieldActor actor : ghosts) {
            actor.updateElPos();
        }
    }

    private void blinkEnergizers() {
        getPlayfieldEl().blinkEnergizers(gameplayMode, globalTime, timing[0]);
    }

    private void blinkScoreLabels() {
        getScoreLabelEl().update(gameplayMode, globalTime, timing[17]);
    }

    private void finishFrightMode() {
        switchMainGhostMode(lastMainGhostMode, false);
    }

    private void handleGameplayModeTimer() {
        if (gameplayModeTime != 0) {
            gameplayModeTime--;
            switch (gameplayMode) {
            case PLAYER_DYING:
            case PLAYER_DIED:
                getPacman().updateAppearance();
                Ghost[] ghosts = getGhosts();
                for (PlayfieldActor actor : ghosts) {
                    actor.updateAppearance();
                }
                break;
            case LEVEL_COMPLETED:
                getPlayfieldEl().blink(gameplayModeTime, timing[11]);
                break;
            }

            if (gameplayModeTime <= 0) {
                gameplayModeTime = 0;
                Ghost[] ghosts = getGhosts();
                switch (gameplayMode) {
                case GHOST_DIED:
                    changeGameplayMode(GameplayMode.ORDINARY_PLAYING);
                    ghostEyesCount++;
                    playAmbientSound();
                    ghostBeingEaten.resetDisplayOrder();
                    ghostBeingEaten.switchGhostMode(GhostMode.EATEN);
                    // If there is no ghost frightened, finish fright mode.
                    boolean frightenedGhostExists = false;
                    for (Ghost ghost : ghosts) {
                        if (ghost.getMode() == GhostMode.FRIGHTENED
                            || (ghost.getMode() == GhostMode.IN_PEN
                                    || ghost.getMode() == GhostMode.RE_LEAVING_FROM_PEN)
                                && !ghost.isEatenInThisFrightMode()) {
                            frightenedGhostExists = true;
                            break;
                        }
                    }
                    if (!frightenedGhostExists) {
                        finishFrightMode();
                    }
                    break;
                case PLAYER_DYING:
                    changeGameplayMode(GameplayMode.PLAYER_DIED);
                    break;
                case PLAYER_DIED:
                    newLife();
                    break;
                case NEWGAME_STARTING:
                    changeGameplayMode(GameplayMode.NEWGAME_STARTED);
                    break;
                case GAME_RESTARTING:
                    changeGameplayMode(GameplayMode.GAME_RESTARTED);
                    break;
                case GAME_RESTARTED:
                case NEWGAME_STARTED:
                    getPlayfieldEl().removeReady();
                    changeGameplayMode(GameplayMode.ORDINARY_PLAYING);
                    break;
                case GAMEOVER:
                    getPlayfieldEl().removeGameover();
                    break;
                case LEVEL_BEING_COMPLETED:
                    changeGameplayMode(GameplayMode.LEVEL_COMPLETED);
                    break;
                case LEVEL_COMPLETED:
                    changeGameplayMode(GameplayMode.TRANSITION_INTO_NEXT_SCENE);
                    break;
                case TRANSITION_INTO_NEXT_SCENE:
                    if (levelConfig.cutsceneId != 0) {
                        cutsceneId = levelConfig.cutsceneId;
                        changeGameplayMode(GameplayMode.CUTSCENE);
                    } else {
                        // canvasEl.style.visibility = "";
                        canvasEl.setVisibility(true);
                        newLevel(false);
                    }
                    break;
                }
            }
        }
    }

    private void handleFruitTimer() {
        if (fruitTime != 0) {
            fruitTime--;
            if (fruitTime <= 0)
                hideFruit();
        }
    }

    private void handleGhostModeTimer() {
        if (frightModeTime != 0) {
            frightModeTime--;
            if (frightModeTime <= 0) {
                frightModeTime = 0;
                finishFrightMode();
            }
        } else if (ghostModeTime > 0) {
            ghostModeTime--;
            if (ghostModeTime <= 0) {
                ghostModeTime = 0;
                ghostModeSwitchPos++;
                if (ghostModeSwitchPos < levelConfig.ghostModeSwitchTimes.length) {
                    ghostModeTime = levelConfig.ghostModeSwitchTimes[ghostModeSwitchPos] * DEFAULT_FPS;
                    switch (mainGhostMode) {
                    case SCATTER:
                        switchMainGhostMode(GhostMode.CHASE, false);
                        break;
                    case CHASE:
                        switchMainGhostMode(GhostMode.SCATTER, false);
                        break;
                    }
                }
            }
        }
    }

    private void handleForcePenLeaveTimer() {
        if (forcePenLeaveTime != 0) {
            forcePenLeaveTime--;
            if (forcePenLeaveTime <= 0) {
                Ghost[] ghosts = getGhosts();
                for (int i = 1; i <= 3; i++) {
                    if (ghosts[i].getMode() == GhostMode.IN_PEN) {
                        ghosts[i].setFreeToLeavePen(true);
                        break;
                    }
                }

                resetForcePenLeaveTime();
            }
        }
    }

    private void handleTimers() {
        if (gameplayMode == GameplayMode.ORDINARY_PLAYING) {
            handleForcePenLeaveTimer();
            handleFruitTimer();
            handleGhostModeTimer();
        }
        handleGameplayModeTimer();
    }

    void tick() {
        long now = new Date().getTime();
        if (paused) {
            pausedTime = now;
            return;
        }

        lastTimeDelta += now - lastTime - tickInterval; // total processing delay
        if (lastTimeDelta > 100) {
            lastTimeDelta = 100;
        }
        if (canDecreaseFps && lastTimeDelta > 50) {
            // If the fps can be reduced, count the number of process which latency is over 50 ms.
            lastTimeSlownessCount++;
            if (lastTimeSlownessCount == 20) {
                decreaseFps(); // reduce the fps when the number of process, which latency is over 50 ms, becomes 20.
            }
        }
        int latencyMultiplyer = 0;
        if (lastTimeDelta > tickInterval) {
            // If the total processing delay is greater than tick​​Interval,
            // the total processing delay is cut down to less than tickInterval.
            latencyMultiplyer = (int) FloatMath.floor(lastTimeDelta / tickInterval);
            lastTimeDelta -= tickInterval * latencyMultiplyer;
        }
        lastTime = now;
        if (gameplayMode == GameplayMode.CUTSCENE) { // Cutscene
            for (int i = 0; i < tickMultiplier + latencyMultiplyer; i++) {
                // run multiple time depending on the tickMultiplier and latency
                advanceCutscene();
                intervalTime = (intervalTime + 1) % DEFAULT_FPS;
                globalTime++;
            }
            checkCutscene();
            blinkScoreLabels();
        } else {
            updateSoundIcon();
            
            for (int i = 0; i < tickMultiplier + latencyMultiplyer; i++) {
                // run multiple time depending on the tickMultiplier and latency
                moveActors();
                if (gameplayMode == GameplayMode.ORDINARY_PLAYING) {
                    if (tilesChanged) {
                        detectCollisions();
                        updateActorTargetPositions();
                    }
                }

                globalTime++;
                intervalTime = (intervalTime + 1) % DEFAULT_FPS;
                blinkEnergizers();
                blinkScoreLabels();
                handleTimers();
            }
        }
        setTimeout();
    }

    private void extraLife() {
        playSound("extra_life", 0);
        extraLifeAwarded = true;
        lives++;
        if (lives > 5) {
            lives = 5;
        }
        updateChromeLives();
    }

    private void addToScore(int s) {
        score += s;
        if (!extraLifeAwarded && score > 10000) {
            extraLife();
        }
        updateChromeScore();
    }

    private void updateChrome() {
        updateChromeLevel();
        updateChromeLives();
        updateChromeScore();
    }

    private void updateChromeLives() {
        getLivesEl().update(lives);
    }

    private void updateChromeLevel() {
        getLevelEl().update(level, LEVEL_CONFIGS);
    }

    private void updateChromeScore() {
        getScoreEl().update(score);
    }

    private void createChrome() {
        canvasEl.reset();
        canvasEl.createScoreLabel();
        canvasEl.createScore();
        canvasEl.createLives();
        canvasEl.createLevel();
        canvasEl.createSoundIcon();
        updateSoundIcon();
    }

    private String oldAmbient;

    private void playSound(String track, int channel) {
        playSound(track, channel, false);
    }

    private void playSound(String track, int channel, boolean noBlank) {
        if (pacManSound) {
            if (!noBlank) {
                stopSoundChannel(channel);
            }
            soundPlayer.playTrack(track, channel);
        }
    }

    private void stopSoundChannel(int channel) {
        soundPlayer.stopChannel(channel);
    }
    
    private void stopAmbient() {
        soundPlayer.stopAmbient();
        oldAmbient = null;
    }

    private void stopAllAudio() {
        stopAmbient();
        for (int i = 0; i < 5; i++) {
            stopSoundChannel(i);
        }
    }

    private void playDotEatingSound() {
        if (pacManSound) {
            if (gameplayMode == GameplayMode.ORDINARY_PLAYING) {
                String track = dotEatingSoundPart == 1
                                ? "eating_dot_1"
                                : "eating_dot_2";
                playSound(track, 1 + dotEatingChannel, true);
                dotEatingChannel = (dotEatingChannel + 1) % 2; // switch between 0 and 1
                dotEatingSoundPart = 3 - dotEatingSoundPart; // switch between 1 and 2
            }
        }
    }

    public void playAmbientSound() {
        if (pacManSound) {
            String ambient = null;
            if (gameplayMode == GameplayMode.ORDINARY_PLAYING
                    || gameplayMode == GameplayMode.GHOST_DIED) {
                Playfield playfieldEl = getPlayfieldEl();
                ambient = ghostEyesCount != 0
                        ? "ambient_eyes"
                        : mainGhostMode == GhostMode.FRIGHTENED
                            ? "ambient_fright"
                            : playfieldEl.getDotsEaten() > 241
                                ? "ambient_4"
                                : playfieldEl.getDotsEaten() > 207
                                    ? "ambient_3"
                                    : playfieldEl.getDotsEaten() > 138
                                        ? "ambient_2" : "ambient_1";
            }

            if (ambient != null) {
                if (ambient.equals(oldAmbient)) {
                    return;
                }

                soundPlayer.playAmbient(ambient);
                oldAmbient = ambient;

            }
        }
    }

    private void playCutsceneAmbient() {
        if (pacManSound) {
            soundPlayer.playCutsceneAmbient();
        }
    }

    private void stopCutsceneAmbient() {
        soundPlayer.stopCutsceneAmbient();
    }

    private void initializeTickTimer() {
        fps = FPS_OPTIONS[fpsChoice];
        tickInterval = 1000 / fps;
        tickMultiplier = DEFAULT_FPS / fps;
        timing = new float[EVENT_TIME_TABLE.length];
        for (int i = 0; i < EVENT_TIME_TABLE.length; i++) {
            float sec = !pacManSound && (i == 7 || i == 8) ? 1 : EVENT_TIME_TABLE[i];
            timing[i] = Math.round(sec * DEFAULT_FPS);
        }
        lastTime = new Date().getTime();
        lastTimeDelta = 0;
        lastTimeSlownessCount = 0;
    }

    private void setTimeout() {
        view.redrawHandler.sleep(Math.round(tickInterval));
    }

    private void decreaseFps() {
        if (fpsChoice < FPS_OPTIONS.length - 1) {
            fpsChoice++;
            initializeTickTimer();
            if (fpsChoice == FPS_OPTIONS.length - 1) {
                canDecreaseFps = false;
            }
        }
    }

    private void createCanvasElement() {
        canvasEl = new PacmanCanvas(sourceImage);
        canvasEl.init();
    }

    private void start() {
        started = true;
        setTimeout();
        newGame();
    }

    void startNewGame() {
        setDefaultKillScreenLevel();
        start();
    }

    void showKillScreen() {
        setKillScreenLevel(1);
        start();
    }

    private void prepareSound() {
        soundPlayer = new SoundPlayer(context);
        soundPlayer.init();
    }

    private void setKillScreenLevel(int level) {
        this.killScreenLevel = level;
    }

    private void setDefaultKillScreenLevel() {
        this.killScreenLevel = DEFAULT_KILL_SCREEN_LEVEL;
    }

    void init() {
        sourceImage =
            BitmapFactory.decodeResource(
                context.getResources(),
                R.drawable.pacman_sprite);
        createCanvasElement();
        speedIntervals = new HashMap<Float, Boolean[]>();
        fpsChoice = 0;
        canDecreaseFps = true;
        initializeTickTimer();
    }
    
    void resume() {
        prepareSound();
        if (started && paused) {
            lastTime += new Date().getTime() - pausedTime;
            paused = false;
            if (oldAmbient != null) {
                soundPlayer.playAmbient(oldAmbient);
            }
            tick();
        }
    }
    
    void pause() {
        paused = true;
        soundPlayer.destroy();
    }
    
    public PacmanCanvas getCanvasEl() {
        return canvasEl;
    }

    private ScoreLabel getScoreLabelEl() {
        if (canvasEl == null) {
            return null;
        }
        return canvasEl.getScoreLabel();
    }

    private Score getScoreEl() {
        if (canvasEl == null) {
            return null;
        }
        return canvasEl.getScore();
    }

    private Sound getSoundEl() {
        if (canvasEl == null) {
            return null;
        }
        return canvasEl.getSound();
    }

    private Lives getLivesEl() {
        if (canvasEl == null) {
            return null;
        }
        return canvasEl.getLives();
    }
    
    private Level getLevelEl() {
        if (canvasEl == null) {
            return null;
        }
        return canvasEl.getLevel();
    }

    public Playfield getPlayfieldEl() {
        if (canvasEl == null) {
            return null;
        }
        return canvasEl.getPlayfield();
    }

    public Pacman getPacman() {
        if (canvasEl == null) {
            return null;
        }
        
        Playfield playfieldEl = canvasEl.getPlayfield();
        if (playfieldEl == null) {
            return null;
        }
        
        return playfieldEl.getPacman();
    }

    private Ghost[] getGhosts() {
        if (canvasEl == null) {
            return null;
        }

        Playfield playfieldEl = canvasEl.getPlayfield();
        if (playfieldEl == null) {
            return null;
        }
        return playfieldEl.getGhosts();
    }
    
    public Ghost getBlinky() {
        return getPlayfieldEl().getBlinky();
    }
    
    public Ghost getPinky() {
        return getPlayfieldEl().getPinky();
    }
    
    public Ghost getInky() {
        return getPlayfieldEl().getInky();
    }

    public Ghost getClyde() {
        return getPlayfieldEl().getClyde();
    }
    
    private Fruit getFruitEl() {
        Playfield playfieldEl = canvasEl.getPlayfield();
        if (playfieldEl == null) {
            return null;
        }
        return playfieldEl.getFruit();
    }

    private Door getDoorEl() {
        Playfield playfieldEl = canvasEl.getPlayfield();
        if (playfieldEl == null) {
            return null;
        }
        return playfieldEl.getDoor();
    }

    private CutsceneField getCutsceneFieldEl() {
        if (canvasEl == null) {
            return null;
        }
        return canvasEl.getCutsceneField();
    }
    
    private CutsceneActor[] getCutsceneActors() {
        if (canvasEl == null) {
            return null;
        }
        CutsceneField cutsceneFieldEl = canvasEl.getCutsceneField();
        if (cutsceneFieldEl == null) {
            return null;
        }
        return cutsceneFieldEl.getActors();
    }

    public boolean isPacManSound() {
        return pacManSound;
    }

    public void setPacManSound(boolean pacManSound) {
        this.pacManSound = pacManSound;
    }

    public LevelConfig getLevels() {
        return levelConfig;
    }

    public long getGlobalTime() {
        return globalTime;
    }

    public int getFrightModeTime() {
        return frightModeTime;
    }

    public int getIntervalTime() {
        return intervalTime;
    }

    public float getGameplayModeTime() {
        return gameplayModeTime;
    }

    public float[] getTiming() {
        return timing;
    }

    public GameplayMode getGameplayMode() {
        return gameplayMode;
    }

    public GhostMode getMainGhostMode() {
        return mainGhostMode;
    }

    public GhostMode getLastMainGhostMode() {
        return lastMainGhostMode;
    }

    public Ghost getGhostBeingEaten() {
        return ghostBeingEaten;
    }

    public int getModeScoreMultiplier() {
        return modeScoreMultiplier;
    }

    public boolean isLostLifeOnThisLevel() {
        return lostLifeOnThisLevel;
    }

    public boolean isGhostExitingPenNow() {
        return ghostExitingPenNow;
    }

    public boolean setGhostExitingPenNow(boolean ghostExitingPenNow) {
        return this.ghostExitingPenNow = ghostExitingPenNow;
    }

    public int getGhostEyesCount() {
        return ghostEyesCount;
    }

    public void incrementGhostEyesCount() {
        ghostEyesCount++;
    }

    public void decrementGhostEyesCount() {
        ghostEyesCount--;
    }

    public boolean isTilesChanged() {
        return tilesChanged;
    }

    public int getCutsceneId() {
        return cutsceneId;
    }

    public int getCutsceneSequenceId() {
        return cutsceneSequenceId;
    }

    public float getCutsceneTime() {
        return cutsceneTime;
    }

    public void setTilesChanged(boolean tilesChanged) {
        this.tilesChanged = tilesChanged;
    }

    public float getCruiseElroySpeed() {
        return cruiseElroySpeed;
    }
    
    public static float getFieldX(float x) {
        return x + -32;
    }

    public static float getFieldY(float y) {
        return y + 0;
    }
}
