package jp.or.iidukat.example.pacman;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

class DpadView extends View {

    PacmanGame game;

    private static final float BTN_SIZE_DP = 52f;
    private static final float BTN_GAP_DP = 6f;
    private static final float EDGE_MARGIN_DP = 20f;
    private static final float CORNER_RADIUS_DP = 10f;

    private final Paint bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint activePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint arrowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private float btnSize, gap, corner;
    private float cx, cy;

    private final RectF upRect = new RectF();
    private final RectF downRect = new RectF();
    private final RectF leftRect = new RectF();
    private final RectF rightRect = new RectF();
    private final RectF centerRect = new RectF();

    private Direction activeDir = Direction.NONE;

    public DpadView(Context context) {
        super(context);
        setBackground(null);
        bgPaint.setColor(Color.argb(80, 220, 220, 220));
        bgPaint.setStyle(Paint.Style.FILL);
        activePaint.setColor(Color.argb(160, 255, 255, 255));
        activePaint.setStyle(Paint.Style.FILL);
        arrowPaint.setColor(Color.argb(220, 60, 60, 60));
        arrowPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        float d = getResources().getDisplayMetrics().density;
        btnSize = BTN_SIZE_DP * d;
        gap = BTN_GAP_DP * d;
        corner = CORNER_RADIUS_DP * d;
        float margin = EDGE_MARGIN_DP * d;

        cx = margin + btnSize + gap;
        cy = h - margin - btnSize - gap;

        float half = btnSize / 2f;
        float step = btnSize + gap;

        upRect.set(cx - half, cy - step - half, cx + half, cy - step + half);
        downRect.set(cx - half, cy + step - half, cx + half, cy + step + half);
        leftRect.set(cx - step - half, cy - half, cx - step + half, cy + half);
        rightRect.set(cx + step - half, cy - half, cx + step + half, cy + half);
        centerRect.set(cx - half, cy - half, cx + half, cy + half);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawBtn(canvas, upRect, Direction.UP);
        drawBtn(canvas, downRect, Direction.DOWN);
        drawBtn(canvas, leftRect, Direction.LEFT);
        drawBtn(canvas, rightRect, Direction.RIGHT);
        canvas.drawRoundRect(centerRect, corner, corner, bgPaint);

        drawArrow(canvas, upRect, Direction.UP);
        drawArrow(canvas, downRect, Direction.DOWN);
        drawArrow(canvas, leftRect, Direction.LEFT);
        drawArrow(canvas, rightRect, Direction.RIGHT);
    }

    private void drawBtn(Canvas canvas, RectF rect, Direction dir) {
        canvas.drawRoundRect(rect, corner, corner, dir == activeDir ? activePaint : bgPaint);
    }

    private void drawArrow(Canvas canvas, RectF rect, Direction dir) {
        float s = btnSize * 0.32f;
        float mx = rect.centerX();
        float my = rect.centerY();
        Path path = new Path();
        switch (dir) {
            case UP:
                path.moveTo(mx, my - s);
                path.lineTo(mx - s, my + s * 0.6f);
                path.lineTo(mx + s, my + s * 0.6f);
                break;
            case DOWN:
                path.moveTo(mx, my + s);
                path.lineTo(mx - s, my - s * 0.6f);
                path.lineTo(mx + s, my - s * 0.6f);
                break;
            case LEFT:
                path.moveTo(mx - s, my);
                path.lineTo(mx + s * 0.6f, my - s);
                path.lineTo(mx + s * 0.6f, my + s);
                break;
            case RIGHT:
                path.moveTo(mx + s, my);
                path.lineTo(mx - s * 0.6f, my - s);
                path.lineTo(mx - s * 0.6f, my + s);
                break;
            default:
                return;
        }
        path.close();
        canvas.drawPath(path, arrowPaint);
    }

    private Direction hitTest(float x, float y) {
        if (upRect.contains(x, y)) return Direction.UP;
        if (downRect.contains(x, y)) return Direction.DOWN;
        if (leftRect.contains(x, y)) return Direction.LEFT;
        if (rightRect.contains(x, y)) return Direction.RIGHT;
        return Direction.NONE;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN: {
                Direction dir = hitTest(event.getX(), event.getY());
                if (dir != Direction.NONE) {
                    setActive(dir);
                    return true;
                }
                return false;
            }
            case MotionEvent.ACTION_MOVE:
                setActive(hitTest(event.getX(), event.getY()));
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                setActive(Direction.NONE);
                return true;
        }
        return false;
    }

    private void setActive(Direction dir) {
        activeDir = dir;
        if (game != null) game.setDpadDir(dir);
        invalidate();
    }
}
