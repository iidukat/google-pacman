package jp.or.iidukat.example.pacman.entity;

import jp.or.iidukat.example.pacman.GhostMode;
import jp.or.iidukat.example.pacman.PacmanGame;

public class Clyde extends Ghost {

	public Clyde(int b, PacmanGame g) {
		super(b, g);
	}

	// ターゲットポジションを決定
	@Override
	public void B() {
	    if (this.mode != GhostMode.CHASE) {
	    	return;
	    }
	    // Playerと距離が近ければ追尾する。遠ければScatterモードでの受け持ち場所を目指す。
	    Actor b = g.getPlayers()[this.targetPlayerId];
    	float distance = PacmanGame.getDistance(b.tilePos, this.tilePos);
    	this.targetPos = distance > 64
    		? new float[] { b.tilePos[0], b.tilePos[1] }
    		: this.scatterPos;
	}

}
