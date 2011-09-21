package jp.or.iidukat.example.pacman.entity;

import jp.or.iidukat.example.pacman.CurrentSpeed;
import jp.or.iidukat.example.pacman.Direction;
import jp.or.iidukat.example.pacman.GameplayMode;
import jp.or.iidukat.example.pacman.GhostMode;
import jp.or.iidukat.example.pacman.PacmanGame;
import jp.or.iidukat.example.pacman.PathElement;
import android.util.FloatMath;

public abstract class Ghost extends Actor {

	public Ghost(int b, PacmanGame g) {
		super(b, g);
	}
	
	public abstract void B();
	
	void o() {
	    float b = this.pos[0] / 8;
	    float c = this.pos[1] / 8;
	    int[] d = { Math.round(b) * 8, Math.round(c) * 8};
	    if (d[0] != this.tilePos[0] || d[1] != this.tilePos[1]) // tileが切り替わる
	    	this.p(d); // tilePosの更新
	    else {
	    	float[] tPoses =
	    		new float[] {
		    			FloatMath.floor(b) * 8,
		    			FloatMath.floor(c) * 8
	    		};
	    	if (this.pos[1] == tPoses[1] && this.pos[0] == tPoses[0])
	    		this.u(); // posの値がtilePosと一致(pos が8の倍数)
	    }
	}
	

	@Override
	void n() {
	    if (this.pos[0] == PacmanGame.getQ()[0].getY() * 8
	    		&& this.pos[1] == PacmanGame.getQ()[0].getX() * 8) { // 画面左から右へワープ
	        this.pos[0] = PacmanGame.getQ()[1].getY() * 8;
	        this.pos[1] = (PacmanGame.getQ()[1].getX() - 1) * 8;
	    } else if (this.pos[0] == PacmanGame.getQ()[1].getY() * 8
	    			&& this.pos[1] == PacmanGame.getQ()[1].getX() * 8) { // 画面右から左へワープ
	        this.pos[0] = PacmanGame.getQ()[0].getY() * 8;
	        this.pos[1] = (PacmanGame.getQ()[0].getX() + 1) * 8;
	    }
	    // モンスターが巣に入る
	    if (this.mode == GhostMode.EATEN
	    		&& this.pos[0] == s[0]
	    		&& this.pos[1] == s[1])
	    	this.a(GhostMode.ENTERING_PEN);
	}

	
	// tilePosとposの差分が有意になったとき呼び出される
	@Override
	void p(int[] b) {
	    g.setTilesChanged(true);
	    if (this.reverseDirectionsNext) { // 方向を反転する(この判定がtrueになるのはモンスターのみ)
	    	this.dir = this.dir.getOpposite();
	    	this.nextDir = Direction.NONE;
	    	this.reverseDirectionsNext = false;
	    	this.i(true);
	    }

	    // モンスターの移動 or プレイヤーがパスであるところへ移動
	    this.lastGoodTilePos = new int[] {b[0], b[1]};
	
	    // トンネル通過[モンスターが食べられた時以外](currentSpeed:2) or それ以外(currentSpeed:0)
	    if (g.getPlayfield().get(Integer.valueOf(b[0]))
	    					.get(Integer.valueOf(b[1]))
	    					.isTunnel()
	    		&& this.mode != GhostMode.EATEN)
	    	this.c(CurrentSpeed.PASSING_TUNNEL);
	    else
	    	this.c(CurrentSpeed.NORMAL);
	    
	    this.tilePos[0] = b[0];
	    this.tilePos[1] = b[1];
	}
	
	// posの値がtilePosと一致(pos が8の倍数)したときに呼び出される
	@Override
	void u() {
	    this.n();
	    this.i(false); // モンスターの交差点/行き止まりでの進行方向決定
	    PathElement b =
	    	g.getPlayfield().get(Integer.valueOf((int) this.pos[0]))
	    					.get(Integer.valueOf((int) this.pos[1]));
	    if (b.isIntersection()) // 行き止まり/交差点にて
	    	if (this.nextDir != Direction.NONE && b.getAllowedDir().contains(this.nextDir)) { // nextDirで指定された方向へ移動可能
		        if (this.dir != Direction.NONE) this.lastActiveDir = this.dir;
		        this.dir = this.nextDir;
		        this.nextDir = Direction.NONE;
		    } else if (!b.getAllowedDir().contains(this.dir)) { // nextDirもdirも移動不可だったら、停止
		    	if (this.dir != Direction.NONE) this.lastActiveDir = this.dir;
		    	this.nextDir = this.dir = Direction.NONE;
		    	this.c(CurrentSpeed.NORMAL);
		    }
	}
	
