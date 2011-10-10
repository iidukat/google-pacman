package jp.or.iidukat.example.pacman.entity;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

import jp.or.iidukat.example.pacman.Direction;
import jp.or.iidukat.example.pacman.Move;
import jp.or.iidukat.example.pacman.PacmanGame;

public class Pinky extends Ghost {

    private static final InitPosition INIT_POS =
        InitPosition.createGhostInitPosition(39.5f, 7, Direction.DOWN, 0, -4);

    // モンスターの巣の中での動き
    private static final Map<GhostMode, MoveInPen[]> MOVES_IN_PEN;
    static {
        Map<GhostMode, MoveInPen[]> m =
            new EnumMap<GhostMode, MoveInPen[]>(GhostMode.class);
        m.put(
            GhostMode.IN_PEN,
            new MoveInPen[] {
                new MoveInPen(39.5f, 7, Direction.DOWN, 7.625f, 0.48f),
                new MoveInPen(39.5f, 7.625f, Direction.UP, 6.375f, 0.48f),
                new MoveInPen(39.5f, 6.375f, Direction.DOWN, 7, 0.48f),
            });
        m.put(
            GhostMode.EXITING_FROM_PEN,
            new MoveInPen[] { new MoveInPen(39.5f, 7, Direction.UP, 4, y) });
        m.put(
            GhostMode.ENTERING_PEN,
            new MoveInPen[] { new MoveInPen(39.5f, 4, Direction.DOWN, 7, 1.6f) });
        m.put(
            GhostMode.RE_EXITING_FROM_PEN,
            new MoveInPen[] { new MoveInPen(39.5f, 7, Direction.UP, 4, y) });

        MOVES_IN_PEN = Collections.unmodifiableMap(m);
    }
    
    public Pinky(int b, PacmanGame g) {
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
        // Playerを先回りする
        Actor b = g.getPlayer();
        Move c = b.dir.getMove();
        this.targetPos = new float[] { b.tilePos[0], b.tilePos[1] };
        this.targetPos[c.getAxis()] += 32 * c.getIncrement();
        if (b.dir == Direction.UP) this.targetPos[1] -= 32;
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
        return 5; // 4 + this.id - 1;
    }
}
