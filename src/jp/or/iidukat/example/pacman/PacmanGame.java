package jp.or.iidukat.example.pacman;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.or.iidukat.example.pacman.entity.CutsceneCanvas;
import jp.or.iidukat.example.pacman.entity.Door;
import jp.or.iidukat.example.pacman.entity.E;
import jp.or.iidukat.example.pacman.entity.Fruit;
import jp.or.iidukat.example.pacman.entity.Level;
import jp.or.iidukat.example.pacman.entity.Lives;
import jp.or.iidukat.example.pacman.entity.PacManCanvas;
import jp.or.iidukat.example.pacman.entity.PlayField;
import jp.or.iidukat.example.pacman.entity.Score;
import jp.or.iidukat.example.pacman.entity.ScoreLabel;
import jp.or.iidukat.example.pacman.entity.Sound;
import jp.or.iidukat.example.pacman.entity.PlayField.Food;
import jp.or.iidukat.example.pacman.entity.PlayField.GameOver;
import jp.or.iidukat.example.pacman.entity.PlayField.KillScreenTile;
import jp.or.iidukat.example.pacman.entity.PlayField.Ready;
import android.util.FloatMath;
import android.view.MotionEvent;


public class PacmanGame {
	
	private static final String TAG = "PacmanGame";
	private static final int DEFAULT_KILL_SCREEN_LEVEL = 256;

    private static final int[] v = {80, 312}; // フルーツ出現位置
    
    public static int[] getV() {
		return v;
	}

	// レベル再開後、一定数のエサが食べられるとモンスターが巣から出てくる
    // そのしきい値をモンスター毎に設定
    private static final int[] m = {0, 7, 17, 32};

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

    // パスの配列.左上:(5, 1), 左下:(5, 15), 右上:(60, 1), 右下:(60, 15).
    // 配列要素のオブジェクトのプロパティは(x, y, w) もしくは(x, y, h)
    // 要素のオブジェクトにwプロパティがあり:横方向, 要素のオブジェクトにhプロパティがあり:縦方向
    // x, yはパスの始点の座標
    // h, wは各々パスの長さを表現
    // [例外] typeプロパティを値1でもつパスはワープつき
    private static class Path {
    	final int x;
    	final int y;
    	final int w;
    	final int h;
    	final boolean tunnel;
    	
    	private Path(int x, int y, int w, int h) {
    		this(x, y, w, h, false);
    	}
    	private Path(int x, int y, int w, int h, boolean tunnel) {
    		this.x = x;
    		this.y = y;
    		this.w = w;
    		this.h = h;
    		this.tunnel = tunnel;
    	}

    	static Path createHorizontalPath(int x, int y, int w) {
    		return new Path(x, y, w, 0);
    	}
    	static Path createVerticalPath(int x, int y, int h) {
    		return new Path(x, y, 0, h);
    	}
    	static Path createTunnelPath(int x, int y, int w) {
    		return new Path(x, y, w, 0, true);
    	}
    }

    private static final Path[] n = {
    	Path.createHorizontalPath(5, 1, 56),
    	Path.createHorizontalPath(5, 4, 5),
    	Path.createVerticalPath(5, 1, 4),
    	Path.createVerticalPath(9, 1, 12),
    	Path.createVerticalPath(5, 12, 4),
    	Path.createVerticalPath(10, 12, 4),
    	Path.createHorizontalPath(5, 15, 16),
    	Path.createHorizontalPath(5, 12, 31),
    	Path.createVerticalPath(60, 1, 4),
    	Path.createVerticalPath(54, 1, 4),
    	Path.createVerticalPath(19, 1, 12),
    	Path.createHorizontalPath(19, 4, 26),
    	Path.createHorizontalPath(13, 5, 7),
    	Path.createVerticalPath(13, 5, 4),
    	Path.createHorizontalPath(13, 8, 3),
    	Path.createVerticalPath(56, 4, 9),
    	Path.createHorizontalPath(48, 4, 13),
    	Path.createVerticalPath(48, 1, 12),
    	Path.createVerticalPath(60, 12, 4),
    	Path.createHorizontalPath(44, 15, 17),
    	Path.createVerticalPath(54, 12, 4),
    	Path.createHorizontalPath(44, 12, 17),
    	Path.createVerticalPath(44, 1, 15),
    	Path.createHorizontalPath(41, 13, 4),
    	Path.createVerticalPath(41, 13, 3),
    	Path.createVerticalPath(38, 13, 3),
    	Path.createHorizontalPath(38, 15, 4),
    	Path.createHorizontalPath(35, 10, 10),
    	Path.createVerticalPath(35, 1, 15),
    	Path.createHorizontalPath(35, 13, 4),
    	Path.createVerticalPath(21, 12, 4),
    	Path.createVerticalPath(24, 12, 4),
    	Path.createHorizontalPath(24, 15, 12),
    	Path.createVerticalPath(27, 4, 9),
    	Path.createHorizontalPath(52, 9, 5),
    	Path.createTunnelPath(56, 8, 10),
    	Path.createTunnelPath(1, 8, 9),
    };
    
    // エサの存在しないパス
    // 左上:(5, 1), 左下:(5, 15), 右上:(60, 1), 右下:(60, 15).
    private static final Path[] o = {
    	Path.createHorizontalPath(1, 8, 8),
    	Path.createHorizontalPath(57, 8, 9),
    	Path.createVerticalPath(44, 2, 10),
    	Path.createVerticalPath(35, 5, 7),
    	Path.createHorizontalPath(36, 4, 8),
    	Path.createHorizontalPath(36, 10, 8),
    	Path.createHorizontalPath(39, 15, 2),
    };

    public static class Position {
    	final int x;
    	final int y;
    	Position(int x, int y) {
    		this.x = x;
    		this.y = y;
    	}
		public int getX() {
			return x;
		}
		public int getY() {
			return y;
		}
    }

    // パワーエサ
    private static final Position[] p = {
    	new Position(5, 15),
    	new Position(5, 3),
    	new Position(15, 8),
    	new Position(60, 3),
    	new Position(60, 15),
    };
    
	// ワープトンネル
    private static final Position[] q = {
    	new Position(2, 8),
    	new Position(63, 8),
    };
    
    public static Position[] getQ() {
		return q;
	}

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
        private int frightTime;
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
            private int frightTotalTime;
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

            Builder frightTotalTime(int val) {
            	this.frightTotalTime = val;
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
            this.frightTime = builder.frightTime;
            this.frightTotalTime = builder.frightTotalTime;
            this.frightBlinkCount = builder.frightBlinkCount;
            this.fruit = builder.fruit;
            this.fruitScore = builder.fruitScore;
            this.ghostModeSwitchTimes = builder.ghostModeSwitchTimes;
            this.penForceTime = builder.penForceTime;
            this.penLeavingLimits = builder.penLeavingLimits;
            this.cutsceneId = builder.cutsceneId;
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
    	final CutsceneActor[] actors;
    	final CutsceneSequence[] sequence;
    	Cutscene(CutsceneActor[] actors, CutsceneSequence[] sequence) {
    		this.actors = actors;
    		this.sequence = sequence;
    	}
    }
    private static class CutsceneActor {
    	final boolean ghost;
    	final float x;
    	final float y;
    	final int id;
    	CutsceneActor(boolean ghost, float x, float y, int id) {
    		this.ghost = ghost;
    		this.x = x;
    		this.y = y;
    		this.id = id;
    	}
    }
    private static class CutsceneSequence {
    	final float time;
    	final MoveInCutscene[] moves;
    	CutsceneSequence(float time, MoveInCutscene[] moves) {
    		this.time = time;
    		this.moves = moves;
    	}
    }
    private static class MoveInCutscene {
    	final Direction dir;
    	final float speed;
    	final String elId;
    	final GhostMode mode;
    	MoveInCutscene(Direction dir, float speed) {
    		this(dir, speed, null, null);
		}
    	MoveInCutscene(Direction dir, float speed, String elId) {
    		this(dir, speed, elId, null);
    	}
    	MoveInCutscene(Direction dir, float speed, GhostMode mode) {
    		this(dir, speed, null, mode);
    	}
    	MoveInCutscene(Direction dir, float speed, String elId, GhostMode mode) {
    		this.dir = dir;
    		this.speed = speed;
    		this.elId = elId;
    		this.mode = mode;
		}
    }

