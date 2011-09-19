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

public class E {

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

    private static final int[] s = {32, 312}; // モンスターの巣の入り口の位置

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
    
	private final int id;
	private final PacmanGame g;
	private boolean ghost;
	private GhostMode mode = GhostMode.NONE;
	private float[] pos;
	private float[] posDelta;
	private int[] tilePos;
	private int[] lastGoodTilePos;
	private float[] elPos;
	private int[] elBackgroundPos;
	private float[] targetPos;
	private float[] scatterPos;
	private Direction dir;
	private Direction lastActiveDir;
	private float speed;
	private float physicalSpeed;
	private Direction requestedDir;
	private Direction nextDir;
	private boolean reverseDirectionsNext;
	private boolean freeToLeavePen;
	private boolean modeChangedWhileInPen;
	private boolean eatenInThisFrightMode;
	private boolean followingRoutine;
	private boolean proceedToNextRoutineMove;
	private int routineToFollow;
	private int routineMoveId;
	private int targetPlayerId;
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
	
	private Presentation el = new ActorPresentation();

	public E(int b, PacmanGame g) {
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
		this.el.setId("actor" + this.id);
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
	void p(int[] b) {
	    g.setTilesChanged(true);
	    if (this.reverseDirectionsNext) { // 方向を反転する(この判定がtrueになるのはモンスターのみ)
	    	this.dir = this.dir.getOpposite();
	    	this.nextDir = Direction.NONE;
	    	this.reverseDirectionsNext = false;
	    	this.i(true);
	    }
	    if (!this.ghost
	    		&& !g.getPlayfield().get(Integer.valueOf(b[0]))
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
	    if (!this.ghost
	    		&& g.getPlayfield().get(Integer.valueOf(b[0]))
	    							.get(Integer.valueOf(b[1]))
	    							.getDot() != 0)
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
	    if (!this.ghost && this.pos[0] == PacmanGame.getV()[0]
	        && (this.pos[1] == PacmanGame.getV()[1] || this.pos[1] == PacmanGame.getV()[1] + 8))
	        g.eatFruit(this.id);
	}

	// posの値がtilePosと一致(pos が8の倍数)したときに呼び出される
	void u() {
	    this.n();
	    if (this.ghost) this.i(false); // モンスターの交差点/行き止まりでの進行方向決定
	    PathElement b =
	    	g.getPlayfield().get(Integer.valueOf((int) this.pos[0]))
	    					.get(Integer.valueOf((int) this.pos[1]));
	    if (b.isIntersection()) // 行き止まり/交差点にて
	    	if (this.nextDir != Direction.NONE && b.getAllowedDir().contains(this.nextDir)) { // nextDirで指定された方向へ移動可能
		        if (this.dir != Direction.NONE) this.lastActiveDir = this.dir;
		        this.dir = this.nextDir;
		        this.nextDir = Direction.NONE;
		        if (!this.ghost) { // 先行入力された移動方向分を更新(メソッドtを参照)
		        	this.pos[0] += this.posDelta[0];
			        this.pos[1] += this.posDelta[1];
			        this.posDelta = new float[] {0, 0};
		        }
	    } else if (!b.getAllowedDir().contains(this.dir)) { // nextDirもdirも移動不可だったら、停止
	    	if (this.dir != Direction.NONE) this.lastActiveDir = this.dir;
	    	this.nextDir = this.dir = Direction.NONE;
	    	this.c(CurrentSpeed.NORMAL);
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
	    	g.getPlayfield().get(Integer.valueOf(d[0]))
	    					.get(Integer.valueOf(d[1]));
	    if (!this.ghost
	    		&& this.nextDir != Direction.NONE
	    		&& pe.isIntersection()
	    		&& pe.getAllowedDir().contains(this.nextDir))
	    		this.t();
	}
	// ターゲットポジションを決定
	public void B() {
	    if (this.id == g.getPlayerCount()
	    		&& g.getDotsRemaining() < g.getLevels().getElroyDotsLeftPart1()
	    		&& this.mode == GhostMode.SCATTER
	    		&& (!g.isLostLifeOnThisLevel() || g.getActors()[g.getPlayerCount() + 3].mode != GhostMode.IN_PEN)) {
	    	E b = g.getActors()[this.targetPlayerId];
	    	this.targetPos = new float[] { b.tilePos[0], b.tilePos[1] };
	    } else if (this.ghost && this.mode == GhostMode.CHASE) {
	    	E b = g.getActors()[this.targetPlayerId];
    		Move c = b.dir.getMove();
	    	if (this.id == g.getPlayerCount()) {
	    		this.targetPos = new float[] { b.tilePos[0], b.tilePos[1] };
	    	} else if (this.id == g.getPlayerCount() + 1) {
	    		this.targetPos = new float[] { b.tilePos[0], b.tilePos[1] };
	    		this.targetPos[c.getAxis()] += 32 * c.getIncrement();
	    		if (b.dir == Direction.UP) this.targetPos[1] -= 32;
	    	} else if (this.id == g.getPlayerCount() + 2) {
	    		E d = g.getActors()[g.getPlayerCount()];
	    		float[] f = new float[] { b.tilePos[0], b.tilePos[1] };
	    		f[c.getAxis()] += 16 * c.getIncrement();
	    		if (b.dir == Direction.UP) f[1] -= 16;
	    		this.targetPos[0] = f[0] * 2 - d.tilePos[0];
	    		this.targetPos[1] = f[1] * 2 - d.tilePos[1];
	    	} else if (this.id == g.getPlayerCount() + 3) {
	    		float distance = PacmanGame.getDistance(b.tilePos, this.tilePos);
	    		this.targetPos = distance > 64 ? new float[] { b.tilePos[0], b.tilePos[1] } : this.scatterPos;
	    	}
	    }
	}
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

	public void move() {
	    if (g.getGameplayMode() == GameplayMode.ORDINARY_PLAYING
	    		|| this.ghost && g.getGameplayMode() == GameplayMode.GHOST_DIED
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
	// Pacman, Ms.Pacman表示画像決定(アニメーション対応)
	int[] s() {
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
	// モンスターの表示画像決定
	int[] r() {
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

	// Actor表示画像切り替え(アニメーション対応)&位置移動
	public void b() {
	    this.k(); //位置移動 
	    int[] b = { 0, 0 };
	    b = g.getGameplayMode() == GameplayMode.GAMEOVER
	    	|| g.getGameplayMode() == GameplayMode.KILL_SCREEN
	    		? new int[] { 0, 3 }
	    		: this.ghost
	    			? this.r()
	    			: this.s();
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

	
	public boolean isGhost() {
		return ghost;
	}

	public void setGhost(boolean ghost) {
		this.ghost = ghost;
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
