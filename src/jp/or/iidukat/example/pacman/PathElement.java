package jp.or.iidukat.example.pacman;

import java.util.EnumSet;

public class PathElement {
    private boolean path;
    private int dot;
    private boolean intersection;
    private boolean tunnel;
    private EnumSet<Direction> allowedDir;
    
    public EnumSet<Direction> getAllowedDir() {
        return allowedDir;
    }
    
    public void setAllowedDir(EnumSet<Direction> allowedDir) {
        this.allowedDir = allowedDir;
    }
    
    public int getDot() {
        return dot;
    }
    
    public void setDot(int dot) {
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
