package jp.or.iidukat.example.pacman;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.or.iidukat.example.pacman.PlayField.Food;
import jp.or.iidukat.example.pacman.PlayField.GameOver;
import jp.or.iidukat.example.pacman.PlayField.Ready;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.FloatMath;
import android.view.MotionEvent;


public class Pacman10Hp3 {
	
	private static final boolean a = true;
	private static final boolean e = false;
    
    private static class Direction {
    	final int axis;
    	final int increment;
    	Direction(int axis, int increment) {
    		this.axis = axis;
    		this.increment = increment;
    	}
    }

    private static final Map<Integer, Direction> l;
    static {
    	Map<Integer, Direction> ds = new HashMap<Integer, Direction>();
    	ds.put(Integer.valueOf(0), new Direction(0, 0));
    	ds.put(Integer.valueOf(1), new Direction(0, -1)); // Up
    	ds.put(Integer.valueOf(2), new Direction(0, 1)); // Down
    	ds.put(Integer.valueOf(4), new Direction(1, -1)); // Left
    	ds.put(Integer.valueOf(8), new Direction(1, 1)); // Right
    	l = Collections.unmodifiableMap(ds);
    }
    
    private static final int[] v = {80, 312}; // フルーツ出現位置
    
    private static class PathElement {
        boolean path;
        int dot;
        boolean intersection;
        boolean tunnel;
        int allowedDir;
    }    

    // Class for Actor(Pacman, Ms.Pacman, Ghost)
    static class E {

        private static final int[] i = {1, 4, 2, 8};
        
        private static class InitPosition {
        	final float x;
        	final float y;
        	final int dir;
        	final float scatterX;
        	final float scatterY;

        	InitPosition(float x, float y, int dir) {
        		this(x, y, dir, 0, 0);
        	}
        	
        	InitPosition(float x, float y, int dir, float scatterX, float scatterY) {
        		this.x = x;
        		this.y = y;
        		this.dir = dir;
        		this.scatterX = scatterX;
        		this.scatterY = scatterY;
        	}
        	
        	static InitPosition createPlayerInitPosition(float x, float y, int dir) {
        		return new InitPosition(x, y, dir);
        	}
        	
        	static InitPosition createGhostInitPosition(float x, float y, int dir,
        											float scatterX, float scatterY) {
        		return new InitPosition(x, y, dir, scatterX, scatterY);
        	}
        }

        // Actor初期配置
        private static final Map<Integer, InitPosition[]> r;
        static {
        	Map<Integer, InitPosition[]> ps = new HashMap<Integer, InitPosition[]>();
        	ps.put(
        		Integer.valueOf(1),
        		new InitPosition[] {
        			InitPosition.createPlayerInitPosition(39.5f, 15, 4), // Pacman
        			InitPosition.createGhostInitPosition(39.5f, 4, 4, 57, -4), // アカベエ
        			InitPosition.createGhostInitPosition(39.5f, 7, 2, 0, -4), // ピンキー
        			InitPosition.createGhostInitPosition(37.625f, 7, 1, 57, 20), // アオスケ
        			InitPosition.createGhostInitPosition(41.375f, 7, 1, 0, 20), // グズタ
        		});
        	ps.put(
           		Integer.valueOf(2),
           		new InitPosition[] {
           			InitPosition.createPlayerInitPosition(40.25f, 15, 8), // Pacman
           			InitPosition.createPlayerInitPosition(38.75f, 15, 4), // Ms.Pacman
           			InitPosition.createGhostInitPosition(39.5f, 4, 4, 57, -4), // アカベエ
           			InitPosition.createGhostInitPosition(39.5f, 7, 2, 0, -4), // ピンキー
           			InitPosition.createGhostInitPosition(37.625f, 7, 1, 57, 20), // アオスケ
           			InitPosition.createGhostInitPosition(41.375f, 7, 1, 0, 20), // グズタ
           		});
        	
        	r = Collections.unmodifiableMap(ps);
        }

        private static final int[] s = {32, 312}; // モンスターの巣の入り口の位置

        private static class MoveInPen {
        	final float x;
        	final float y;
        	final int dir;
        	final float dest;
        	final float speed;
        	MoveInPen(float x, float y, int dir, float dest, float speed) {
        		this.x = x;
        		this.y = y;
        		this.dir = dir;
        		this.dest = dest;
        		this.speed = speed;
        	}
        }

        // 配列Aの要素のspeedプロパティで使用される
        private static float y = 0.8f * 0.4f;

        // モンスターの巣の中での動き
        private static final Map<Integer, MoveInPen[]> A;
        static {
        	Map<Integer, MoveInPen[]> mvs = new HashMap<Integer, MoveInPen[]>();
        	mvs.put(
        		Integer.valueOf(1),
        		new MoveInPen[] {
        			new MoveInPen(37.6f, 7, 1, 6.375f, 0.48f),
        			new MoveInPen(37.6f, 6.375f, 2, 7.625f, 0.48f),
        			new MoveInPen(37.6f, 7.625f, 1, 7, 0.48f),
        		});
        	mvs.put(
        		Integer.valueOf(2),
        		new MoveInPen[] {
        			new MoveInPen(39.5f, 7, 2, 7.625f, 0.48f),
        			new MoveInPen(39.5f, 7.625f, 1, 6.375f, 0.48f),
        			new MoveInPen(39.5f, 6.375f, 2, 7, 0.48f),
        		});
        	mvs.put(
        		Integer.valueOf(3),
        		new MoveInPen[] {
        			new MoveInPen(41.4f, 7, 1, 6.375f, 0.48f),
        			new MoveInPen(41.4f, 6.375f, 2, 7.625f, 0.48f),
        			new MoveInPen(41.4f, 7.625f, 1, 7, 0.48f),
        		});
        	mvs.put(
        		Integer.valueOf(4),
        		new MoveInPen[] {
        			new MoveInPen(37.6f, 7, 8, 39.5f, y),
        			new MoveInPen(39.5f, 7, 1, 4, y),
        		});
        	mvs.put(
        		Integer.valueOf(5),
        		new MoveInPen[] { new MoveInPen(39.5f, 7, 1, 4, y) });
        	mvs.put(
            	Integer.valueOf(6),
            	new MoveInPen[] {
            		new MoveInPen(41.4f, 7, 4, 39.5f, y),
            		new MoveInPen(39.5f, 7, 1, 4, y),
            	});
        	mvs.put(
               	Integer.valueOf(7),
               	new MoveInPen[] {
               		new MoveInPen(39.5f, 4, 2, 7, 1.6f),
               		new MoveInPen(39.5f, 7, 4, 37.625f, 1.6f),
               	});
        	mvs.put(
           		Integer.valueOf(8),
           		new MoveInPen[] { new MoveInPen(39.5f, 4, 2, 7, 1.6f) });
        	mvs.put(
               	Integer.valueOf(9),
               	new MoveInPen[] {
               		new MoveInPen(39.5f, 4, 2, 7, 1.6f),
               		new MoveInPen(39.5f, 7, 8, 41.375f, 1.6f),
               	});
        	mvs.put(
               	Integer.valueOf(10),
               	new MoveInPen[] {
               		new MoveInPen(37.6f, 7, 8, 39.5f, y),
               		new MoveInPen(39.5f, 7, 1, 4, y),
               	});
        	mvs.put(
           		Integer.valueOf(11),
           		new MoveInPen[] { new MoveInPen(39.5f, 7, 1, 4, y) });
        	mvs.put(
                Integer.valueOf(12),
                new MoveInPen[] {
                	new MoveInPen(41.4f, 7, 4, 39.5f, y),
                	new MoveInPen(39.5f, 7, 1, 4, y),
                });
        	A = Collections.unmodifiableMap(mvs);
        }
        
    	final int id;
    	private final Game g;
    	boolean ghost;
    	int mode;
    	float[] pos;
    	float[] posDelta;
    	int[] tilePos;
    	int[] lastGoodTilePos;
    	float[] elPos;
    	int[] elBackgroundPos;
    	float[] targetPos;
    	float[] scatterPos;
    	int dir;
    	int lastActiveDir;
    	float speed;
    	float physicalSpeed;
    	int requestedDir;
    	int nextDir;
    	boolean reverseDirectionsNext;
    	boolean freeToLeavePen;
    	boolean modeChangedWhileInPen;
    	boolean eatenInThisFrightMode;
    	boolean followingRoutine;
    	boolean proceedToNextRoutineMove;
    	int routineToFollow;
    	int routineMoveId;
    	int targetPlayerId;
    	int currentSpeed;
    	float fullSpeed;
    	float dotEatingSpeed;
    	float tunnelSpeed;
    	Boolean[] speedIntervals;
    	int dotCount;
        
    	Presentation el = new Presentation();

    	E(int b, Game g) {
    		this.id = b;
    		this.g = g;
    	}
    	
    	// Actorを再配置
    	void A() {
		    InitPosition b = r.get(g.playerCount)[this.id];
		    this.pos = new float[] {b.y * 8, b.x * 8};
		    this.posDelta = new float[] {0, 0};
		    this.tilePos = new int[] {(int) b.y * 8, (int) b.x * 8};
		    this.targetPos = new float[] {b.scatterY * 8, b.scatterX * 8};
		    this.scatterPos = new float[] {b.scatterY * 8, b.scatterX * 8};
		    this.lastActiveDir = this.dir = b.dir;
		    this.physicalSpeed = 0;
		    this.requestedDir = this.nextDir = 0;
		    this.c(0);
		    this.reverseDirectionsNext = this.freeToLeavePen = this.modeChangedWhileInPen = this.eatenInThisFrightMode = e;
		    this.l();
    	}
    	
    	// Actor表示に使用するdivタグを生成: 表示位置、バックグランドのオフセットはダミー値
    	void createElement() {
    		// this.el.className = "pcm-ac";
    		this.el.width = 16;
    		this.el.height = 16;
    		this.el.id = "actor" + this.id;
    		this.el.parent = g.playfieldEl.presentation;
    		g.prepareElement(this.el, 0, 0);
    		g.playfieldEl.actors.add(this);
    		this.elPos = new float[] {0, 0};
    		this.elBackgroundPos = new int[] {0, 0};
    	}
    	// モンスターのモード設定
    	void a(int b) {
    		int c = this.mode;
    		this.mode = b;
    		if (this.id == g.playerCount + 3 && (b == 16 || c == 16)) g.updateCruiseElroySpeed();
    		switch (c) {
    		case 32:
    			g.ghostExitingPenNow = e;
    			break;
    		case 8:
    			if (g.ghostEyesCount > 0) g.ghostEyesCount--;
    			if (g.ghostEyesCount == 0) g.playAmbientSound();
    			break;
    		}
		    switch (b) {
		    case 4:
		    	this.fullSpeed = g.levels.ghostFrightSpeed * 0.8f;
		    	this.tunnelSpeed = g.levels.ghostTunnelSpeed * 0.8f;
		    	this.followingRoutine = e;
		    	break;
		    case 1:
		    	this.fullSpeed = g.levels.ghostSpeed * 0.8f;
		    	this.tunnelSpeed = g.levels.ghostTunnelSpeed * 0.8f;
		    	this.followingRoutine = e;
		    	break;
		    case 2:
		    	this.targetPos = this.scatterPos;
		    	this.fullSpeed = g.levels.ghostSpeed * 0.8f;
		    	this.tunnelSpeed = g.levels.ghostTunnelSpeed * 0.8f;
		    	this.followingRoutine = e;
		    	break;
		    case 8:
		    	this.tunnelSpeed = this.fullSpeed = 1.6f;
		    	this.targetPos = new float[] {s[0], s[1]};
		    	this.freeToLeavePen = this.followingRoutine = e;
		    	break;
		    case 16:
		    	this.l();
		    	this.followingRoutine = a;
		    	this.routineMoveId = -1;
		    	if (this.id == g.playerCount + 1)
			        this.routineToFollow = 2;
		    	else if (this.id == g.playerCount + 2)
			        this.routineToFollow = 1;
		    	else if (this.id == g.playerCount + 3)
			        this.routineToFollow = 3;
	
		    	break;
		    case 32:
		    	this.followingRoutine = a;
		    	this.routineMoveId = -1;
		    	if (this.id == g.playerCount + 1)
		    		this.routineToFollow = 5;
		    	else if (this.id == g.playerCount + 2)
		    		this.routineToFollow = 4;
		    	else if (this.id == g.playerCount + 3)
		    		this.routineToFollow = 6;
	
		    	g.ghostExitingPenNow = a;
		    	break;
		    case 64:
		    	this.followingRoutine = a;
		    	this.routineMoveId = -1;
		    	
		    	if (this.id == g.playerCount || this.id == g.playerCount + 1)
		    		this.routineToFollow = 8;
		    	else if (this.id == g.playerCount + 2)
		    		this.routineToFollow = 7;
		    	else if (this.id == g.playerCount + 3)
		    		this.routineToFollow = 9;
		    	
		    	break;
		    case 128:
		    	this.followingRoutine = a;
		    	this.routineMoveId = -1;
		    	
		    	if (this.id == g.playerCount || this.id == g.playerCount + 1)
		    		this.routineToFollow = 11;
		    	else if (this.id == g.playerCount + 2)
		    		this.routineToFollow = 10;
		    	else if (this.id == g.playerCount + 3)
		    		this.routineToFollow = 12;
		    	
		    	break;
		    }
		    this.d();
    	}
		// 追跡対象のActorを決定(Pacman or Ms.Pacman)
		void l() {
			if (this.id >= g.playerCount)
				this.targetPlayerId = (int) FloatMath.floor(g.rand() * g.playerCount);
		}
	
