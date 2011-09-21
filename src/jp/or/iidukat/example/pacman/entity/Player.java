package jp.or.iidukat.example.pacman.entity;

import jp.or.iidukat.example.pacman.CurrentSpeed;
import jp.or.iidukat.example.pacman.Direction;
import jp.or.iidukat.example.pacman.GameplayMode;
import jp.or.iidukat.example.pacman.GhostMode;
import jp.or.iidukat.example.pacman.PacmanGame;
import jp.or.iidukat.example.pacman.PathElement;
import android.util.FloatMath;

public class Player extends Actor {

	public Player(int b, PacmanGame g) {
		super(b, g);
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
	    
	    // プレイヤーがフルーツを食べる
	    if (this.pos[0] == PacmanGame.getV()[0]
	            && (this.pos[1] == PacmanGame.getV()[1] || this.pos[1] == PacmanGame.getV()[1] + 8))
	        g.eatFruit(this.id);
	}

	@Override
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
	    PathElement pe =
	    	g.getPlayfield().get(Integer.valueOf(d[0]))
	    					.get(Integer.valueOf(d[1]));
	    if (this.nextDir != Direction.NONE
	    		&& pe.isIntersection()
	    		&& pe.getAllowedDir().contains(this.nextDir))
	    		this.t();
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
	    if (!g.getPlayfield().get(Integer.valueOf(b[0]))
	    					.get(Integer.valueOf(b[1]))
	    					.isPath()) { // プレイヤーがパスでないところへ移動しようとする
		    // 最後に正常に移動成功した位置に補正
		    this.pos[0] = this.lastGoodTilePos[0];
		    this.pos[1] = this.lastGoodTilePos[1];
		    b[0] = this.lastGoodTilePos[0];
		    b[1] = this.lastGoodTilePos[1];
		    this.dir = Direction.NONE;
	    } else // モンスターの移動 or プレイヤーがパスであるところへ移動
	    	this.lastGoodTilePos = new int[] {b[0], b[1]};
	
	    // トンネル通過[モンスターが食べられた時以外](currentSpeed:2) or それ以外(currentSpeed:0)
	    if (g.getPlayfield().get(Integer.valueOf(b[0]))
	    					.get(Integer.valueOf(b[1]))
	    					.isTunnel()
	    		&& this.mode != GhostMode.EATEN)
	    	this.c(CurrentSpeed.PASSING_TUNNEL);
	    else
	    	this.c(CurrentSpeed.NORMAL);
	    
	    // プレイヤーがエサを食べる
	    if (g.getPlayfield().get(Integer.valueOf(b[0]))
	    					.get(Integer.valueOf(b[1]))
	    					.getDot() != 0)
	    	g.dotEaten(this.id, b);
	    
