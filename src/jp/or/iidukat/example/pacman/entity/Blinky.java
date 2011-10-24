package jp.or.iidukat.example.pacman.entity;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

import jp.or.iidukat.example.pacman.Direction;
import jp.or.iidukat.example.pacman.PacmanGame;
import android.graphics.Bitmap;

public class Blinky extends Ghost {

    private static final InitPosition INIT_POS =
        InitPosition.createGhostInitPosition(39.5f, 4, Direction.LEFT, 57, -4);

    // モンスターの巣の中での動き
    private static final Map<GhostMode, MoveInPen[]> MOVES_IN_PEN;
    static {
        Map<GhostMode, MoveInPen[]> m =
            new EnumMap<GhostMode, MoveInPen[]>(GhostMode.class);
        m.put(
            GhostMode.ENTERING_PEN,
            new MoveInPen[] { new MoveInPen(39.5f, 4, Direction.DOWN, 7, 1.6f) });
        m.put(
            GhostMode.RE_LEAVING_FROM_PEN,
            new MoveInPen[] { new MoveInPen(39.5f, 7, Direction.UP, 4, EXIT_PEN_SPEED) });

        MOVES_IN_PEN = Collections.unmodifiableMap(m);
    }

    public Blinky(Bitmap sourceImage, int b, PacmanGame g) {
        super(sourceImage, b, g);
    }

    @Override
    InitPosition getInitPosition() {
        return INIT_POS;
    }
    
    // ターゲットポジションを決定
    @Override
    public void B() {
        // Playerを追尾する
        Actor b = g.getPlayer();
        if (g.getDotsRemaining() < g.getLevels().getElroyDotsLeftPart1()
                && this.mode == GhostMode.SCATTER
                && (!g.isLostLifeOnThisLevel() || g.getGhosts()[3].mode != GhostMode.IN_PEN)) {
            this.targetPos = new float[] { b.tilePos[0], b.tilePos[1] };
        } else if (this.mode == GhostMode.CHASE) {
            this.targetPos = new float[] { b.tilePos[0], b.tilePos[1] };
        }
    }
    
    @Override
    float getNormalSpeed() {
        return (this.mode == GhostMode.SCATTER
                    || this.mode == GhostMode.CHASE)
                    ? g.getCruiseElroySpeed()
                    : this.fullSpeed;
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
        return 4; // 4 + this.id - 1;
    }
}