		// 位置, 速度の決定
		void z(int b) {
		    if (!g.userDisabledSound) { // サウンドアイコンの更新
		    	g.pacManSound = a;
		    	g.updateSoundIcon();
		    }
		    if (this.dir == g.oppositeDirections.get(Integer.valueOf(b)).intValue()) {
		    	this.dir = b;
		    	this.posDelta = new float[] {0, 0};
		    	if (this.currentSpeed != 2) this.c(0);
		    	if (this.dir != 0) this.lastActiveDir = this.dir;
		    	this.nextDir = 0;
		    } else if (this.dir != b)
		    	if (this.dir == 0) {
		    		if ((g.playfield.get(Integer.valueOf((int) this.pos[0]))
		    						.get(Integer.valueOf((int) this.pos[1]))
		    						.allowedDir & b) != 0)
		    			this.dir = b;
		    	} else {
		    		PathElement p = g.playfield.get(Integer.valueOf(this.tilePos[0])).get(Integer.valueOf(this.tilePos[1]));
		    		if (p != null && (p.allowedDir & b) != 0) { // 移動可能な方向が入力された場合
		    			// 	遅延ぎみに方向入力されたかどうか判定
		    			Direction c = l.get(this.dir);
		    			float[] d = new float[] {this.pos[0], this.pos[1]};
		    			d[c.axis] -= c.increment;
		    			int f = 0;
		    			if (d[0] == this.tilePos[0] && d[1] == this.tilePos[1]) {
		    				f = 1;
		    			} else {
		    				d[c.axis] -= c.increment;
		    				if (d[0] == this.tilePos[0] && d[1] == this.tilePos[1]) {
		    					f = 2;
		    				}
		    			}
		    			if (f != 0) { // 遅延ぎみに方向入力された場合、新しい移動方向に応じて位置を補正
		    				this.dir = b;
		    				this.pos[0] = this.tilePos[0];
		    				this.pos[1] = this.tilePos[1];
		    				c = l.get(this.dir);
		    				this.pos[c.axis] += c.increment * f;
		    				return;
		    			}
		    		}
		    		// 移動方向の先行入力対応
		    		this.nextDir = b;
		    		this.posDelta = new float[] {0, 0};
		    	}
		}
		// モンスターが交差点/行き止まり にたどり着いたときの動作. nextDirの決定
		// 	b: 反転済みフラグ
		void i(boolean b) {
			int[] c = this.tilePos;
			Direction d = l.get(Integer.valueOf(this.dir));
			int[] f = new int[] {c[0], c[1]};
			f[d.axis] += d.increment * 8; // 進行方向へ1マス先取り
			PathElement h =
				g.playfield.get(Integer.valueOf(f[0]))
							.get(Integer.valueOf(f[1]));
			if (b && !h.intersection)
				h = g.playfield.get(Integer.valueOf(c[0]))
								.get(Integer.valueOf(c[1])); // 交差点/行き止まり でなければ現在位置に戻る(反転済みの場合)
			
		    if (h.intersection)
		    	switch (this.mode) {
		    	case 2: // Scatter
		    	case 1: // 追跡
		    	case 8: // プレイヤーに食べられる
		    		int nDir = 0;
			        if ((this.dir & h.allowedDir) == 0
			        		&& h.allowedDir == g.oppositeDirections.get(Integer.valueOf(this.dir)).intValue()) // 反対向きしか通れないなら反対向きを選ぶ
			        	this.nextDir = g.oppositeDirections.get(Integer.valueOf(this.dir)).intValue();
			        else { // 反対向き以外を選択可能なら、目的地に最も近い方向を選択する
			        	float max = 99999999999f;
			        	float distance = 0;
			        	for (int k : i) {
			        		if ((h.allowedDir & k) != 0
			        				&& this.dir != g.oppositeDirections.get(Integer.valueOf(k)).intValue()) {
			        			d = l.get(k);
			        			float[] x = new float[] {(float) f[0], (float) f[1]};
			        			x[d.axis] += d.increment;
			        			distance = g.getDistance(x, new float[] {this.targetPos[0], this.targetPos[1]});
			        			if (distance < max) {
			        				max = distance;
			        				nDir = k;
			        			}
			        		}
			        	}
			        	if (nDir != 0) this.nextDir = nDir;
			        }
			        break;
		    	case 4: // ブルーモード
			        if ((this.dir & h.allowedDir) == 0
			        		&& h.allowedDir == g.oppositeDirections.get(Integer.valueOf(this.dir)).intValue()) // 反対向きしか通れないなら反対向きを選ぶ
			        	this.nextDir = g.oppositeDirections.get(Integer.valueOf(this.dir)).intValue();
			        else { // 移動可能な方向のうち反対向き以外を選択
			        	int ndir = 0;
			        	do ndir = i[(int) FloatMath.floor(g.rand() * 4)];
			        	while ((ndir & h.allowedDir) == 0
			        				|| ndir == g.oppositeDirections.get(Integer.valueOf(this.dir)).intValue());
			        	this.nextDir = ndir;
			        }
			        break;
		      }
		}
		// tilePosとposの差分が有意になったとき呼び出される
		void p(int[] b) {
		    g.tilesChanged = a;
		    if (this.reverseDirectionsNext) { // 方向を反転する(この判定がtrueになるのはモンスターのみ)
		    	this.dir = g.oppositeDirections.get(Integer.valueOf(this.dir)).intValue();
		    	this.nextDir = 0;
		    	this.reverseDirectionsNext = e;
		    	this.i(a);
		    }
		    if (!this.ghost
		    		&& !g.playfield.get(Integer.valueOf(b[0]))
		    						.get(Integer.valueOf(b[1]))
		    						.path) { // プレイヤーがパスでないところへ移動しようとする
			    // 最後に正常に移動成功した位置に補正
			    this.pos[0] = this.lastGoodTilePos[0];
			    this.pos[1] = this.lastGoodTilePos[1];
			    b[0] = this.lastGoodTilePos[0];
			    b[1] = this.lastGoodTilePos[1];
			    this.dir = 0;
		    } else // モンスターの移動 or プレイヤーがパスであるところへ移動
		    	this.lastGoodTilePos = new int[] {b[0], b[1]};
		
		    // トンネル通過[モンスターが食べられた時以外](currentSpeed:2) or それ以外(currentSpeed:0)
		    if (g.playfield.get(Integer.valueOf(b[0]))
		    				.get(Integer.valueOf(b[1]))
		    				.tunnel
		    		&& this.mode != 8)
		    	this.c(2);
		    else
		    	this.c(0);
		    
		    // プレイヤーがエサを食べる
		    if (!this.ghost
		    		&& g.playfield.get(Integer.valueOf(b[0]))
		    						.get(Integer.valueOf(b[1]))
		    						.dot != 0)
		    	g.dotEaten(this.id, b);
		    
		    this.tilePos[0] = b[0];
		    this.tilePos[1] = b[1];
		}
	
		// 先行入力された方向に対応
		void t() {
		    int[] b = this.tilePos;
		    float[] c;
		    float[] d;
		    switch (this.dir) {
		    case 1:
		    	c = new float[] { b[0], b[1] };
		        d = new float[] { b[0] + 3.6f, b[1] };
		        break;
		    case 2:
		    	c = new float[] { b[0] - 4, b[1] };
		    	d = new float[] { b[0], b[1] };
		    	break;
		    case 4:
		    	c = new float[] { b[0], b[1] };
		    	d = new float[] { b[0], b[1] + 3.6f };
		    	break;
		    case 8:
		    	c = new float[] { b[0], b[1] - 4 };
		    	d = new float[] { b[0], b[1] };
		    	break;
		    default:
		    	// posDeltaの更新が行われないようにダミーの値をセット
		    	c = new float[] { this.pos[0] + 1, this.pos[1] + 1 };
		    	d = new float[] { this.pos[0] - 1, this.pos[1] - 1 };
		    	break;
		    }
		    if (this.pos[0] >= c[0]
		        && this.pos[0] <= d[0]
		        && this.pos[1] >= c[1]
		        && this.pos[1] <= d[1]) {
		    	Direction dir = l.get(Integer.valueOf(this.nextDir));
		    	this.posDelta[dir.axis] += dir.increment;
		    }
		}
	
		void n() {
		    if (this.pos[0] == g.q[0].y * 8 && this.pos[1] == g.q[0].x * 8) { // 画面左から右へワープ
		        this.pos[0] = g.q[1].y * 8;
		        this.pos[1] = (g.q[1].x - 1) * 8;
		    } else if (this.pos[0] == g.q[1].y * 8 && this.pos[1] == g.q[1].x * 8) { // 画面右から左へワープ
		        this.pos[0] = g.q[0].y * 8;
		        this.pos[1] = (g.q[0].x + 1) * 8;
		    }
		    // モンスターが巣に入る
		    if (this.mode == 8
		    		&& this.pos[0] == s[0]
		    		&& this.pos[1] == s[1])
		    	this.a(64);
		    
		    // プレイヤーがフルーツを食べる
		    if (!this.ghost && this.pos[0] == v[0]
		        && (this.pos[1] == v[1] || this.pos[1] == v[1] + 8))
		        g.eatFruit(this.id);
		}
	
		// posの値がtilePosと一致(pos が8の倍数)したときに呼び出される
		void u() {
		    this.n();
		    if (this.ghost) this.i(e); // モンスターの交差点/行き止まりでの進行方向決定
		    PathElement b =
		    	g.playfield.get(Integer.valueOf((int) this.pos[0]))
		    				.get(Integer.valueOf((int) this.pos[1]));
		    if (b.intersection) // 行き止まり/交差点にて
		    	if (this.nextDir != 0 && (this.nextDir & b.allowedDir) != 0) { // nextDirで指定された方向へ移動可能
			        if (this.dir != 0) this.lastActiveDir = this.dir;
			        this.dir = this.nextDir;
			        this.nextDir = 0;
			        if (!this.ghost) { // 先行入力された移動方向分を更新(メソッドtを参照)
			        	this.pos[0] += this.posDelta[0];
				        this.pos[1] += this.posDelta[1];
				        this.posDelta = new float[] {0, 0};
			        }
		    } else if ((this.dir & b.allowedDir) == 0) { // nextDirもdirも移動不可だったら、停止
		    	if (this.dir != 0) this.lastActiveDir = this.dir;
		    	this.nextDir = this.dir = 0;
		    	this.c(0);
		    }
		}

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
		    	g.playfield.get(Integer.valueOf(d[0]))
		    				.get(Integer.valueOf(d[1]));
		    if (!this.ghost
		    		&& this.nextDir != 0
		    		&& pe.intersection
		    		&& (this.nextDir & pe.allowedDir) != 0)
		    		this.t();
		}
		// ターゲットポジションを決定
		void B() {
		    if (this.id == g.playerCount
		    		&& g.dotsRemaining < g.levels.elroyDotsLeftPart1
		    		&& this.mode == 2
		    		&& (!g.lostLifeOnThisLevel || g.actors[g.playerCount + 3].mode != 16)) {
		    	E b = g.actors[this.targetPlayerId];
		    	this.targetPos = new float[] { b.tilePos[0], b.tilePos[1] };
		    } else if (this.ghost && this.mode == 1) {
		    	E b = g.actors[this.targetPlayerId];
	    		Direction c = l.get(Integer.valueOf(b.dir));
		    	if (this.id == g.playerCount) {
		    		this.targetPos = new float[] { b.tilePos[0], b.tilePos[1] };
		    	} else if (this.id == g.playerCount + 1) {
		    		this.targetPos = new float[] { b.tilePos[0], b.tilePos[1] };
		    		this.targetPos[c.axis] += 32 * c.increment;
		    		if (b.dir == 1) this.targetPos[1] -= 32;
		    	} else if (this.id == g.playerCount + 2) {
		    		E d = g.actors[g.playerCount];
		    		float[] f = new float[] { b.tilePos[0], b.tilePos[1] };
		    		f[c.axis] += 16 * c.increment;
		    		if (b.dir == 1) f[1] -= 16;
		    		this.targetPos[0] = f[0] * 2 - d.tilePos[0];
		    		this.targetPos[1] = f[1] * 2 - d.tilePos[1];
		    	} else if (this.id == g.playerCount + 3) {
		    		float distance = g.getDistance(b.tilePos, this.tilePos);
		    		this.targetPos = distance > 64 ? new float[] { b.tilePos[0], b.tilePos[1] } : this.scatterPos;
		    	}
		    }
		}
		// モンスターの巣の中/巣から出る挙動を管理(モンスター個別のモード管理)
		void v() {
		    this.routineMoveId++;
		    if (this.routineMoveId == A.get(Integer.valueOf(this.routineToFollow)).length) // ルーチンの最後に到達
		    	if (this.mode == 16 && this.freeToLeavePen && !g.ghostExitingPenNow) { // 外に出る条件が満たされた
		    		if (this.eatenInThisFrightMode) this.a(128);
		    		else this.a(32);
		    		return;
		    	} else if (this.mode == 32 || this.mode == 128) { // 将に外に出むとす
		    	    this.pos = new float[] { s[0], s[1] + 4 };
		    	    this.dir = this.modeChangedWhileInPen ? 8 : 4;
		    	    int b = g.mainGhostMode;
		    	    if (this.mode == 128 && b == 4) b = g.lastMainGhostMode;
		    	    this.a(b);
		    	    return;
		    	} else if (this.mode == 64) { // 食べられて巣に入る
		    	    if (this.id == g.playerCount || this.freeToLeavePen) this.a(128); // アカベエはすぐに巣から出てくる
		    	    else {
		    		    this.eatenInThisFrightMode = a;
  		    		    this.a(16);
		    	    }
		    	    return;
		        } else // 外にでる条件が満たされなければ、ルーチンを繰り返す
		    	    this.routineMoveId = 0;
		
		    MoveInPen mv = A.get(Integer.valueOf(this.routineToFollow))[this.routineMoveId];
		    this.pos[0] = mv.y * 8;
		    this.pos[1] = mv.x * 8;
		    this.dir = mv.dir;
		    this.physicalSpeed = 0;
		    this.speedIntervals = g.getSpeedIntervals(mv.speed);
		    this.proceedToNextRoutineMove = e;
		    this.b();
		}
		// モンスターの巣の中/巣から出る挙動を管理(表示画像決定&位置移動)
		void m() {
			
		    MoveInPen b = null;
		    MoveInPen[] mvs = A.get(Integer.valueOf(this.routineToFollow));
		    
		    if (0 <= this.routineMoveId && this.routineMoveId < mvs.length)
		    	b = A.get(Integer.valueOf(this.routineToFollow))[this.routineMoveId];
		    
		    if (b != null)
		    	if (this.speedIntervals[g.intervalTime]) {
		    		Direction c = l.get(Integer.valueOf(this.dir));
		    		this.pos[c.axis] += c.increment;
			        switch (this.dir) {
			        case 1:
			        case 4:
			        	if (this.pos[c.axis] < b.dest * 8) {
			        		this.pos[c.axis] = b.dest * 8;
			        		this.proceedToNextRoutineMove = a;
			        	}
			            break;
			        case 2:
			        case 8:
			        	if (this.pos[c.axis] > b.dest * 8) {
				            this.pos[c.axis] = b.dest * 8;
				            this.proceedToNextRoutineMove = a;
			        	}
			        	break;
			        }
			        this.b();
		    	}
		}
		// モンスターの巣の中/巣から出る挙動を管理
		void j() {
		    if (this.routineMoveId == -1 || this.proceedToNextRoutineMove)
		    	this.v();
		    
		    this.m();
		}
		// Actorの速度設定(currentSpeedプロパティを利用)
		void d() {
			float b = 0;
		    switch (this.currentSpeed) {
		    case 0:
		    	b = this.id == g.playerCount && (this.mode == 2 || this.mode == 1)
		    			? g.cruiseElroySpeed
		    			: this.fullSpeed;
			    break;
		    case 1:
		    	b = this.dotEatingSpeed;
		    	break;
		    case 2:
		    	b = this.tunnelSpeed;
		    	break;
		    }
		    if (this.physicalSpeed != b) {
		      this.physicalSpeed = b;
		      this.speedIntervals = g.getSpeedIntervals(this.physicalSpeed);
		    }
		}
		// Actorの速度設定変更
		void c(int b) {
		    this.currentSpeed = b;
		    this.d();
		}
		// Actorの移動(ルーチン以外)
		void e() {
		    if (this.dir != 0)
		    	if (this.speedIntervals[g.intervalTime]) { // この判定で速度を表現
		    		Direction b = l.get(Integer.valueOf(this.dir));
		    		this.pos[b.axis] += b.increment;
		    		this.o();
		    		this.b();
		    	}
		}
	