    private static final Map<Integer, Cutscene> B;
    static {
    	Map<Integer, Cutscene> css = new HashMap<Integer, Cutscene>();
    	css.put(
    		Integer.valueOf(1),
    		new Cutscene(
    			new CutsceneActor[] {
    				new CutsceneActor(false, 64, 9, 0),
    				new CutsceneActor(true, 68.2f, 9, 1),
    			},
    			new CutsceneSequence[] {
    				new CutsceneSequence(
    					5.5f,
	    				new MoveInCutscene[] {
	        				new MoveInCutscene(Direction.LEFT, 0.75f * 0.8f * 2),
	    					new MoveInCutscene(Direction.LEFT, 0.78f * 0.8f * 2),
    					}),
    				new CutsceneSequence(
    					0.1f,
	    				new MoveInCutscene[] {
	        				new MoveInCutscene(Direction.LEFT, 32),
	    					new MoveInCutscene(Direction.LEFT, 0),
    					}),
    				new CutsceneSequence(
       					9,
   	    				new MoveInCutscene[] {
   	        				new MoveInCutscene(Direction.RIGHT, 0.75f * 0.8f * 2, "pcm-bpcm"),
   	    					new MoveInCutscene(Direction.RIGHT, 0.8f, GhostMode.FRIGHTENED),
       					}),
    			}));
    	
    	css.put(
    		Integer.valueOf(2),
    		new Cutscene(
    			new CutsceneActor[] {
    				new CutsceneActor(false, 64, 9, 0),
    				new CutsceneActor(true, 70.2f, 9, 1),
    				new CutsceneActor(true, 32, 9.5f, 2),
    			},
    			new CutsceneSequence[] {
    				new CutsceneSequence(
    					2.7f,
    					new MoveInCutscene[] {
    						new MoveInCutscene(Direction.LEFT, 0.75f * 0.8f * 2),
    						new MoveInCutscene(Direction.LEFT, 0.78f * 0.8f * 2),
    						new MoveInCutscene(Direction.NONE, 0, "pcm-stck"),
    					}),
       				new CutsceneSequence(
       					1,
       					new MoveInCutscene[] {
       						new MoveInCutscene(Direction.LEFT, 0.75f * 0.8f * 2),
       						new MoveInCutscene(Direction.LEFT, 0.1f * 0.8f),
       						new MoveInCutscene(Direction.NONE, 0, "pcm-stck"),
       					}),
       				new CutsceneSequence(
       					1.3f,
       					new MoveInCutscene[] {
       						new MoveInCutscene(Direction.LEFT, 0.75f * 0.8f * 2),
       						new MoveInCutscene(Direction.LEFT, 0),
       						new MoveInCutscene(Direction.NONE, 0, "pcm-stck"),
       					}),
       				new CutsceneSequence(
       					1,
       					new MoveInCutscene[] {
       						new MoveInCutscene(Direction.LEFT, 0.75f * 0.8f * 2),
      						new MoveInCutscene(Direction.LEFT, 0, "pcm-ghfa"),
       						new MoveInCutscene(Direction.NONE, 0, "pcm-stck"),
       					}),
       				new CutsceneSequence(
       					2.5f,
       					new MoveInCutscene[] {
       						new MoveInCutscene(Direction.LEFT, 0.75f * 0.8f * 2),
      						new MoveInCutscene(Direction.LEFT, 0, "pcm-ghfa"),
       						new MoveInCutscene(Direction.NONE, 0, "pcm-stck"),
       					}),
    			}));
    	css.put(
    		Integer.valueOf(3),
    		new Cutscene(
    			new CutsceneActor[] {
    				new CutsceneActor(false, 64, 9, 0),
    				new CutsceneActor(true, 70.2f, 9, 2),
    			},
    			new CutsceneSequence[] {
    				new CutsceneSequence(
    					5.3f,
    					new MoveInCutscene[] {
        					new MoveInCutscene(Direction.LEFT, 0.75f * 0.8f * 2),
    						new MoveInCutscene(Direction.LEFT, 0.78f * 0.8f * 2, "pcm-ghin"),
    					}),
    				new CutsceneSequence(
       					5.3f,
       					new MoveInCutscene[] {
           					new MoveInCutscene(Direction.LEFT, 0),
       						new MoveInCutscene(Direction.RIGHT, 0.78f * 0.8f * 2, "pcm-gbug"),
       					}),
    			}));
    	B = Collections.unmodifiableMap(css);
    }
    
    private static final int[] C = { 90, 45, 30, }; // fps オプション
    private static final int D = C[0]; // 本来想定されているfps
    
    private final GameFieldView view;
    private SoundPlayer soundPlayer;
    private AudioClip cutsceneAudioClip;
    private boolean ready;
    private boolean soundReady;
    private boolean graphicsReady;        
	private long randSeed;
	private int playfieldWidth;
	private int playfieldHeight;
	private Map<Integer, Map<Integer, PathElement>> playfield;
	private int dotsRemaining;
	private int dotsEaten;
	private PacManCanvas canvasEl;
	private PlayField playfieldEl;
	private CutsceneCanvas cutsceneCanvasEl;
	private Fruit fruitEl;
	private Door doorEl;
	private Sound soundEl;
	private int playerCount;
	private E[] actors;
	
	private float touchDX;
	private float touchDY;
	private float touchStartX;
	private float touchStartY;
	private boolean touchCanceld = true;
	
    private int[] score;
    private boolean[] extraLifeAwarded;
    private int lives = 3;
    private int level = 0;
    private int killScreenLevel = DEFAULT_KILL_SCREEN_LEVEL;
    private LevelConfig levels;
    private boolean paused = false;
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
    private int[] dotEatingChannel;
    private int[] dotEatingSoundPart;
    
    private GameplayMode gameplayMode;
    
    private int killScreenTileX;
    private int killScreenTileY;

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
	
	private int ghostBeingEatenId;
	private int playerEatingGhostId;
	private int playerDyingId;
	
	private boolean pacManSound = true;
	volatile boolean soundAvailable;
	private boolean userDisabledSound;
	
	private Cutscene cutscene;
	private int cutsceneId;
	private int cutsceneSequenceId;
	private E[] cutsceneActors;
	private float cutsceneTime;
	
	private float tickInterval;
	private float lastTimeDelta;
	private long lastTime;
	private int fpsChoice;
	private int fps;
	private boolean canDecreaseFps;
	private int lastTimeSlownessCount;
	private int tickMultiplier;
	
	private int scoreDigits;
	private ScoreLabel[] scoreLabelEl;
	private Score[] scoreEl;
	private Lives livesEl;
	private Level levelEl;
	
    private boolean[] dotEatingNow;
    private boolean[] dotEatingNext;
    
    PacmanGame(GameFieldView view) {
    	this.view = view;
    }


	public float rand() {
		long b = 4294967296L;
		long c = 134775813L;
		c = c * randSeed + 1;
		return (randSeed = c % b) / (float) b;
	}
	  
	void seed(long b) {
		this.randSeed = b;
	}

	public static float getDistance(int [] b, int [] c) {
		return FloatMath.sqrt((c[1] - b[1]) * (c[1] - b[1]) + (c[0] - b[0]) * (c[0] - b[0]));
	}

	public static float getDistance(float[] b, float[] c) {
		return FloatMath.sqrt((c[1] - b[1]) * (c[1] - b[1]) + (c[0] - b[0]) * (c[0] - b[0]));
	}
  
	public static float getPlayfieldX(float b) {
		return b + -32;
	}
  
	public static float getPlayfieldY(float b) {
		return b + 0;
	}
  
	int getCorrectedSpritePos(int b) {
		return b / 8 * 10 + 2;
	}
  
	int getDotElementIndex(int b, int c) {
		return 1000 * b + c;
	}
  
	float[] getAbsoluteElPos(Presentation presentation) {
		// TODO: 要修正
		Presentation b = presentation;
		float[] c = { 0, 0 };
		do {
			c[0] += b.getTop();
			c[1] += b.getLeft();
		} while ((b = b.getParent()) != null);
		return c;
	}
	
	public void prepareElement(Presentation b, int c, int d) {
		c = getCorrectedSpritePos(c);
		d = getCorrectedSpritePos(d);
		b.setBgPosX(c);
		b.setBgPosY(d);
	}
	public void changeElementBkPos(Presentation b, int c, int d, boolean f) {
		if (f) {
			c = getCorrectedSpritePos(c);
			d = getCorrectedSpritePos(d);
		}
		b.setBgPosX(c);
		b.setBgPosY(d);
	}
	void determinePlayfieldDimensions() {
		playfieldWidth = 0;
	    playfieldHeight = 0;
	    for (Path c : n) {
	    	if (c.w > 0) {
	    		int x = c.x + c.w - 1;
	    		if (x > playfieldWidth) playfieldWidth = x;
	    	} else {
	    		int y = c.y + c.h - 1;
	    		if (y > playfieldHeight) playfieldHeight = y;
	    	}
	    }
	}
	
	void preparePlayfield() {
		playfield = new HashMap<Integer, Map<Integer, PathElement>>();
	    for (int b = 0; b <= playfieldHeight + 1; b++) {
	    	Map<Integer, PathElement> row = new HashMap<Integer, PathElement>();
	    	for (int c = -2; c <= playfieldWidth + 1; c++) {
	    		PathElement p = new PathElement();
	    		p.setPath(false);
	    		p.setDot(0);
	    		p.setIntersection(false);
	    		row.put(Integer.valueOf(c * 8), p);
	    	}
	    	playfield.put(Integer.valueOf(b * 8), row);
	    }
	}
	void preparePaths() {
		for (Path c : n) {
			boolean d = c.tunnel;
			if (c.w > 0) {
				int f = c.y * 8;
				for (int h = c.x * 8; h <= (c.x + c.w - 1) * 8; h += 8) {
					PathElement pe = playfield.get(Integer.valueOf(f)).get(Integer.valueOf(h));
					pe.setPath(true);
					if (pe.getDot() == 0) {
						pe.setDot(1);
						dotsRemaining++;
					}
					pe.setTunnel(!d || h != c.x * 8 && h != (c.x + c.w - 1) * 8 ? d : false);
				}	
				playfield.get(Integer.valueOf(f)).get(Integer.valueOf(c.x * 8)).setIntersection(true);
				playfield.get(Integer.valueOf(f)).get(Integer.valueOf((c.x + c.w - 1) * 8)).setIntersection(true);
			} else {
				int h = c.x * 8;
				for (int f = c.y * 8; f <= (c.y + c.h - 1) * 8; f += 8) {
					PathElement pe = playfield.get(Integer.valueOf(f)).get(Integer.valueOf(h));		        	
					if (pe.isPath()) pe.setIntersection(true);
					pe.setPath(true);
					if (pe.getDot() == 0) {
						pe.setDot(1);
						dotsRemaining++;
					}
					pe.setTunnel(!d || f != c.y * 8 && f != (c.y + c.h - 1) * 8 ? d : false);
				}
				playfield.get(Integer.valueOf(c.y * 8)).get(Integer.valueOf(h)).setIntersection(true);
				playfield.get(Integer.valueOf((c.y + c.h - 1) * 8)).get(Integer.valueOf(h)).setIntersection(true);
			}
	    }
	    for (Path p : o)
	    	if (p.w != 0)
	    		for (int h = p.x * 8; h <= (p.x + p.w - 1) * 8; h += 8) {
	    			playfield.get(Integer.valueOf(p.y * 8)).get(Integer.valueOf(h)).setDot(0);
	    			dotsRemaining--;
	    		}
	    	else
	    		for (int f = p.y * 8; f <= (p.y + p.h - 1) * 8; f += 8) {
	    			playfield.get(Integer.valueOf(f)).get(Integer.valueOf(p.x * 8)).setDot(0);
	    			dotsRemaining--;
	    		}
	}
	
