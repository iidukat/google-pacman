package jp.or.iidukat.example.pacman.entity;

import jp.or.iidukat.example.pacman.PacmanGame;

public class CutsceneGhost extends Ghost {

    public CutsceneGhost(int b, PacmanGame g) {
        super(b, g);
    }

    @Override
    public void B() {
        // TODO Auto-generated method stub

    }
    
    @Override
    InitPosition getInitPosition() {
        return null;
    }
    
    @Override
    int getOrdinaryImageRow() {
        return 4; // 4 + this.id - 1;
    }
}
