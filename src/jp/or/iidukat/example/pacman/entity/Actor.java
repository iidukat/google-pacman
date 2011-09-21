package jp.or.iidukat.example.pacman.entity;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import jp.or.iidukat.example.pacman.Direction;
import jp.or.iidukat.example.pacman.CurrentSpeed;
import jp.or.iidukat.example.pacman.GameplayMode;
import jp.or.iidukat.example.pacman.GhostMode;
import jp.or.iidukat.example.pacman.Move;
import jp.or.iidukat.example.pacman.PacmanGame;
import jp.or.iidukat.example.pacman.PathElement;
import jp.or.iidukat.example.pacman.Presentation;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.FloatMath;

public abstract class Actor {

    private static class InitPosition {
    	final float x;
    	final float y;
    	final Direction dir;
    	final float scatterX;
    	final float scatterY;

    	InitPosition(float x, float y, Direction dir) {
    		this(x, y, dir, 0, 0);
    	}
    	
    	InitPosition(float x, float y, Direction dir, float scatterX, float scatterY) {
    		this.x = x;
    		this.y = y;
    		this.dir = dir;
    		this.scatterX = scatterX;
    		this.scatterY = scatterY;
    	}
    	
    	static InitPosition createPlayerInitPosition(float x, float y, Direction dir) {
    		return new InitPosition(x, y, dir);
    	}
    	
    	static InitPosition createGhostInitPosition(float x, float y, Direction dir,
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
    			InitPosition.createPlayerInitPosition(39.5f, 15, Direction.LEFT), // Pacman
    			InitPosition.createGhostInitPosition(39.5f, 4, Direction.LEFT, 57, -4), // アカベエ
    			InitPosition.createGhostInitPosition(39.5f, 7, Direction.DOWN, 0, -4), // ピンキー
    			InitPosition.createGhostInitPosition(37.625f, 7, Direction.UP, 57, 20), // アオスケ
    			InitPosition.createGhostInitPosition(41.375f, 7, Direction.UP, 0, 20), // グズタ
    		});
    	ps.put(
       		Integer.valueOf(2),
       		new InitPosition[] {
       			InitPosition.createPlayerInitPosition(40.25f, 15, Direction.RIGHT), // Pacman
       			InitPosition.createPlayerInitPosition(38.75f, 15, Direction.LEFT), // Ms.Pacman
       			InitPosition.createGhostInitPosition(39.5f, 4, Direction.LEFT, 57, -4), // アカベエ
       			InitPosition.createGhostInitPosition(39.5f, 7, Direction.DOWN, 0, -4), // ピンキー
       			InitPosition.createGhostInitPosition(37.625f, 7, Direction.UP, 57, 20), // アオスケ
       			InitPosition.createGhostInitPosition(41.375f, 7, Direction.UP, 0, 20), // グズタ
       		});
    	
