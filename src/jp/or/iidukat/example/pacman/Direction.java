package jp.or.iidukat.example.pacman;

public class Direction {
	private final int axis;
	private final int increment;
	Direction(int axis, int increment) {
		this.axis = axis;
		this.increment = increment;
	}
	
	public int getAxis() {
		return axis;
	}
	
	public int getIncrement() {
		return increment;
	}
}
