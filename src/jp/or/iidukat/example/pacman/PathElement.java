package jp.or.iidukat.example.pacman;

import java.util.EnumSet;

public class PathElement {
    public enum Dot {
        NONE(0), FOOD(1), ENERGIZER(2);
        
        private int dot;
        
        Dot(int dot) {
            this.dot = dot;
        }
        
        public int getDot() {
            return dot;
        }
    }
    
    private boolean path;
    private Dot dot;
    private boolean intersection;
    private boolean tunnel;
    private EnumSet<Direction> allowedDir;
    
    public EnumSet<Direction> getAllowedDir() {
        return allowedDir;
    }
    
    public void setAllowedDir(EnumSet<Direction> allowedDir) {
        this.allowedDir = allowedDir;
    }
    
    public Dot getDot() {
        return dot;
    }
    
    public void setDot(Dot dot) {
        this.dot = dot;
    }

    public boolean isIntersection() {
        return intersection;
    }
    
    public void setIntersection(boolean intersection) {
        this.intersection = intersection;
    }

    public boolean isPath() {
        return path;
    }
    
    public void setPath(boolean path) {
        this.path = path;
    }

    public boolean isTunnel() {
        return tunnel;
    }
    
    public void setTunnel(boolean tunnel) {
        this.tunnel = tunnel;
    }
}