    	r = Collections.unmodifiableMap(ps);
    }

    static final int[] s = {32, 312}; // モンスターの巣の入り口の位置

    private static class MoveInPen {
    	final float x;
    	final float y;
    	final Direction dir;
    	final float dest;
    	final float speed;
    	MoveInPen(float x, float y, Direction dir, float dest, float speed) {
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
    			new MoveInPen(37.6f, 7, Direction.UP, 6.375f, 0.48f),
    			new MoveInPen(37.6f, 6.375f, Direction.DOWN, 7.625f, 0.48f),
    			new MoveInPen(37.6f, 7.625f, Direction.UP, 7, 0.48f),
    		});
    	mvs.put(
    		Integer.valueOf(2),
    		new MoveInPen[] {
    			new MoveInPen(39.5f, 7, Direction.DOWN, 7.625f, 0.48f),
    			new MoveInPen(39.5f, 7.625f, Direction.UP, 6.375f, 0.48f),
    			new MoveInPen(39.5f, 6.375f, Direction.DOWN, 7, 0.48f),
    		});
    	mvs.put(
    		Integer.valueOf(3),
    		new MoveInPen[] {
    			new MoveInPen(41.4f, 7, Direction.UP, 6.375f, 0.48f),
    			new MoveInPen(41.4f, 6.375f, Direction.DOWN, 7.625f, 0.48f),
    			new MoveInPen(41.4f, 7.625f, Direction.UP, 7, 0.48f),
    		});
    	mvs.put(
    		Integer.valueOf(4),
    		new MoveInPen[] {
    			new MoveInPen(37.6f, 7, Direction.RIGHT, 39.5f, y),
    			new MoveInPen(39.5f, 7, Direction.UP, 4, y),
    		});
    	mvs.put(
    		Integer.valueOf(5),
    		new MoveInPen[] { new MoveInPen(39.5f, 7, Direction.UP, 4, y) });
    	mvs.put(
        	Integer.valueOf(6),
        	new MoveInPen[] {
        		new MoveInPen(41.4f, 7, Direction.LEFT, 39.5f, y),
        		new MoveInPen(39.5f, 7, Direction.UP, 4, y),
        	});
    	mvs.put(
           	Integer.valueOf(7),
           	new MoveInPen[] {
           		new MoveInPen(39.5f, 4, Direction.DOWN, 7, 1.6f),
           		new MoveInPen(39.5f, 7, Direction.LEFT, 37.625f, 1.6f),
           	});
    	mvs.put(
       		Integer.valueOf(8),
       		new MoveInPen[] { new MoveInPen(39.5f, 4, Direction.DOWN, 7, 1.6f) });
    	mvs.put(
           	Integer.valueOf(9),
           	new MoveInPen[] {
           		new MoveInPen(39.5f, 4, Direction.DOWN, 7, 1.6f),
           		new MoveInPen(39.5f, 7, Direction.RIGHT, 41.375f, 1.6f),
           	});
    	mvs.put(
           	Integer.valueOf(10),
           	new MoveInPen[] {
           		new MoveInPen(37.6f, 7, Direction.RIGHT, 39.5f, y),
           		new MoveInPen(39.5f, 7, Direction.UP, 4, y),
           	});
    	mvs.put(
       		Integer.valueOf(11),
       		new MoveInPen[] { new MoveInPen(39.5f, 7, Direction.UP, 4, y) });
    	mvs.put(
            Integer.valueOf(12),
            new MoveInPen[] {
            	new MoveInPen(41.4f, 7, Direction.LEFT, 39.5f, y),
            	new MoveInPen(39.5f, 7, Direction.UP, 4, y),
            });
    	A = Collections.unmodifiableMap(mvs);
    }
    
	final int id;
	final PacmanGame g;
	GhostMode mode = GhostMode.NONE;
	float[] pos;
	float[] posDelta;
	int[] tilePos;
	int[] lastGoodTilePos;
	private float[] elPos;
	int[] elBackgroundPos;
	float[] targetPos;
	float[] scatterPos;
	Direction dir = Direction.NONE;
	Direction lastActiveDir = Direction.NONE;
	float speed;
	private float physicalSpeed;
	Direction requestedDir = Direction.NONE;
	Direction nextDir = Direction.NONE;
	boolean reverseDirectionsNext;
	private boolean freeToLeavePen;
	private boolean modeChangedWhileInPen;
	boolean eatenInThisFrightMode;
	boolean followingRoutine;
	private boolean proceedToNextRoutineMove;
	private int routineToFollow;
	private int routineMoveId;
	int targetPlayerId;
	private CurrentSpeed currentSpeed = CurrentSpeed.NONE;
	private float fullSpeed;
	private float dotEatingSpeed;
	private float tunnelSpeed;
	private Boolean[] speedIntervals;
	private int dotCount;

	static class ActorPresentation extends Presentation {

		@Override
		public void drawBitmap(Bitmap sourceImage, Canvas c) {
			float top = getTop();
			float left = getLeft();
			Presentation p = this;
			
			while ((p = p.getParent()) != null) {
				top += p.getTop();
				left += p.getLeft();
			}

			// TODO: floatをintに変更して問題ないかどうか検討すること
			Rect src = getSrc();
			src.set(
				Math.round(getBgPosX()),
				Math.round(getBgPosY()),
				Math.round(getBgPosX() + getWidth()),
				Math.round(getBgPosY() + getHeight()));
			RectF dest = getDest();
			dest.set(
					left,
					top,
					left + getWidth(),
					top + getHeight());
			c.drawBitmap(sourceImage, src, dest, null);
		}
		
		@Override
		public void drawRectShape(Canvas c) {
			float top = getTop();
			float left = getLeft();
			Presentation p = this;
			
			while ((p = p.getParent()) != null) {
				top += p.getTop();
				left += p.getLeft();
			}
			
			RectF dest = getDest();
			dest.set(
					left,
					top,
					left + getWidth(),
					top + getHeight());
			
			Paint paint = getPaint();
			paint.setColor(getBgColor());
			paint.setAlpha(0xff);
			
			c.drawRect(dest, paint);
		}
		
		public float getLeft() {
			if ("pcm-bpcm".equals(getId())) {
				return super.getLeft() - 20;
			} else {
				return super.getLeft() - 4;
			}
		}

		public float getTop() {
			if ("pcm-bpcm".equals(getId())) {
				return super.getTop() - 20;
			} else {
				return super.getTop() - 4;
			}
		}

	}
	
	Presentation el = new ActorPresentation();

	public Actor(int b, PacmanGame g) {
		this.id = b;
		this.g = g;
	}
	
	// Actorを再配置
	public void A() {
	    InitPosition b = r.get(g.getPlayerCount())[this.id];
	    this.pos = new float[] {b.y * 8, b.x * 8};
	    this.posDelta = new float[] {0, 0};
	    this.tilePos = new int[] {(int) b.y * 8, (int) b.x * 8};
	    this.targetPos = new float[] {b.scatterY * 8, b.scatterX * 8};
	    this.scatterPos = new float[] {b.scatterY * 8, b.scatterX * 8};
	    this.lastActiveDir = this.dir = b.dir;
	    this.physicalSpeed = 0;
	    this.requestedDir = this.nextDir = Direction.NONE;
	    this.c(CurrentSpeed.NORMAL);
	    this.reverseDirectionsNext = this.freeToLeavePen = this.modeChangedWhileInPen = this.eatenInThisFrightMode = false;
	    this.l();
	}
	
	// Actor表示に使用するdivタグを生成: 表示位置、バックグランドのオフセットはダミー値
	public void createElement() {
		// this.el.className = "pcm-ac";
		this.el.setWidth(16);
		this.el.setHeight(16);
		this.el.setParent(g.getPlayfieldEl().getPresentation());
		g.prepareElement(this.el, 0, 0);
		g.getPlayfieldEl().addActor(this);
		this.elPos = new float[] {0, 0};
		this.elBackgroundPos = new int[] {0, 0};
	}
	// モンスターのモード設定
	public void a(GhostMode b) {
		GhostMode c = this.mode;
		this.mode = b;
		if (this.id == g.getPlayerCount() + 3
				&& (b == GhostMode.IN_PEN || c == GhostMode.IN_PEN))
			g.updateCruiseElroySpeed();
		switch (c) {
		case EXITING_FROM_PEN:
			g.setGhostExitingPenNow(false);
			break;
		case EATEN:
			if (g.getGhostEyesCount() > 0) g.decrementGhostEyesCount();
			if (g.getGhostEyesCount() == 0) g.playAmbientSound();
			break;
		}
	    switch (b) {
	    case FRIGHTENED:
	    	this.fullSpeed = g.getLevels().getGhostFrightSpeed() * 0.8f;
	    	this.tunnelSpeed = g.getLevels().getGhostTunnelSpeed() * 0.8f;
	    	this.followingRoutine = false;
	    	break;
	    case CHASE:
	    	this.fullSpeed = g.getLevels().getGhostSpeed() * 0.8f;
	    	this.tunnelSpeed = g.getLevels().getGhostTunnelSpeed() * 0.8f;
	    	this.followingRoutine = false;
	    	break;
	    case SCATTER:
	    	this.targetPos = this.scatterPos;
	    	this.fullSpeed = g.getLevels().getGhostSpeed() * 0.8f;
	    	this.tunnelSpeed = g.getLevels().getGhostTunnelSpeed() * 0.8f;
	    	this.followingRoutine = false;
	    	break;
	    case EATEN:
	    	this.tunnelSpeed = this.fullSpeed = 1.6f;
	    	this.targetPos = new float[] {s[0], s[1]};
	    	this.freeToLeavePen = this.followingRoutine = false;
	    	break;
	    case IN_PEN:
	    	this.l();
	    	this.followingRoutine = true;
	    	this.routineMoveId = -1;
	    	if (this.id == g.getPlayerCount() + 1)
		        this.routineToFollow = 2;
	    	else if (this.id == g.getPlayerCount() + 2)
		        this.routineToFollow = 1;
	    	else if (this.id == g.getPlayerCount() + 3)
		        this.routineToFollow = 3;

	    	break;
	    case EXITING_FROM_PEN:
	    	this.followingRoutine = true;
	    	this.routineMoveId = -1;
	    	if (this.id == g.getPlayerCount() + 1)
	    		this.routineToFollow = 5;
	    	else if (this.id == g.getPlayerCount() + 2)
	    		this.routineToFollow = 4;
	    	else if (this.id == g.getPlayerCount() + 3)
	    		this.routineToFollow = 6;

	    	g.setGhostExitingPenNow(true);
	    	break;
	    case ENTERING_PEN:
	    	this.followingRoutine = true;
	    	this.routineMoveId = -1;
	    	
	    	if (this.id == g.getPlayerCount() || this.id == g.getPlayerCount() + 1)
	    		this.routineToFollow = 8;
	    	else if (this.id == g.getPlayerCount() + 2)
	    		this.routineToFollow = 7;
	    	else if (this.id == g.getPlayerCount() + 3)
	    		this.routineToFollow = 9;
	    	
	    	break;
	    case RE_EXITING_FROM_PEN:
	    	this.followingRoutine = true;
	    	this.routineMoveId = -1;
	    	
	    	if (this.id == g.getPlayerCount() || this.id == g.getPlayerCount() + 1)
	    		this.routineToFollow = 11;
	    	else if (this.id == g.getPlayerCount() + 2)
	    		this.routineToFollow = 10;
	    	else if (this.id == g.getPlayerCount() + 3)
	    		this.routineToFollow = 12;
	    	
	    	break;
	    }
	    this.d();
	}
	// 追跡対象のActorを決定(Pacman or Ms.Pacman)
	void l() {
		if (this.id >= g.getPlayerCount())
			this.targetPlayerId = (int) FloatMath.floor(g.rand() * g.getPlayerCount());
	}

	// 位置, 速度の決定
	void z(Direction b) {
	    if (!g.isUserDisabledSound()) { // サウンドアイコンの更新
	    	g.setPacManSound(true);
	    	g.updateSoundIcon();
	    }
	    if (this.dir == b.getOpposite()) {
	    	this.dir = b;
	    	this.posDelta = new float[] {0, 0};
	    	if (this.currentSpeed != CurrentSpeed.PASSING_TUNNEL) this.c(CurrentSpeed.NORMAL);
	    	if (this.dir != Direction.NONE) this.lastActiveDir = this.dir;
	    	this.nextDir = Direction.NONE;
	    } else if (this.dir != b)
	    	if (this.dir == Direction.NONE) {
	    		if (g.getPlayfield().get(Integer.valueOf((int) this.pos[0]))
	    							.get(Integer.valueOf((int) this.pos[1]))
	    							.getAllowedDir().contains(b))
	    			this.dir = b;
	    	} else {
	    		PathElement p = g.getPlayfield().get(Integer.valueOf(this.tilePos[0])).get(Integer.valueOf(this.tilePos[1]));
	    		if (p != null && p.getAllowedDir().contains(b)) { // 移動可能な方向が入力された場合
	    			// 	遅延ぎみに方向入力されたかどうか判定
	    			Move c = this.dir.getMove();
	    			float[] d = new float[] {this.pos[0], this.pos[1]};
	    			d[c.getAxis()] -= c.getIncrement();
	    			int f = 0;
	    			if (d[0] == this.tilePos[0] && d[1] == this.tilePos[1]) {
	    				f = 1;
	    			} else {
	    				d[c.getAxis()] -= c.getIncrement();
	    				if (d[0] == this.tilePos[0] && d[1] == this.tilePos[1]) {
	    					f = 2;
	    				}
	    			}
	    			if (f != 0) { // 遅延ぎみに方向入力された場合、新しい移動方向に応じて位置を補正
	    				this.dir = b;
	    				this.pos[0] = this.tilePos[0];
	    				this.pos[1] = this.tilePos[1];
	    				c = this.dir.getMove();
	    				this.pos[c.getAxis()] += c.getIncrement() * f;
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
		Move d = this.dir.getMove();
		int[] f = new int[] {c[0], c[1]};
		f[d.getAxis()] += d.getIncrement() * 8; // 進行方向へ1マス先取り
		PathElement h = g.getPlayfield().get(Integer.valueOf(f[0]))
										.get(Integer.valueOf(f[1]));
		if (b && !h.isIntersection())
			h = g.getPlayfield().get(Integer.valueOf(c[0]))
								.get(Integer.valueOf(c[1])); // 交差点/行き止まり でなければ現在位置に戻る(反転済みの場合)
		
	    if (h.isIntersection())
	    	switch (this.mode) {
	    	case SCATTER: // Scatter
	    	case CHASE: // 追跡
	    	case EATEN: // プレイヤーに食べられる
		        if (!h.getAllowedDir().contains(this.dir)
		        		&& h.getAllowedDir().contains(this.dir.getOpposite())
		        		&& h.getAllowedDir().size() == 1)// 反対向きしか通れないなら反対向きを選ぶ
		        	this.nextDir = this.dir.getOpposite();
		        else { // 反対向き以外を選択可能なら、目的地に最も近い方向を選択する
		        	float max = 99999999999f;
		        	float distance = 0;
		    		Direction nDir = Direction.NONE;
		        	for (Direction k : Direction.getAllMoves()) {
		        		if (h.getAllowedDir().contains(k) && this.dir != k.getOpposite()) {
		        			d = k.getMove();
		        			float[] x = new float[] {(float) f[0], (float) f[1]};
		        			x[d.getAxis()] += d.getIncrement();
		        			distance = PacmanGame.getDistance(x, new float[] {this.targetPos[0], this.targetPos[1]});
		        			if (distance < max) {
		        				max = distance;
		        				nDir = k;
		        			}
		        		}
		        	}
		        	if (nDir != Direction.NONE) this.nextDir = nDir;
		        }
		        break;
	    	case FRIGHTENED: // ブルーモード
		        if (!h.getAllowedDir().contains(this.dir)
		        		&& h.getAllowedDir().contains(this.dir.getOpposite())
		        		&& h.getAllowedDir().size() == 1) // 反対向きしか通れないなら反対向きを選ぶ
		        	this.nextDir = this.dir.getOpposite();
		        else { // 移動可能な方向のうち反対向き以外を選択
		        	Direction nDir = Direction.NONE;
		        	do nDir = Direction.getAllMoves().get((int) FloatMath.floor(g.rand() * Direction.getAllMoves().size()));
		        	while (!h.getAllowedDir().contains(nDir)
		        				|| nDir == this.dir.getOpposite());
		        	this.nextDir = nDir;
		        }
		        break;
	      }
	}
	// tilePosとposの差分が有意になったとき呼び出される
	abstract void p(int[] b);

	// 先行入力された方向に対応
	void t() {
	    int[] b = this.tilePos;
	    float[] c;
	    float[] d;
	    switch (this.dir) {
	    case UP:
	    	c = new float[] { b[0], b[1] };
	        d = new float[] { b[0] + 3.6f, b[1] };
	        break;
	    case DOWN:
	    	c = new float[] { b[0] - 4, b[1] };
	    	d = new float[] { b[0], b[1] };
	    	break;
	    case LEFT:
	    	c = new float[] { b[0], b[1] };
	    	d = new float[] { b[0], b[1] + 3.6f };
	    	break;
	    case RIGHT:
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
	    	Move dir = this.nextDir.getMove();
	    	this.posDelta[dir.getAxis()] += dir.getIncrement();
	    }
	}

	abstract void n();

	// posの値がtilePosと一致(pos が8の倍数)したときに呼び出される
	abstract void u();

	abstract void o();

	// モンスターの巣の中/巣から出る挙動を管理(モンスター個別のモード管理)
	void v() {
	    this.routineMoveId++;
	    if (this.routineMoveId == A.get(Integer.valueOf(this.routineToFollow)).length) // ルーチンの最後に到達
	    	if (this.mode == GhostMode.IN_PEN && this.freeToLeavePen && !g.isGhostExitingPenNow()) { // 外に出る条件が満たされた
	    		if (this.eatenInThisFrightMode) this.a(GhostMode.RE_EXITING_FROM_PEN);
	    		else this.a(GhostMode.EXITING_FROM_PEN);
	    		return;
	    	} else if (this.mode == GhostMode.EXITING_FROM_PEN
	    				|| this.mode == GhostMode.RE_EXITING_FROM_PEN) { // 将に外に出むとす
	    	    this.pos = new float[] { s[0], s[1] + 4 };
	    	    this.dir = this.modeChangedWhileInPen ? Direction.RIGHT : Direction.LEFT;
	    	    GhostMode b = g.getMainGhostMode();
	    	    if (this.mode == GhostMode.RE_EXITING_FROM_PEN
	    	    		&& b == GhostMode.FRIGHTENED)
	    	    	b = g.getLastMainGhostMode();
	    	    this.a(b);
	    	    return;
	    	} else if (this.mode == GhostMode.ENTERING_PEN) { // 食べられて巣に入る
	    	    if (this.id == g.getPlayerCount() || this.freeToLeavePen)
	    	    	this.a(GhostMode.RE_EXITING_FROM_PEN); // アカベエはすぐに巣から出てくる
	    	    else {
	    		    this.eatenInThisFrightMode = true;
		    		this.a(GhostMode.IN_PEN);
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
	    this.proceedToNextRoutineMove = false;
	    this.b();
	}
	// モンスターの巣の中/巣から出る挙動を管理(表示画像決定&位置移動)
	void m() {
		
	    MoveInPen b = null;
	    MoveInPen[] mvs = A.get(Integer.valueOf(this.routineToFollow));
	    
	    if (0 <= this.routineMoveId && this.routineMoveId < mvs.length)
	    	b = A.get(Integer.valueOf(this.routineToFollow))[this.routineMoveId];
	    
	    if (b != null)
	    	if (this.speedIntervals[g.getIntervalTime()]) {
	    		Move c = this.dir.getMove();
	    		this.pos[c.getAxis()] += c.getIncrement();
		        switch (this.dir) {
		        case UP:
		        case LEFT:
		        	if (this.pos[c.getAxis()] < b.dest * 8) {
		        		this.pos[c.getAxis()] = b.dest * 8;
		        		this.proceedToNextRoutineMove = true;
		        	}
		            break;
		        case DOWN:
		        case RIGHT:
		        	if (this.pos[c.getAxis()] > b.dest * 8) {
			            this.pos[c.getAxis()] = b.dest * 8;
			            this.proceedToNextRoutineMove = true;
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
	public void d() {
		float b = 0;
	    switch (this.currentSpeed) {
	    case NORMAL:
	    	b = this.id == g.getPlayerCount()
	    			&& (this.mode == GhostMode.SCATTER || this.mode == GhostMode.CHASE)
	    			? g.getCruiseElroySpeed()
	    			: this.fullSpeed;
		    break;
	    case PACMAN_EATING_DOT:
	    	b = this.dotEatingSpeed;
	    	break;
	    case PASSING_TUNNEL:
	    	b = this.tunnelSpeed;
	    	break;
	    }
	    if (this.physicalSpeed != b) {
	      this.physicalSpeed = b;
	      this.speedIntervals = g.getSpeedIntervals(this.physicalSpeed);
	    }
	}
	// Actorの速度設定変更
	public void c(CurrentSpeed b) {
	    this.currentSpeed = b;
	    this.d();
	}
	// Actorの移動(ルーチン以外)
	void e() {
	    if (this.dir != Direction.NONE)
	    	if (this.speedIntervals[g.getIntervalTime()]) { // この判定で速度を表現
	    		Move b = this.dir.getMove();
	    		this.pos[b.getAxis()] += b.getIncrement();
	    		this.o();
	    		this.b();
	    	}
	}

	public abstract void move();
	
	// 位置移動
	public void k() {
	    float b = PacmanGame.getPlayfieldX(this.pos[1] + this.posDelta[1]);
	    float c = PacmanGame.getPlayfieldY(this.pos[0] + this.posDelta[0]);
	    if (this.elPos[0] != c || this.elPos[1] != b) {
	    	this.elPos[0] = c;
	    	this.elPos[1] = b;
	    	this.el.setLeft(b);
	    	this.el.setTop(c);
	    }
	}
	
	abstract int[] getImagePos();
	

	// Actor表示画像切り替え(アニメーション対応)&位置移動
	public void b() {
	    this.k(); //位置移動 
	    int[] b = { 0, 0 };
	    b = g.getGameplayMode() == GameplayMode.GAMEOVER
	    	|| g.getGameplayMode() == GameplayMode.KILL_SCREEN
	    		? new int[] { 0, 3 }
	    		: getImagePos();
	    if (this.elBackgroundPos[0] != b[0] || this.elBackgroundPos[1] != b[1]) {
	    	this.elBackgroundPos[0] = b[0];
	    	this.elBackgroundPos[1] = b[1];
	    	b[0] *= 16;
	    	b[1] *= 16;
	    	g.changeElementBkPos(this.el, b[1], b[0], true);
	    }
	}
	
	public void draw(Bitmap sourceImage, Canvas c) {
		if (!el.isVisible()) return;
		
		el.drawBitmap(sourceImage, c);

	}

	public GhostMode getMode() {
		return mode;
	}

	public void setMode(GhostMode mode) {
		this.mode = mode;
	}

	public int[] getTilePos() {
		return tilePos;
	}

	public Direction getRequestedDir() {
		return requestedDir;
	}
	
	public void setRequestedDir(Direction requestedDir) {
		this.requestedDir = requestedDir;
	}

	public float[] getPos() {
		return pos;
	}

	public void setPos(float[] pos) {
		this.pos = pos;
	}

	public float[] getPosDelta() {
		return posDelta;
	}

	public void setPosDelta(float[] posDelta) {
		this.posDelta = posDelta;
	}

	public float[] getElPos() {
		return elPos;
	}

	public void setElPos(float[] elPos) {
		this.elPos = elPos;
	}

	public int[] getElBackgroundPos() {
		return elBackgroundPos;
	}

	public void setElBackgroundPos(int[] elBackgroundPos) {
		this.elBackgroundPos = elBackgroundPos;
	}

	public Presentation getEl() {
		return el;
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}
	
	public float getFullSpeed() {
		return fullSpeed;
	}

	public void setFullSpeed(float fullSpeed) {
		this.fullSpeed = fullSpeed;
	}

	public float getDotEatingSpeed() {
		return dotEatingSpeed;
	}

	public void setDotEatingSpeed(float dotEatingSpeed) {
		this.dotEatingSpeed = dotEatingSpeed;
	}

	public float getTunnelSpeed() {
		return tunnelSpeed;
	}

	public void setTunnelSpeed(float tunnelSpeed) {
		this.tunnelSpeed = tunnelSpeed;
	}

	public Direction getDir() {
		return dir;
	}

	public void setDir(Direction dir) {
		this.dir = dir;
	}
	
	public boolean isReverseDirectionsNext() {
		return reverseDirectionsNext;
	}

	public void setReverseDirectionsNext(boolean reverseDirectionsNext) {
		this.reverseDirectionsNext = reverseDirectionsNext;
	}

	public int getDotCount() {
		return dotCount;
	}

	public void setDotCount(int dotCount) {
		this.dotCount = dotCount;
	}
	
	public void incrementDotCount() {
		this.dotCount++;
	}

	public boolean isModeChangedWhileInPen() {
		return modeChangedWhileInPen;
	}

	public void setModeChangedWhileInPen(boolean modeChangedWhileInPen) {
		this.modeChangedWhileInPen = modeChangedWhileInPen;
	}

	public boolean isEatenInThisFrightMode() {
		return eatenInThisFrightMode;
	}

	public void setEatenInThisFrightMode(boolean eatenInThisFrightMode) {
		this.eatenInThisFrightMode = eatenInThisFrightMode;
	}

	public boolean isFreeToLeavePen() {
		return freeToLeavePen;
	}

	public void setFreeToLeavePen(boolean freeToLeavePen) {
		this.freeToLeavePen = freeToLeavePen;
	}
	
}