	    this.tilePos[0] = b[0];
	    this.tilePos[1] = b[1];
	}
	
	// posの値がtilePosと一致(pos が8の倍数)したときに呼び出される
	@Override
	void u() {
	    this.n();
	    PathElement b =
	    	g.getPlayfield().get(Integer.valueOf((int) this.pos[0]))
	    					.get(Integer.valueOf((int) this.pos[1]));
	    if (b.isIntersection()) // 行き止まり/交差点にて
	    	if (this.nextDir != Direction.NONE && b.getAllowedDir().contains(this.nextDir)) { // nextDirで指定された方向へ移動可能
		        if (this.dir != Direction.NONE) this.lastActiveDir = this.dir;
		        this.dir = this.nextDir;
		        this.nextDir = Direction.NONE;
		        // 先行入力された移動方向分を更新(メソッドtを参照)
	        	this.pos[0] += this.posDelta[0];
		        this.pos[1] += this.posDelta[1];
		        this.posDelta = new float[] {0, 0};
	    	} else if (!b.getAllowedDir().contains(this.dir)) { // nextDirもdirも移動不可だったら、停止
	    		if (this.dir != Direction.NONE) this.lastActiveDir = this.dir;
	    		this.nextDir = this.dir = Direction.NONE;
	    		this.c(CurrentSpeed.NORMAL);
	    	}
	}

	// Pacman, Ms.Pacman表示画像決定(アニメーション対応)
	@Override
	int[] getImagePos() {
	    int b = 0;
	    int c = 0;
	    Direction d = this.dir;
	    if (d == Direction.NONE) d = this.lastActiveDir;
	    if (g.getGameplayMode() == GameplayMode.GHOST_DIED && this.id == g.getPlayerEatingGhostId()) { // モンスターを食べたとき。画像なし
	    	b = 3;
	    	c = 0;
	    } else if ((g.getGameplayMode() == GameplayMode.LEVEL_BEING_COMPLETED
	    					|| g.getGameplayMode() == GameplayMode.LEVEL_COMPLETED)
	    				&& this.id == 0) { // レベルクリア。Pacmanは丸まる
	    	b = 2;
	    	c = 0;
	    } else if (g.getGameplayMode() == GameplayMode.NEWGAME_STARTING
	    				|| g.getGameplayMode() == GameplayMode.NEWGAME_STARTED
	    				|| g.getGameplayMode() == GameplayMode.GAME_RESTARTED) { // ゲーム開始直後の表示画像決定
	    	b = this.id == 0 ? 2 : 4;
	    	c = 0;
	    } else if (g.getGameplayMode() == GameplayMode.PLAYER_DIED) // プレイヤーが死んだ時の画像決定.
	    	if (this.id == g.getPlayerDyingId()) { // 死んだ方
	    		int t = 20 - (int) FloatMath.floor(g.getGameplayModeTime() / g.getTiming()[4] * 21);
		        if (this.id == 0) { // Pacman
		        	b = t - 1;
		        	switch (b) {
		        	case -1:
		        		b = 0;
		        		break;
		        	case 11:
		        		b = 10;
		        		break;
			          case 12:
			          case 13:
			          case 14:
			          case 15:
			          case 16:
			          case 17:
			          case 18:
			          case 19:
			          case 20:
			        	  b = 11;
			        	  break;
		        	}
		        	c = 12;
		        } else // Ms.Pacman
		        	switch (t) {
		        	case 0:
			        case 1:
			        case 2:
			        case 6:
			        case 10:
			            b = 4;
				        c = 3;
				        break;
			        case 3:
			        case 7:
			        case 11:
			        	b = 4;
			        	c = 0;
			        	break;
			        case 4:
			        case 8:
			        case 12:
			        case 13:
			        case 14:
			        case 15:
			        case 16:
			        case 17:
			        case 18:
			        case 19:
			        case 20:
			        	b = 4;
			        	c = 2;
			        	break;
			        case 5:
			        case 9:
			        	b = 4;
			        	c = 1;
			        	break;
		        	}
		    	} else { // 死んでない方のプレーヤーの画像は非表示
			        b = 3;
			        c = 0;
		    	}
	    else if ("pcm-bpcm".equals(this.el.getId())) { // Cutscene
	    	b = 14;
	    	c = 0;
	    	int t = (int) (Math.floor(g.getGlobalTime() * 0.2) % 4);
	    	if (t == 3) t = 1;
	    	c += 2 * t;
	    	// BigPacMan
	    	this.el.setWidth(32);
	    	this.el.setHeight(32);
	    } else { // 通常時のプレイヤー画像決定
	    	switch (d) {
	    	case LEFT:
	    		c = 0;
	    		break;
	    	case RIGHT:
	    		c = 1;
	    		break;
	    	case UP:
	    		c = 2;
	    		break;
	    	case DOWN:
	    		c = 3;
	    		break;
	    	}
	    	if (g.getGameplayMode() != GameplayMode.PLAYER_DYING) b = (int) (Math.floor(g.getGlobalTime() * 0.3) % 4);
	    	if (b == 3 && this.dir == Direction.NONE) b = 0;
	    	if (b == 2 && this.id == 0) b = 0;
	    	if (b == 3) {
	    		b = 2;
	    		if (this.id == 0) c = 0;
	    	}
	    	if (this.id == 1) b += 4;
	    }
	    return new int[] { c, b };
	}
	
	@Override
	public void move() {
	    if (g.getGameplayMode() == GameplayMode.ORDINARY_PLAYING) {
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