	void prepareAllowedDirections() {
	    for (int b = 8; b <= playfieldHeight * 8; b += 8)
	    	for (int c = 8; c <= playfieldWidth * 8; c += 8) {
	    		PathElement pe = playfield.get(Integer.valueOf(b)).get(Integer.valueOf(c));
	    		EnumSet<Direction> allowedDir = EnumSet.noneOf(Direction.class);
	    		if (playfield.get(Integer.valueOf(b - 8)).get(Integer.valueOf(c)).isPath()) allowedDir.add(Direction.UP);
	    		if (playfield.get(Integer.valueOf(b + 8)).get(Integer.valueOf(c)).isPath()) allowedDir.add(Direction.DOWN);
	    		if (playfield.get(Integer.valueOf(b)).get(Integer.valueOf(c - 8)).isPath()) allowedDir.add(Direction.LEFT);
	    		if (playfield.get(Integer.valueOf(b)).get(Integer.valueOf(c + 8)).isPath()) allowedDir.add(Direction.RIGHT);
	    		pe.setAllowedDir(allowedDir);
	    	}
	}
	// エサを作成
	void createDotElements() {
		playfieldEl.clearFoods();
	    for (int b = 8; b <= playfieldHeight * 8; b += 8)
	    	for (int c = 8; c <= playfieldWidth * 8; c += 8)
	    		if (playfield.get(Integer.valueOf(b)).get(Integer.valueOf(c)).getDot() != 0) {
	    			Food food = new Food();
	    			food.getPresentation().setIndex(getDotElementIndex(b, c));
	          		food.getPresentation().setLeft(c + -32 + 3); // margin-left: 3
	          		food.getPresentation().setTop(b + 0 + 3); // margint-top: 3
	          		food.getPresentation().setWidth(2);
	          		food.getPresentation().setHeight(2);
	          		food.getPresentation().setBgColor(0xf8b090);
	          		food.getPresentation().setParent(playfieldEl.getPresentation());
	          		playfieldEl.addFood(food);
	    		}
	}
	
	// パワーエサを作成
	void createEnergizerElements() {
	    for (Position c : p) {
	      	int d = getDotElementIndex(c.y * 8, c.x * 8);
	      	Food f = getDotElement(d);
	      	if (f == null) continue;
//		      	document.getElementById(d).className = "pcm-e";
	      	f.getPresentation().setLeft(f.getPresentation().getLeft() - 3);
	      	f.getPresentation().setTop(f.getPresentation().getTop() - 3);
	      	f.getPresentation().setWidth(8);
	      	f.getPresentation().setHeight(8);
	      	prepareElement(f.getPresentation(), 0, 144);
	    	playfield.get(Integer.valueOf(c.y * 8)).get(Integer.valueOf(c.x * 8)).setDot(2);
	    }
	}
	
	private Food getDotElement(int index) {
		for (Food f : playfieldEl.getFoods()) {
			if (f.getPresentation().getIndex() == index) {
				return f;
			}
		}
		
		return null;
	}
	
	void createFruitElement() {
		fruitEl = new Fruit();
	    fruitEl.getPresentation().setId("pcm-f");
	    fruitEl.getPresentation().setWidth(32);
	    fruitEl.getPresentation().setHeight(16);
	    fruitEl.getPresentation().setLeft(getPlayfieldX(v[1]) - 8);
	    fruitEl.getPresentation().setTop(getPlayfieldY(v[0]) - 4);
	    prepareElement(fruitEl.getPresentation(), -32, -16);
	    fruitEl.getPresentation().setParent(playfieldEl.getPresentation());
	    playfieldEl.setFruit(fruitEl);
	}
	
	void createPlayfieldElements() {
		doorEl = new Door();
		doorEl.getPresentation().setId("pcm-do");
		doorEl.getPresentation().setWidth(19);
		doorEl.getPresentation().setHeight(2);
		doorEl.getPresentation().setLeft(279);
		doorEl.getPresentation().setTop(46);
		doorEl.getPresentation().setBgColor(0xffaaa5);
		doorEl.getPresentation().setVisibility(false);
		doorEl.getPresentation().setParent(playfieldEl.getPresentation());
		playfieldEl.setDoor(doorEl);
		createDotElements();
		createEnergizerElements();
		createFruitElement();
	}

  	void createActors() {
  		List<E> as = new ArrayList<E>(); 
  		for (int b = 0; b < playerCount + 4; b++) {
  			E actor = new E(b, this);
  			if (b < playerCount) {
  				actor.setGhost(false);
  				actor.setMode(GhostMode.CHASE);
  			} else {
  				actor.setGhost(true);
  			}
  			as.add(actor);	
	  	}
  		actors = as.toArray(new E[0]);
  	}

  	void restartActors() {
  		for (E actor : actors)
  			actor.A();
  	}
  	
	void createActorElements() {
		for (E actor : actors)
			actor.createElement();
	}

	void createPlayfield() {
	    playfieldEl = new PlayField();
	    Presentation p = playfieldEl.getPresentation();
	    p.setId("pcm-p");
	    p.setLeft(45);
	    p.setWidth(464);
	    p.setHeight(136);
	    p.setParent(canvasEl.getPresentation());
	    canvasEl.setPlayfield(playfieldEl);
	}
	
    void resetPlayfield() {
	    dotsRemaining = 0;
	    dotsEaten = 0;
	    prepareElement(playfieldEl.getPresentation(), 256, 0);
	    determinePlayfieldDimensions();
	    preparePlayfield();
	    preparePaths();
	    prepareAllowedDirections();
	    createPlayfieldElements();
	    createActorElements();
    }
    
//	    boolean keyPressed(int b){
//		    boolean  c = e;
//		    switch (b) {
//		    case 37: // Left
//		      actors[0].requestedDir = 4;
//		      c = a;
//		      break;
//		    case 38: // Up
//		      actors[0].requestedDir = 1;
//		      c = a;
//		      break;
//		    case 39: // Right
//		      actors[0].requestedDir = 8;
//		      c = a;
//		      break;
//		    case 40: // Down
//		      actors[0].requestedDir = 2;
//		      c = a;
//		      break;
//		    case 65: // A as Left for 2P
//		      if (playerCount == 2) {
//		        actors[1].requestedDir = 4;
//		        c = a;
//		      }
//		      break;
//		    case 83: // S as Down for 2P
//		      if (playerCount == 2) {
//		        actors[1].requestedDir = 2;
//		        c = a;
//		      }
//		      break;
//		    case 68: // D as Right for 2P
//		      if (playerCount == 2) {
//		        actors[1].requestedDir = 8;
//		        c = a;
//		      }
//		      break;
//		    case 87: // W as UP for 2P
//		      if (playerCount == 2) {
//		        actors[1].requestedDir = 1;
//		        c = a;
//		      }
//		      break;
//		    }
//		    return c;
//		}
//	    
//	    void handleKeyDown(Object b) {
////	    	if (b != null) b = window.event;
////	    	if (g.keyPressed(b.keyCode))
////	    		if (b.preventDefault) b.preventDefault();
////	    		else b.returnValue = e
//	    }
    
	void canvasClicked(float b, float c) {
		// TODO: actorsがロードされる前にクリックされるケースに対処する
		if (handleSoundIconClick(b, c)) {
			return;
		}
		
	    float[] d = getAbsoluteElPos(canvasEl.getPresentation());
	    b -= d[1] - -32;
	    c -= d[0] - 0;
	    E player = actors[0];
	    float f = getPlayfieldX(player.getPos()[1] + player.getPosDelta()[1]) + 16;
	    float h = getPlayfieldY(player.getPos()[0] + player.getPosDelta()[0]) + 32;
	    float j = Math.abs(b - f);
	    float k = Math.abs(c - h);
	    if (j > 8 && k < j) player.setRequestedDir(b > f ? Direction.RIGHT : Direction.LEFT);
	    else if (k > 8 && j < k) player.setRequestedDir(c > h ? Direction.DOWN : Direction.UP);
	}
	
	boolean handleSoundIconClick(float b, float c) {
		// TODO: サウンドアイコンがロードされる前にクリックされるケースに対処する
		// タイトル画面とゲーム画面で別々のViewを用意し、タイトル画面でサウンドまわりの初期化を行えば解決しそう
		if (!soundAvailable) return false;
		
		float[] d = getAbsoluteElPos(soundEl.getPresentation());
		if (d[1] <= b && b <= d[1] + 12) {
			if (d[0] <= c && c <= d[0] + 12) {
				toggleSound();
				return true;
			}
		}
		
		return false;
	}

//		void handleClick(Object b) {
////			if (b != null) b = window.event;
////			canvasClicked(b.clientX, b.clientY);
//		}
	
//	    void registerTouch() {
////	    	document.body.addEventListener("touchstart", g.handleTouchStart, a);
////	    	canvasEl.addEventListener("touchstart", g.handleTouchStart, a);
////	    	document.f && document.f.q && document.f.q.addEventListener("touchstart", g.handleTouchStart, a);
//	    }
    
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
    	if (touchCanceld) return;
    	
