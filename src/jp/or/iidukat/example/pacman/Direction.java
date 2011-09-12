package jp.or.iidukat.example.pacman;

public class Direction {
	final int axis;
	final int increment;
	Direction(int axis, int increment) {
		this.axis = axis;
		this.increment = increment;
	}
}
