package jp.or.iidukat.example.pacman.entity;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import jp.or.iidukat.example.pacman.CurrentSpeed;
import jp.or.iidukat.example.pacman.Direction;
import jp.or.iidukat.example.pacman.GameplayMode;
import jp.or.iidukat.example.pacman.Move;
import jp.or.iidukat.example.pacman.PacmanGame;
import jp.or.iidukat.example.pacman.Presentation;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

public abstract class Actor {

    static class InitPosition {
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
    static final Map<Integer, InitPosition[]> r;
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


    
	final int id;
	final PacmanGame g;
	float[] pos;
	float[] posDelta;
	int[] tilePos;
	int[] lastGoodTilePos;
	private float[] elPos;
	int[] elBackgroundPos;
	Direction dir = Direction.NONE;
	Direction lastActiveDir = Direction.NONE;
	float speed;
	float physicalSpeed;
	Direction nextDir = Direction.NONE;
	CurrentSpeed currentSpeed = CurrentSpeed.NONE;
	float fullSpeed;
	float tunnelSpeed;
	Boolean[] speedIntervals;
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
	public abstract void A();
	
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
	
	
	// tilePosとposの差分が有意になったとき呼び出される
	abstract void p(int[] b);

	abstract void n();

	// posの値がtilePosと一致(pos が8の倍数)したときに呼び出される
	abstract void u();

	abstract void o();

	// Actorの速度設定(currentSpeedプロパティを利用)
	public abstract void d();
	
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

	public int[] getTilePos() {
		return tilePos;
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
	
	public int getDotCount() {
		return dotCount;
	}

	public void setDotCount(int dotCount) {
		this.dotCount = dotCount;
	}
	
	public void incrementDotCount() {
		this.dotCount++;
	}
	
}
