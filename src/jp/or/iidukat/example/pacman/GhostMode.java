package jp.or.iidukat.example.pacman;

public enum GhostMode {
	NONE(0), CHASE(1), SCATTER(2), FRIGHTENED(4), 
	EATEN(8), IN_PEN(16), EXITING_FROM_PEN(32),
	ENTERING_PEN(64), RE_EXITING_FROM_PEN(128);
	
	private final int mode;
	
	private GhostMode(int mode) {
		this.mode = mode;
	}

	public int getMode() {
		return mode;
	}
	
}
