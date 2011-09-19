package jp.or.iidukat.example.pacman;

import java.util.Arrays;
import java.util.List;

public enum Direction {
	NONE(0) {
		@Override
		public Move getMove() {
			return Move.NONE;
		}
		@Override
		public Direction getOpposite() {
			return NONE;
		}
	},
	UP(1) {
		@Override
		public Move getMove() {
			return Move.UP;
		}
		@Override
		public Direction getOpposite() {
			return DOWN;
		}
	},
	DOWN(2) {
		@Override
		public Move getMove() {
			return Move.DOWN;
		}
		@Override
		public Direction getOpposite() {
			return UP;
		}
	},
	LEFT(4) {
		@Override
		public Move getMove() {
			return Move.LEFT;
		}
		@Override
		public Direction getOpposite() {
			return RIGHT;
		}
	},
	RIGHT(8) {
		@Override
		public Move getMove() {
			return Move.RIGHT;
		}
		@Override
		public Direction getOpposite() {
			return LEFT;
		}
	};
	
    private static final List<Direction> MOVES;
    static {
    	MOVES = Arrays.asList(UP, DOWN, LEFT, RIGHT);
    }

	private final int dir;
	
	private Direction(int dir) {
		this.dir = dir;
	}

	public int getDir() {
		return dir;
	}
	
	public abstract Direction getOpposite();
	
	public abstract Move getMove();
	
	public static List<Direction> getAllMoves() {
		return MOVES;
	}
}
