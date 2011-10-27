package jp.or.iidukat.example.pacman;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.or.iidukat.example.pacman.entity.Actor;
import jp.or.iidukat.example.pacman.entity.Actor.CurrentSpeed;
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
    
    // レベル再開後、一定数のエサが食べられるとモンスターが巣から出てくる
    // そのしきい値をモンスター毎に設定
    private static final int[] FOOD_LIMITS_PEN_LEAVING = { 0, 7, 17, 32 };

    // イベント時間管理テーブル. index 7, 8しか使わない
    private static final float[] w = {
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

    private static final int[] C = { 90, 45, 30, }; // fps オプション
    private static final int D = C[0]; // 本来想定されているfps

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
            
            this.frightTime = builder.frightTime * D;
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

    // ゲームレベル毎の設定
    private static final LevelConfig[] z = {
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

    private static final Map<Integer, Cutscene> B;
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
        B = Collections.unmodifiableMap(css);
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

    private void seed(long b) {
        this.randSeed = b;
    }

    private void restartActors() {
        getPacman().arrange();

        for (Actor ghost : getGhosts())
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
        float px = getFieldX(pacman.getPos()[1] + pacman.getPosDelta()[1]) + 48;
        float py = getFieldY(pacman.getPos()[0] + pacman.getPosDelta()[0]) + 32;
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
        if (touchCanceld)
            return;

        if (e.getPointerCount() > 1)
            cancelTouch();
        else {
            touchDX = e.getX(0) - touchStartX;
            touchDY = e.getY(0) - touchStartY;
        }
    }

    void handleTouchEnd(MotionEvent e) {
        if (touchCanceld)
            return;

        float c = Math.abs(touchDX);
        float d = Math.abs(touchDY);
        Pacman pacman = getPacman();
        if (c < 8 && d < 8) {
            canvasClicked(touchStartX, touchStartY);
        } else if (c > 15 && d < c * 2 / 3) {
            pacman.setRequestedDir(touchDX > 0 ? Direction.RIGHT : Direction.LEFT);
        } else if (d > 15 && c < d * 2 / 3) {
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

    // b true:新規ゲーム false:ゲーム再開(プレイヤー死亡後or新レベル)
    private void restartGameplay(boolean b) {
        seed(0);
        frightModeTime = 0;
        intervalTime = 0;
        gameplayModeTime = 0;
        fruitTime = 0;
        ghostModeSwitchPos = 0;
        ghostModeTime = levelConfig.ghostModeSwitchTimes[0] * D;
        ghostExitingPenNow = false;
        ghostEyesCount = 0;
        tilesChanged = false;
        updateCruiseElroySpeed();
        hideFruit();
        resetForcePenLeaveTime();
        restartActors();
        updateActorPositions();
        switchMainGhostMode(GhostMode.SCATTER, true);
        for (int c = 1; c < 4; c++)
            getGhosts()[c].updateMode(GhostMode.IN_PEN);
        dotEatingChannel = 0;
        dotEatingSoundPart = 1;

        if (b) {
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

    private void newLevel(boolean b) {
        level++;
        levelConfig = level >= z.length ? z[z.length - 1] : z[level];
        levelConfig.frightTotalTime =
            levelConfig.frightTime + ((int) timing[1]) * (levelConfig.frightBlinkCount * 2 - 1);
        alternatePenLeavingScheme = false;
        lostLifeOnThisLevel = false;
        updateChrome();
        resetPlayfield();
        restartGameplay(b);
        if (level == killScreenLevel)
            killScreen();
    }

    private void newLife() {
        lostLifeOnThisLevel = true;
        alternatePenLeavingScheme = true;
        alternateDotCount = 0;
        lives--;
        updateChromeLives();

        if (lives == -1)
            changeGameplayMode(GameplayMode.GAMEOVER);
        else
            restartGameplay(false);
    }

    // MainGhostMode切り替え
    // b: 切り替え先のモード c: 開始直後フラグ(trueなら開始直後)
    private void switchMainGhostMode(GhostMode b, boolean c) {
        Ghost[] ghosts = getGhosts();
        if (b == GhostMode.FRIGHTENED && levelConfig.frightTime == 0)
            for (Ghost ghost : ghosts) {
                ghost.setReverseDirectionsNext(true); // frightTimeが0なら、ブルーモードになってもモンスターは反対に向きを変えるだけ
            }
        else {
            GhostMode f = mainGhostMode;
            if (b == GhostMode.FRIGHTENED
                    && mainGhostMode != GhostMode.FRIGHTENED) {
                lastMainGhostMode = mainGhostMode;
            }
            mainGhostMode = b;
            if (b == GhostMode.FRIGHTENED || f == GhostMode.FRIGHTENED)
                playAmbientSound();
            switch (b) {
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
                // b(Main Ghost Mode)は1, 2, 4。 64になるケースは存在しないように思える.
                // 巣の中にいるときにGhostMainModeが変わった、という条件にも合致していない
                if (b != GhostMode.ENTERING_PEN && !c)
                    ghost.setModeChangedWhileInPen(true);
                if (b == GhostMode.FRIGHTENED)
                    ghost.setEatenInThisFrightMode(false);
                if (ghost.getMode() != GhostMode.EATEN
                        && ghost.getMode() != GhostMode.IN_PEN
                        && ghost.getMode() != GhostMode.LEAVING_PEN
                        && ghost.getMode() != GhostMode.RE_LEAVING_FROM_PEN
                        && ghost.getMode() != GhostMode.ENTERING_PEN || c) {
                    // ゲーム再開直後(c:true)以外では, モンスターのモードが8, 16, 32, 64,
                    // 128ならモード更新対象とならない
                    // ゲーム再開直後以外でブルーモード(4)以外から異なるモード[追跡モード(1), Scatterモード(2),
                    // ブルーモード(4)]への切り替え時が行われるとき、反対に向きを変える
                    if (!c && ghost.getMode() != GhostMode.FRIGHTENED
                            && ghost.getMode() != b) {
                        ghost.setReverseDirectionsNext(true);
                    }
                    ghost.updateMode(b);
                }
            }

            Pacman pacman = getPacman();
            pacman.setFullSpeed(currentPlayerSpeed);
            pacman.setDotEatingSpeed(currentDotEatingSpeed);
            pacman.setTunnelSpeed(currentPlayerSpeed);
            pacman.updateSpeed();
        }
    }

    private void figureOutPenLeaving() {
        Ghost[] ghosts = getGhosts();
        if (alternatePenLeavingScheme) { // レベル再開後のみ食べられたエサの数によりモンスターが出撃するタイミングを管理
            alternateDotCount++;
            if (alternateDotCount == FOOD_LIMITS_PEN_LEAVING[1])
                ghosts[1].setFreeToLeavePen(true);
            else if (alternateDotCount == FOOD_LIMITS_PEN_LEAVING[2])
                ghosts[2].setFreeToLeavePen(true);
            else if (alternateDotCount == FOOD_LIMITS_PEN_LEAVING[3])
                if (ghosts[3].getMode() == GhostMode.IN_PEN)
                    alternatePenLeavingScheme = false;
        } else if (ghosts[1].getMode() == GhostMode.IN_PEN
                || ghosts[1].getMode() == GhostMode.EATEN) {
            ghosts[1].incrementDotCount();
            if (ghosts[1].getDotCount() >= levelConfig.penLeavingLimits[1])
                ghosts[1].setFreeToLeavePen(true);
        } else if (ghosts[2].getMode() == GhostMode.IN_PEN
                || ghosts[2].getMode() == GhostMode.EATEN) {
            ghosts[2].incrementDotCount();
            if (ghosts[2].getDotCount() >= levelConfig.penLeavingLimits[2])
                ghosts[2].setFreeToLeavePen(true);
        } else if (ghosts[3].getMode() == GhostMode.IN_PEN
                || ghosts[3].getMode() == GhostMode.EATEN) {
            ghosts[3].incrementDotCount();
            if (ghosts[3].getDotCount() >= levelConfig.penLeavingLimits[3])
                ghosts[3].setFreeToLeavePen(true);
        }
    }

    private void resetForcePenLeaveTime() {
        forcePenLeaveTime = levelConfig.penForceTime * D;
    }

    public void dotEaten(int[] c) {
        getPlayfieldEl().decrementDotsRemaining();
        getPlayfieldEl().incrementDotsEaten();
        getPacman().updateSpeed(CurrentSpeed.PACMAN_EATING_DOT);
        playDotEatingSound();
        if (getPathElement(c[1], c[0]).getDot() == Dot.ENERGIZER) { // パワーエサを食べたとき
            switchMainGhostMode(GhostMode.FRIGHTENED, false);
            addToScore(50);
        } else {
            addToScore(10); // 普通のエサ
        }

        getPlayfieldEl().clearDot(c[1], c[0]);
        updateCruiseElroySpeed();
        resetForcePenLeaveTime();
        figureOutPenLeaving();
        if (getPlayfieldEl().getDotsEaten() == 70
                || getPlayfieldEl().getDotsEaten() == 170)
            showFruit();
        if (getPlayfieldEl().getDotsRemaining() == 0)
            finishLevel();
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
        fruitTime = (int) timing[15] + (int) ((timing[16] - timing[15]) * rand());
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
        for (Ghost ghost : ghosts)
            ghost.updateTargetPos();
    }

    private void moveActors() {
        getPacman().move();
        Ghost[] ghosts = getGhosts();
        for (Actor actor : ghosts)
            actor.move();
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
        for (int i = 0; i < 4; i++)
            if (ghosts[i].getTilePos()[0] == pacman.getTilePos()[0]
                    && ghosts[i].getTilePos()[1] == pacman.getTilePos()[1])
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

    public void updateCruiseElroySpeed() {
        float b = levelConfig.ghostSpeed * 0.8f;
        if (!lostLifeOnThisLevel || getClyde().getMode() != GhostMode.IN_PEN) {
            LevelConfig c = levelConfig;
            if (getPlayfieldEl().getDotsRemaining() < c.elroyDotsLeftPart2)
                b = c.elroySpeedPart2 * 0.8f;
            else if (getPlayfieldEl().getDotsRemaining() < c.elroyDotsLeftPart1)
                b = c.elroySpeedPart1 * 0.8f;
        }
        if (b != cruiseElroySpeed) {
            cruiseElroySpeed = b;
            getBlinky().updateSpeed(); // アカベエの速度を更新
        }
    }

    // speed: intervalTimeをインデックスに対応させた配列。
    // あるintervalTimeでキャラの移動処理が必要かどうかが配列の要素(true/false)
    // ex) 0.64: [false, true, false, true, false, true, ...]
    public Boolean[] getSpeedIntervals(float b) {
        Float speed = Float.valueOf(b);
        if (!speedIntervals.containsKey(speed)) {
            float c = 0;
            double d = 0;
            List<Boolean> bools = new ArrayList<Boolean>();
            for (int f = 0; f < D; f++) {
                c += b;
                float flr = FloatMath.floor(c);
                if (flr > d) {
                    bools.add(true);
                    d = flr;
                } else
                    bools.add(false);
            }
            speedIntervals.put(speed, bools.toArray(new Boolean[0]));
        }
        return speedIntervals.get(speed);
    }

    private void finishLevel() {
        changeGameplayMode(GameplayMode.LEVEL_BEING_COMPLETED);
    }

    private void changeGameplayMode(GameplayMode b) {
        gameplayMode = b;
        if (b != GameplayMode.CUTSCENE) {
            getPacman().updatePresentation();

            Ghost[] ghosts = getGhosts();
            for (Actor actor : ghosts)
                actor.updatePresentation();
        }

        switch (b) {
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
        
        cutscene = B.get(Integer.valueOf(cutsceneId));
        cutsceneSequenceId = -1;
        frightModeTime = levelConfig.frightTotalTime;
        createCutsceneActors();
        
        cutsceneNextSequence();
        stopAllAudio();
        // playAmbientSound();
        playCutsceneTrack();
    }
    
    private void createCutsceneActors() {
        getCutsceneFieldEl().createActors(this, cutsceneId, cutscene.actors);
    }

    private void stopCutscene() {
        stopCutsceneTrack();
        getPlayfieldEl().setVisibility(true);
        canvasEl.removeCutsceneField();
        canvasEl.showChrome(true);
        newLevel(false);
    }

    private void cutsceneNextSequence() {
        cutsceneSequenceId++;
        if (cutscene.sequenceTimes.length == cutsceneSequenceId)
            stopCutscene();
        else {
            cutsceneTime = cutscene.sequenceTimes[cutsceneSequenceId] * D;
            CutsceneActor[] cutsceneActors = getCutsceneActors();
            for (int c = 0; c < cutsceneActors.length; c++) {
                CutsceneActor d = cutsceneActors[c];
                d.setupSequence();
                d.updatePresentation();
            }
        }
    }

    private void checkCutscene() {
        if (cutsceneTime <= 0)
            cutsceneNextSequence();
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
        for (Actor actor : ghosts)
            actor.updateElPos();
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
                getPacman().updatePresentation();
                Ghost[] ghosts = getGhosts();
                for (Actor actor : ghosts)
                    actor.updatePresentation();
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
                    ghostBeingEaten.updateMode(GhostMode.EATEN);
                    // ブルーモードのモンスターがいない場合、 ブルーモードを終了させる
                    boolean c = false;
                    for (Ghost ghost : ghosts)
                        if (ghost.getMode() == GhostMode.FRIGHTENED
                                || (ghost.getMode() == GhostMode.IN_PEN
                                || ghost.getMode() == GhostMode.RE_LEAVING_FROM_PEN)
                                && !ghost.isEatenInThisFrightMode()) {
                            c = true;
                            break;
                        }
                    if (!c)
                        finishFrightMode();
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
                    ghostModeTime = levelConfig.ghostModeSwitchTimes[ghostModeSwitchPos] * D;
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
                for (int b = 1; b <= 3; b++)
                    if (ghosts[b].getMode() == GhostMode.IN_PEN) {
                        ghosts[b].setFreeToLeavePen(true);
                        break;
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

        lastTimeDelta += now - lastTime - tickInterval; // 処理遅延時間累計
        if (lastTimeDelta > 100)
            lastTimeDelta = 100;
        if (canDecreaseFps && lastTimeDelta > 50) { // fpsを下げることができるなら、処理遅延時間50ms超の回数をカウント
            lastTimeSlownessCount++;
            if (lastTimeSlownessCount == 20)
                decreaseFps(); // 処理遅延時間50ms超 20回でfpsを下げる
        }
        int latencyMultiplyer = 0;
        if (lastTimeDelta > tickInterval) { // 処理遅延時間累計がtickインターバルより大きい場合、tickインターバル未満に値を切り詰める
            latencyMultiplyer = (int) FloatMath.floor(lastTimeDelta / tickInterval);
            lastTimeDelta -= tickInterval * latencyMultiplyer;
        }
        lastTime = now;
        if (gameplayMode == GameplayMode.CUTSCENE) { // Cutscene
            for (int i = 0; i < tickMultiplier + latencyMultiplyer; i++) { // tickMultiplierと処理遅延に応じて複数回のロジックを実行
                advanceCutscene();
                intervalTime = (intervalTime + 1) % D;
                globalTime++;
            }
            checkCutscene();
            blinkScoreLabels();
        } else {
            updateSoundIcon();
            
            for (int i = 0; i < tickMultiplier + latencyMultiplyer; i++) { // tickMultiplierと処理遅延に応じて複数回のロジックを実行
                moveActors();
                if (gameplayMode == GameplayMode.ORDINARY_PLAYING)
                    if (tilesChanged) {
                        detectCollisions();
                        updateActorTargetPositions();
                    }

                globalTime++;
                intervalTime = (intervalTime + 1) % D;
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
        if (lives > 5)
            lives = 5;
        updateChromeLives();
    }

    private void addToScore(int b) {
        score += b;
        if (!extraLifeAwarded && score > 10000)
            extraLife();
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
        getLevelEl().update(level, z);
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
    // サウンド再生
    // b -> トラック, c -> チャンネル番号
    private void playSound(String b, int c) {
        playSound(b, c, false);
    }

    // サウンド再生
    // b -> トラック, c -> チャンネル番号, d -> 再生中サウンド停止フラグ
    private void playSound(String b, int c, boolean d) {
        if (pacManSound) {
            if (!d) {
                stopSoundChannel(c);
            }
            soundPlayer.playTrack(b, c);
        }
    }

    private void stopSoundChannel(int b) {
        soundPlayer.stopChannel(b);
    }
    
    private void stopAmbient() {
        soundPlayer.stopAmbient();
        oldAmbient = null;
    }

    private void stopAllAudio() {
        stopAmbient();
        for (int c = 0; c < 5; c++) {
            stopSoundChannel(c);
        }
    }

    private void playDotEatingSound() {
        if (pacManSound) {
            if (gameplayMode == GameplayMode.ORDINARY_PLAYING) {
                String c = dotEatingSoundPart == 1
                                ? "eating_dot_1"
                                : "eating_dot_2";
                playSound(c, 1 + dotEatingChannel, true);
                dotEatingChannel = (dotEatingChannel + 1) % 2; // 0 と 1をスイッチ
                dotEatingSoundPart = 3 - dotEatingSoundPart; // 1 と 2 をスイッチ
            }
        }
    }

    public void playAmbientSound() {
        if (pacManSound) {
            String b = null;
            if (gameplayMode == GameplayMode.ORDINARY_PLAYING
                    || gameplayMode == GameplayMode.GHOST_DIED) {
                Playfield playfieldEl = getPlayfieldEl();
                b = ghostEyesCount != 0
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

            if (b != null) {
                if (b.equals(oldAmbient)) {
                    return;
                }

                soundPlayer.playAmbient(b);
                oldAmbient = b;

            }
        }
    }

    private void playCutsceneTrack() {
        if (pacManSound) {
            soundPlayer.playCutsceneAmbient();
        }
    }

    private void stopCutsceneTrack() {
        soundPlayer.stopCutsceneAmbient();
    }

    private void initializeTickTimer() {
        fps = C[fpsChoice];
        tickInterval = 1000 / fps;
        tickMultiplier = D / fps;
        timing = new float[w.length];
        for (int b = 0; b < w.length; b++) {
            float c = !pacManSound && (b == 7 || b == 8) ? 1 : w[b]; // timing[7] -> GameplayMode 4, timing[8] -> GameplayMode 5. ともにゲーム開始直後がらみ.
            timing[b] = Math.round(c * D); // D = 90より、timingの要素はindex 7, 8以外は90.
        }
        lastTime = new Date().getTime();
        lastTimeDelta = 0;
        lastTimeSlownessCount = 0;
    }

    private void setTimeout() {
        view.redrawHandler.sleep(Math.round(tickInterval));
    }

    private void decreaseFps() {
        if (fpsChoice < C.length - 1) {
            fpsChoice++;
            initializeTickTimer();
            if (fpsChoice == C.length - 1)
                canDecreaseFps = false;
        }
    }

    private void createCanvasElement() {
        canvasEl = new PacmanCanvas(sourceImage);
        canvasEl.init();
    }

    // TODO: 要メソッド名見直し
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
