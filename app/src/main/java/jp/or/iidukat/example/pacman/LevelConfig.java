package jp.or.iidukat.example.pacman;

public class LevelConfig {

    static final int DEFAULT_FPS = 90;

    private final double ghostSpeed;
    private final double ghostTunnelSpeed;
    private final double playerSpeed;
    private final double dotEatingSpeed;
    private final double ghostFrightSpeed;
    private final double playerFrightSpeed;
    private final double dotEatingFrightSpeed;
    private final int elroyDotsLeftPart1;
    private final double elroySpeedPart1;
    private final int elroyDotsLeftPart2;
    private final double elroySpeedPart2;
    private final int frightTime;
    private int frightTotalTime;
    private final int frightBlinkCount;
    private final int fruit;
    private final int fruitScore;
    private final double[] ghostModeSwitchTimes;
    private final int penForceTime;
    private final double[] penLeavingLimits;
    private final int cutsceneId;

    static class Builder {
        private double ghostSpeed;
        private double ghostTunnelSpeed;
        private double playerSpeed;
        private double dotEatingSpeed;
        private double ghostFrightSpeed;
        private double playerFrightSpeed;
        private double dotEatingFrightSpeed;
        private int elroyDotsLeftPart1;
        private double elroySpeedPart1;
        private int elroyDotsLeftPart2;
        private double elroySpeedPart2;
        private int frightTime;
        private int frightBlinkCount;
        private int fruit;
        private int fruitScore;
        private double[] ghostModeSwitchTimes;
        private int penForceTime;
        private double[] penLeavingLimits;
        private int cutsceneId;

        Builder ghostSpeed(double val) { this.ghostSpeed = val; return this; }
        Builder ghostTunnelSpeed(double val) { this.ghostTunnelSpeed = val; return this; }
        Builder playerSpeed(double val) { this.playerSpeed = val; return this; }
        Builder dotEatingSpeed(double val) { this.dotEatingSpeed = val; return this; }
        Builder ghostFrightSpeed(double val) { this.ghostFrightSpeed = val; return this; }
        Builder playerFrightSpeed(double val) { this.playerFrightSpeed = val; return this; }
        Builder dotEatingFrightSpeed(double val) { this.dotEatingFrightSpeed = val; return this; }
        Builder elroyDotsLeftPart1(int val) { this.elroyDotsLeftPart1 = val; return this; }
        Builder elroySpeedPart1(double val) { this.elroySpeedPart1 = val; return this; }
        Builder elroyDotsLeftPart2(int val) { this.elroyDotsLeftPart2 = val; return this; }
        Builder elroySpeedPart2(double val) { this.elroySpeedPart2 = val; return this; }
        Builder frightTime(int val) { this.frightTime = val; return this; }
        Builder frightBlinkCount(int val) { this.frightBlinkCount = val; return this; }
        Builder fruit(int val) { this.fruit = val; return this; }
        Builder fruitScore(int val) { this.fruitScore = val; return this; }
        Builder ghostModeSwitchTimes(double[] val) { this.ghostModeSwitchTimes = val; return this; }
        Builder penForceTime(int val) { this.penForceTime = val; return this; }
        Builder penLeavingLimits(double[] val) { this.penLeavingLimits = val; return this; }
        Builder cutsceneId(int val) { this.cutsceneId = val; return this; }

        LevelConfig build() { return new LevelConfig(this); }
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

    public double getGhostSpeed() { return ghostSpeed; }
    public double getGhostTunnelSpeed() { return ghostTunnelSpeed; }
    public double getPlayerSpeed() { return playerSpeed; }
    public double getDotEatingSpeed() { return dotEatingSpeed; }
    public double getGhostFrightSpeed() { return ghostFrightSpeed; }
    public double getPlayerFrightSpeed() { return playerFrightSpeed; }
    public double getDotEatingFrightSpeed() { return dotEatingFrightSpeed; }
    public int getElroyDotsLeftPart1() { return elroyDotsLeftPart1; }
    public double getElroySpeedPart1() { return elroySpeedPart1; }
    public int getElroyDotsLeftPart2() { return elroyDotsLeftPart2; }
    public double getElroySpeedPart2() { return elroySpeedPart2; }
    public int getFrightTime() { return frightTime; }
    public int getFrightTotalTime() { return frightTotalTime; }

