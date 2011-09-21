package jp.or.iidukat.example.pacman.entity;

import jp.or.iidukat.example.pacman.Direction;
import jp.or.iidukat.example.pacman.GhostMode;
import jp.or.iidukat.example.pacman.Move;
import jp.or.iidukat.example.pacman.PacmanGame;

public class Inky extends Ghost {

	public Inky(int b, PacmanGame g) {
		super(b, g);
	}
	
	// ターゲットポジションを決定
	@Override
	public void B() {
	    if (this.mode != GhostMode.CHASE) {
	    	return;
	    }
	    
	    // PlayerをBLINKYと挟み撃ちにする
    	Actor b = g.getPlayers()[this.targetPlayerId];
		Move c = b.dir.getMove();
		Actor d = g.getGhosts()[0];
		float[] f = new float[] { b.tilePos[0], b.tilePos[1] };
		f[c.getAxis()] += 16 * c.getIncrement();
		if (b.dir == Direction.UP) f[1] -= 16;
		this.targetPos[0] = f[0] * 2 - d.tilePos[0];
		this.targetPos[1] = f[1] * 2 - d.tilePos[1];
	}
}
