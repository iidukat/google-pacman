package jp.or.iidukat.example.pacman.entity;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

import jp.or.iidukat.example.pacman.Direction;
import jp.or.iidukat.example.pacman.GhostMode;
import jp.or.iidukat.example.pacman.PacmanGame;

public class Clyde extends Ghost {

	private static final InitPosition INIT_POS =
		InitPosition.createGhostInitPosition(41.375f, 7, Direction.UP, 0, 20);

    // モンスターの巣の中での動き
	private static final Map<GhostMode, MoveInPen[]> MOVES_IN_PEN;
	static {
		Map<GhostMode, MoveInPen[]> m =
			new EnumMap<GhostMode, MoveInPen[]>(GhostMode.class);
		m.put(
			GhostMode.IN_PEN,
	  		new MoveInPen[] {
		  			new MoveInPen(41.4f, 7, Direction.UP, 6.375f, 0.48f),
		  			new MoveInPen(41.4f, 6.375f, Direction.DOWN, 7.625f, 0.48f),
		  			new MoveInPen(41.4f, 7.625f, Direction.UP, 7, 0.48f),
		  	});			
		m.put(
			GhostMode.EXITING_FROM_PEN,
	      	new MoveInPen[] {
		      		new MoveInPen(41.4f, 7, Direction.LEFT, 39.5f, y),
		      		new MoveInPen(39.5f, 7, Direction.UP, 4, y),
			});			
		m.put(
			GhostMode.ENTERING_PEN,
         	new MoveInPen[] {
	         		new MoveInPen(39.5f, 4, Direction.DOWN, 7, 1.6f),
	         		new MoveInPen(39.5f, 7, Direction.RIGHT, 41.375f, 1.6f),
	        });			
		m.put(
			GhostMode.RE_EXITING_FROM_PEN,
	        new MoveInPen[] {
					new MoveInPen(41.4f, 7, Direction.LEFT, 39.5f, y),
		          	new MoveInPen(39.5f, 7, Direction.UP, 4, y),
		    });     		
		MOVES_IN_PEN = Collections.unmodifiableMap(m);
	}

	public Clyde(int b, PacmanGame g) {
		super(b, g);
	}
	
	@Override
	InitPosition getInitPosition() {
		return INIT_POS;
	}

	// ターゲットポジションを決定
	@Override
	public void B() {
	    if (this.mode != GhostMode.CHASE) {
	    	return;
	    }
	    // Playerと距離が近ければ追尾する。遠ければScatterモードでの受け持ち場所を目指す。
	    Actor b = g.getPlayer();
    	float distance = PacmanGame.getDistance(b.tilePos, this.tilePos);
    	this.targetPos = distance > 64
    		? new float[] { b.tilePos[0], b.tilePos[1] }
    		: this.scatterPos;
	}

	@Override
	MoveInPen[] getMovesInPen() {
		if (MOVES_IN_PEN.containsKey(mode)) {
			return MOVES_IN_PEN.get(mode);
		} else {
			return new MoveInPen[0];
		}
	}

	@Override
	int getOrdinaryImageRow() {
        return 7; // 4 + this.id - 1;		
	}
}