	// モンスターの表示画像決定
	@Override
	int[] getImagePos() {
	    int b = 0;
	    int c = 0;
	    if (g.getGameplayMode() == GameplayMode.LEVEL_COMPLETED
	    		|| g.getGameplayMode() == GameplayMode.NEWGAME_STARTING
	    		|| g.getGameplayMode() == GameplayMode.PLAYER_DIED) {
	    	// Pacman or Ms.Pacmanが死んだ直後。モンスターの姿は消える 
	    	b = 3;
	    	c = 0;
	    } else if (g.getGameplayMode() == GameplayMode.GHOST_DIED && this.id == g.getGhostBeingEatenId()) {
	    	switch (g.getModeScoreMultiplier()) {// モンスターが食べられたときに表示させるスコアを決定
	    	case 2:
	    		b = 0;
	    		break;
	    	case 4:
	    		b = 1;
	    		break;
	    	case 8:
	    		b = 2;
	    		break;
	    	case 16:
	    		b = 3;
	    		break;
	    	}
	    	c = 11;
//	      	this.el.className = "pcm-ac pcm-n"
	    } else if (this.mode == GhostMode.FRIGHTENED
	              	|| (this.mode == GhostMode.IN_PEN || this.mode == GhostMode.EXITING_FROM_PEN)
	              		&& g.getMainGhostMode() == GhostMode.FRIGHTENED
	              		&& !this.eatenInThisFrightMode) {
	    	// ブルーモード.ただし、食べられてはいない
	    	b = 0;
	    	c = 8;
	    	// ブルーモード時間切れ間近の青白明滅
	    	if (g.getFrightModeTime() < g.getLevels().getFrightTotalTime() - g.getLevels().getFrightTime()
	    			&& FloatMath.floor(g.getFrightModeTime() / g.getTiming()[1]) % 2 == 0)
	    		b += 2;
	
	    	b += (int) (Math.floor(g.getGlobalTime() / 16) % 2); // ブルーモードの画像切り替え
	    } else if (this.mode == GhostMode.EATEN || this.mode == GhostMode.ENTERING_PEN) { // 食べられて目玉だけ
	    	Direction ndir = this.nextDir;
	    	if (ndir != Direction.NONE) ndir = this.dir;
	    	switch (ndir) {
	    	case LEFT:
	    		b = 2;
	    		break;
	    	case RIGHT:
	    		b = 3;
	    		break;
	    	case UP:
	    		b = 0;
	    		break;
	    	case DOWN:
	    		b = 1;
	    		break;
	    	}
	    	c = 10;
	    } else if ("pcm-ghin".equals(this.el.getId())) {
	    	b = 6;
	    	c = 8;
	    	b += (int) (Math.floor(g.getGlobalTime() / 16) % 2);
	    } else if ("pcm-gbug".equals(this.el.getId())) {
	    	b = 6;
	    	c = 9;
	    	c += (int) (Math.floor(g.getGlobalTime() / 16) % 2);
	    } else if ("pcm-ghfa".equals(this.el.getId())) {
	    	b = g.getCutsceneSequenceId() == 3 ? 6 : 7;
	    	c = 11;
	    } else if ("pcm-stck".equals(this.el.getId())) {
	    	b = g.getCutsceneSequenceId() == 1
	            ? g.getCutsceneTime() > 60
	                ? 1
	                : g.getCutsceneTime() > 45
	                    ? 2
	                    : 3
	            : g.getCutsceneSequenceId() == 2
	                ? 3
	                : g.getCutsceneSequenceId() == 3 || g.getCutsceneSequenceId() == 4
	                    ? 4
	                    : 0;
	        c = 13;
	    } else { // 通常時の画像表示
	    	Direction ndir = this.nextDir;
	    	if (ndir == Direction.NONE
	    		|| g.getPlayfield().get(Integer.valueOf(this.tilePos[0]))
	    							.get(Integer.valueOf(this.tilePos[1]))
	    							.isTunnel()) {
	    		ndir = this.dir;
	    	}
	    	
	        switch (ndir) {
	        case LEFT:
	        	b = 4;
	        	break;
	        case RIGHT:
	        	b = 6;
	        	break;
	        case UP:
	        	b = 0;
	        	break;
	        case DOWN:
	        	b = 2;
	        	break;
	        }
	        c = 4 + this.id - g.getPlayerCount();
	        if (this.speed > 0 || g.getGameplayMode() != GameplayMode.CUTSCENE)
	        	b += (int) (Math.floor(g.getGlobalTime() / 16) % 2);
	    }
	    return new int[] { c, b };
	}

	@Override
	public void move() {
	    if (g.getGameplayMode() == GameplayMode.ORDINARY_PLAYING
	    		|| g.getGameplayMode() == GameplayMode.GHOST_DIED
	    			&& (this.mode == GhostMode.EATEN
	    					|| this.mode == GhostMode.ENTERING_PEN)) {
	    	if (this.requestedDir != Direction.NONE) {
	    		this.z(this.requestedDir);
	    		this.requestedDir = Direction.NONE;
	    	}
	    	if (this.followingRoutine) {
	    		this.j();
	    		if (this.mode == GhostMode.ENTERING_PEN) this.j();
	    	} else {
	    		this.e();
	    		if (this.mode == GhostMode.EATEN) this.e();
	    	}
	    }
	}


}
