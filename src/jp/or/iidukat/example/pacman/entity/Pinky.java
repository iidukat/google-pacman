package jp.or.iidukat.example.pacman.entity;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

import jp.or.iidukat.example.pacman.Direction;
import jp.or.iidukat.example.pacman.Direction.Move;
import jp.or.iidukat.example.pacman.PacmanGame;
import android.graphics.Bitmap;

public class Pinky extends Ghost {

    private static final InitPosition INIT_POS =
        InitPosition.createGhostInitPosition(39.5f, 7, Direction.DOWN, 0, -4);

    // movements of Pinky in the pen
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
            GhostMode.LEAVING_PEN,
            new MoveInPen[] { new MoveInPen(39.5f, 7, Direction.UP, 4, LEAVING_PEN_SPEED) });
        m.put(
            GhostMode.ENTERING_PEN,
            new MoveInPen[] { new MoveInPen(39.5f, 4, Direction.DOWN, 7, 1.6f) });
        m.put(
            GhostMode.RE_LEAVING_FROM_PEN,
            new MoveInPen[] { new MoveInPen(39.5f, 7, Direction.UP, 4, LEAVING_PEN_SPEED) });

        MOVES_IN_PEN = Collections.unmodifiableMap(m);
    }
    
    Pinky(Bitmap sourceImage, PacmanGame game) {
        super(sourceImage, game);
    }

    @Override
    InitPosition getInitPosition() {
        return INIT_POS;
    }

    @Override
    public void updateTargetPos() {
        if (this.mode != GhostMode.CHASE) {
            return;
            
        }
        // go to the player's destination.
        PlayfieldActor pacman = game.getPacman();
        Move mv = pacman.dir.getMove();
        this.targetPos = new float[] { pacman.tilePos[0], pacman.tilePos[1] };
        this.targetPos[mv.getAxis()] += 32 * mv.getIncrement();
        if (pacman.dir == Direction.UP) {
            this.targetPos[1] -= 32;
        }
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
        return 5;
    }
}