    	if (e.getPointerCount() > 1) cancelTouch();
    	else {
    		touchDX = e.getX(0) - touchStartX;
    		touchDY = e.getY(0) - touchStartY;
    	}
    }

    void handleTouchEnd(MotionEvent e) {
    	if (touchCanceld) return;
    	
    	float c = Math.abs(touchDX);
    	float d = Math.abs(touchDY);
    	if (c < 8 && d < 8) canvasClicked(touchStartX, touchStartY);
    	else if (c > 15 && d < c * 2 / 3) actors[0].setRequestedDir(touchDX > 0 ? Direction.RIGHT : Direction.LEFT);
    	else if (d > 15 && c < d * 2 / 3) actors[0].setRequestedDir(touchDY > 0 ? Direction.DOWN : Direction.UP);
	    cancelTouch();
    }
    
	void cancelTouch() {
	    touchStartX = Float.NaN;
	    touchStartY = Float.NaN;
	    touchCanceld = true;
	}
	
	void startGameplay() {
	    score = new int[] {0, 0};
	    extraLifeAwarded = new boolean[] {false, false};
	    lives = 3;
	    level = 0;
	    paused = false;
	    globalTime = 0;
	    newLevel(true);
	}
	
	// b true:新規ゲーム false:ゲーム再開(プレイヤー死亡後or新レベル)
	void restartGameplay(boolean b) {
	    seed(0);
	    frightModeTime = 0;
	    intervalTime = 0;
	    gameplayModeTime = 0;
	    fruitTime = 0;
	    ghostModeSwitchPos = 0;
	    ghostModeTime = levels.ghostModeSwitchTimes[0] * D;
	    ghostExitingPenNow = false;
	    ghostEyesCount = 0;
	    tilesChanged = false;
	    updateCruiseElroySpeed();
	    hideFruit();
	    resetForcePenLeaveTime();
	    restartActors();
	    updateActorPositions();
	    switchMainGhostMode(GhostMode.SCATTER, true);
	    for (int c = playerCount + 1; c < playerCount + 4; c++) actors[c].a(GhostMode.IN_PEN);
	    dotEatingChannel = new int[] {0, 0};
	    dotEatingSoundPart = new int[] {1, 1};
	    clearDotEatingNow();

	    if (b) changeGameplayMode(GameplayMode.NEWGAME_STARTING);
	    else changeGameplayMode(GameplayMode.GAME_RESTARTING);
//		    changeGameplayMode(10);
	}

	void initiateDoubleMode() {
	    if (playerCount != 2) {
	    	stopAllAudio();
	    	changeGameplayMode(GameplayMode.TWO_PLAYER_PLAY_STARTED);
	    }
	}
	
	void newGame() {
	    playerCount = 1;
	    createChrome();
	    createPlayfield();
	    createActors();
	    startGameplay();
	}

	void switchToDoubleMode() {
	    playerCount = 2;
	    createChrome();
	    createPlayfield();
	    createActors();
	    startGameplay();
	}
	
	void insertCoin() {
	    if (gameplayMode == GameplayMode.GAMEOVER
	    		|| gameplayMode == GameplayMode.KILL_SCREEN)
	    	newGame();
	    else
	    	initiateDoubleMode();
	}

	void createKillScreenElement(int b, int c, int d, int f, boolean h) {
//		    var j = document.createElement("div");
		KillScreenTile j = new KillScreenTile();
		j.getPresentation().setLeft(b); // j.style.left = b + "px";
		j.getPresentation().setTop(c); // j.style.top = c + "px";
		j.getPresentation().setWidth(d); // j.style.width = d + "px";
		j.getPresentation().setHeight(f); // j.style.height = f + "px";
//		    j.style.zIndex = 119;
	    if (h) {
//		      	j.style.background = "url(src/pacman10-hp-sprite-2.png) -" + killScreenTileX + "px -" + killScreenTileY + "px no-repeat";
	    	j.getPresentation().setBgPosX(killScreenTileX);
	    	j.getPresentation().setBgPosY(killScreenTileY);
	    	killScreenTileY += 8;
	    } else {
	    	j.getPresentation().setBgColor(0x000000); // j.style.background = "black";
	    }
	    playfieldEl.addKillScreenTile(j);
	    j.getPresentation().setParent(playfieldEl.getPresentation()); // playfieldEl.appendChild(j)
	}
  
	void killScreen() {
	    seed(0);
//	    canvasEl.style.visibility = "";
	    canvasEl.getPresentation().setVisibility(true);
	    createKillScreenElement(272, 0, 200, 80, false);
	    createKillScreenElement(280, 80, 192, 56, false);
	    killScreenTileX = 80;
	    killScreenTileY = 0;
	    for (int b = 280; b <= 472; b += 8)
	    	for (int c = 0; c <= 136; c += 8) {
	    		if (rand() < 0.03) {
	    			killScreenTileX = (int) FloatMath.floor(rand() * 25) * 10;
	    			killScreenTileY = (int) FloatMath.floor(rand() * 2) * 10;
	    		}
	    		createKillScreenElement(b, c, 8, 8, true);
	    	}
	
	    changeGameplayMode(GameplayMode.KILL_SCREEN);
	}
	
	void newLevel(boolean b) {
	    level++;
	    levels = level >= z.length ? z[z.length - 1] : z[level];
	    // start issue 14: Ghosts stay blue permanently on restart
	    if ((levels.frightTime > 0) && (levels.frightTime <= 6))
	    	levels.frightTime = Math.round(levels.frightTime * D); // z 配列を定義する際にこの処理を行っておくべきでは?
	    // end issue 14
	    levels.frightTotalTime = levels.frightTime + ((int) timing[1]) * (levels.frightBlinkCount * 2 - 1);
	    for (E actor : actors) actor.setDotCount(0);
	    alternatePenLeavingScheme = false;
	    lostLifeOnThisLevel = false;
	    updateChrome();
	    resetPlayfield();
	    restartGameplay(b);
	    if (level == killScreenLevel) killScreen();
	}
	
	void newLife() {
	    lostLifeOnThisLevel = true;
	    alternatePenLeavingScheme = true;
	    alternateDotCount = 0;
	    lives--;
	    updateChromeLives();
	    
	    if (lives == -1) changeGameplayMode(GameplayMode.GAMEOVER);
	    else restartGameplay(false);
	}
	
	// MainGhostMode切り替え
	// b: 切り替え先のモード c: 開始直後フラグ(trueなら開始直後)
	void switchMainGhostMode(GhostMode b, boolean c) {
	    if (b == GhostMode.FRIGHTENED && levels.frightTime == 0)
	    	for (E actor : actors) {
	    		if (actor.isGhost()) actor.setReverseDirectionsNext(true); // frightTimeが0なら、ブルーモードになってもモンスターは反対に向きを変えるだけ
	    	}
	    else {
	    	GhostMode f = mainGhostMode;
	    	if (b == GhostMode.FRIGHTENED && mainGhostMode != GhostMode.FRIGHTENED) {
	    		lastMainGhostMode = mainGhostMode;
	    	}
	    	mainGhostMode = b;
	    	if (b == GhostMode.FRIGHTENED || f == GhostMode.FRIGHTENED) playAmbientSound();
	    	switch (b) {
	    	case CHASE:
	    	case SCATTER:
	    		currentPlayerSpeed = levels.playerSpeed * 0.8f;
	    		currentDotEatingSpeed = levels.dotEatingSpeed * 0.8f;
	    		break;
	    	case FRIGHTENED:
	    		currentPlayerSpeed = levels.playerFrightSpeed * 0.8f;
	    		currentDotEatingSpeed = levels.dotEatingFrightSpeed * 0.8f;
	    		frightModeTime = levels.frightTotalTime;
	    		modeScoreMultiplier = 1;
	    		break;
	    	}
	    	for (E actor : actors) {
	    		if (actor.isGhost()) {
	    			// b(Main Ghost Mode)は1, 2, 4。 64になるケースは存在しないように思える. 巣の中にいるときにGhostMainModeが変わった、という条件にも合致していない
		        	if (b != GhostMode.ENTERING_PEN && !c) actor.setModeChangedWhileInPen(true); 
		        	if (b == GhostMode.FRIGHTENED) actor.setEatenInThisFrightMode(false);
		        	if (actor.getMode() != GhostMode.EATEN
		        			&& actor.getMode() != GhostMode.IN_PEN
		        			&& actor.getMode() != GhostMode.EXITING_FROM_PEN
		        			&& actor.getMode() != GhostMode.RE_EXITING_FROM_PEN
		        			&& actor.getMode() != GhostMode.ENTERING_PEN || c) {
			        	// ゲーム再開直後(c:true)以外では, モンスターのモードが8, 16, 32, 64, 128ならモード更新対象とならない
			        	// ゲーム再開直後以外でブルーモード(4)以外から異なるモード[追跡モード(1), Scatterモード(2), ブルーモード(4)]への切り替え時が行われるとき、反対に向きを変える 
			        	if (!c && actor.getMode() != GhostMode.FRIGHTENED
			        			&& actor.getMode() != b) {
			        		actor.setReverseDirectionsNext(true);
			        	}
			        	actor.a(b);
		        	}
		        } else {
		        	actor.setFullSpeed(currentPlayerSpeed);
		        	actor.setDotEatingSpeed(currentDotEatingSpeed);
		        	actor.setTunnelSpeed(currentPlayerSpeed);
		        	actor.d();
		        }
	    	}
	    }
	}
	
	void figureOutPenLeaving() {
	    if (alternatePenLeavingScheme) { // レベル再開後のみ食べられたエサの数によりモンスターが出撃するタイミングを管理
	    	alternateDotCount++;
	    	if (alternateDotCount == m[1])
	    		actors[playerCount + 1].setFreeToLeavePen(true);
	    	else if (alternateDotCount == m[2])
	    		actors[playerCount + 2].setFreeToLeavePen(true);
	    	else if (alternateDotCount == m[3])
	    		if (actors[playerCount + 3].getMode() == GhostMode.IN_PEN)
	    			alternatePenLeavingScheme = false;
	    } else if (actors[playerCount + 1].getMode() == GhostMode.IN_PEN
	    			|| actors[playerCount + 1].getMode() == GhostMode.EATEN) {
	    	actors[playerCount + 1].incrementDotCount();
	    	if (actors[playerCount + 1].getDotCount() >= levels.penLeavingLimits[1])
	    		actors[playerCount + 1].setFreeToLeavePen(true);
	    } else if (actors[playerCount + 2].getMode() == GhostMode.IN_PEN
	    			|| actors[playerCount + 2].getMode() == GhostMode.EATEN) {
	    	actors[playerCount + 2].incrementDotCount();
	    	if (actors[playerCount + 2].getDotCount() >= levels.penLeavingLimits[2])
	    		actors[playerCount + 2].setFreeToLeavePen(true);
	    } else if (actors[playerCount + 3].getMode() == GhostMode.IN_PEN
	    			|| actors[playerCount + 3].getMode() == GhostMode.EATEN) {
	    	actors[playerCount + 3].incrementDotCount();
	    	if (actors[playerCount + 3].getDotCount() >= levels.penLeavingLimits[3])
	    		actors[playerCount + 3].setFreeToLeavePen(true);
	    }
	}
	
	void resetForcePenLeaveTime() {
	    forcePenLeaveTime = levels.penForceTime * D;
	}
	
	public void dotEaten(int b, int[] c) {
	    dotsRemaining--;
	    dotsEaten++;
	    actors[b].c(CurrentSpeed.PACMAN_EATING_DOT);
	    playDotEatingSound(b);
	    if (playfield.get(Integer.valueOf(c[0])).get(Integer.valueOf(c[1])).getDot() == 2) { // パワーエサを食べたとき
	    	switchMainGhostMode(GhostMode.FRIGHTENED, false);
	    	addToScore(50, b);
	    } else addToScore(10, b); // 普通のエサ
	    
	    Food d = getDotElement(getDotElementIndex(c[0], c[1]));
//		    d.style.display = "none";
	    d.setEaten(true);
	    d.getPresentation().setVisibility(false);
	    playfield.get(Integer.valueOf(c[0])).get(Integer.valueOf(c[1])).setDot(0);
	    updateCruiseElroySpeed();
	    resetForcePenLeaveTime();
	    figureOutPenLeaving();
	    if (dotsEaten == 70 || dotsEaten == 170) showFruit();
	    if (dotsRemaining == 0) finishLevel();
	    playAmbientSound();
	}
	
	int[] getFruitSprite(int b) {
		int c = b <= 4 ? 128 : 160;
		b = 128 + 16 * ((b - 1) % 4);
		return new int[] { c, b };
	}

	int[] getFruitScoreSprite(int b) {
		int c = 128;
		b = 16 * (b - 1);
		return new int[] { c, b };
	}
		
	void hideFruit() {
		fruitShown = false;
		changeElementBkPos(fruitEl.getPresentation(), 32, 16, true);
	}
	
	void showFruit() {
		fruitShown = true;
		int[] b = getFruitSprite(levels.fruit);
		changeElementBkPos(fruitEl.getPresentation(), b[0], b[1], true);
		fruitTime = (int) timing[15] + (int) ((timing[16] - timing[15]) * rand());
	}
	
	public void eatFruit(int b) {
	    if (fruitShown) {
	    	playSound("fruit", 0);
	    	fruitShown = false;
	    	int[] c = getFruitScoreSprite(levels.fruit);
	    	changeElementBkPos(fruitEl.getPresentation(), c[0], c[1], true);
	    	fruitTime = (int) timing[14];
	    	addToScore(levels.fruitScore, b);
	    }
	}
	
	void updateActorTargetPositions() {
	    for (int b = playerCount; b < playerCount + 4; b++) actors[b].B();
	}
	
	void moveActors() {
		for (E actor : actors) actor.move();
	}
	
	void ghostDies(int b, int c) {
	    playSound("eating_ghost", 0);
		    addToScore(200 * modeScoreMultiplier, c);
		    modeScoreMultiplier *= 2;
		    ghostBeingEatenId = b;
		    playerEatingGhostId = c;
		    changeGameplayMode(GameplayMode.GHOST_DIED);
		}

		void playerDies(int b) {
		    playerDyingId = b;
		    changeGameplayMode(GameplayMode.PLAYER_DYING);
		}
  
		void detectCollisions() {
		    tilesChanged = false;
		    for (int b = playerCount; b < playerCount + 4; b++)
		    	for (int c = 0; c < playerCount; c++)
		    		if (actors[b].getTilePos()[0] == actors[c].getTilePos()[0]
		    		        && actors[b].getTilePos()[1] == actors[c].getTilePos()[1])
		    			if (actors[b].getMode() == GhostMode.FRIGHTENED) {
		    				ghostDies(b, c);
		    				return;
		    			} else
		    				if (actors[b].getMode() != GhostMode.EATEN
		    						&& actors[b].getMode() != GhostMode.IN_PEN
		    						&& actors[b].getMode() != GhostMode.EXITING_FROM_PEN
		    						&& actors[b].getMode() != GhostMode.RE_EXITING_FROM_PEN
		    						&& actors[b].getMode() != GhostMode.ENTERING_PEN)
		    					playerDies(c);
		  }

		public void updateCruiseElroySpeed() {
			float b = levels.ghostSpeed * 0.8f;
			if (!lostLifeOnThisLevel || actors[playerCount + 3].getMode() != GhostMode.IN_PEN) {
				LevelConfig c = levels;
				if (dotsRemaining < c.elroyDotsLeftPart2) b = c.elroySpeedPart2 * 0.8f;
				else if (dotsRemaining < c.elroyDotsLeftPart1) b = c.elroySpeedPart1 * 0.8f;
			}
			if (b != cruiseElroySpeed) {
				cruiseElroySpeed = b;
				actors[playerCount].d(); // アカベエの速度を更新
		}
	}
	
	// speed: intervalTimeをインデックスに対応させた配列。あるintervalTimeでキャラの移動処理が必要かどうかが配列の要素(true/false)
	// ex) 0.64: [false, true, false, true, false, true, ...]
	public Boolean[] getSpeedIntervals(float b){
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
	
	void finishLevel() {
	    changeGameplayMode(GameplayMode.LEVEL_BEING_COMPLETED);
	}
	
	void changeGameplayMode(GameplayMode b){
	    gameplayMode = b;
	    if (b != GameplayMode.CUTSCENE)
	    	for (int c = 0; c < playerCount + 4; c++)
	    		actors[c].b();
	
	    switch (b) {
	    case ORDINARY_PLAYING:
	    	playAmbientSound();
	    	break;
	    case PLAYER_DYING:
	    	stopAllAudio();
	    	gameplayModeTime = timing[3];
	    	break;
	    case PLAYER_DIED:
	    	if (playerDyingId == 0) playSound("death", 0);
	    	else playSound("death_double", 0);
	    	
	    	gameplayModeTime = timing[4];
	    	break;
	    case GAME_RESTARTING:
	    	canvasEl.getPresentation().setVisibility(false);
	    	gameplayModeTime = timing[5];
	    	break;
	    case GAME_RESTARTED:
	    	stopAllAudio();
//		    	canvasEl.style.visibility = "";
	    	canvasEl.getPresentation().setVisibility(true);
//		    	doorEl.style.display = "block";
	    	doorEl.getPresentation().setVisibility(true);
	    	Ready m7_ready = new Ready();
	      	m7_ready.getPresentation().setId("pcm-re");
	      	m7_ready.getPresentation().setWidth(48);
	      	m7_ready.getPresentation().setHeight(8);
	      	m7_ready.getPresentation().setLeft(264);
	      	m7_ready.getPresentation().setTop(80);
	      	prepareElement(m7_ready.getPresentation(), 160, 0);
	      	m7_ready.getPresentation().setParent(playfieldEl.getPresentation());
	      	playfieldEl.setReady(m7_ready);
	    	gameplayModeTime = timing[6];
	    	break;
	    case NEWGAME_STARTING:
//		    	doorEl.style.display = "block";
	    	doorEl.getPresentation().setVisibility(true);
	    	
	    	Ready m4_ready = new Ready();
	      	m4_ready.getPresentation().setId("pcm-re");
	      	m4_ready.getPresentation().setWidth(48);
	      	m4_ready.getPresentation().setHeight(8);
	      	m4_ready.getPresentation().setLeft(264);
	      	m4_ready.getPresentation().setTop(80);
	      	prepareElement(m4_ready.getPresentation(), 160, 0);
	      	m4_ready.getPresentation().setParent(playfieldEl.getPresentation());
	      	playfieldEl.setReady(m4_ready);
		    gameplayModeTime = timing[7];
		    stopAllAudio();
		    
		    if (playerCount == 2) playSound("start_music_double", 0, true);
		    else playSound("start_music", 0, true);
		    
		    break;
	    case NEWGAME_STARTED:
	    	lives--;
	    	updateChromeLives();
	    	gameplayModeTime = timing[8];
	    	break;
	    case GAMEOVER:
	    case KILL_SCREEN:
//		    	Object ready = document.getElementById("pcm-re");
//			    google.dom.remove(ready);
	    	playfieldEl.setReady(null);
		    stopAllAudio();
		    GameOver go = new GameOver();
		    go.getPresentation().setId("pcm-go");
		    go.getPresentation().setWidth(80);
		    go.getPresentation().setHeight(8);
		    go.getPresentation().setLeft(248);
		    go.getPresentation().setTop(80);
		    prepareElement(go.getPresentation(), 8, 152);
		    go.getPresentation().setParent(playfieldEl.getPresentation());
		    playfieldEl.setGameover(go);
		    gameplayModeTime = timing[9];
		    break;
	    case LEVEL_BEING_COMPLETED:
	    	stopAllAudio();
	    	gameplayModeTime = timing[10];
	    	break;
	    case LEVEL_COMPLETED:
//		    	doorEl.style.display = "none";
	    	doorEl.getPresentation().setVisibility(false);
	    	gameplayModeTime = timing[11];
	    	break;
	    case TRANSITION_INTO_NEXT_SCENE:
//		    	canvasEl.style.visibility = "hidden";
	    	canvasEl.getPresentation().setVisibility(false);
	    	gameplayModeTime = timing[12];
	    	break;
	    case TWO_PLAYER_PLAY_STARTED:
//		    	playfieldEl.style.visibility = "hidden";
	    	playfieldEl.getPresentation().setVisibility(false);
	    	gameplayModeTime = timing[13];
	    	break;
	    case GHOST_DIED:
	    	gameplayModeTime = timing[2];
	    	break;
	    case CUTSCENE:
	    	startCutscene();
	    	break;
	    }
	 }

	void showChrome(boolean b) {
	    if (scoreLabelEl[0] != null)
	    	scoreLabelEl[0].getPresentation().setVisibility(b); // showElementById("pcm-sc-1-l", b);
	    
	    if (scoreLabelEl[1] != null)
	    	scoreLabelEl[1].getPresentation().setVisibility(b); // showElementById("pcm-sc-2-l", b);
	    
	    if (scoreEl[0] != null)
	    	scoreEl[0].getPresentation().setVisibility(b); //showElementById("pcm-sc-1", b);
	    
	    if (scoreEl[1] != null)
	    	scoreEl[1].getPresentation().setVisibility(b); // showElementById("pcm-sc-2", b);
	    
	    if (livesEl != null)
	    	livesEl.getPresentation().setVisibility(b);// showElementById("pcm-li", b);
	    
	    if (soundEl != null)
	    	soundEl.getPresentation().setVisibility(b);// showElementById("pcm-so", b);
	}
	
	boolean toggleSound() { // 元は引数b
//		    b = window.event || b;
//	    	b.cancelBubble = a;
	    if (pacManSound) {
	    	userDisabledSound = true;
	    	stopAllAudio();
	    	pacManSound = false;
	    } else {
	    	pacManSound = true;
	    	playAmbientSound();
	    }
	    updateSoundIcon();
//		    return b.returnValue = e;
	    return false;
	}

	public void updateSoundIcon() {
		if (soundEl != null)
			if (pacManSound) changeElementBkPos(soundEl.getPresentation(), 216, 105, false);
			else changeElementBkPos(soundEl.getPresentation(), 236, 105, false);
	}
	
	void startCutscene() {
//			playfieldEl.style.visibility = "hidden";
		playfieldEl.getPresentation().setVisibility(false);
//		    canvasEl.style.visibility = "";
		canvasEl.getPresentation().setVisibility(true);
	    showChrome(false);
	    cutsceneCanvasEl = new CutsceneCanvas();
	    cutsceneCanvasEl.getPresentation().setId("pcm-cc");
	    cutsceneCanvasEl.getPresentation().setLeft(45);
	    cutsceneCanvasEl.getPresentation().setWidth(464);
	    cutsceneCanvasEl.getPresentation().setHeight(136);
	    cutsceneCanvasEl.getPresentation().setParent(canvasEl.getPresentation());
	    canvasEl.setCutsceneCanvas(cutsceneCanvasEl);
	    cutscene = B.get(Integer.valueOf(cutsceneId));
	    cutsceneSequenceId = -1;
	    frightModeTime = levels.frightTotalTime;
	    List<E> cas = new ArrayList<E>();
	    for (CutsceneActor ca : cutscene.actors) {
	    	int c = ca.id;
	    	if (c > 0) c += playerCount - 1;

	    	E actor = new E(c, this);
//		    	d.className = "pcm-ac";
    		actor.getEl().setWidth(16);
    		actor.getEl().setHeight(16);
    		actor.getEl().setId("actor" + c);
	    	prepareElement(actor.getEl(), 0, 0);
	    	actor.setElBackgroundPos(new int[] { 0, 0 });
	    	actor.setElPos(new float[] { 0, 0 });
	    	actor.setPos(new float[] { ca.y * 8, ca.x * 8 });
	    	actor.setPosDelta(new float[] { 0, 0 });
	    	actor.setGhost(ca.ghost);
	    	cutsceneCanvasEl.addActor(actor);
	    	cas.add(actor);
	    }
	    cutsceneActors = cas.toArray(new E[0]); 
	    cutsceneNextSequence();
	    stopAllAudio();
//		    playAmbientSound();
	    playCutsceneTrack();
	}
	
	void stopCutscene() {
		stopCutsceneTrack();
//		    playfieldEl.style.visibility = "";
	    playfieldEl.getPresentation().setVisibility(true);
	    canvasEl.setCutsceneCanvas(null);
	    showChrome(true);
	    newLevel(false);
	}
	
	void cutsceneNextSequence() {
	    cutsceneSequenceId++;
	    if (cutscene.sequence.length == cutsceneSequenceId) stopCutscene();
	    else {
	    	CutsceneSequence b = cutscene.sequence[cutsceneSequenceId];
	    	cutsceneTime = b.time * D;
	    	for (int c = 0; c < cutsceneActors.length; c++) {
	    		E d = cutsceneActors[c];
	    		d.setDir(b.moves[c].dir);
	    		d.setSpeed(b.moves[c].speed);
	        	if (b.moves[c].elId != null) d.getEl().setId(b.moves[c].elId);
	    		if (b.moves[c].mode != null) d.setMode(b.moves[c].mode);
	    		d.b();
	    	}
	    }
	}
	
	void checkCutscene() {
		if (cutsceneTime <= 0) cutsceneNextSequence();
	}
	
	void advanceCutscene() {
	    for (E actor : cutsceneActors) {
	      Move d = actor.getDir().getMove();
	      actor.getPos()[d.getAxis()] += d.getIncrement() * actor.getSpeed();
	      actor.b();
	    }
	    cutsceneTime--;
	}
	
	void updateActorPositions() {
	    for (E actor : actors) actor.k();
	}
	
	// TODO: パワーエサの取得方法を再考する
	void blinkEnergizers() {
	    switch (gameplayMode) {
	    case NEWGAME_STARTING:
	    case NEWGAME_STARTED:
	    case GAME_RESTARTING:
	    case GAME_RESTARTED:
	    case LEVEL_BEING_COMPLETED:
	    case LEVEL_COMPLETED:
	    case TRANSITION_INTO_NEXT_SCENE:
	    case TWO_PLAYER_PLAY_STARTED:
//		      	playfieldEl.className = "";
	    	for (Food f : playfieldEl.getFoods()) {
	    		if (f.getPresentation().hasBackground()) {
	    			f.getPresentation().setVisibility(true);
	    		}
	    	}
	    	break;
	    case GAMEOVER:
	    case KILL_SCREEN:
//		    	playfieldEl.className = "blk";
	    	for (Food f : playfieldEl.getFoods()) {
	    		if (f.getPresentation().hasBackground()) {
	    			f.getPresentation().setVisibility(false);
	    		}
	    	}
	    	break;
	    default:
	    	if (globalTime % (timing[0] * 2) == 0) {
	    		// playfieldEl.className = "";
		    	for (Food f : playfieldEl.getFoods()) {
		    		if (f.getPresentation().hasBackground()) {
		    			f.getPresentation().setVisibility(true);
		    		}
		    	}
	    	} else if (globalTime % (timing[0] * 2) == timing[0]) {
	    		// playfieldEl.className = "blk";
		    	for (Food f : playfieldEl.getFoods()) {
		    		if (f.getPresentation().hasBackground()) {
		    			f.getPresentation().setVisibility(false);
		    		}
		    	}
	    	}
	    	break;
	    }
	}
	
	void blinkScoreLabels() {
	    if (gameplayMode != GameplayMode.CUTSCENE) {
	    	boolean modify = true;
	    	boolean b = false;
	    	
	    	if (globalTime % (timing[17] * 2) == 0)
	    		b = true;
	    	else if (globalTime % (timing[17] * 2) == timing[17])
	    		b = false;
	    	else
	    		modify = false;
	    	
	    	if (modify)
	    		for (int c = 0; c < playerCount; c++)
	    			scoreLabelEl[c].getPresentation().setVisibility(b);
	    }
	}
	
	void finishFrightMode() {
	    switchMainGhostMode(lastMainGhostMode, false);
	}
	
	void handleGameplayModeTimer() {
	    if (gameplayModeTime != 0) {
	    	gameplayModeTime--;
	    	switch (gameplayMode) {
	    	case PLAYER_DYING:
	    	case PLAYER_DIED:
	    		for (int b = 0; b < playerCount + 4; b++) actors[b].b();
	    		break;
	    	case LEVEL_COMPLETED:
	    		if (FloatMath.floor(gameplayModeTime / (timing[11] / 8)) % 2 == 0)
	    			changeElementBkPos(playfieldEl.getPresentation(), 322, 2, false);
	    		else
	    			changeElementBkPos(playfieldEl.getPresentation(), 322, 138, false);
	    	}
	    	
	    	if (gameplayModeTime <= 0) {
	    		gameplayModeTime = 0;
	    		switch (gameplayMode) {
		        case GHOST_DIED:
		        	changeGameplayMode(GameplayMode.ORDINARY_PLAYING);
		        	ghostEyesCount++;
		        	playAmbientSound();
//		        	actors[ghostBeingEatenId].el.className = "pcm-ac";
		        	actors[ghostBeingEatenId].a(GhostMode.EATEN);
	        		// ブルーモードのモンスターがいない場合、 ブルーモードを終了させる
		        	boolean c = false;
		        	for (int b = playerCount; b < playerCount + 4; b++)
			            if (actors[b].getMode() == GhostMode.FRIGHTENED
			            		|| (actors[b].getMode() == GhostMode.IN_PEN
			            				|| actors[b].getMode() == GhostMode.RE_EXITING_FROM_PEN)
			            				&& !actors[b].isEatenInThisFrightMode()) {
			            	c = true;
			            	break;
			            }
		        	if (!c) finishFrightMode();
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
//		        	document.getElementById("pcm-re");
//		        	google.dom.remove(b);
		        	playfieldEl.setReady(null);
		        	changeGameplayMode(GameplayMode.ORDINARY_PLAYING);
		        	break;
		        case GAMEOVER:
//		        	b = document.getElementById("pcm-go");
//		        	google.dom.remove(b);
		        	playfieldEl.setGameover(null);
//		        	google.pacManQuery && google.pacManQuery(); // google.pacManQueryというfunctionは存在しない
		        	break;
		        case LEVEL_BEING_COMPLETED:
		        	changeGameplayMode(GameplayMode.LEVEL_COMPLETED);
		        	break;
		        case LEVEL_COMPLETED:
		        	changeGameplayMode(GameplayMode.TRANSITION_INTO_NEXT_SCENE);
		        	break;
		        case TRANSITION_INTO_NEXT_SCENE:
		        	if (levels.cutsceneId != 0) {
		        		cutsceneId = levels.cutsceneId;
		        		changeGameplayMode(GameplayMode.CUTSCENE);
		        	} else {
//		        		canvasEl.style.visibility = "";
		        		canvasEl.getPresentation().setVisibility(true);
		        		newLevel(false);
		        	}
		        	break;
		        case TWO_PLAYER_PLAY_STARTED:
//		        	playfieldEl.style.visibility = "";
		        	playfieldEl.getPresentation().setVisibility(true);
//		        	canvasEl.style.visibility = "";
		        	canvasEl.getPresentation().setVisibility(true);
		        	switchToDoubleMode();
		        	break;
		        }
	    	}
	    }
	}

	void handleFruitTimer(){
	    if (fruitTime != 0) {
	      fruitTime--;
	      if (fruitTime <= 0) hideFruit();
	    }
	}
	
	void handleGhostModeTimer() {
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
		    	if (ghostModeSwitchPos < levels.ghostModeSwitchTimes.length) {
	    			ghostModeTime = levels.ghostModeSwitchTimes[ghostModeSwitchPos] * D;
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
	
	void handleForcePenLeaveTimer() {
	    if (forcePenLeaveTime != 0) {
	    	forcePenLeaveTime--;
	    	if (forcePenLeaveTime <= 0) {
	    		for (int b = 1; b <= 3; b++)
	    			if (actors[playerCount + b].getMode() == GhostMode.IN_PEN) {
	    				actors[playerCount + b].setFreeToLeavePen(true);
	    				break;
	    			}
	
	    		resetForcePenLeaveTime();
	    	}
	    }
	}
	
	void handleTimers() {
	    if (gameplayMode == GameplayMode.ORDINARY_PLAYING) {
	    	handleForcePenLeaveTimer();
	    	handleFruitTimer();
	    	handleGhostModeTimer();
	    }
	    handleGameplayModeTimer();
	}
	
	void tick() {
	    long b = new Date().getTime();
	    lastTimeDelta += b - lastTime - tickInterval; // 処理遅延時間累計
	    if (lastTimeDelta > 100) lastTimeDelta = 100;
	    if (canDecreaseFps && lastTimeDelta > 50) { // fpsを下げることができるなら、処理遅延時間50ms超の回数をカウント
	    	lastTimeSlownessCount++;
	    	if (lastTimeSlownessCount == 20) decreaseFps(); // 処理遅延時間50ms超 20回でfpsを下げる
	    }
	    int c = 0;
	    if (lastTimeDelta > tickInterval) { // 処理遅延時間累計がtickインターバルより大きい場合、tickインターバル未満に値を切り詰める
	    	c = (int) FloatMath.floor(lastTimeDelta / tickInterval);
	    	lastTimeDelta -= tickInterval * c;
	    }
	    lastTime = b;
	    if (gameplayMode == GameplayMode.CUTSCENE) { // Cutscene
		    for (int i = 0; i < tickMultiplier + c; i++) { // tickMultiplierと処理地縁に応じて複数回のロジックを実行
		        advanceCutscene();
		        intervalTime = (intervalTime + 1) % D;
		        globalTime++;
		    }
		    checkCutscene();
		    blinkScoreLabels();
	    } else
	    	for (int i = 0; i < tickMultiplier + c; i++) { // tickMultiplierと処理地縁に応じて複数回のロジックを実行
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
	    
	    setTimeout();
	}

	void extraLife(int b) {
	    playSound("extra_life", 0);
	    extraLifeAwarded[b] = true;
	    lives++;
	    if (lives > 5) lives = 5;
	    updateChromeLives();
	}
	
	void addToScore(int b, int c){
	    score[c] += b;
	    if (!extraLifeAwarded[c] && score[c] > 10000) extraLife(c);
	    updateChromeScore(c);
	}
	
	void updateChrome() {
	    updateChromeLevel();
	    updateChromeLives();
	    for (int b = 0; b < playerCount; b++) updateChromeScore(b);
	}
	
	void updateChromeScore(int b) {
	    String c = String.valueOf(score[b]);
	    if (c.length() > scoreDigits) c = c.substring(c.length() - scoreDigits);
	    for (int d = 0; d < scoreDigits; d++) {
	    	Score.Number f = scoreEl[b].getNumber(d);
	    	String h = null;
	    	if (d < c.length()) h = c.substring(d, d + 1);
	        if (h != null)
	        	changeElementBkPos(f.getPresentation(), 8 + 8 * Integer.parseInt(h, 10), 144, true);
	        else
	        	changeElementBkPos(f.getPresentation(), 48, 0, true);
	    }
	}
	
	void updateChromeLives() {
		livesEl.clearLives();
	    for (int b = 0; b < lives; b++) {
	    	Lives.Life life = new Lives.Life();
	    	prepareElement(life.getPresentation(), 64, 129);
//		    	c.className = "pcm-lif";
	    	life.getPresentation().setWidth(16);
	    	life.getPresentation().setHeight(12);
	    	life.getPresentation().setTop(b * 15); // margin-bottom: 3px
	    	life.getPresentation().setParent(livesEl.getPresentation());
	    	livesEl.addLife(life);
	    }
	}
	
	void updateChromeLevel() {
		levelEl.clearFruits();
	    int top = (4 - Math.min(level, 4)) * 16 - 16;
	    for (int b = level; b >= Math.max(level - 4 + 1, 1); b--) {
	    	int c = b >= z.length ? z[z.length - 1].fruit : z[b].fruit;
	    	Fruit d = new Fruit();
	    	int[] fs = getFruitSprite(c);
	    	prepareElement(d.getPresentation(), fs[0], fs[1]);
	    	d.getPresentation().setWidth(32);
	    	d.getPresentation().setHeight(16);
	    	top += 16;
	    	d.getPresentation().setTop(top);
	    	d.getPresentation().setParent(levelEl.getPresentation());
	    	levelEl.addFruit(d);
	    }
	}

	// スコアとサウンドアイコンを生成
	void createChrome() {
		canvasEl.reset();
	    scoreDigits = playerCount == 1 ? 10 : 5;
	    scoreLabelEl = new ScoreLabel[2];
	    scoreLabelEl[0] = new ScoreLabel();
	    scoreLabelEl[0].getPresentation().setId("pcm-sc-1-l");
	    scoreLabelEl[0].getPresentation().setLeft(-2);
	    scoreLabelEl[0].getPresentation().setTop(0);
	    scoreLabelEl[0].getPresentation().setWidth(48);
	    scoreLabelEl[0].getPresentation().setHeight(8);
	    prepareElement(scoreLabelEl[0].getPresentation(), 160, 56);
	    scoreLabelEl[0].getPresentation().setParent(canvasEl.getPresentation());
	    canvasEl.getScoreLabels()[0] = scoreLabelEl[0];
	    scoreEl = new Score[2];
	    scoreEl[0] = new Score();
	    scoreEl[0].getPresentation().setId("pcm-sc-1");
	    scoreEl[0].getPresentation().setLeft(18);
	    scoreEl[0].getPresentation().setTop(16);
	    scoreEl[0].getPresentation().setWidth(8);
	    scoreEl[0].getPresentation().setHeight(56);
	    scoreEl[0].getPresentation().setParent(canvasEl.getPresentation());
	    
	    for (int b = 0; b < scoreDigits; b++) {
	    	Score.Number c = new Score.Number();
	    	c.getPresentation().setId("pcm-sc-1-" + b);
	    	c.getPresentation().setTop(b * 8);
	    	c.getPresentation().setLeft(0);
	    	c.getPresentation().setWidth(8);
	    	c.getPresentation().setHeight(8);
	    	prepareElement(c.getPresentation(), 48, 0);
	    	c.getPresentation().setParent(scoreEl[0].getPresentation());
	    	scoreEl[0].addNumber(c);
	    }
	    canvasEl.getScores()[0] = scoreEl[0];
	    livesEl = new Lives();
	    livesEl.getPresentation().setId("pcm-li");
	    livesEl.getPresentation().setLeft(523);
	    livesEl.getPresentation().setTop(0);
	    livesEl.getPresentation().setHeight(80);
	    livesEl.getPresentation().setWidth(16);
	    livesEl.getPresentation().setParent(canvasEl.getPresentation());
	    canvasEl.setLives(livesEl);
	    levelEl = new Level();
	    levelEl.getPresentation().setId("pcm-le");
	    levelEl.getPresentation().setLeft(515);
	    levelEl.getPresentation().setTop(74);
	    levelEl.getPresentation().setHeight(64);
	    levelEl.getPresentation().setWidth(32);
	    levelEl.getPresentation().setParent(canvasEl.getPresentation());
	    canvasEl.setLevel(levelEl);
	    if (playerCount == 2) {
	    	scoreLabelEl[1] = new ScoreLabel();
	    	scoreLabelEl[1].getPresentation().setId("pcm-sc-2-l");
		    scoreLabelEl[1].getPresentation().setLeft(-2);
		    scoreLabelEl[1].getPresentation().setTop(64);
		    scoreLabelEl[1].getPresentation().setWidth(48);
		    scoreLabelEl[1].getPresentation().setHeight(8);
	    	prepareElement(scoreLabelEl[1].getPresentation(), 160, 64);
		    scoreLabelEl[1].getPresentation().setParent(canvasEl.getPresentation());
	    	canvasEl.getScoreLabels()[1] = scoreLabelEl[1];
	    	scoreEl[1] = new Score();
	    	scoreEl[1].getPresentation().setId("pcm-sc-2");
	    	scoreEl[1].getPresentation().setLeft(18);
	    	scoreEl[1].getPresentation().setTop(80);
	    	scoreEl[1].getPresentation().setWidth(8);
	    	scoreEl[1].getPresentation().setHeight(56);
		    scoreEl[1].getPresentation().setParent(canvasEl.getPresentation());		    	
	    	for (int b = 0; b < scoreDigits; b++) {
	    		Score.Number c = new Score.Number();
	    		c.getPresentation().setId("pcm-sc-2-" + b);
	    		c.getPresentation().setTop(b * 8);
	    		c.getPresentation().setLeft(0);
	    		c.getPresentation().setWidth(8);
	    		c.getPresentation().setHeight(8);
	    		prepareElement(c.getPresentation(), 48, 0);
		    	c.getPresentation().setParent(scoreEl[1].getPresentation());
	    		scoreEl[1].addNumber(c);
	    	}
	    	canvasEl.getScores()[1] = scoreEl[1];
	    
	    }
	    if (soundAvailable) {
	    	soundEl = new Sound();
	    	soundEl.getPresentation().setId("pcm-so");
	    	soundEl.getPresentation().setLeft(15); // 7 + 8
	    	soundEl.getPresentation().setTop(124); // 116 + 8
	    	soundEl.getPresentation().setWidth(12);
	    	soundEl.getPresentation().setHeight(12);
	    	prepareElement(soundEl.getPresentation(), -32, -16);
	    	soundEl.getPresentation().setParent(canvasEl.getPresentation());
		    canvasEl.setSound(soundEl);
//			    soundEl.onclick = toggleSound;
		    updateSoundIcon();
	    }
	}
	
	void clearDotEatingNow() {
	    dotEatingNow = new boolean[] {false, false};
	    dotEatingNext = new boolean[] {false, false};
	}
	
	// サウンド再生
	// b -> トラック, c -> チャンネル番号
	void playSound(String b, int c) {
		playSound(b, c, false);
	}

	// サウンド再生
	// b -> トラック, c -> チャンネル番号, d -> 再生中サウンド停止フラグ
	void playSound(String b, int c, boolean d) {
	    if (!(!soundAvailable || !pacManSound || paused)) {
	    	if (!d) stopSoundChannel(c);
	    	try {
	    		soundPlayer.playTrack(b, c);
	    	} catch (Exception f) {
	    		soundAvailable = false;
//		    		Log.e(TAG, "playSound", f);
	    	}
	    }
	}
	
	void stopSoundChannel(int b) {
	    if (soundAvailable)
	    	try {
	    		 soundPlayer.stopChannel(b);
	    	} catch (Exception c) {
	    		soundAvailable = false;
//		    		Log.e(TAG, "stopSoundChannel", c);
	    	}
	}
	
	void stopAllAudio() {
		if (soundAvailable) {
			try {
				soundPlayer.stopAmbientTrack();
			} catch (Exception b) {
				soundAvailable = false;
//		    		Log.e(TAG, "stopAllAudio", b);
			}
			for (int c = 0; c < 5; c++) stopSoundChannel(c);
		}
	}
	
	void playDotEatingSound(int b) {
	    if (soundAvailable && pacManSound)
	    	if (gameplayMode == GameplayMode.ORDINARY_PLAYING)
	    		if (dotEatingNow[b]) // 常にfalse
	    			dotEatingNext[b] = true; // デッドコード
	    		else {
	    			if (b == 0) {
	    				String c = dotEatingSoundPart[b] == 1 ? "eating_dot_1" : "eating_dot_2";
	    				playSound(c, 1 + dotEatingChannel[b], true);
	//    				dotTimer = window.setInterval(g.repeatDotEatingSoundPacMan, 150) // 無意味な処理
	    			} else {
	    				playSound("eating_dot_double", 3 + dotEatingChannel[b], true);
	//    				dotTimerMs = window.setInterval(g.repeatDotEatingSoundMsPacMan, 150) // 無意味な処理
	    			}
	    			dotEatingChannel[b] = (dotEatingChannel[b] + 1) % 2; // 0 と 1 をスイッチ
	    			dotEatingSoundPart[b] = 3 - dotEatingSoundPart[b]; // 1 と 2 をスイッチ
	    		}
	}

	void repeatDotEatingSound(int b) {
	    dotEatingNow[b] = false;
	    if (dotEatingNext[b]) { // 常にfalseのためこのブロックはデッドコード
	    	dotEatingNext[b] = false;
	    	playDotEatingSound(b);
	    }
	}
	
	void repeatDotEatingSoundPacMan() {
	    repeatDotEatingSound(0);
	}
	
	void repeatDotEatingSoundMsPacMan() {
		repeatDotEatingSound(1);
	}
	
	public void playAmbientSound() {
	    if (soundAvailable && pacManSound) {
	    	String b = null;
	    	if (gameplayMode == GameplayMode.ORDINARY_PLAYING || gameplayMode == GameplayMode.GHOST_DIED)
	    		b = ghostEyesCount != 0
	              	? "ambient_eyes"
	              	: mainGhostMode == GhostMode.FRIGHTENED
	              		? "ambient_fright"
	              		: dotsEaten > 241
	              			? "ambient_4"
	              			: dotsEaten > 207
	              				? "ambient_3"
	              				: dotsEaten > 138
	              					? "ambient_2" : "ambient_1";
//		        else if (gameplayMode == 13) b = "cutscene";
	    	
	    	if (b != null) {
	    		if (b.equals(soundPlayer.oldAmbient)) {
	    			return;
	    		}
	    		try {
	    			soundPlayer.playAmbientTrack(b);
	    			soundPlayer.oldAmbient = b;
	    		} catch (Exception c) {
	    			soundAvailable = false;
//			    		Log.e(TAG, "playAmbientSound", c);
	    		}
	    	}
	    }
	}
	
	void playCutsceneTrack() {
		cutsceneAudioClip.loop();
	}
	
	void stopCutsceneTrack() {
		cutsceneAudioClip.stop();
	}
	
	void initializeTickTimer() {
	    fps = C[fpsChoice];
	    tickInterval = 1000 / fps;
	    tickMultiplier = D / fps;
	    timing = new float[w.length];
	    for (int b = 0; b < w.length; b++) {
	    	float c = !pacManSound && (b == 7 || b == 8) ? 1 : w[b]; // timing[7] -> Gameplay Mode 4, timing[8] -> Gameplay Mode 5. ともにゲーム開始直後がらみ.
	    	timing[b] = Math.round(c * D); // D = 90より、timingの要素はindex 7, 8以外は90.
	    }
	    lastTime = new Date().getTime();
	    lastTimeDelta = 0;
	    lastTimeSlownessCount = 0;
	}
	
	void setTimeout() {
	    view.redrawHandler.sleep(Math.round(tickInterval)); // TODO: 要見直し			
	}
	
	void decreaseFps() {
	    if (fpsChoice < C.length - 1) {
	    	fpsChoice++;
	    	initializeTickTimer();
	    	if (fpsChoice == C.length - 1) canDecreaseFps = false;
	    }
	}
    
	void createCanvasElement() {
		canvasEl = new PacManCanvas();
	    canvasEl.getPresentation().setId("pcm-c");
	    canvasEl.getPresentation().setWidth(554);
	    canvasEl.getPresentation().setHeight(136);
	    canvasEl.getPresentation().setBgColor(0x000000);
//		    canvasEl.hideFocus = a;
//		    document.getElementById("logo").appendChild(canvasEl);
//		    canvasEl.tabIndex = 0;
//		    canvasEl.focus();
	}
	
	void everythingIsReady() {
	    if (!ready) {
	    	ready = true;
	    	createCanvasElement();
	    	speedIntervals = new HashMap<Float, Boolean[]>();
	    	fpsChoice = 0;
	    	canDecreaseFps = true;
	    	initializeTickTimer();
	    	view.invalidate();
	    }
	}
	
	// TODO: 要メソッド名見直し
	void start() {
		if (ready) {
			setTimeout();
			newGame();
		}
	}
	
	void checkIfEverythingIsReady() {
	    if (soundReady && graphicsReady) {
	    	everythingIsReady();
	    }
	}
	
	void preloadImage() {
// 		    var c = new Image,
//    			d = google.browser.engine.IE;
//    		if (!d) c.onload = g.imageLoaded;
//    		c.src = b;
//    		d && g.imageLoaded()
		imageLoaded();
	}
	
	void imageLoaded() {
	    graphicsReady = true;
	    checkIfEverythingIsReady();
	}
	
	void prepareGraphics() {
		graphicsReady = false;
		preloadImage();
	}
	
	void prepareSound() {
	    soundAvailable = false;
	    soundReady = false;
	    
	    soundPlayer = new SoundPlayer(view.getContext(), this);
	    soundPlayer.init();
	    
	    cutsceneAudioClip = new AudioClip(view.getContext(), R.raw.cutscene);
	    cutsceneAudioClip.init();

		soundReady = true;
		checkIfEverythingIsReady();
	}

	void setKillScreenLevel(int level) {
		this.killScreenLevel = level;
	}

	void setDefaultKillScreenLevel() {
		this.killScreenLevel = DEFAULT_KILL_SCREEN_LEVEL;
	}

	void init() {
	    ready = false;
	    prepareGraphics();
	    prepareSound();
	}
	
	void destroy() {
		soundPlayer.destroy();
		cutsceneAudioClip.destroy();
	}

//	public Map<Dir, Dir> getOppositeDirections() {
//		return oppositeDirections;
//	}

	public int getPlayerCount() {
		return playerCount;
	}

	public Map<Integer, Map<Integer, PathElement>> getPlayfield() {
		return playfield;
	}

	public PlayField getPlayfieldEl() {
		return playfieldEl;
	}

	public PacManCanvas getCanvasEl() {
		return canvasEl;
	}

	public boolean isPacManSound() {
		return pacManSound;
	}

	public void setPacManSound(boolean pacManSound) {
		this.pacManSound = pacManSound;
	}

	public LevelConfig getLevels() {
		return levels;
	}

	public E[] getActors() {
		return actors;
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

	public int getGhostBeingEatenId() {
		return ghostBeingEatenId;
	}

	public int getPlayerEatingGhostId() {
		return playerEatingGhostId;
	}
	
	public int getPlayerDyingId() {
		return playerDyingId;
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

	public boolean isUserDisabledSound() {
		return userDisabledSound;
	}

	public boolean isTilesChanged() {
		return tilesChanged;
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

	public int getDotsRemaining() {
		return dotsRemaining;
	}

	public float getCruiseElroySpeed() {
		return cruiseElroySpeed;
	}
	
}
