package jp.or.iidukat.example.pacman;

public enum Move {
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