		void move() {
		    if (g.gameplayMode == 0 || this.ghost && g.gameplayMode == 1 && (this.mode == 8 || this.mode == 64)) {
		    	if (this.requestedDir != 0) {
		    		this.z(this.requestedDir);
		    		this.requestedDir = 0;
		    	}
		    	if (this.followingRoutine) {
		    		this.j();
		    		if (this.mode == 64) this.j();
		    	} else {
		    		this.e();
		    		if (this.mode == 8) this.e();
		    	}
		    }
		}
		// 位置移動
		void k() {
		    float b = g.getPlayfieldX(this.pos[1] + this.posDelta[1]);
		    float c = g.getPlayfieldY(this.pos[0] + this.posDelta[0]);
		    if (this.elPos[0] != c || this.elPos[1] != b) {
		    	this.elPos[0] = c;
		    	this.elPos[1] = b;
		    	this.el.left = b;
		    	this.el.top = c;
		    }
		}
		// Pacman, Ms.Pacman表示画像決定(アニメーション対応)
		int[] s() {
		    int b = 0;
		    int c = 0;
		    int d = this.dir;
		    if (d == 0) d = this.lastActiveDir;
		    if (g.gameplayMode == 1 && this.id == g.playerEatingGhostId) { // モンスターを食べたとき。画像なし
		    	b = 3;
		    	c = 0;
		    } else if ((g.gameplayMode == 9 || g.gameplayMode == 10) && this.id == 0) { // レベルクリア。Pacmanは丸まる
		    	b = 2;
		    	c = 0;
		    } else if (g.gameplayMode == 4 || g.gameplayMode == 5 || g.gameplayMode == 7) { // ゲーム開始直後の表示画像決定
		    	b = this.id == 0 ? 2 : 4;
		    	c = 0;
		    } else if (g.gameplayMode == 3) // プレイヤーが死んだ時の画像決定.
		    	if (this.id == g.playerDyingId) { // 死んだ方
		    		d = 20 - (int) FloatMath.floor(g.gameplayModeTime / g.timing[4] * 21);
			        if (this.id == 0) { // Pacman
			        	b = d - 1;
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
			        	switch (d) {
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
		    else if ("pcm-bpcm".equals(this.el.id)) { // Cutscene
		    	b = 14;
		    	c = 0;
		    	d = (int) (Math.floor(g.globalTime * 0.2) % 4);
		    	if (d == 3) d = 1;
		    	c += 2 * d;
		    } else { // 通常時のプレイヤー画像決定
		    	switch (d) {
		    	case 4:
		    		c = 0;
		    		break;
		    	case 8:
		    		c = 1;
		    		break;
		    	case 1:
		    		c = 2;
		    		break;
		    	case 2:
		    		c = 3;
		    		break;
		    	}
		    	if (g.gameplayMode != 2) b = (int) (Math.floor(g.globalTime * 0.3) % 4);
		    	if (b == 3 && this.dir == 0) b = 0;
		    	if (b == 2 && this.id == 0) b = 0;
		    	if (b == 3) {
		    		b = 2;
		    		if (this.id == 0) c = 0;
		    	}
		    	if (this.id == 1) b += 4;
		    }
		    return new int[] { c, b };
		}
		// モンスターの表示画像決定
		int[] r() {
		    int b = 0;
		    int c = 0;
		    if (g.gameplayMode == 10 || g.gameplayMode == 4 || g.gameplayMode == 3) {
		    	// Pacman or Ms.Pacmanが死んだ直後。モンスターの姿は消える 
		    	b = 3;
		    	c = 0;
		    } else if (g.gameplayMode == 1 && this.id == g.ghostBeingEatenId) {
		    	switch (g.modeScoreMultiplier) {// モンスターが食べられたときに表示させるスコアを決定
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
//		      	this.el.className = "pcm-ac pcm-n"
		    } else if (this.mode == 4
		              || (this.mode == 16 || this.mode == 32)
		                  && g.mainGhostMode == 4
		                  && !this.eatenInThisFrightMode) {
		    	// ブルーモード.ただし、食べられてはいない
		    	b = 0;
		    	c = 8;
		    	// ブルーモード時間切れ間近の青白明滅
		    	if (g.frightModeTime < g.levels.frightTotalTime - g.levels.frightTime
		    			&& FloatMath.floor(g.frightModeTime / g.timing[1]) % 2 == 0)
		    		b += 2;
		
		    	b += (int) (Math.floor(g.globalTime / 16) % 2); // ブルーモードの画像切り替え
		    } else if (this.mode == 8 || this.mode == 64) { // 食べられて目玉だけ
		    	int ndir = this.nextDir;
		    	if (ndir != 0) ndir = this.dir;
		    	switch (ndir) {
		    	case 4:
		    		b = 2;
		    		break;
		    	case 8:
		    		b = 3;
		    		break;
		    	case 1:
		    		b = 0;
		    		break;
		    	case 2:
		    		b = 1;
		    		break;
		    	}
		    	c = 10;
		    } else if ("pcm-ghin".equals(this.el.id)) {
		    	b = 6;
		    	c = 8;
		    	b += (int) (Math.floor(g.globalTime / 16) % 2);
		    } else if ("pcm-gbug".equals(this.el.id)) {
		    	b = 6;
		    	c = 9;
		    	c += (int) (Math.floor(g.globalTime / 16) % 2);
		    } else if ("pcm-ghfa".equals(this.el.id)) {
		    	b = g.cutsceneSequenceId == 3 ? 6 : 7;
		    	c = 11;
		    } else if ("pcm-stck".equals(this.el.id)) {
		    	b = g.cutsceneSequenceId == 1
		            ? g.cutsceneTime > 60
		                ? 1
		                : g.cutsceneTime > 45
		                    ? 2
		                    : 3
		            : g.cutsceneSequenceId == 2
		                ? 3
		                : g.cutsceneSequenceId == 3 || g.cutsceneSequenceId == 4
		                    ? 4
		                    : 0;
		        c = 13;
		    } else { // 通常時の画像表示
		    	int ndir = this.nextDir;
		    	if (ndir == 0
		    		|| g.playfield.get(Integer.valueOf(this.tilePos[0]))
		    						.get(Integer.valueOf(this.tilePos[1]))
		    						.tunnel)
		    		ndir = this.dir;
		    	
  			        switch (ndir) {
  			        case 4:
  			        	b = 4;
  			        	break;
  			        case 8:
  			        	b = 6;
  			        	break;
  			        case 1:
  			        	b = 0;
  			        	break;
  			        case 2:
  			        	b = 2;
  			        	break;
  			        }
  			        c = 4 + this.id - g.playerCount;
  			        if (this.speed > 0 || g.gameplayMode != 13)
  			        	b += (int) (Math.floor(g.globalTime / 16) % 2);
		    }
		    return new int[] { c, b };
		}
  
		// Actor表示画像切り替え(アニメーション対応)&位置移動
		void b() {
		    this.k(); //位置移動 
		    int[] b = { 0, 0 };
		    b = g.gameplayMode == 8 || g.gameplayMode == 14
		    		? new int[] { 0, 3 }
		    		: this.ghost
		    			? this.r()
		    			: this.s();
		    if (this.elBackgroundPos[0] != b[0] || this.elBackgroundPos[1] != b[1]) {
		    	this.elBackgroundPos[0] = b[0];
		    	this.elBackgroundPos[1] = b[1];
		    	b[0] *= 16;
		    	b[1] *= 16;
		    	g.changeElementBkPos(this.el, b[1], b[0], a);
		    }
		}
		
		void draw(Bitmap sourceImage, Canvas c) {
			if (!el.visibility) return;

			if ("pcm-bpcm".equals(el.id)) {
				el.width = 32;
				el.height = 32;
				el.left -= 20;
				el.top -= 20;
				el.drawBitmap(sourceImage, c);
				el.left += 20;
				el.top += 20;
			} else {
				// TODO: Margin処理をきちんと実装する
				el.left -= 4;
				el.top -= 4;
				el.drawBitmap(sourceImage, c);
				el.left += 4;
				el.top += 4;				
			}
		}
	}
    
    static class Game {

        // レベル再開後、一定数のエサが食べられるとモンスターが巣から出てくる
        // そのしきい値をモンスター毎に設定
        private static final int[] m = {0, 7, 17, 32};

        // イベント時間管理テーブル. index 7, 8しか使わない
        private static final float[] w = {
    	    0.16f,
    	    0.23f,
    	    1,
    	    1,
    	    2.23f,
    	    0.3f,
    	    1.9f,
    	    2.23f,
    	    1.9f,
    	    5,
    	    1.9f,
    	    1.18f,
    	    0.3f,
    	    0.5f,
    	    1.9f,
    	    9,
    	    10,
    	    0.26f
        };

        // パスの配列.左上:(5, 1), 左下:(5, 15), 右上:(60, 1), 右下:(60, 15).
        // 配列要素のオブジェクトのプロパティは(x, y, w) もしくは(x, y, h)
        // 要素のオブジェクトにwプロパティがあり:横方向, 要素のオブジェクトにhプロパティがあり:縦方向
        // x, yはパスの始点の座標
        // h, wは各々パスの長さを表現
        // [例外] typeプロパティを値1でもつパスはワープつき
        private static class Path {
        	final int x;
        	final int y;
        	final int w;
        	final int h;
        	final boolean tunnel;
        	
        	private Path(int x, int y, int w, int h) {
        		this(x, y, w, h, false);
        	}
        	private Path(int x, int y, int w, int h, boolean tunnel) {
        		this.x = x;
        		this.y = y;
        		this.w = w;
        		this.h = h;
        		this.tunnel = tunnel;
        	}

        	static Path createHorizontalPath(int x, int y, int w) {
        		return new Path(x, y, w, 0);
        	}
        	static Path createVerticalPath(int x, int y, int h) {
        		return new Path(x, y, 0, h);
        	}
        	static Path createTunnelPath(int x, int y, int w) {
        		return new Path(x, y, w, 0, true);
        	}
        }

        private static final Path[] n = {
        	Path.createHorizontalPath(5, 1, 56),
        	Path.createHorizontalPath(5, 4, 5),
        	Path.createVerticalPath(5, 1, 4),
        	Path.createVerticalPath(9, 1, 12),
        	Path.createVerticalPath(5, 12, 4),
        	Path.createVerticalPath(10, 12, 4),
        	Path.createHorizontalPath(5, 15, 16),
        	Path.createHorizontalPath(5, 12, 31),
        	Path.createVerticalPath(60, 1, 4),
        	Path.createVerticalPath(54, 1, 4),
        	Path.createVerticalPath(19, 1, 12),
        	Path.createHorizontalPath(19, 4, 26),
        	Path.createHorizontalPath(13, 5, 7),
        	Path.createVerticalPath(13, 5, 4),
        	Path.createHorizontalPath(13, 8, 3),
        	Path.createVerticalPath(56, 4, 9),
        	Path.createHorizontalPath(48, 4, 13),
        	Path.createVerticalPath(48, 1, 12),
        	Path.createVerticalPath(60, 12, 4),
        	Path.createHorizontalPath(44, 15, 17),
        	Path.createVerticalPath(54, 12, 4),
        	Path.createHorizontalPath(44, 12, 17),
        	Path.createVerticalPath(44, 1, 15),
        	Path.createHorizontalPath(41, 13, 4),
        	Path.createVerticalPath(41, 13, 3),
        	Path.createVerticalPath(38, 13, 3),
        	Path.createHorizontalPath(38, 15, 4),
        	Path.createHorizontalPath(35, 10, 10),
        	Path.createVerticalPath(35, 1, 15),
        	Path.createHorizontalPath(35, 13, 4),
        	Path.createVerticalPath(21, 12, 4),
        	Path.createVerticalPath(24, 12, 4),
        	Path.createHorizontalPath(24, 15, 12),
        	Path.createVerticalPath(27, 4, 9),
        	Path.createHorizontalPath(52, 9, 5),
        	Path.createTunnelPath(56, 8, 10),
        	Path.createTunnelPath(1, 8, 9),
        };
        
        // エサの存在しないパス
        // 左上:(5, 1), 左下:(5, 15), 右上:(60, 1), 右下:(60, 15).
        private static final Path[] o = {
        	Path.createHorizontalPath(1, 8, 8),
        	Path.createHorizontalPath(57, 8, 9),
        	Path.createVerticalPath(44, 2, 10),
        	Path.createVerticalPath(35, 5, 7),
        	Path.createHorizontalPath(36, 4, 8),
        	Path.createHorizontalPath(36, 10, 8),
        	Path.createHorizontalPath(39, 15, 2),
        };

        private static class Position {
        	final int x;
        	final int y;
        	Position(int x, int y) {
        		this.x = x;
        		this.y = y;
        	}
        }

        // パワーエサ
        private static final Position[] p = {
        	new Position(5, 15),
        	new Position(5, 3),
        	new Position(15, 8),
        	new Position(60, 3),
        	new Position(60, 15),
        };
        
        // ワープトンネル
        private static final Position[] q = {
        	new Position(2, 8),
        	new Position(63, 8),
        };

        private static class LevelConfig {
            private final float ghostSpeed;
            private final float ghostTunnelSpeed;
            private final float playerSpeed;
            private final float dotEatingSpeed;
            private final float ghostFrightSpeed;
            private final float playerFrightSpeed;
            private final float dotEatingFrightSpeed;
            private final int elroyDotsLeftPart1;
            private final float elroySpeedPart1;
            private final int elroyDotsLeftPart2;
            private final float elroySpeedPart2;
            private int frightTime;
            private int frightTotalTime;
            private final int frightBlinkCount;
            private final int fruit;
            private final int fruitScore;
            private final float[] ghostModeSwitchTimes;
            private final int penForceTime;
            private final float[] penLeavingLimits;
            private final int cutsceneId;
        	
        	private static class Builder {
                private float ghostSpeed;
                private float ghostTunnelSpeed;
                private float playerSpeed;
                private float dotEatingSpeed;
                private float ghostFrightSpeed;
                private float playerFrightSpeed;
                private float dotEatingFrightSpeed;
                private int elroyDotsLeftPart1;
                private float elroySpeedPart1;
                private int elroyDotsLeftPart2;
                private float elroySpeedPart2;
                private int frightTime;
                private int frightTotalTime;
                private int frightBlinkCount;
                private int fruit;
                private int fruitScore;
                private float[] ghostModeSwitchTimes;
                private int penForceTime;
                private float[] penLeavingLimits;
                private int cutsceneId;
                
                Builder ghostSpeed(float val) {
                	this.ghostSpeed = val;
                	return this;
                }

                Builder ghostTunnelSpeed(float val) {
                	this.ghostTunnelSpeed = val;
                	return this;
                }
                
                Builder playerSpeed(float val) {
                	this.playerSpeed = val;
                	return this;
                }
                
                Builder dotEatingSpeed(float val) {
                	this.dotEatingSpeed = val;
                	return this;
                }
                
                Builder ghostFrightSpeed(float val) {
                	this.ghostFrightSpeed = val;
                	return this;
                }
                
                Builder playerFrightSpeed(float val) {
                	this.playerFrightSpeed = val;
                	return this;
                }
                
                Builder dotEatingFrightSpeed(float val) {
                	this.dotEatingFrightSpeed = val;
                	return this;
                }
                
                Builder elroyDotsLeftPart1(int val) {
                	this.elroyDotsLeftPart1 = val;
                	return this;
                }
                
                Builder elroySpeedPart1(float val) {
                	this.elroySpeedPart1 = val;
                	return this;
                }
                
                Builder elroyDotsLeftPart2(int val) {
                	this.elroyDotsLeftPart2 = val;
                	return this;
                }
                
                Builder elroySpeedPart2(float val) {
                	this.elroySpeedPart2 = val;
                	return this;
                }
                
                Builder frightTime(int val) {
                	this.frightTime = val;
                	return this;
                }

                Builder frightTotalTime(int val) {
                	this.frightTotalTime = val;
                	return this;
                }

                Builder frightBlinkCount(int val) {
                	this.frightBlinkCount = val;
                	return this;
                }
                
                Builder fruit(int val) {
                	this.fruit = val;
                	return this;
                }
                
                Builder fruitScore(int val) {
                	this.fruitScore = val;
                	return this;
                }

                Builder ghostModeSwitchTimes(float[] val) {
                	this.ghostModeSwitchTimes = val;
                	return this;
                }

                Builder penForceTime(int val) {
                	this.penForceTime = val;
                	return this;
                }
                
                Builder penLeavingLimits(float[] val) {
                	this.penLeavingLimits = val;
                	return this;
                }

                Builder cutsceneId(int val) {
                	this.cutsceneId = val;
                	return this;
                }
                
                LevelConfig build() {
                	return new LevelConfig(this);
                }
        	}
        	
        	private LevelConfig(Builder builder) {
                this.ghostSpeed = builder.ghostSpeed;
                this.ghostTunnelSpeed = builder.ghostTunnelSpeed;
                this.playerSpeed = builder.playerSpeed;
                this.dotEatingSpeed = builder.dotEatingSpeed;
                this.ghostFrightSpeed = builder.ghostFrightSpeed;
                this.playerFrightSpeed = builder.playerFrightSpeed;
                this.dotEatingFrightSpeed = builder.dotEatingFrightSpeed;
                this.elroyDotsLeftPart1 = builder.elroyDotsLeftPart1;
                this.elroySpeedPart1 = builder.elroySpeedPart1;
                this.elroyDotsLeftPart2 = builder.elroyDotsLeftPart2;
                this.elroySpeedPart2 = builder.elroySpeedPart2;
                this.frightTime = builder.frightTime;
                this.frightTotalTime = builder.frightTotalTime;
                this.frightBlinkCount = builder.frightBlinkCount;
                this.fruit = builder.fruit;
                this.fruitScore = builder.fruitScore;
                this.ghostModeSwitchTimes = builder.ghostModeSwitchTimes;
                this.penForceTime = builder.penForceTime;
                this.penLeavingLimits = builder.penLeavingLimits;
                this.cutsceneId = builder.cutsceneId;
        	}
        }

        // ゲームレベル毎の設定
        private static final LevelConfig[] z = {
        	new LevelConfig.Builder().build(),
        	new LevelConfig.Builder()
        			.ghostSpeed(0.75f)
        			.ghostTunnelSpeed(0.4f)
        			.playerSpeed(0.8f)
        			.dotEatingSpeed(0.71f)
        			.ghostFrightSpeed(0.5f)
        			.playerFrightSpeed(0.9f)
        			.dotEatingFrightSpeed(0.79f)
        			.elroyDotsLeftPart1(20)
        			.elroySpeedPart1(0.8f)
        			.elroyDotsLeftPart2(10)
        			.elroySpeedPart2(0.85f)
        			.frightTime(6)
        			.frightBlinkCount(5)
        			.fruit(1)
        			.fruitScore(100)
        			.ghostModeSwitchTimes(new float[] { 7, 20, 7, 20, 5, 20, 5, 1, })
        			.penForceTime(4)
        			.penLeavingLimits(new float[] { 0, 0, 30, 60, })
        			.build(),
        	new LevelConfig.Builder()
    				.ghostSpeed(0.85f)
    				.ghostTunnelSpeed(0.45f)
    				.playerSpeed(0.9f)
    				.dotEatingSpeed(0.79f)
    				.ghostFrightSpeed(0.55f)
    				.playerFrightSpeed(0.95f)
    				.dotEatingFrightSpeed(0.83f)
    				.elroyDotsLeftPart1(30)
    				.elroySpeedPart1(0.9f)
    				.elroyDotsLeftPart2(15)
    				.elroySpeedPart2(0.95f)
    				.frightTime(5)
    				.frightBlinkCount(5)
    				.fruit(2)
    				.fruitScore(300)
    				.ghostModeSwitchTimes(new float[] { 7, 20, 7, 20, 5, 1033, 1f / 60, 1, })
    				.penForceTime(4)
    				.penLeavingLimits(new float[] { 0, 0, 0, 50, })
    				.cutsceneId(1)
    				.build(),
        	new LevelConfig.Builder()
    				.ghostSpeed(0.85f)
    				.ghostTunnelSpeed(0.45f)
    				.playerSpeed(0.9f)
    				.dotEatingSpeed(0.79f)
    				.ghostFrightSpeed(0.55f)
    				.playerFrightSpeed(0.95f)
    				.dotEatingFrightSpeed(0.83f)
    				.elroyDotsLeftPart1(40)
    				.elroySpeedPart1(0.9f)
    				.elroyDotsLeftPart2(20)
    				.elroySpeedPart2(0.95f)
    				.frightTime(4)
    				.frightBlinkCount(5)
    				.fruit(3)
    				.fruitScore(500)
    				.ghostModeSwitchTimes(new float[] { 7, 20, 7, 20, 5, 1033, 1f / 60, 1, })
    				.penForceTime(4)
    				.penLeavingLimits(new float[] { 0, 0, 0, 0, })
    				.build(),    			
        	new LevelConfig.Builder()
    				.ghostSpeed(0.85f)
    				.ghostTunnelSpeed(0.45f)
    				.playerSpeed(0.9f)
    				.dotEatingSpeed(0.79f)
    				.ghostFrightSpeed(0.55f)
    				.playerFrightSpeed(0.95f)
    				.dotEatingFrightSpeed(0.83f)
    				.elroyDotsLeftPart1(40)
    				.elroySpeedPart1(0.9f)
    				.elroyDotsLeftPart2(20)
    				.elroySpeedPart2(0.95f)
    				.frightTime(3)
    				.frightBlinkCount(5)
    				.fruit(3)
    				.fruitScore(500)
    				.ghostModeSwitchTimes(new float[] { 7, 20, 7, 20, 5, 1033, 1f / 60, 1, })
    				.penForceTime(4)
    				.penLeavingLimits(new float[] { 0, 0, 0, 0, })
    				.build(),    			
        	new LevelConfig.Builder()
    				.ghostSpeed(0.95f)
    				.ghostTunnelSpeed(0.5f)
    				.playerSpeed(1)
    				.dotEatingSpeed(0.87f)
    				.ghostFrightSpeed(0.6f)
    				.playerFrightSpeed(1)
    				.dotEatingFrightSpeed(0.87f)
    				.elroyDotsLeftPart1(40)
    				.elroySpeedPart1(1)
    				.elroyDotsLeftPart2(20)
    				.elroySpeedPart2(1.05f)
    				.frightTime(2)
    				.frightBlinkCount(5)
    				.fruit(4)
    				.fruitScore(700)
    				.ghostModeSwitchTimes(new float[] { 5, 20, 5, 20, 5, 1037, 1f / 60, 1, })
    				.penForceTime(3)
    				.penLeavingLimits(new float[] { 0, 0, 0, 0, })
    				.cutsceneId(2)
    				.build(),    			
        	new LevelConfig.Builder()
    				.ghostSpeed(0.95f)
    				.ghostTunnelSpeed(0.5f)
    				.playerSpeed(1)
    				.dotEatingSpeed(0.87f)
    				.ghostFrightSpeed(0.6f)
    				.playerFrightSpeed(1)
    				.dotEatingFrightSpeed(0.87f)
    				.elroyDotsLeftPart1(50)
    				.elroySpeedPart1(1)
    				.elroyDotsLeftPart2(25)
    				.elroySpeedPart2(1.05f)
    				.frightTime(5)
    				.frightBlinkCount(5)
    				.fruit(4)
    				.fruitScore(700)
    				.ghostModeSwitchTimes(new float[] { 5, 20, 5, 20, 5, 1037, 1f / 60, 1, })
    				.penForceTime(3)
    				.penLeavingLimits(new float[] { 0, 0, 0, 0, })
    				.build(),    			
        	new LevelConfig.Builder()
    				.ghostSpeed(0.95f)
    				.ghostTunnelSpeed(0.5f)
    				.playerSpeed(1)
    				.dotEatingSpeed(0.87f)
    				.ghostFrightSpeed(0.6f)
    				.playerFrightSpeed(1)
    				.dotEatingFrightSpeed(0.87f)
    				.elroyDotsLeftPart1(50)
    				.elroySpeedPart1(1)
    				.elroyDotsLeftPart2(25)
    				.elroySpeedPart2(1.05f)
    				.frightTime(2)
    				.frightBlinkCount(5)
    				.fruit(5)
    				.fruitScore(1000)
    				.ghostModeSwitchTimes(new float[] { 5, 20, 5, 20, 5, 1037, 1f / 60, 1, })
    				.penForceTime(3)
    				.penLeavingLimits(new float[] { 0, 0, 0, 0, })
    				.build(),    			
        	new LevelConfig.Builder()
    				.ghostSpeed(0.95f)
    				.ghostTunnelSpeed(0.5f)
    				.playerSpeed(1)
    				.dotEatingSpeed(0.87f)
    				.ghostFrightSpeed(0.6f)
    				.playerFrightSpeed(1)
    				.dotEatingFrightSpeed(0.87f)
    				.elroyDotsLeftPart1(50)
    				.elroySpeedPart1(1)
    				.elroyDotsLeftPart2(25)
    				.elroySpeedPart2(1.05f)
    				.frightTime(2)
    				.frightBlinkCount(5)
    				.fruit(5)
    				.fruitScore(1000)
    				.ghostModeSwitchTimes(new float[] { 5, 20, 5, 20, 5, 1037, 1f / 60, 1, })
    				.penForceTime(3)
    				.penLeavingLimits(new float[] { 0, 0, 0, 0, })
    				.build(),    			
        	new LevelConfig.Builder()
    				.ghostSpeed(0.95f)
    				.ghostTunnelSpeed(0.5f)
    				.playerSpeed(1)
    				.dotEatingSpeed(0.87f)
    				.ghostFrightSpeed(0.6f)
    				.playerFrightSpeed(1)
    				.dotEatingFrightSpeed(0.87f)
    				.elroyDotsLeftPart1(60)
    				.elroySpeedPart1(1)
    				.elroyDotsLeftPart2(30)
    				.elroySpeedPart2(1.05f)
    				.frightTime(1)
    				.frightBlinkCount(3)
    				.fruit(6)
    				.fruitScore(2000)
    				.ghostModeSwitchTimes(new float[] { 5, 20, 5, 20, 5, 1037, 1f / 60, 1, })
    				.penForceTime(3)
    				.penLeavingLimits(new float[] { 0, 0, 0, 0, })
    				.cutsceneId(3)
    				.build(),    			
        	new LevelConfig.Builder()
    				.ghostSpeed(0.95f)
    				.ghostTunnelSpeed(0.5f)
    				.playerSpeed(1)
    				.dotEatingSpeed(0.87f)
    				.ghostFrightSpeed(0.6f)
    				.playerFrightSpeed(1)
    				.dotEatingFrightSpeed(0.87f)
    				.elroyDotsLeftPart1(60)
    				.elroySpeedPart1(1)
    				.elroyDotsLeftPart2(30)
    				.elroySpeedPart2(1.05f)
    				.frightTime(5)
    				.frightBlinkCount(5)
    				.fruit(6)
    				.fruitScore(2000)
    				.ghostModeSwitchTimes(new float[] { 5, 20, 5, 20, 5, 1037, 1f / 60, 1, })
    				.penForceTime(3)
    				.penLeavingLimits(new float[] { 0, 0, 0, 0, })
    				.build(),    			
        	new LevelConfig.Builder()
    				.ghostSpeed(0.95f)
    				.ghostTunnelSpeed(0.5f)
    				.playerSpeed(1)
    				.dotEatingSpeed(0.87f)
    				.ghostFrightSpeed(0.6f)
    				.playerFrightSpeed(1)
    				.dotEatingFrightSpeed(0.87f)
    				.elroyDotsLeftPart1(60)
    				.elroySpeedPart1(1)
    				.elroyDotsLeftPart2(30)
    				.elroySpeedPart2(1.05f)
    				.frightTime(2)
    				.frightBlinkCount(5)
    				.fruit(7)
    				.fruitScore(3000)
    				.ghostModeSwitchTimes(new float[] { 5, 20, 5, 20, 5, 1037, 1f / 60, 1, })
    				.penForceTime(3)
    				.penLeavingLimits(new float[] { 0, 0, 0, 0, })
    				.build(),    			
        	new LevelConfig.Builder()
    				.ghostSpeed(0.95f)
    				.ghostTunnelSpeed(0.5f)
    				.playerSpeed(1)
    				.dotEatingSpeed(0.87f)
    				.ghostFrightSpeed(0.6f)
    				.playerFrightSpeed(1)
    				.dotEatingFrightSpeed(0.87f)
    				.elroyDotsLeftPart1(80)
    				.elroySpeedPart1(1)
    				.elroyDotsLeftPart2(40)
    				.elroySpeedPart2(1.05f)
    				.frightTime(1)
    				.frightBlinkCount(3)
    				.fruit(7)
    				.fruitScore(3000)
    				.ghostModeSwitchTimes(new float[] { 5, 20, 5, 20, 5, 1037, 1f / 60, 1, })
    				.penForceTime(3)
    				.penLeavingLimits(new float[] { 0, 0, 0, 0, })
    				.build(),    			
        	new LevelConfig.Builder()
    				.ghostSpeed(0.95f)
    				.ghostTunnelSpeed(0.5f)
    				.playerSpeed(1)
    				.dotEatingSpeed(0.87f)
    				.ghostFrightSpeed(0.6f)
    				.playerFrightSpeed(1)
    				.dotEatingFrightSpeed(0.87f)
    				.elroyDotsLeftPart1(80)
    				.elroySpeedPart1(1)
    				.elroyDotsLeftPart2(40)
    				.elroySpeedPart2(1.05f)
    				.frightTime(1)
    				.frightBlinkCount(3)
    				.fruit(8)
    				.fruitScore(5000)
    				.ghostModeSwitchTimes(new float[] { 5, 20, 5, 20, 5, 1037, 1f / 60, 1, })
    				.penForceTime(3)
    				.penLeavingLimits(new float[] { 0, 0, 0, 0, })
    				.cutsceneId(3)
    				.build(),    			
        	new LevelConfig.Builder()
    				.ghostSpeed(0.95f)
    				.ghostTunnelSpeed(0.5f)
    				.playerSpeed(1)
    				.dotEatingSpeed(0.87f)
    				.ghostFrightSpeed(0.6f)
    				.playerFrightSpeed(1)
    				.dotEatingFrightSpeed(0.87f)
    				.elroyDotsLeftPart1(80)
    				.elroySpeedPart1(1)
    				.elroyDotsLeftPart2(40)
    				.elroySpeedPart2(1.05f)
    				.frightTime(3)
    				.frightBlinkCount(5)
    				.fruit(8)
    				.fruitScore(5000)
    				.ghostModeSwitchTimes(new float[] { 5, 20, 5, 20, 5, 1037, 1f / 60, 1, })
    				.penForceTime(3)
    				.penLeavingLimits(new float[] { 0, 0, 0, 0, })
    				.build(),    			
        	new LevelConfig.Builder()
    				.ghostSpeed(0.95f)
    				.ghostTunnelSpeed(0.5f)
    				.playerSpeed(1)
    				.dotEatingSpeed(0.87f)
    				.ghostFrightSpeed(0.6f)
    				.playerFrightSpeed(1)
    				.dotEatingFrightSpeed(0.87f)
    				.elroyDotsLeftPart1(100)
    				.elroySpeedPart1(1)
    				.elroyDotsLeftPart2(50)
    				.elroySpeedPart2(1.05f)
    				.frightTime(1)
    				.frightBlinkCount(3)
    				.fruit(8)
    				.fruitScore(5000)
    				.ghostModeSwitchTimes(new float[] { 5, 20, 5, 20, 5, 1037, 1f / 60, 1, })
    				.penForceTime(3)
    				.penLeavingLimits(new float[] { 0, 0, 0, 0, })
    				.build(),    			
        	new LevelConfig.Builder()
    				.ghostSpeed(0.95f)
    				.ghostTunnelSpeed(0.5f)
    				.playerSpeed(1)
    				.dotEatingSpeed(0.87f)
    				.ghostFrightSpeed(0.6f)
    				.playerFrightSpeed(1)
    				.dotEatingFrightSpeed(0.87f)
    				.elroyDotsLeftPart1(100)
    				.elroySpeedPart1(1)
    				.elroyDotsLeftPart2(50)
    				.elroySpeedPart2(1.05f)
    				.frightTime(1)
    				.frightBlinkCount(3)
    				.fruit(8)
    				.fruitScore(5000)
    				.ghostModeSwitchTimes(new float[] { 5, 20, 5, 20, 5, 1037, 1f / 60, 1, })
    				.penForceTime(3)
    				.penLeavingLimits(new float[] { 0, 0, 0, 0, })
    				.build(),    			
        	new LevelConfig.Builder()
    				.ghostSpeed(0.95f)
    				.ghostTunnelSpeed(0.5f)
    				.playerSpeed(1)
    				.dotEatingSpeed(0.87f)
    				.ghostFrightSpeed(0.6f)
    				.playerFrightSpeed(1)
    				.dotEatingFrightSpeed(0.87f)
    				.elroyDotsLeftPart1(100)
    				.elroySpeedPart1(1)
    				.elroyDotsLeftPart2(50)
    				.elroySpeedPart2(1.05f)
    				.frightTime(0)
    				.frightBlinkCount(0)
    				.fruit(8)
    				.fruitScore(5000)
    				.ghostModeSwitchTimes(new float[] { 5, 20, 5, 20, 5, 1037, 1f / 60, 1, })
    				.penForceTime(3)
    				.penLeavingLimits(new float[] { 0, 0, 0, 0, })
    				.cutsceneId(3)
    				.build(),    			
        	new LevelConfig.Builder()
    				.ghostSpeed(0.95f)
    				.ghostTunnelSpeed(0.5f)
    				.playerSpeed(1)
    				.dotEatingSpeed(0.87f)
    				.ghostFrightSpeed(0.6f)
    				.playerFrightSpeed(1)
    				.dotEatingFrightSpeed(0.87f)
    				.elroyDotsLeftPart1(100)
    				.elroySpeedPart1(1)
    				.elroyDotsLeftPart2(50)
    				.elroySpeedPart2(1.05f)
    				.frightTime(1)
    				.frightBlinkCount(3)
    				.fruit(8)
    				.fruitScore(5000)
    				.ghostModeSwitchTimes(new float[] { 5, 20, 5, 20, 5, 1037, 1f / 60, 1, })
    				.penForceTime(3)
    				.penLeavingLimits(new float[] { 0, 0, 0, 0, })
    				.build(),
        	new LevelConfig.Builder()
    				.ghostSpeed(0.95f)
    				.ghostTunnelSpeed(0.5f)
    				.playerSpeed(1)
    				.dotEatingSpeed(0.87f)
    				.ghostFrightSpeed(0.6f)
    				.playerFrightSpeed(1)
    				.dotEatingFrightSpeed(0.87f)
    				.elroyDotsLeftPart1(120)
    				.elroySpeedPart1(1)
    				.elroyDotsLeftPart2(60)
    				.elroySpeedPart2(1.05f)
    				.frightTime(0)
    				.frightBlinkCount(0)
    				.fruit(8)
    				.fruitScore(5000)
    				.ghostModeSwitchTimes(new float[] { 5, 20, 5, 20, 5, 1037, 1f / 60, 1, })
    				.penForceTime(3)
    				.penLeavingLimits(new float[] { 0, 0, 0, 0, })
    				.build(),		
        	new LevelConfig.Builder()
    				.ghostSpeed(0.95f)
    				.ghostTunnelSpeed(0.5f)
    				.playerSpeed(1)
    				.dotEatingSpeed(0.87f)
    				.ghostFrightSpeed(0.6f)
    				.playerFrightSpeed(1)
    				.dotEatingFrightSpeed(0.87f)
    				.elroyDotsLeftPart1(120)
    				.elroySpeedPart1(1)
    				.elroyDotsLeftPart2(60)
    				.elroySpeedPart2(1.05f)
    				.frightTime(0)
    				.frightBlinkCount(0)
    				.fruit(8)
    				.fruitScore(5000)
    				.ghostModeSwitchTimes(new float[] { 5, 20, 5, 20, 5, 1037, 1f / 60, 1, })
    				.penForceTime(3)
    				.penLeavingLimits(new float[] { 0, 0, 0, 0, })
    				.build(),		
        	new LevelConfig.Builder()
    				.ghostSpeed(0.95f)
    				.ghostTunnelSpeed(0.5f)
    				.playerSpeed(0.9f)
    				.dotEatingSpeed(0.79f)
    				.ghostFrightSpeed(0.75f)
    				.playerFrightSpeed(0.9f)
    				.dotEatingFrightSpeed(0.79f)
    				.elroyDotsLeftPart1(120)
    				.elroySpeedPart1(1)
    				.elroyDotsLeftPart2(60)
    				.elroySpeedPart2(1.05f)
    				.frightTime(0)
    				.frightBlinkCount(0)
    				.fruit(8)
    				.fruitScore(5000)
    				.ghostModeSwitchTimes(new float[] { 5, 20, 5, 20, 5, 1037, 1f / 60, 1, })
    				.penForceTime(3)
    				.penLeavingLimits(new float[] { 0, 0, 0, 0, })
    				.build(),
        };

        // Cutscene Animation
        private static class Cutscene {
        	final CutsceneActor[] actors;
        	final CutsceneSequence[] sequence;
        	Cutscene(CutsceneActor[] actors, CutsceneSequence[] sequence) {
        		this.actors = actors;
        		this.sequence = sequence;
        	}
        }
        private static class CutsceneActor {
        	final boolean ghost;
        	final float x;
        	final float y;
        	final int id;
        	CutsceneActor(boolean ghost, float x, float y, int id) {
        		this.ghost = ghost;
        		this.x = x;
        		this.y = y;
        		this.id = id;
        	}
        }
        private static class CutsceneSequence {
        	final float time;
        	final MoveInCutscene[] moves;
        	CutsceneSequence(float time, MoveInCutscene[] moves) {
        		this.time = time;
        		this.moves = moves;
        	}
        }
        private static class MoveInCutscene {
        	final int dir;
        	final float speed;
        	final String elId;
        	final int mode;
        	MoveInCutscene(int dir, float speed) {
        		this(dir, speed, null, -1);
    		}
        	MoveInCutscene(int dir, float speed, String elId) {
        		this(dir, speed, elId, -1);
        	}
        	MoveInCutscene(int dir, float speed, int mode) {
        		this(dir, speed, null, mode);
        	}
        	MoveInCutscene(int dir, float speed, String elId, int mode) {
        		this.dir = dir;
        		this.speed = speed;
        		this.elId = elId;
        		this.mode = mode;
    		}
        }

        private static final Map<Integer, Cutscene> B;
        static {
        	Map<Integer, Cutscene> css = new HashMap<Integer, Cutscene>();
        	css.put(
        		Integer.valueOf(1),
        		new Cutscene(
        			new CutsceneActor[] {
        				new CutsceneActor(false, 64, 9, 0),
        				new CutsceneActor(true, 68.2f, 9, 1),
        			},
        			new CutsceneSequence[] {
        				new CutsceneSequence(
        					5.5f,
    	    				new MoveInCutscene[] {
    	        				new MoveInCutscene(4, 0.75f * 0.8f * 2),
    	    					new MoveInCutscene(4, 0.78f * 0.8f * 2),
        					}),
        				new CutsceneSequence(
        					0.1f,
    	    				new MoveInCutscene[] {
    	        				new MoveInCutscene(4, 32),
    	    					new MoveInCutscene(4, 0),
        					}),
        				new CutsceneSequence(
           					9,
       	    				new MoveInCutscene[] {
       	        				new MoveInCutscene(8, 0.75f * 0.8f * 2, "pcm-bpcm"),
       	    					new MoveInCutscene(8, 0.8f, 4),
           					}),
        			}));
        	
        	css.put(
        		Integer.valueOf(2),
        		new Cutscene(
        			new CutsceneActor[] {
        				new CutsceneActor(false, 64, 9, 0),
        				new CutsceneActor(true, 70.2f, 9, 1),
        				new CutsceneActor(true, 32, 9.5f, 2),
        			},
        			new CutsceneSequence[] {
        				new CutsceneSequence(
        					2.7f,
        					new MoveInCutscene[] {
        						new MoveInCutscene(4, 0.75f * 0.8f * 2),
        						new MoveInCutscene(4, 0.78f * 0.8f * 2),
        						new MoveInCutscene(0, 0, "pcm-stck"),
        					}),
           				new CutsceneSequence(
           					1,
           					new MoveInCutscene[] {
           						new MoveInCutscene(4, 0.75f * 0.8f * 2),
           						new MoveInCutscene(4, 0.1f * 0.8f),
           						new MoveInCutscene(0, 0, "pcm-stck"),
           					}),
           				new CutsceneSequence(
           					1.3f,
           					new MoveInCutscene[] {
           						new MoveInCutscene(4, 0.75f * 0.8f * 2),
           						new MoveInCutscene(4, 0),
           						new MoveInCutscene(0, 0, "pcm-stck"),
           					}),
           				new CutsceneSequence(
           					1,
           					new MoveInCutscene[] {
           						new MoveInCutscene(4, 0.75f * 0.8f * 2),
          						new MoveInCutscene(4, 0, "pcm-ghfa"),
           						new MoveInCutscene(0, 0, "pcm-stck"),
           					}),
           				new CutsceneSequence(
           					2.5f,
           					new MoveInCutscene[] {
           						new MoveInCutscene(4, 0.75f * 0.8f * 2),
          						new MoveInCutscene(4, 0, "pcm-ghfa"),
           						new MoveInCutscene(0, 0, "pcm-stck"),
           					}),
        			}));
        	css.put(
        		Integer.valueOf(3),
        		new Cutscene(
        			new CutsceneActor[] {
        				new CutsceneActor(false, 64, 9, 0),
        				new CutsceneActor(true, 70.2f, 9, 2),
        			},
        			new CutsceneSequence[] {
        				new CutsceneSequence(
        					5.3f,
        					new MoveInCutscene[] {
            					new MoveInCutscene(4, 0.75f * 0.8f * 2),
        						new MoveInCutscene(4, 0.78f * 0.8f * 2, "pcm-ghin"),
        					}),
        				new CutsceneSequence(
           					5.3f,
           					new MoveInCutscene[] {
               					new MoveInCutscene(4, 0),
           						new MoveInCutscene(8, 0.78f * 0.8f * 2, "pcm-gbug"),
           					}),
        			}));
        	B = Collections.unmodifiableMap(css);
        }
        
        private static final int[] C = { 90, 45, 30, }; // fps オプション
        private static final int D = C[0]; // 本来想定されているfps
        
        private final GameFieldView view;
        SoundPlayer soundPlayer;
        private boolean ready;
        private boolean soundReady;
        private boolean graphicsReady;        
    	private long randSeed;
    	private int playfieldWidth;
    	private int playfieldHeight;
    	private Map<Integer, Map<Integer, PathElement>> playfield;
    	private int dotsRemaining;
    	private int dotsEaten;
    	PacManCanvas canvasEl;
    	private PlayField playfieldEl;
    	private CutsceneCanvas cutsceneCanvasEl;
    	private Fruit fruitEl;
    	private Door doorEl;
    	private Sound soundEl;
    	private int playerCount;
    	E[] actors;
    	
    	private float touchDX;
    	private float touchDY;
    	private float touchStartX;
    	private float touchStartY;
    	private boolean touchCanceld = true;
    	
	    private int[] score;
	    private boolean[] extraLifeAwarded;
	    private int lives = 3;
	    private int level = 0;
	    private LevelConfig levels;
	    private boolean paused = e;
	    private long globalTime = 0;
	    
	    private int frightModeTime = 0;
	    private int intervalTime = 0;
	    private float gameplayModeTime = 0;
	    private int fruitTime = 0;
	    private int forcePenLeaveTime;
	    private int ghostModeSwitchPos = 0;
	    private float ghostModeTime;
	    private boolean ghostExitingPenNow = e;
	    private int ghostEyesCount = 0;
	    private boolean tilesChanged = e;
	    private int[] dotEatingChannel;
	    private int[] dotEatingSoundPart;
	    
	    private int gameplayMode;
	    
	    private int killScreenTileX;
	    private int killScreenTileY;

	    private float[] timing;
	    private boolean alternatePenLeavingScheme;
	    private int alternateDotCount;
	    private boolean lostLifeOnThisLevel;

	    private int lastMainGhostMode;
	    private int mainGhostMode;

		private float currentPlayerSpeed;
		private float currentDotEatingSpeed;
		private float cruiseElroySpeed;
		private Map<Float, Boolean[]> speedIntervals;
		private Map<Integer, Integer> oppositeDirections;
		
		private int modeScoreMultiplier;
		
		private boolean fruitShown;
		
		private int ghostBeingEatenId;
		private int playerEatingGhostId;
		private int playerDyingId;
		
		private boolean pacManSound = true;
		volatile boolean soundAvailable;
		private boolean userDisabledSound;
		
		private Cutscene cutscene;
		private int cutsceneId;
		private int cutsceneSequenceId;
		private E[] cutsceneActors;
		private float cutsceneTime;
		
		private float tickInterval;
		private float lastTimeDelta;
		private long lastTime;
		private int fpsChoice;
		private int fps;
		private boolean canDecreaseFps;
		private int lastTimeSlownessCount;
		private int tickMultiplier;
		
		private int scoreDigits;
		private ScoreLabel[] scoreLabelEl;
		private Score[] scoreEl;
		private Lives livesEl;
		private Level levelEl;
		
	    private boolean[] dotEatingNow;
	    private boolean[] dotEatingNext;
	    
	    Game(GameFieldView view) {
	    	this.view = view;
	    }


    	float rand() {
			long b = 4294967296L;
			long c = 134775813L;
			c = c * randSeed + 1;
			return (randSeed = c % b) / (float) b;
		}
		  
    	void seed(long b) {
    		this.randSeed = b;
    	}

		float getDistance(int [] b, int [] c) {
			return FloatMath.sqrt((c[1] - b[1]) * (c[1] - b[1]) + (c[0] - b[0]) * (c[0] - b[0]));
		}

		float getDistance(float[] b, float[] c) {
			return FloatMath.sqrt((c[1] - b[1]) * (c[1] - b[1]) + (c[0] - b[0]) * (c[0] - b[0]));
		}
	  
		float getPlayfieldX(float b) {
			return b + -32;
		}
	  
		float getPlayfieldY(float b) {
			return b + 0;
		}
	  
		int getCorrectedSpritePos(int b) {
			return b / 8 * 10 + 2;
		}
	  
		String getDotElementId(int b, int c) {
			return "pcm-d" + b + "-" + c;
		}
	  
		float[] getAbsoluteElPos(Presentation presentation) {
			// TODO: 要修正
			Presentation b = presentation;
			float[] c = { 0, 0 };
			do {
				c[0] += b.top;
				c[1] += b.left;
			} while ((b = b.parent) != null);
			return c;
		}
		
		void prepareElement(Presentation b, int c, int d) {
			c = getCorrectedSpritePos(c);
			d = getCorrectedSpritePos(d);
			b.bgPosX = c;
			b.bgPosY = d;
		}
		void changeElementBkPos(Presentation b, int c, int d, boolean f) {
			if (f) {
				c = getCorrectedSpritePos(c);
				d = getCorrectedSpritePos(d);
			}
			b.bgPosX = c;
			b.bgPosY = d;
		}
		void determinePlayfieldDimensions() {
			playfieldWidth = 0;
		    playfieldHeight = 0;
		    for (Path c : n) {
		    	if (c.w > 0) {
		    		int x = c.x + c.w - 1;
		    		if (x > playfieldWidth) playfieldWidth = x;
		    	} else {
		    		int y = c.y + c.h - 1;
		    		if (y > playfieldHeight) playfieldHeight = y;
		    	}
		    }
		}
		
		void preparePlayfield() {
			playfield = new HashMap<Integer, Map<Integer, PathElement>>();
		    for (int b = 0; b <= playfieldHeight + 1; b++) {
		    	Map<Integer, PathElement> row = new HashMap<Integer, PathElement>();
		    	for (int c = -2; c <= playfieldWidth + 1; c++) {
		    		PathElement p = new PathElement();
		    		p.path = false;
		    		p.dot = 0;
		    		p.intersection = false;
		    		row.put(Integer.valueOf(c * 8), p);
		    	}
		    	playfield.put(Integer.valueOf(b * 8), row);
		    }
		}
		void preparePaths() {
			for (Path c : n) {
				boolean d = c.tunnel;
				if (c.w > 0) {
					int f = c.y * 8;
					for (int h = c.x * 8; h <= (c.x + c.w - 1) * 8; h += 8) {
						PathElement pe = playfield.get(Integer.valueOf(f)).get(Integer.valueOf(h));
						pe.path = a;
						if (pe.dot == 0) {
							pe.dot = 1;
							dotsRemaining++;
						}
						pe.tunnel = !d || h != c.x * 8 && h != (c.x + c.w - 1) * 8 ? d : false;
					}	
					playfield.get(Integer.valueOf(f)).get(Integer.valueOf(c.x * 8)).intersection = a;
					playfield.get(Integer.valueOf(f)).get(Integer.valueOf((c.x + c.w - 1) * 8)).intersection = a;
				} else {
					int h = c.x * 8;
					for (int f = c.y * 8; f <= (c.y + c.h - 1) * 8; f += 8) {
						PathElement pe = playfield.get(Integer.valueOf(f)).get(Integer.valueOf(h));		        	
						if (pe.path) pe.intersection = a;
						pe.path = a;
						if (pe.dot == 0) {
							pe.dot = 1;
							dotsRemaining++;
						}
						pe.tunnel = !d || f != c.y * 8 && f != (c.y + c.h - 1) * 8 ? d : false;
					}
					playfield.get(Integer.valueOf(c.y * 8)).get(Integer.valueOf(h)).intersection = a;
					playfield.get(Integer.valueOf((c.y + c.h - 1) * 8)).get(Integer.valueOf(h)).intersection = a;
				}
		    }
		    for (Path p : o)
		    	if (p.w != 0)
		    		for (int h = p.x * 8; h <= (p.x + p.w - 1) * 8; h += 8) {
		    			playfield.get(Integer.valueOf(p.y * 8)).get(Integer.valueOf(h)).dot = 0;
		    			dotsRemaining--;
		    		}
		    	else
		    		for (int f = p.y * 8; f <= (p.y + p.h - 1) * 8; f += 8) {
		    			playfield.get(Integer.valueOf(f)).get(Integer.valueOf(p.x * 8)).dot = 0;
		    			dotsRemaining--;
		    		}
		}
		
		void prepareAllowedDirections() {
		    for (int b = 8; b <= playfieldHeight * 8; b += 8)
		    	for (int c = 8; c <= playfieldWidth * 8; c += 8) {
		    		PathElement pe = playfield.get(Integer.valueOf(b)).get(Integer.valueOf(c));
		    		pe.allowedDir = 0;
		    		if (playfield.get(Integer.valueOf(b - 8)).get(Integer.valueOf(c)).path) pe.allowedDir += 1;
		    		if (playfield.get(Integer.valueOf(b + 8)).get(Integer.valueOf(c)).path) pe.allowedDir += 2;
		    		if (playfield.get(Integer.valueOf(b)).get(Integer.valueOf(c - 8)).path) pe.allowedDir += 4;
		    		if (playfield.get(Integer.valueOf(b)).get(Integer.valueOf(c + 8)).path) pe.allowedDir += 8;
		    	}
		}
		// エサを作成
		void createDotElements() {
			playfieldEl.foods.clear();
		    for (int b = 8; b <= playfieldHeight * 8; b += 8)
		    	for (int c = 8; c <= playfieldWidth * 8; c += 8)
		    		if (playfield.get(Integer.valueOf(b)).get(Integer.valueOf(c)).dot != 0) {
		    			Food food = new Food();
		    			food.presentation.id = getDotElementId(b, c);
		          		food.presentation.left = c + -32;
		          		food.presentation.top = b + 0;
		          		food.presentation.width = 2;
		          		food.presentation.height = 2;
		          		food.presentation.bgColor = 0xf8b090;
		          		food.presentation.parent = playfieldEl.presentation;
		          		playfieldEl.foods.add(food);
		    		}
		}
		
		// パワーエサを作成
		void createEnergizerElements() {
		    for (Position c : p) {
		      	String d = getDotElementId(c.y * 8, c.x * 8);
		      	Food f = getDotElement(d);
		      	if (f == null) continue;
//		      	document.getElementById(d).className = "pcm-e";
		      	f.presentation.width = 8;
		      	f.presentation.height = 8;
		      	prepareElement(f.presentation, 0, 144);
		    	playfield.get(Integer.valueOf(c.y * 8)).get(Integer.valueOf(c.x * 8)).dot = 2;
		    }
		}
		
		private Food getDotElement(String id) {
			for (Food f : playfieldEl.foods) {
				if (f.presentation.id.equals(id)) {
					return f;
				}
			}
			
			return null;
		}
		
		void createFruitElement() {
			fruitEl = new Fruit(true);
		    fruitEl.presentation.id = "pcm-f";
		    fruitEl.presentation.width = 32;
		    fruitEl.presentation.height = 16;
		    fruitEl.presentation.left = getPlayfieldX(v[1]);
		    fruitEl.presentation.top = getPlayfieldY(v[0]);
		    prepareElement(fruitEl.presentation, -32, -16);
		    fruitEl.presentation.parent = playfieldEl.presentation;
		    playfieldEl.fruit = fruitEl;
		}
		
		void createPlayfieldElements() {
			doorEl = new Door();
			doorEl.presentation.id = "pcm-do";
			doorEl.presentation.width = 19;
			doorEl.presentation.height = 2;
			doorEl.presentation.left = 279;
			doorEl.presentation.top = 46;
			doorEl.presentation.bgColor = 0xffaaa5;
			doorEl.presentation.visibility = false;
			doorEl.presentation.parent = playfieldEl.presentation;
			playfieldEl.door = doorEl;
			createDotElements();
			createEnergizerElements();
			createFruitElement();
		}
	
	  	void createActors() {
	  		List<E> as = new ArrayList<E>(); 
	  		for (int b = 0; b < playerCount + 4; b++) {
	  			E actor = new E(b, this);
	  			if (b < playerCount) {
	  				actor.ghost = e;
	  				actor.mode = 1;
	  			} else {
	  				actor.ghost = a;
	  			}
	  			as.add(actor);	
		  	}
	  		actors = as.toArray(new E[0]);
	  	}
  	
	  	void restartActors() {
	  		for (E actor : actors)
	  			actor.A();
	  	}
	  	
		void createActorElements() {
			for (E actor : actors)
				actor.createElement();
		}
	
		void createPlayfield() {
		    playfieldEl = new PlayField();
		    playfieldEl.presentation.id = "pcm-p";
		    playfieldEl.presentation.left = 45;
		    playfieldEl.presentation.width = 464;
		    playfieldEl.presentation.height = 136;
		    playfieldEl.presentation.parent = canvasEl.presentation;
		    canvasEl.playfield = playfieldEl;
		}
		
	    void resetPlayfield() {
		    dotsRemaining = 0;
		    dotsEaten = 0;
		    prepareElement(playfieldEl.presentation, 256, 0);
		    determinePlayfieldDimensions();
		    preparePlayfield();
		    preparePaths();
		    prepareAllowedDirections();
		    createPlayfieldElements();
		    createActorElements();
	    }
	    
//	    boolean keyPressed(int b){
//		    boolean  c = e;
//		    switch (b) {
//		    case 37: // Left
//		      actors[0].requestedDir = 4;
//		      c = a;
//		      break;
//		    case 38: // Up
//		      actors[0].requestedDir = 1;
//		      c = a;
//		      break;
//		    case 39: // Right
//		      actors[0].requestedDir = 8;
//		      c = a;
//		      break;
//		    case 40: // Down
//		      actors[0].requestedDir = 2;
//		      c = a;
//		      break;
//		    case 65: // A as Left for 2P
//		      if (playerCount == 2) {
//		        actors[1].requestedDir = 4;
//		        c = a;
//		      }
//		      break;
//		    case 83: // S as Down for 2P
//		      if (playerCount == 2) {
//		        actors[1].requestedDir = 2;
//		        c = a;
//		      }
//		      break;
//		    case 68: // D as Right for 2P
//		      if (playerCount == 2) {
//		        actors[1].requestedDir = 8;
//		        c = a;
//		      }
//		      break;
//		    case 87: // W as UP for 2P
//		      if (playerCount == 2) {
//		        actors[1].requestedDir = 1;
//		        c = a;
//		      }
//		      break;
//		    }
//		    return c;
//		}
//	    
//	    void handleKeyDown(Object b) {
////	    	if (b != null) b = window.event;
////	    	if (g.keyPressed(b.keyCode))
////	    		if (b.preventDefault) b.preventDefault();
////	    		else b.returnValue = e
//	    }
	    
		void canvasClicked(float b, float c) {
		    float[] d = getAbsoluteElPos(canvasEl.presentation);
		    b -= d[1] - -32;
		    c -= d[0] - 0;
		    E player = actors[0];
		    float f = getPlayfieldX(player.pos[1] + player.posDelta[1]) + 16;
		    float h = getPlayfieldY(player.pos[0] + player.posDelta[0]) + 32;
		    float j = Math.abs(b - f);
		    float k = Math.abs(c - h);
		    if (j > 8 && k < j) player.requestedDir = b > f ? 8 : 4;
		    else if (k > 8 && j < k) player.requestedDir = c > h ? 2 : 1;
		}

//		void handleClick(Object b) {
////			if (b != null) b = window.event;
////			canvasClicked(b.clientX, b.clientY);
//		}
		
//	    void registerTouch() {
////	    	document.body.addEventListener("touchstart", g.handleTouchStart, a);
////	    	canvasEl.addEventListener("touchstart", g.handleTouchStart, a);
////	    	document.f && document.f.q && document.f.q.addEventListener("touchstart", g.handleTouchStart, a);
//	    }
	    
	    void handleTouchStart(MotionEvent e) {
		    touchDX = 0;
		    touchDY = 0;
		    if (e.getPointerCount() == 1) {
			    touchCanceld = false;
		    	touchStartX = e.getX(0);
		    	touchStartY = e.getY(0);
		    }
	    }
	    
	    void handleTouchMove(MotionEvent e) {
	    	if (touchCanceld) return;
	    	
	    	if (e.getPointerCount() > 1) cancelTouch();
	    	else {
	    		touchDX = e.getX(0) - touchStartX;
	    		touchDY = e.getY(0) - touchStartY;
	    	}
	    }
    
	    void handleTouchEnd(MotionEvent e) {
	    	if (touchCanceld) return;
	    	
	    	float c = Math.abs(touchDX);
	    	float d = Math.abs(touchDY);
	    	if (c < 8 && d < 8) canvasClicked(touchStartX, touchStartY);
	    	else if (c > 15 && d < c * 2 / 3) actors[0].requestedDir = touchDX > 0 ? 8 : 4;
	    	else if (d > 15 && c < d * 2 / 3) actors[0].requestedDir = touchDY > 0 ? 2 : 1;
		    cancelTouch();
	    }
	    
		void cancelTouch() {
		    touchStartX = Float.NaN;
		    touchStartY = Float.NaN;
		    touchCanceld = true;
		}
		
		void startGameplay() {
		    score = new int[] {0, 0};
		    extraLifeAwarded = new boolean[] {e, e};
		    lives = 3;
		    level = 0;
		    paused = e;
		    globalTime = 0;
		    newLevel(a);
		}
		
		// b true:新規ゲーム false:ゲーム再開(プレイヤー死亡後or新レベル)
		void restartGameplay(boolean b) {
		    seed(0);
		    frightModeTime = 0;
		    intervalTime = 0;
		    gameplayModeTime = 0;
		    fruitTime = 0;
		    ghostModeSwitchPos = 0;
		    ghostModeTime = levels.ghostModeSwitchTimes[0] * D;
		    ghostExitingPenNow = e;
		    ghostEyesCount = 0;
		    tilesChanged = e;
		    updateCruiseElroySpeed();
		    hideFruit();
		    resetForcePenLeaveTime();
		    restartActors();
		    updateActorPositions();
		    switchMainGhostMode(2, a);
		    for (int c = playerCount + 1; c < playerCount + 4; c++) actors[c].a(16);
		    dotEatingChannel = new int[] {0, 0};
		    dotEatingSoundPart = new int[] {1, 1};
		    clearDotEatingNow();

		    if (b) changeGameplayMode(4);
		    else changeGameplayMode(6);
//		    changeGameplayMode(10);
		}

		void initiateDoubleMode() {
		    if (playerCount != 2) {
		    	stopAllAudio();
		    	changeGameplayMode(12);
		    }
		}
		
		void newGame() {
		    playerCount = 1;
		    createChrome();
		    createPlayfield();
		    createActors();
		    startGameplay();
		}

		void switchToDoubleMode() {
		    playerCount = 2;
		    createChrome();
		    createPlayfield();
		    createActors();
		    startGameplay();
		}
		
		void insertCoin() {
		    if (gameplayMode == 8 || gameplayMode == 14) newGame();
		    else initiateDoubleMode();
		}

		void createKillScreenElement(int b, int c, int d, int f, boolean h) {
//		    var j = document.createElement("div");
//		    j.style.left = b + "px";
//		    j.style.top = c + "px";
//		    j.style.width = d + "px";
//		    j.style.height = f + "px";
//		    j.style.zIndex = 119;
		    if (h) {
//		      j.style.background = "url(src/pacman10-hp-sprite-2.png) -" + killScreenTileX + "px -" + killScreenTileY + "px no-repeat";
		      killScreenTileY += 8;
		    } else ;// j.style.background = "black";
//		    playfieldEl.appendChild(j)
		}
  
		void killScreen() {
		    seed(0);
//		    canvasEl.style.visibility = "";
		    canvasEl.presentation.visibility = true;
		    createKillScreenElement(272, 0, 200, 80, e);
		    createKillScreenElement(280, 80, 192, 56, e);
		    killScreenTileX = 80;
		    killScreenTileY = 0;
		    for (int b = 280; b <= 472; b += 8)
		    	for (int c = 0; c <= 136; c += 8) {
		    		if (rand() < 0.03) {
		    			killScreenTileX = (int) FloatMath.floor(rand() * 25) * 10;
		    			killScreenTileY = (int) FloatMath.floor(rand() * 2) * 10;
		    		}
		    		createKillScreenElement(b, c, 8, 8, a);
		    	}
		
		    changeGameplayMode(14);
		}
		
		void newLevel(boolean b) {
		    level++;
		    levels = level >= z.length ? z[z.length - 1] : z[level];
		    // start issue 14: Ghosts stay blue permanently on restart
		    if ((levels.frightTime > 0) && (levels.frightTime <= 6))
		    	levels.frightTime = Math.round(levels.frightTime * D); // z 配列を定義する際にこの処理を行っておくべきでは?
		    // end issue 14
		    levels.frightTotalTime = levels.frightTime + ((int) timing[1]) * (levels.frightBlinkCount * 2 - 1);
		    for (E actor : actors) actor.dotCount = 0;
		    alternatePenLeavingScheme = e;
		    lostLifeOnThisLevel = e;
		    updateChrome();
		    resetPlayfield();
		    restartGameplay(b);
		    if (level == 256) killScreen();
		}
		
		void newLife() {
		    lostLifeOnThisLevel = a;
		    alternatePenLeavingScheme = a;
		    alternateDotCount = 0;
		    lives--;
		    updateChromeLives();
		    
		    if (lives == -1) changeGameplayMode(8);
		    else restartGameplay(e);
		}
		
		// MainGhostMode切り替え
		// b: 切り替え先のモード c: 開始直後フラグ(trueなら開始直後)
		void switchMainGhostMode(int b, boolean c) {
		    if (b == 4 && levels.frightTime == 0)
		    	for (E actor : actors) {
		    		if (actor.ghost) actor.reverseDirectionsNext = a; // frightTimeが0なら、ブルーモードになってもモンスターは反対に向きを変えるだけ
		    	}
		    else {
		    	int f = mainGhostMode;
		    	if (b == 4 && mainGhostMode != 4) lastMainGhostMode = mainGhostMode;
		    	mainGhostMode = b;
		    	if (b == 4 || f == 4) playAmbientSound();
		    	switch (b) {
		    	case 1:
		    	case 2:
		    		currentPlayerSpeed = levels.playerSpeed * 0.8f;
		    		currentDotEatingSpeed = levels.dotEatingSpeed * 0.8f;
		    		break;
		    	case 4:
		    		currentPlayerSpeed = levels.playerFrightSpeed * 0.8f;
		    		currentDotEatingSpeed = levels.dotEatingFrightSpeed * 0.8f;
		    		frightModeTime = levels.frightTotalTime;
		    		modeScoreMultiplier = 1;
		    		break;
		    	}
		    	for (E actor : actors) {
		    		if (actor.ghost) {
			        	if (b != 64 && !c) actor.modeChangedWhileInPen = a; // b(Main Ghost Modeは1, 2, 4。 64になるケースは存在しないように思える.)
			        	if (b == 4) actor.eatenInThisFrightMode = e;
			        	if (actor.mode != 8 && actor.mode != 16 && actor.mode != 32 && actor.mode != 128 && actor.mode != 64 || c) {
				        	// ゲーム再開直後(c:true)以外では, モンスターのモードが8, 16, 36, 64, 128ならモード更新対象とならない
				        	// ゲーム再開直後以外でブルーモード(4)以外から異なるモード[追跡モード(1), Scatterモード(2), ブルーモード(4)]への切り替え時が行われるとき、反対に向きを変える 
				        	if (!c && actor.mode != 4 && actor.mode != b) actor.reverseDirectionsNext = a;
				        	actor.a(b);
			        	}
			        } else {
			        	actor.fullSpeed = currentPlayerSpeed;
			        	actor.dotEatingSpeed = currentDotEatingSpeed;
			        	actor.tunnelSpeed = currentPlayerSpeed;
			        	actor.d();
			        }
		    	}
		    }
		}
		
		void figureOutPenLeaving() {
		    if (alternatePenLeavingScheme) { // レベル再開後のみ食べられたエサの数によりモンスターが出撃するタイミングを管理
		    	alternateDotCount++;
		    	if (alternateDotCount == m[1])
		    		actors[playerCount + 1].freeToLeavePen = a;
		    	else if (alternateDotCount == m[2])
		    		actors[playerCount + 2].freeToLeavePen = a;
		    	else if (alternateDotCount == m[3])
		    		if (actors[playerCount + 3].mode == 16)
		    			alternatePenLeavingScheme = e;
		    } else if (actors[playerCount + 1].mode == 16 || actors[playerCount + 1].mode == 8) {
		    	actors[playerCount + 1].dotCount++;
		    	if (actors[playerCount + 1].dotCount >= levels.penLeavingLimits[1]) actors[playerCount + 1].freeToLeavePen = a;
		    } else if (actors[playerCount + 2].mode == 16 || actors[playerCount + 2].mode == 8) {
		    	actors[playerCount + 2].dotCount++;
		    	if (actors[playerCount + 2].dotCount >= levels.penLeavingLimits[2]) actors[playerCount + 2].freeToLeavePen = a;
		    } else if (actors[playerCount + 3].mode == 16 || actors[playerCount + 3].mode == 8) {
		    	actors[playerCount + 3].dotCount++;
		    	if (actors[playerCount + 3].dotCount >= levels.penLeavingLimits[3]) actors[playerCount + 3].freeToLeavePen = a;
		    }
		}
		
		void resetForcePenLeaveTime() {
		    forcePenLeaveTime = levels.penForceTime * D;
		}
		
		void dotEaten(int b, int[] c) {
		    dotsRemaining--;
		    dotsEaten++;
		    actors[b].c(1);
		    playDotEatingSound(b);
		    if (playfield.get(Integer.valueOf(c[0])).get(Integer.valueOf(c[1])).dot == 2) { // パワーエサを食べたとき
		    	switchMainGhostMode(4, e);
		    	addToScore(50, b);
		    } else addToScore(10, b); // 普通のエサ
		    
		    Food d = getDotElement(getDotElementId(c[0], c[1]));
//		    d.style.display = "none";
		    d.eaten = true;
		    d.presentation.visibility = false;
		    playfield.get(Integer.valueOf(c[0])).get(Integer.valueOf(c[1])).dot = 0;
		    updateCruiseElroySpeed();
		    resetForcePenLeaveTime();
		    figureOutPenLeaving();
		    if (dotsEaten == 70 || dotsEaten == 170) showFruit();
		    if (dotsRemaining == 0) finishLevel();
		    playAmbientSound();
		}
		
		int[] getFruitSprite(int b) {
			int c = b <= 4 ? 128 : 160;
			b = 128 + 16 * ((b - 1) % 4);
			return new int[] { c, b };
		}

		int[] getFruitScoreSprite(int b) {
			int c = 128;
			b = 16 * (b - 1);
			return new int[] { c, b };
		}
			
		void hideFruit() {
			fruitShown = e;
			changeElementBkPos(fruitEl.presentation, 32, 16, a);
		}
		
		void showFruit() {
			fruitShown = a;
			int[] b = getFruitSprite(levels.fruit);
			changeElementBkPos(fruitEl.presentation, b[0], b[1], a);
			fruitTime = (int) timing[15] + (int) ((timing[16] - timing[15]) * rand());
		}
		
		void eatFruit(int b) {
		    if (fruitShown) {
		    	playSound("fruit", 0);
		    	fruitShown = e;
		    	int[] c = getFruitScoreSprite(levels.fruit);
		    	changeElementBkPos(fruitEl.presentation, c[0], c[1], a);
		    	fruitTime = (int) timing[14];
		    	addToScore(levels.fruitScore, b);
		    }
		}
		
		void updateActorTargetPositions() {
		    for (int b = playerCount; b < playerCount + 4; b++) actors[b].B();
		}
		
		void moveActors() {
			for (E actor : actors) actor.move();
		}
		
		void ghostDies(int b, int c) {
		    playSound("eating_ghost", 0);
		    addToScore(200 * modeScoreMultiplier, c);
		    modeScoreMultiplier *= 2;
		    ghostBeingEatenId = b;
		    playerEatingGhostId = c;
		    changeGameplayMode(1);
		}

		void playerDies(int b) {
		    playerDyingId = b;
		    changeGameplayMode(2);
		}
  
		void detectCollisions() {
		    tilesChanged = e;
		    for (int b = playerCount; b < playerCount + 4; b++)
		    	for (int c = 0; c < playerCount; c++)
		    		if (actors[b].tilePos[0] == actors[c].tilePos[0]
		    		        && actors[b].tilePos[1] == actors[c].tilePos[1])
		    			if (actors[b].mode == 4) {
		    				ghostDies(b, c);
		    				return;
		    			} else
		    				if (actors[b].mode != 8 && actors[b].mode != 16
		    					&& actors[b].mode != 32 && actors[b].mode != 128
		    					&& actors[b].mode != 64)
		    					playerDies(c);
		  }

		void updateCruiseElroySpeed() {
			float b = levels.ghostSpeed * 0.8f;
			if (!lostLifeOnThisLevel || actors[playerCount + 3].mode != 16) {
				LevelConfig c = levels;
				if (dotsRemaining < c.elroyDotsLeftPart2) b = c.elroySpeedPart2 * 0.8f;
				else if (dotsRemaining < c.elroyDotsLeftPart1) b = c.elroySpeedPart1 * 0.8f;
			}
			if (b != cruiseElroySpeed) {
				cruiseElroySpeed = b;
				actors[playerCount].d(); // アカベエの速度を更新
			}
		}
		
		// speed: intervalTimeをインデックスに対応させた配列。あるintervalTimeでキャラの移動処理が必要かどうかが配列の要素(true/false)
		// ex) 0.64: [false, true, false, true, false, true, ...]
		Boolean[] getSpeedIntervals(float b){
			Float speed = Float.valueOf(b);
		    if (!speedIntervals.containsKey(speed)) {
		    	float c = 0;
		    	double d = 0;
		    	List<Boolean> bools = new ArrayList<Boolean>();
		    	for (int f = 0; f < D; f++) {
		    		c += b;
		    		float flr = FloatMath.floor(c);
		    		if (flr > d) {
		    			bools.add(a);
		    			d = flr;
		    		} else
		    			bools.add(e);
		    	}
		    	speedIntervals.put(speed, bools.toArray(new Boolean[0]));
		    }
		    return speedIntervals.get(speed);
		}
		
		void finishLevel() {
		    changeGameplayMode(9);
		}
		
		void changeGameplayMode(int b){
		    gameplayMode = b;
		    if (b != 13)
		    	for (int c = 0; c < playerCount + 4; c++)
		    		actors[c].b();
		
		    switch (b) {
		    case 0:
		    	playAmbientSound();
		    	break;
		    case 2:
		    	stopAllAudio();
		    	gameplayModeTime = timing[3];
		    	break;
		    case 3:
		    	if (playerDyingId == 0) playSound("death", 0);
		    	else playSound("death_double", 0);
		    	
		    	gameplayModeTime = timing[4];
		    	break;
		    case 6:
		    	canvasEl.presentation.visibility = false;
		    	gameplayModeTime = timing[5];
		    	break;
		    case 7:
		    	stopAllAudio();
//		    	canvasEl.style.visibility = "";
		    	canvasEl.presentation.visibility = true;
//		    	doorEl.style.display = "block";
		    	doorEl.presentation.visibility = true;
		    	Ready m7_ready = new Ready();
		      	m7_ready.presentation.id = "pcm-re";
		      	m7_ready.presentation.width = 48;
		      	m7_ready.presentation.height = 8;
		      	m7_ready.presentation.left = 264;
		      	m7_ready.presentation.top = 80;
		      	prepareElement(m7_ready.presentation, 160, 0);
		      	m7_ready.presentation.parent = playfieldEl.presentation;
		      	playfieldEl.ready = m7_ready;
		    	gameplayModeTime = timing[6];
		    	break;
		    case 4:
//		    	doorEl.style.display = "block";
		    	doorEl.presentation.visibility = true;
		    	
		    	Ready m4_ready = new Ready();
		      	m4_ready.presentation.id = "pcm-re";
		      	m4_ready.presentation.width = 48;
		      	m4_ready.presentation.height = 8;
		      	m4_ready.presentation.left = 264;
		      	m4_ready.presentation.top = 80;
		      	prepareElement(m4_ready.presentation, 160, 0);
		      	m4_ready.presentation.parent = playfieldEl.presentation;
		      	playfieldEl.ready = m4_ready;
			    gameplayModeTime = timing[7];
			    stopAllAudio();
			    
			    if (playerCount == 2) playSound("start_music_double", 0, a);
			    else playSound("start_music", 0, a);
			    
			    break;
		    case 5:
		    	lives--;
		    	updateChromeLives();
		    	gameplayModeTime = timing[8];
		    	break;
		    case 8:
		    case 14:
//		    	Object ready = document.getElementById("pcm-re");
//			    google.dom.remove(ready);
		    	playfieldEl.ready = null;
			    stopAllAudio();
			    GameOver go = new GameOver();
			    go.presentation.id = "pcm-go";
			    go.presentation.width = 80;
			    go.presentation.height = 8;
			    go.presentation.left = 248;
			    go.presentation.top = 80;
			    prepareElement(go.presentation, 8, 152);
			    go.presentation.parent = playfieldEl.presentation;
			    playfieldEl.gameover = go;
			    gameplayModeTime = timing[9];
			    break;
		    case 9:
		    	stopAllAudio();
		    	gameplayModeTime = timing[10];
		    	break;
		    case 10:
//		    	doorEl.style.display = "none";
		    	doorEl.presentation.visibility = false;
		    	gameplayModeTime = timing[11];
		    	break;
		    case 11:
//		    	canvasEl.style.visibility = "hidden";
		    	canvasEl.presentation.visibility = false;
		    	gameplayModeTime = timing[12];
		    	break;
		    case 12:
//		    	playfieldEl.style.visibility = "hidden";
		    	playfieldEl.presentation.visibility = false;
		    	gameplayModeTime = timing[13];
		    	break;
		    case 1:
		    	gameplayModeTime = timing[2];
		    	break;
		    case 13:
		    	startCutscene();
		    	break;
		    }
		 }

		void showChrome(boolean b) {
		    if (scoreLabelEl[0] != null)
		    	scoreLabelEl[0].presentation.visibility = b; // showElementById("pcm-sc-1-l", b);
		    
		    if (scoreLabelEl[1] != null)
		    	scoreLabelEl[1].presentation.visibility = b; // showElementById("pcm-sc-2-l", b);
		    
		    if (scoreEl[0] != null)
		    	scoreEl[0].presentation.visibility = b; //showElementById("pcm-sc-1", b);
		    
		    if (scoreEl[1] != null)
		    	scoreEl[1].presentation.visibility = b; // showElementById("pcm-sc-2", b);
		    
		    if (livesEl != null)
		    	livesEl.presentation.visibility = b;// showElementById("pcm-li", b);
		    
		    if (soundEl != null)
		    	soundEl.presentation.visibility = b;// showElementById("pcm-so", b);
		}
		
		boolean toggleSound(Object b) {
//		    b = window.event || b;
//	    	b.cancelBubble = a;
		    if (pacManSound) {
		    	userDisabledSound = a;
		    	stopAllAudio();
		    	pacManSound = e;
		    } else {
		    	pacManSound = a;
		    	playAmbientSound();
		    }
		    updateSoundIcon();
//		    return b.returnValue = e;
		    return false;
		}
	
		void updateSoundIcon() {
			if (soundEl != null)
				if (pacManSound) changeElementBkPos(soundEl.presentation, 216, 105, e);
				else changeElementBkPos(soundEl.presentation, 236, 105, e);
		}
		
		void startCutscene() {
//			playfieldEl.style.visibility = "hidden";
			playfieldEl.presentation.visibility = false;
//		    canvasEl.style.visibility = "";
			canvasEl.presentation.visibility = true;
		    showChrome(e);
		    cutsceneCanvasEl = new CutsceneCanvas();
		    cutsceneCanvasEl.presentation.id = "pcm-cc";
		    cutsceneCanvasEl.presentation.left = 45;
		    cutsceneCanvasEl.presentation.width = 464;
		    cutsceneCanvasEl.presentation.height = 136;
		    cutsceneCanvasEl.presentation.parent = canvasEl.presentation;
		    canvasEl.cutsceneCanvas = cutsceneCanvasEl;
		    cutscene = B.get(Integer.valueOf(cutsceneId));
		    cutsceneSequenceId = -1;
		    frightModeTime = levels.frightTotalTime;
		    List<E> cas = new ArrayList<E>();
		    for (CutsceneActor ca : cutscene.actors) {
		    	int c = ca.id;
		    	if (c > 0) c += playerCount - 1;

		    	E actor = new E(c, this);
//		    	d.className = "pcm-ac";
	    		actor.el.width = 16;
	    		actor.el.height = 16;
	    		actor.el.id = "actor" + c;
		    	prepareElement(actor.el, 0, 0);
		    	actor.elBackgroundPos = new int[] { 0, 0 };
		    	actor.elPos = new float[] { 0, 0 };
		    	actor.pos = new float[] { ca.y * 8, ca.x * 8 };
		    	actor.posDelta = new float[] { 0, 0 };
		    	actor.ghost = ca.ghost;
		    	cutsceneCanvasEl.actors.add(actor);
		    	cas.add(actor);
		    }
		    cutsceneActors = cas.toArray(new E[0]); 
		    cutsceneNextSequence();
		    stopAllAudio();
		    playAmbientSound();
		}
		
		void stopCutscene() {
//		    playfieldEl.style.visibility = "";
		    playfieldEl.presentation.visibility = true;
		    canvasEl.cutsceneCanvas = null;
		    showChrome(a);
		    newLevel(e);
		}
		
		void cutsceneNextSequence() {
		    cutsceneSequenceId++;
		    if (cutscene.sequence.length == cutsceneSequenceId) stopCutscene();
		    else {
		    	CutsceneSequence b = cutscene.sequence[cutsceneSequenceId];
		    	cutsceneTime = b.time * D;
		    	for (int c = 0; c < cutsceneActors.length; c++) {
		    		E d = cutsceneActors[c];
		    		d.dir = b.moves[c].dir;
		    		d.speed = b.moves[c].speed;
		        	if (b.moves[c].elId != null) d.el.id = b.moves[c].elId;
		    		if (b.moves[c].mode != 0) d.mode = b.moves[c].mode;
		    		d.b();
		    	}
		    }
		}
		
		void checkCutscene() {
			if (cutsceneTime <= 0) cutsceneNextSequence();
		}
		
		void advanceCutscene() {
		    for (E actor : cutsceneActors) {
		      Direction d = l.get(actor.dir);
		      actor.pos[d.axis] += d.increment * actor.speed;
		      actor.b();
		    }
		    cutsceneTime--;
		}
		
		void updateActorPositions() {
		    for (E actor : actors) actor.k();
		}
		
		// TODO: パワーエサの取得方法を再考する
		void blinkEnergizers() {
		    switch (gameplayMode) {
		    case 4:
		    case 5:
		    case 6:
		    case 7:
		    case 9:
		    case 10:
		    case 11:
		    case 12:
//		      	playfieldEl.className = "";
		    	for (Food f : playfieldEl.foods) {
		    		if (f.presentation.hasBackground()) {
		    			f.presentation.visibility = true;
		    		}
		    	}
		    	break;
		    case 8:
		    case 14:
//		    	playfieldEl.className = "blk";
		    	for (Food f : playfieldEl.foods) {
		    		if (f.presentation.hasBackground()) {
		    			f.presentation.visibility = false;
		    		}
		    	}
		    	break;
		    default:
		    	if (globalTime % (timing[0] * 2) == 0) {
		    		// playfieldEl.className = "";
			    	for (Food f : playfieldEl.foods) {
			    		if (f.presentation.hasBackground()) {
			    			f.presentation.visibility = true;
			    		}
			    	}
		    	} else if (globalTime % (timing[0] * 2) == timing[0]) {
		    		// playfieldEl.className = "blk";
			    	for (Food f : playfieldEl.foods) {
			    		if (f.presentation.hasBackground()) {
			    			f.presentation.visibility = false;
			    		}
			    	}
		    	}
		    	break;
		    }
		}
		
		void blinkScoreLabels() {
		    if (gameplayMode != 13) {
		    	boolean modify = true;
		    	boolean b = false;
		    	
		    	if (globalTime % (timing[17] * 2) == 0)
		    		b = true;
		    	else if (globalTime % (timing[17] * 2) == timing[17])
		    		b = false;
		    	else
		    		modify = false;
		    	
		    	if (modify)
		    		for (int c = 0; c < playerCount; c++)
		    			scoreLabelEl[c].presentation.visibility = b;
		    }
		}
		
		void finishFrightMode() {
		    switchMainGhostMode(lastMainGhostMode, e);
		}
		
		void handleGameplayModeTimer() {
		    if (gameplayModeTime != 0) {
		    	gameplayModeTime--;
		    	switch (gameplayMode) {
		    	case 2:
		    	case 3:
		    		for (int b = 0; b < playerCount + 4; b++) actors[b].b();
		    		break;
		    	case 10:
		    		if (FloatMath.floor(gameplayModeTime / (timing[11] / 8)) % 2 == 0)
		    			changeElementBkPos(playfieldEl.presentation, 322, 2, e);
		    		else
		    			changeElementBkPos(playfieldEl.presentation, 322, 138, e);
		    	}
		    	
		    	if (gameplayModeTime <= 0) {
		    		gameplayModeTime = 0;
		    		switch (gameplayMode) {
			        case 1:
			        	changeGameplayMode(0);
			        	ghostEyesCount++;
			        	playAmbientSound();
	//		        	actors[ghostBeingEatenId].el.className = "pcm-ac";
			        	actors[ghostBeingEatenId].a(8);
			        	boolean c = e;
			        	for (int b = playerCount; b < playerCount + 4; b++)
			            if (actors[b].mode == 4
			                || (actors[b].mode == 16 || actors[b].mode == 128)
			                    && !actors[b].eatenInThisFrightMode) {
			            	c = a;
			            	break;
			            }
			        	if (!c) finishFrightMode();
			        	break;
			        case 2:
			        	changeGameplayMode(3);
			        	break;
			        case 3:
			        	newLife();
			        	break;
			        case 4:
			        	changeGameplayMode(5);
			        	break;
			        case 6:
			        	changeGameplayMode(7);
			        	break;
			        case 7:
			        case 5:
	//		        	document.getElementById("pcm-re");
	//		        	google.dom.remove(b);
			        	playfieldEl.ready = null;
			        	changeGameplayMode(0);
			        	break;
			        case 8:
	//		        	b = document.getElementById("pcm-go");
	//		        	google.dom.remove(b);
			        	playfieldEl.gameover = null;
	//		        	google.pacManQuery && google.pacManQuery(); // google.pacManQueryというfunctionは存在しない
			        	break;
			        case 9:
			        	changeGameplayMode(10);
			        	break;
			        case 10:
			        	changeGameplayMode(11);
			        	break;
			        case 11:
			        	if (levels.cutsceneId != 0) {
			        		cutsceneId = levels.cutsceneId;
			        		changeGameplayMode(13);
			        	} else {
	//		        		canvasEl.style.visibility = "";
			        		canvasEl.presentation.visibility = true;
			        		newLevel(e);
			        	}
			        	break;
			        case 12:
	//		        	playfieldEl.style.visibility = "";
			        	playfieldEl.presentation.visibility = true;
	//		        	canvasEl.style.visibility = "";
			        	canvasEl.presentation.visibility = true;
			        	switchToDoubleMode();
			        	break;
			        }
		    	}
		    }
		}

		void handleFruitTimer(){
		    if (fruitTime != 0) {
		      fruitTime--;
		      if (fruitTime <= 0) hideFruit();
		    }
		}
		
		void handleGhostModeTimer() {
		    if (frightModeTime != 0) {
		    	frightModeTime--;
		    	if (frightModeTime <= 0) {
		    		frightModeTime = 0;
		    		finishFrightMode();
		    	}
		    } else if (ghostModeTime > 0) {
		    	ghostModeTime--;
		    	if (ghostModeTime <= 0) {
		    		ghostModeTime = 0;
		    		ghostModeSwitchPos++;
			    	if (ghostModeSwitchPos < levels.ghostModeSwitchTimes.length) {
		    			ghostModeTime = levels.ghostModeSwitchTimes[ghostModeSwitchPos] * D;
		    			switch (mainGhostMode) {
		    			case 2:
		    				switchMainGhostMode(1, e);
		    				break;
		    			case 1:
		    				switchMainGhostMode(2, e);
		    				break;
		    			}
		    		}
		    	}
		    }
		}
		
		void handleForcePenLeaveTimer() {
		    if (forcePenLeaveTime != 0) {
		    	forcePenLeaveTime--;
		    	if (forcePenLeaveTime <= 0) {
		    		for (int b = 1; b <= 3; b++)
		    			if (actors[playerCount + b].mode == 16) {
		    				actors[playerCount + b].freeToLeavePen = a;
		    				break;
		    			}
		
		    		resetForcePenLeaveTime();
		    	}
		    }
		}
		
		void handleTimers() {
		    if (gameplayMode == 0) {
		    	handleForcePenLeaveTimer();
		    	handleFruitTimer();
		    	handleGhostModeTimer();
		    }
		    handleGameplayModeTimer();
		}
		
		void tick() {
		    long b = new Date().getTime();
		    lastTimeDelta += b - lastTime - tickInterval; // 処理遅延時間累計
		    if (lastTimeDelta > 100) lastTimeDelta = 100;
		    if (canDecreaseFps && lastTimeDelta > 50) { // fpsを下げることができるなら、処理遅延時間50ms超の回数をカウント
		    	lastTimeSlownessCount++;
		    	if (lastTimeSlownessCount == 20) decreaseFps(); // 処理遅延時間50ms超 20回でfpsを下げる
		    }
		    int c = 0;
		    if (lastTimeDelta > tickInterval) { // 処理遅延時間累計がtickインターバルより大きい場合、tickインターバル未満に値を切り詰める
		    	c = (int) FloatMath.floor(lastTimeDelta / tickInterval);
		    	lastTimeDelta -= tickInterval * c;
		    }
		    lastTime = b;
		    if (gameplayMode == 13) { // Cutscene
			    for (int i = 0; i < tickMultiplier + c; i++) { // tickMultiplierと処理地縁に応じて複数回のロジックを実行
			        advanceCutscene();
			        intervalTime = (intervalTime + 1) % D;
			        globalTime++;
			    }
			    checkCutscene();
			    blinkScoreLabels();
		    } else
		    	for (int i = 0; i < tickMultiplier + c; i++) { // tickMultiplierと処理地縁に応じて複数回のロジックを実行
		    		moveActors();
		    		if (gameplayMode == 0)
		    			if (tilesChanged) {
		    				detectCollisions();
		    				updateActorTargetPositions();
		    			}
		
			        globalTime++;
			        intervalTime = (intervalTime + 1) % D;
			        blinkEnergizers();
			        blinkScoreLabels();
			        handleTimers();
		    	}
		    
		    setTimeout();
		}

		void extraLife(int b) {
		    playSound("extra_life", 0);
		    extraLifeAwarded[b] = a;
		    lives++;
		    if (lives > 5) lives = 5;
		    updateChromeLives();
		}
		
		void addToScore(int b, int c){
		    score[c] += b;
		    if (!extraLifeAwarded[c] && score[c] > 10000) extraLife(c);
		    updateChromeScore(c);
		}
		
		void updateChrome() {
		    updateChromeLevel();
		    updateChromeLives();
		    for (int b = 0; b < playerCount; b++) updateChromeScore(b);
		}
		
		void updateChromeScore(int b) {
		    String c = String.valueOf(score[b]);
		    if (c.length() > scoreDigits) c = c.substring(c.length() - scoreDigits);
		    for (int d = 0; d < scoreDigits; d++) {
		    	Score.Number f = scoreEl[b].numbers.get(d);
		    	String h = null;
		    	if (d < c.length()) h = c.substring(d, d + 1);
		        if (h != null)
		        	changeElementBkPos(f.presentation, 8 + 8 * Integer.parseInt(h, 10), 144, a);
		        else
		        	changeElementBkPos(f.presentation, 48, 0, a);
		    }
		}
		
		void updateChromeLives() {
			livesEl.lives.clear();
		    for (int b = 0; b < lives; b++) {
		    	Lives.Life life = new Lives.Life();
		    	prepareElement(life.presentation, 64, 129);
//		    	c.className = "pcm-lif";
		    	life.presentation.width = 16;
		    	life.presentation.height = 12;
		    	life.presentation.top = b * 15; // margin-bottom: 3px
		    	life.presentation.parent = livesEl.presentation;
		    	livesEl.lives.add(life);
		    }
		}
		
		void updateChromeLevel() {
			levelEl.fruits.clear();
		    int top = (4 - Math.min(level, 4)) * 16 - 16;
		    for (int b = level; b >= Math.max(level - 4 + 1, 1); b--) {
		    	int c = b >= z.length ? z[z.length - 1].fruit : z[b].fruit;
		    	Fruit d = new Fruit(false);
		    	int[] fs = getFruitSprite(c);
		    	prepareElement(d.presentation, fs[0], fs[1]);
		    	d.presentation.width = 32;
		    	d.presentation.height = 16;
		    	top += 16;
		    	d.presentation.top = top;
		    	d.presentation.parent = levelEl.presentation;
		    	levelEl.fruits.add(d);
		    }
		}

		// スコアとサウンドアイコンを生成
		void createChrome() {
			canvasEl.reset();
		    scoreDigits = playerCount == 1 ? 10 : 5;
		    scoreLabelEl = new ScoreLabel[2];
		    scoreLabelEl[0] = new ScoreLabel();
		    scoreLabelEl[0].presentation.id = "pcm-sc-1-l";
		    scoreLabelEl[0].presentation.left = -2;
		    scoreLabelEl[0].presentation.top = 0;
		    scoreLabelEl[0].presentation.width = 48;
		    scoreLabelEl[0].presentation.height = 8;
		    prepareElement(scoreLabelEl[0].presentation, 160, 56);
		    scoreLabelEl[0].presentation.parent = canvasEl.presentation;
		    canvasEl.scoreLabels[0] = scoreLabelEl[0];
		    scoreEl = new Score[2];
		    scoreEl[0] = new Score();
		    scoreEl[0].presentation.id = "pcm-sc-1";
		    scoreEl[0].presentation.left = 18;
		    scoreEl[0].presentation.top = 16;
		    scoreEl[0].presentation.width = 8;
		    scoreEl[0].presentation.height = 56;
		    scoreEl[0].presentation.parent = canvasEl.presentation;
		    
		    for (int b = 0; b < scoreDigits; b++) {
		    	Score.Number c = new Score.Number();
		    	c.presentation.id = "pcm-sc-1-" + b;
		    	c.presentation.top = b * 8;
		    	c.presentation.left = 0;
		    	c.presentation.width = 8;
		    	c.presentation.height = 8;
		    	prepareElement(c.presentation, 48, 0);
		    	c.presentation.parent = scoreEl[0].presentation;
		    	scoreEl[0].numbers.add(c);
		    }
		    canvasEl.scores[0] = scoreEl[0];
		    livesEl = new Lives();
		    livesEl.presentation.id = "pcm-li";
		    livesEl.presentation.left = 523;
		    livesEl.presentation.top = 0;
		    livesEl.presentation.height = 80;
		    livesEl.presentation.width = 16;
		    livesEl.presentation.parent = canvasEl.presentation;
		    canvasEl.lives = livesEl;
		    levelEl = new Level();
		    levelEl.presentation.id = "pcm-le";
		    levelEl.presentation.left = 515;
		    levelEl.presentation.top = 74;
		    levelEl.presentation.height = 64;
		    levelEl.presentation.width = 32;
		    levelEl.presentation.parent = canvasEl.presentation;
		    canvasEl.level = levelEl;
		    if (playerCount == 2) {
		    	scoreLabelEl[1] = new ScoreLabel();
		    	scoreLabelEl[1].presentation.id = "pcm-sc-2-l";
			    scoreLabelEl[1].presentation.left = -2;
			    scoreLabelEl[1].presentation.top = 64;
			    scoreLabelEl[1].presentation.width = 48;
			    scoreLabelEl[1].presentation.height = 8;
		    	prepareElement(scoreLabelEl[1].presentation, 160, 64);
			    scoreLabelEl[1].presentation.parent = canvasEl.presentation;
		    	canvasEl.scoreLabels[1] = scoreLabelEl[1];
		    	scoreEl[1] = new Score();
		    	scoreEl[1].presentation.id = "pcm-sc-2";
		    	scoreEl[1].presentation.left = 18;
		    	scoreEl[1].presentation.top = 80;
		    	scoreEl[1].presentation.width = 8;
		    	scoreEl[1].presentation.height = 56;
			    scoreEl[1].presentation.parent = canvasEl.presentation;		    	
		    	for (int b = 0; b < scoreDigits; b++) {
		    		Score.Number c = new Score.Number();
		    		c.presentation.id = "pcm-sc-2-" + b;
		    		c.presentation.top = b * 8;
		    		c.presentation.left = 0;
		    		c.presentation.width = 8;
		    		c.presentation.height = 8;
		    		prepareElement(c.presentation, 48, 0);
			    	c.presentation.parent = scoreEl[1].presentation;
		    		scoreEl[1].numbers.add(c);
		    	}
		    	canvasEl.scores[1] = scoreEl[1];
		    
		    }
		    if (soundAvailable) {
		    	soundEl = new Sound();
		    	soundEl.presentation.id = "pcm-so";
		    	soundEl.presentation.left = 15; // 7 + 8
		    	soundEl.presentation.top = 124; // 116 + 8
		    	soundEl.presentation.width = 12;
		    	soundEl.presentation.height = 12;
		    	prepareElement(soundEl.presentation, -32, -16);
		    	soundEl.presentation.parent = canvasEl.presentation;
			    canvasEl.sound = soundEl;
//			    soundEl.onclick = toggleSound;
			    updateSoundIcon();
		    }
		}
		
		void clearDotEatingNow() {
		    dotEatingNow = new boolean[] {e, e};
		    dotEatingNext = new boolean[] {e, e};
		}
		
		// サウンド再生
		// b -> トラック, c -> チャンネル番号
		void playSound(String b, int c) {
			playSound(b, c, false);
		}

		// サウンド再生
		// b -> トラック, c -> チャンネル番号, d -> 再生中サウンド停止フラグ
		void playSound(String b, int c, boolean d) {
		    if (!(!soundAvailable || !pacManSound || paused)) {
		    	if (!d) stopSoundChannel(c);
		    	try {
		    		soundPlayer.playTrack(b, c);
		    	} catch (Exception f) {
//		    		soundAvailable = e;
		    	}
		    }
		}
		
		void stopSoundChannel(int b) {
		    if (soundAvailable)
		    	try {
		    		 soundPlayer.stopChannel(b);
		    	} catch (Exception c) {
		    		soundAvailable = e;
		    	}
		}
		
		void stopAllAudio() {
			if (soundAvailable) {
				try {
					soundPlayer.stopAmbientTrack();
				} catch (Exception b) {
					soundAvailable = e;
				}
				for (int c = 0; c < 5; c++) stopSoundChannel(c);
			}
		}
		
		void playDotEatingSound(int b) {
		    if (soundAvailable && pacManSound)
		    	if (gameplayMode == 0)
		    		if (dotEatingNow[b]) // 常にfalse
		    			dotEatingNext[b] = a; // デッドコード
		    		else {
		    			if (b == 0) {
		    				String c = dotEatingSoundPart[b] == 1 ? "eating_dot_1" : "eating_dot_2";
		    				playSound(c, 1 + dotEatingChannel[b], a);
		//    				dotTimer = window.setInterval(g.repeatDotEatingSoundPacMan, 150) // 無意味な処理
		    			} else {
		    				playSound("eating_dot_double", 3 + dotEatingChannel[b], a);
		//    				dotTimerMs = window.setInterval(g.repeatDotEatingSoundMsPacMan, 150) // 無意味な処理
		    			}
		    			dotEatingChannel[b] = (dotEatingChannel[b] + 1) % 2; // 0 と 1 をスイッチ
		    			dotEatingSoundPart[b] = 3 - dotEatingSoundPart[b]; // 1 と 2 をスイッチ
		    		}
		}

		void repeatDotEatingSound(int b) {
		    dotEatingNow[b] = e;
		    if (dotEatingNext[b]) { // 常にfalseのためこのブロックはデッドコード
		    	dotEatingNext[b] = e;
		    	playDotEatingSound(b);
		    }
		}
		
		void repeatDotEatingSoundPacMan() {
		    repeatDotEatingSound(0);
		}
		
		void repeatDotEatingSoundMsPacMan() {
			repeatDotEatingSound(1);
		}
		
		void playAmbientSound() {
		    if (soundAvailable && pacManSound) {
		    	String b = null;
		    	if (gameplayMode == 0 || gameplayMode == 1)
		    		b = ghostEyesCount != 0
		              	? "ambient_eyes"
		              	: mainGhostMode == 4
		              		? "ambient_fright"
		              		: dotsEaten > 241
		              			? "ambient_4"
		              			: dotsEaten > 207
		              				? "ambient_3"
		              				: dotsEaten > 138
		              					? "ambient_2" : "ambient_1";
		        else if (gameplayMode == 13) b = "cutscene";
		    	
		    	if (b != null)
		    		if (b.equals(soundPlayer.oldAmbient)) {
		    			return;
		    		}
		    		try {
		    			soundPlayer.playAmbientTrack(b);
		    			soundPlayer.oldAmbient = b;
		    		} catch (Exception c) {
		    			soundAvailable = e;
		    		}
		    }
		}
		
		void initializeTickTimer() {
		    fps = C[fpsChoice];
		    tickInterval = 1000 / fps;
		    tickMultiplier = D / fps;
		    timing = new float[w.length];
		    for (int b = 0; b < w.length; b++) {
		    	float c = !pacManSound && (b == 7 || b == 8) ? 1 : w[b]; // timing[7] -> Gameplay Mode 4, timing[8] -> Gameplay Mode 5. ともにゲーム開始直後がらみ.
		    	timing[b] = Math.round(c * D); // D = 90より、timingの要素はindex 7, 8以外は90.
		    }
		    lastTime = new Date().getTime();
		    lastTimeDelta = 0;
		    lastTimeSlownessCount = 0;
		}
		
		void setTimeout() {
		    view.redrawHandler.sleep(Math.round(tickInterval)); // TODO: 要見直し			
		}
		
		void decreaseFps() {
		    if (fpsChoice < C.length - 1) {
		    	fpsChoice++;
		    	initializeTickTimer();
		    	if (fpsChoice == C.length - 1) canDecreaseFps = e;
		    }
		}
	    
		void createCanvasElement() {
			canvasEl = new PacManCanvas();
		    canvasEl.presentation.id = "pcm-c";
		    canvasEl.presentation.width = 554;
		    canvasEl.presentation.height = 136;
		    canvasEl.presentation.bgColor = 0x000000;
//		    canvasEl.hideFocus = a;
//		    document.getElementById("logo").appendChild(canvasEl);
//		    canvasEl.tabIndex = 0;
//		    canvasEl.focus();
		}
		
		void everythingIsReady() {
		    if (!ready) {
		    	ready = a;
		    	createCanvasElement();
		    	speedIntervals = new HashMap<Float, Boolean[]>();
		    	oppositeDirections = new HashMap<Integer, Integer>();
		    	oppositeDirections.put(Integer.valueOf(1), Integer.valueOf(2));
		    	oppositeDirections.put(Integer.valueOf(2), Integer.valueOf(1));
		    	oppositeDirections.put(Integer.valueOf(4), Integer.valueOf(8));
		    	oppositeDirections.put(Integer.valueOf(8), Integer.valueOf(4));
		    	fpsChoice = 0;
		    	canDecreaseFps = a;
		    	initializeTickTimer();
		    	view.invalidate();
		    }
		}
		
		// TODO: 要メソッド名見直し
		void start() {
			if (ready) {
				setTimeout();
				newGame();
			}
		}
		
		void checkIfEverythingIsReady() {
		    if (soundReady && graphicsReady) {
		    	everythingIsReady();
		    }
		}
		
		void preloadImage(String b) {
// 		    var c = new Image,
//    			d = google.browser.engine.IE;
//    		if (!d) c.onload = g.imageLoaded;
//    		c.src = b;
//    		d && g.imageLoaded()
			imageLoaded();
		}
		
		void imageLoaded() {
		    graphicsReady = a;
		    checkIfEverythingIsReady();
		}
		
		void prepareGraphics() {
			graphicsReady = e;
			preloadImage("src/pacman10-hp-sprite-2.png");
		}
		
		void prepareSound() {
		    soundAvailable = e;
		    soundReady = e;
		    
		    soundPlayer = new SoundPlayer(view.getContext(), this);
		    soundPlayer.init();

			soundReady = a;
			checkIfEverythingIsReady();

		}
		
		void init() {
		    ready = e;
		    prepareGraphics();
		    prepareSound();
		}
    }
}
