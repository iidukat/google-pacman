package jp.or.iidukat.example.pacman;

public enum GameplayMode {
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
