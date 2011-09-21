package jp.or.iidukat.example.pacman.entity;

import jp.or.iidukat.example.pacman.GhostMode;
import jp.or.iidukat.example.pacman.PacmanGame;

public class Blinky extends Ghost {

	public Blinky(int b, PacmanGame g) {
		super(b, g);
	}

	// ターゲットポジションを決定
	@Override
	public void B() {
		// Playerを追尾する
    	Actor b = g.getPlayers()[this.targetPlayerId];
	    if (g.getDotsRemaining() < g.getLevels().getElroyDotsLeftPart1()
	    		&& this.mode == GhostMode.SCATTER
	    		&& (!g.isLostLifeOnThisLevel() || g.getGhosts()[3].mode != GhostMode.IN_PEN)) {
	    	this.targetPos = new float[] { b.tilePos[0], b.tilePos[1] };
	    } else if (this.mode == GhostMode.CHASE) {
	    	this.targetPos = new float[] { b.tilePos[0], b.tilePos[1] };
	    }
	}

}
