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
    
    public static enum Move {
        NONE(0, 0), UP(0, -1), DOWN(0, 1), LEFT(1, -1), RIGHT(1, 1);
        
        private final int axis;
        private final int increment;
        
        private Move(int axis, int increment) {
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
}
