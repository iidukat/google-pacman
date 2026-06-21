package jp.or.iidukat.example.pacman;

class Timing {

    static final double FRIGHT_BLINK_SECS = 0.23;

    final double energizerBlink;
    final double frightBlink;
    final double ghostDied;
    final double playerDying;
    final double playerDied;
    final double gameRestarting;
    final double gameRestarted;
    final double newgameStarting;
    final double newgameStarted;
    final double gameover;
    final double levelBeingCompleted;
    final double levelCompleted;
    final double transition;
    final double fruitScore;
    final double fruitShowMin;
    final double fruitShowMax;
    final double scoreLabelBlink;

    Timing(boolean noSound) {
        int fps = GameConstants.DEFAULT_FPS;
        energizerBlink      = Math.round(0.16  * fps);
        frightBlink         = Math.round(FRIGHT_BLINK_SECS * fps);
        ghostDied           = Math.round(1.0   * fps);
        playerDying         = Math.round(1.0   * fps);
        playerDied          = Math.round(2.23  * fps);
        gameRestarting      = Math.round(0.3   * fps);
        gameRestarted       = Math.round(1.9   * fps);
        newgameStarting     = Math.round((noSound ? 1.0 : 2.23) * fps);
        newgameStarted      = Math.round((noSound ? 1.0 : 1.9)  * fps);
        gameover            = Math.round(5.0   * fps);
        levelBeingCompleted = Math.round(1.9   * fps);
        levelCompleted      = Math.round(1.18  * fps);
        transition          = Math.round(0.3   * fps);
        fruitScore          = Math.round(1.9   * fps);
        fruitShowMin        = Math.round(9.0   * fps);
        fruitShowMax        = Math.round(10.0  * fps);
        scoreLabelBlink     = Math.round(0.26  * fps);
    }
}
