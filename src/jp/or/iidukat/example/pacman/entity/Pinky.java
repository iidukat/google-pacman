package jp.or.iidukat.example.pacman.entity;

import jp.or.iidukat.example.pacman.Direction;
import jp.or.iidukat.example.pacman.GhostMode;
import jp.or.iidukat.example.pacman.Move;
import jp.or.iidukat.example.pacman.PacmanGame;

public class Pinky extends Ghost {

	public Pinky(int b, PacmanGame g) {
		super(b, g);
	}

	// ターゲットポジションを決定
	@Override
	public void B() {
	    if (this.mode != GhostMode.CHASE) {
	    	return;
	    	
	    }
	    // Playerを先回りする
    	Actor b = g.getPlayers()[this.targetPlayerId];
		Move c = b.dir.getMove();
    	this.targetPos = new float[] { b.tilePos[0], b.tilePos[1] };
    	this.targetPos[c.getAxis()] += 32 * c.getIncrement();
    	if (b.dir == Direction.UP) this.targetPos[1] -= 32;
	}
	
}