    void initFrightTotalTime(int blinkDuration) {
        this.frightTotalTime = this.frightTime + blinkDuration * (this.frightBlinkCount * 2 - 1);
    }
    public int getFrightBlinkCount() { return frightBlinkCount; }
    public int getFruit() { return fruit; }
    public int getFruitScore() { return fruitScore; }
    public double[] getGhostModeSwitchTimes() { return ghostModeSwitchTimes; }
    public int getPenForceTime() { return penForceTime; }
    public double[] getPenLeavingLimits() { return penLeavingLimits; }
    public int getCutsceneId() { return cutsceneId; }

    static final LevelConfig[] LEVEL_CONFIGS = {
        new LevelConfig.Builder().build(),
        new LevelConfig.Builder()
                .ghostSpeed(0.75f).ghostTunnelSpeed(0.4f)
                .playerSpeed(0.8f).dotEatingSpeed(0.71f)
                .ghostFrightSpeed(0.5f).playerFrightSpeed(0.9f).dotEatingFrightSpeed(0.79f)
                .elroyDotsLeftPart1(20).elroySpeedPart1(0.8f)
                .elroyDotsLeftPart2(10).elroySpeedPart2(0.85f)
                .frightTime(6).frightBlinkCount(5)
                .fruit(1).fruitScore(100)
                .ghostModeSwitchTimes(new double[] { 7, 20, 7, 20, 5, 20, 5, 1, })
                .penForceTime(4).penLeavingLimits(new double[] { 0, 0, 30, 60, })
                .build(),
        new LevelConfig.Builder()
                .ghostSpeed(0.85f).ghostTunnelSpeed(0.45f)
                .playerSpeed(0.9f).dotEatingSpeed(0.79f)
                .ghostFrightSpeed(0.55f).playerFrightSpeed(0.95f).dotEatingFrightSpeed(0.83f)
                .elroyDotsLeftPart1(30).elroySpeedPart1(0.9f)
                .elroyDotsLeftPart2(15).elroySpeedPart2(0.95f)
                .frightTime(5).frightBlinkCount(5)
                .fruit(2).fruitScore(300)
                .ghostModeSwitchTimes(new double[] { 7, 20, 7, 20, 5, 1033, 1f / 60, 1, })
                .penForceTime(4).penLeavingLimits(new double[] { 0, 0, 0, 50, })
                .cutsceneId(1)
                .build(),
        new LevelConfig.Builder()
                .ghostSpeed(0.85f).ghostTunnelSpeed(0.45f)
                .playerSpeed(0.9f).dotEatingSpeed(0.79f)
                .ghostFrightSpeed(0.55f).playerFrightSpeed(0.95f).dotEatingFrightSpeed(0.83f)
                .elroyDotsLeftPart1(40).elroySpeedPart1(0.9f)
                .elroyDotsLeftPart2(20).elroySpeedPart2(0.95f)
                .frightTime(4).frightBlinkCount(5)
                .fruit(3).fruitScore(500)
                .ghostModeSwitchTimes(new double[] { 7, 20, 7, 20, 5, 1033, 1f / 60, 1, })
                .penForceTime(4).penLeavingLimits(new double[] { 0, 0, 0, 0, })
                .build(),
        new LevelConfig.Builder()
                .ghostSpeed(0.85f).ghostTunnelSpeed(0.45f)
                .playerSpeed(0.9f).dotEatingSpeed(0.79f)
                .ghostFrightSpeed(0.55f).playerFrightSpeed(0.95f).dotEatingFrightSpeed(0.83f)
                .elroyDotsLeftPart1(40).elroySpeedPart1(0.9f)
                .elroyDotsLeftPart2(20).elroySpeedPart2(0.95f)
                .frightTime(3).frightBlinkCount(5)
                .fruit(3).fruitScore(500)
                .ghostModeSwitchTimes(new double[] { 7, 20, 7, 20, 5, 1033, 1f / 60, 1, })
                .penForceTime(4).penLeavingLimits(new double[] { 0, 0, 0, 0, })
                .build(),
        new LevelConfig.Builder()
                .ghostSpeed(0.95f).ghostTunnelSpeed(0.5f)
                .playerSpeed(1).dotEatingSpeed(0.87f)
                .ghostFrightSpeed(0.6f).playerFrightSpeed(1).dotEatingFrightSpeed(0.87f)
                .elroyDotsLeftPart1(40).elroySpeedPart1(1)
                .elroyDotsLeftPart2(20).elroySpeedPart2(1.05f)
                .frightTime(2).frightBlinkCount(5)
                .fruit(4).fruitScore(700)
                .ghostModeSwitchTimes(new double[] { 5, 20, 5, 20, 5, 1037, 1f / 60, 1, })
                .penForceTime(3).penLeavingLimits(new double[] { 0, 0, 0, 0, })
                .cutsceneId(2)
                .build(),
        new LevelConfig.Builder()
                .ghostSpeed(0.95f).ghostTunnelSpeed(0.5f)
                .playerSpeed(1).dotEatingSpeed(0.87f)
                .ghostFrightSpeed(0.6f).playerFrightSpeed(1).dotEatingFrightSpeed(0.87f)
                .elroyDotsLeftPart1(50).elroySpeedPart1(1)
                .elroyDotsLeftPart2(25).elroySpeedPart2(1.05f)
                .frightTime(5).frightBlinkCount(5)
                .fruit(4).fruitScore(700)
                .ghostModeSwitchTimes(new double[] { 5, 20, 5, 20, 5, 1037, 1f / 60, 1, })
                .penForceTime(3).penLeavingLimits(new double[] { 0, 0, 0, 0, })
                .build(),
        new LevelConfig.Builder()
                .ghostSpeed(0.95f).ghostTunnelSpeed(0.5f)
                .playerSpeed(1).dotEatingSpeed(0.87f)
                .ghostFrightSpeed(0.6f).playerFrightSpeed(1).dotEatingFrightSpeed(0.87f)
                .elroyDotsLeftPart1(50).elroySpeedPart1(1)
                .elroyDotsLeftPart2(25).elroySpeedPart2(1.05f)
                .frightTime(2).frightBlinkCount(5)
                .fruit(5).fruitScore(1000)
                .ghostModeSwitchTimes(new double[] { 5, 20, 5, 20, 5, 1037, 1f / 60, 1, })
                .penForceTime(3).penLeavingLimits(new double[] { 0, 0, 0, 0, })
                .build(),
        new LevelConfig.Builder()
                .ghostSpeed(0.95f).ghostTunnelSpeed(0.5f)
                .playerSpeed(1).dotEatingSpeed(0.87f)
                .ghostFrightSpeed(0.6f).playerFrightSpeed(1).dotEatingFrightSpeed(0.87f)
                .elroyDotsLeftPart1(50).elroySpeedPart1(1)
                .elroyDotsLeftPart2(25).elroySpeedPart2(1.05f)
                .frightTime(2).frightBlinkCount(5)
                .fruit(5).fruitScore(1000)
                .ghostModeSwitchTimes(new double[] { 5, 20, 5, 20, 5, 1037, 1f / 60, 1, })
                .penForceTime(3).penLeavingLimits(new double[] { 0, 0, 0, 0, })
                .build(),
        new LevelConfig.Builder()
                .ghostSpeed(0.95f).ghostTunnelSpeed(0.5f)
                .playerSpeed(1).dotEatingSpeed(0.87f)
                .ghostFrightSpeed(0.6f).playerFrightSpeed(1).dotEatingFrightSpeed(0.87f)
                .elroyDotsLeftPart1(60).elroySpeedPart1(1)
                .elroyDotsLeftPart2(30).elroySpeedPart2(1.05f)
                .frightTime(1).frightBlinkCount(3)
                .fruit(6).fruitScore(2000)
                .ghostModeSwitchTimes(new double[] { 5, 20, 5, 20, 5, 1037, 1f / 60, 1, })
                .penForceTime(3).penLeavingLimits(new double[] { 0, 0, 0, 0, })
                .cutsceneId(3)
                .build(),
        new LevelConfig.Builder()
                .ghostSpeed(0.95f).ghostTunnelSpeed(0.5f)
                .playerSpeed(1).dotEatingSpeed(0.87f)
                .ghostFrightSpeed(0.6f).playerFrightSpeed(1).dotEatingFrightSpeed(0.87f)
                .elroyDotsLeftPart1(60).elroySpeedPart1(1)
                .elroyDotsLeftPart2(30).elroySpeedPart2(1.05f)
                .frightTime(5).frightBlinkCount(5)
                .fruit(6).fruitScore(2000)
                .ghostModeSwitchTimes(new double[] { 5, 20, 5, 20, 5, 1037, 1f / 60, 1, })
                .penForceTime(3).penLeavingLimits(new double[] { 0, 0, 0, 0, })
                .build(),
        new LevelConfig.Builder()
                .ghostSpeed(0.95f).ghostTunnelSpeed(0.5f)
                .playerSpeed(1).dotEatingSpeed(0.87f)
                .ghostFrightSpeed(0.6f).playerFrightSpeed(1).dotEatingFrightSpeed(0.87f)
                .elroyDotsLeftPart1(60).elroySpeedPart1(1)
                .elroyDotsLeftPart2(30).elroySpeedPart2(1.05f)
                .frightTime(2).frightBlinkCount(5)
                .fruit(7).fruitScore(3000)
                .ghostModeSwitchTimes(new double[] { 5, 20, 5, 20, 5, 1037, 1f / 60, 1, })
                .penForceTime(3).penLeavingLimits(new double[] { 0, 0, 0, 0, })
                .build(),
        new LevelConfig.Builder()
                .ghostSpeed(0.95f).ghostTunnelSpeed(0.5f)
                .playerSpeed(1).dotEatingSpeed(0.87f)
                .ghostFrightSpeed(0.6f).playerFrightSpeed(1).dotEatingFrightSpeed(0.87f)
                .elroyDotsLeftPart1(80).elroySpeedPart1(1)
                .elroyDotsLeftPart2(40).elroySpeedPart2(1.05f)
                .frightTime(1).frightBlinkCount(3)
                .fruit(7).fruitScore(3000)
                .ghostModeSwitchTimes(new double[] { 5, 20, 5, 20, 5, 1037, 1f / 60, 1, })
                .penForceTime(3).penLeavingLimits(new double[] { 0, 0, 0, 0, })
                .build(),
        new LevelConfig.Builder()
                .ghostSpeed(0.95f).ghostTunnelSpeed(0.5f)
                .playerSpeed(1).dotEatingSpeed(0.87f)
                .ghostFrightSpeed(0.6f).playerFrightSpeed(1).dotEatingFrightSpeed(0.87f)
                .elroyDotsLeftPart1(80).elroySpeedPart1(1)
                .elroyDotsLeftPart2(40).elroySpeedPart2(1.05f)
                .frightTime(1).frightBlinkCount(3)
                .fruit(8).fruitScore(5000)
                .ghostModeSwitchTimes(new double[] { 5, 20, 5, 20, 5, 1037, 1f / 60, 1, })
                .penForceTime(3).penLeavingLimits(new double[] { 0, 0, 0, 0, })
                .cutsceneId(3)
                .build(),
        new LevelConfig.Builder()
                .ghostSpeed(0.95f).ghostTunnelSpeed(0.5f)
                .playerSpeed(1).dotEatingSpeed(0.87f)
                .ghostFrightSpeed(0.6f).playerFrightSpeed(1).dotEatingFrightSpeed(0.87f)
                .elroyDotsLeftPart1(80).elroySpeedPart1(1)
                .elroyDotsLeftPart2(40).elroySpeedPart2(1.05f)
                .frightTime(3).frightBlinkCount(5)
                .fruit(8).fruitScore(5000)
                .ghostModeSwitchTimes(new double[] { 5, 20, 5, 20, 5, 1037, 1f / 60, 1, })
                .penForceTime(3).penLeavingLimits(new double[] { 0, 0, 0, 0, })
                .build(),
        new LevelConfig.Builder()
                .ghostSpeed(0.95f).ghostTunnelSpeed(0.5f)
                .playerSpeed(1).dotEatingSpeed(0.87f)
                .ghostFrightSpeed(0.6f).playerFrightSpeed(1).dotEatingFrightSpeed(0.87f)
                .elroyDotsLeftPart1(100).elroySpeedPart1(1)
                .elroyDotsLeftPart2(50).elroySpeedPart2(1.05f)
                .frightTime(1).frightBlinkCount(3)
                .fruit(8).fruitScore(5000)
                .ghostModeSwitchTimes(new double[] { 5, 20, 5, 20, 5, 1037, 1f / 60, 1, })
                .penForceTime(3).penLeavingLimits(new double[] { 0, 0, 0, 0, })
                .build(),
        new LevelConfig.Builder()
                .ghostSpeed(0.95f).ghostTunnelSpeed(0.5f)
                .playerSpeed(1).dotEatingSpeed(0.87f)
                .ghostFrightSpeed(0.6f).playerFrightSpeed(1).dotEatingFrightSpeed(0.87f)
                .elroyDotsLeftPart1(100).elroySpeedPart1(1)
                .elroyDotsLeftPart2(50).elroySpeedPart2(1.05f)
                .frightTime(1).frightBlinkCount(3)
                .fruit(8).fruitScore(5000)
                .ghostModeSwitchTimes(new double[] { 5, 20, 5, 20, 5, 1037, 1f / 60, 1, })
                .penForceTime(3).penLeavingLimits(new double[] { 0, 0, 0, 0, })
                .build(),
        new LevelConfig.Builder()
                .ghostSpeed(0.95f).ghostTunnelSpeed(0.5f)
                .playerSpeed(1).dotEatingSpeed(0.87f)
                .ghostFrightSpeed(0.6f).playerFrightSpeed(1).dotEatingFrightSpeed(0.87f)
                .elroyDotsLeftPart1(100).elroySpeedPart1(1)
                .elroyDotsLeftPart2(50).elroySpeedPart2(1.05f)
                .frightTime(0).frightBlinkCount(0)
                .fruit(8).fruitScore(5000)
                .ghostModeSwitchTimes(new double[] { 5, 20, 5, 20, 5, 1037, 1f / 60, 1, })
                .penForceTime(3).penLeavingLimits(new double[] { 0, 0, 0, 0, })
                .cutsceneId(3)
                .build(),
        new LevelConfig.Builder()
                .ghostSpeed(0.95f).ghostTunnelSpeed(0.5f)
                .playerSpeed(1).dotEatingSpeed(0.87f)
                .ghostFrightSpeed(0.6f).playerFrightSpeed(1).dotEatingFrightSpeed(0.87f)
                .elroyDotsLeftPart1(100).elroySpeedPart1(1)
                .elroyDotsLeftPart2(50).elroySpeedPart2(1.05f)
                .frightTime(1).frightBlinkCount(3)
                .fruit(8).fruitScore(5000)
                .ghostModeSwitchTimes(new double[] { 5, 20, 5, 20, 5, 1037, 1f / 60, 1, })
                .penForceTime(3).penLeavingLimits(new double[] { 0, 0, 0, 0, })
                .build(),
        new LevelConfig.Builder()
                .ghostSpeed(0.95f).ghostTunnelSpeed(0.5f)
                .playerSpeed(1).dotEatingSpeed(0.87f)
                .ghostFrightSpeed(0.6f).playerFrightSpeed(1).dotEatingFrightSpeed(0.87f)
                .elroyDotsLeftPart1(120).elroySpeedPart1(1)
                .elroyDotsLeftPart2(60).elroySpeedPart2(1.05f)
                .frightTime(0).frightBlinkCount(0)
                .fruit(8).fruitScore(5000)
                .ghostModeSwitchTimes(new double[] { 5, 20, 5, 20, 5, 1037, 1f / 60, 1, })
                .penForceTime(3).penLeavingLimits(new double[] { 0, 0, 0, 0, })
                .build(),
        new LevelConfig.Builder()
                .ghostSpeed(0.95f).ghostTunnelSpeed(0.5f)
                .playerSpeed(1).dotEatingSpeed(0.87f)
                .ghostFrightSpeed(0.6f).playerFrightSpeed(1).dotEatingFrightSpeed(0.87f)
                .elroyDotsLeftPart1(120).elroySpeedPart1(1)
                .elroyDotsLeftPart2(60).elroySpeedPart2(1.05f)
                .frightTime(0).frightBlinkCount(0)
                .fruit(8).fruitScore(5000)
                .ghostModeSwitchTimes(new double[] { 5, 20, 5, 20, 5, 1037, 1f / 60, 1, })
                .penForceTime(3).penLeavingLimits(new double[] { 0, 0, 0, 0, })
                .build(),
        new LevelConfig.Builder()
                .ghostSpeed(0.95f).ghostTunnelSpeed(0.5f)
                .playerSpeed(0.9f).dotEatingSpeed(0.79f)
                .ghostFrightSpeed(0.75f).playerFrightSpeed(0.9f).dotEatingFrightSpeed(0.79f)
                .elroyDotsLeftPart1(120).elroySpeedPart1(1)
                .elroyDotsLeftPart2(60).elroySpeedPart2(1.05f)
                .frightTime(0).frightBlinkCount(0)
                .fruit(8).fruitScore(5000)
                .ghostModeSwitchTimes(new double[] { 5, 20, 5, 20, 5, 1037, 1f / 60, 1, })
                .penForceTime(3).penLeavingLimits(new double[] { 0, 0, 0, 0, })
                .build(),
    };
}
