package jp.or.iidukat.example.pacman;

public enum CurrentSpeed {
	NONE(-1), NORMAL(0), PACMAN_EATING_DOT(1), PASSING_TUNNEL(2);
	
	private final int mode;
	
	private CurrentSpeed(int mode) {
		this.mode = mode;
	}

	public int getMode() {
		return mode;
	}

}
