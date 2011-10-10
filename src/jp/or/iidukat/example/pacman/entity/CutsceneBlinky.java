package jp.or.iidukat.example.pacman.entity;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import jp.or.iidukat.example.pacman.Direction;
import jp.or.iidukat.example.pacman.GameplayMode;
import jp.or.iidukat.example.pacman.PacmanGame;
import jp.or.iidukat.example.pacman.Presentation;
import jp.or.iidukat.example.pacman.entity.CutsceneActor.Cutscene.Move;
import jp.or.iidukat.example.pacman.entity.CutsceneActor.Cutscene.StartPoint;
import android.util.FloatMath;

public class CutsceneBlinky extends CutsceneActor {

    private static enum Mode {
        CHASE, FRIGHTENED, STUCK, TORN_CLOTH, BUG
    }

    private static class Sequence extends Cutscene.Sequence {
        private Mode mode;
        Sequence(Move move, Mode mode) {
            super(move);
            this.mode = mode;
        }
        
    }

    private static final Map<Integer, Cutscene> CUTSCENES;
    static {
        Map<Integer, Cutscene> css = new HashMap<Integer, Cutscene>();
        css.put(
            Integer.valueOf(1),
            new Cutscene(
                new StartPoint(68.2f, 9),
                new Sequence[] {
                    new Sequence(
                        new Cutscene.Move(Direction.LEFT, 0.78f * 0.8f * 2),
                        Mode.CHASE),
                    new Sequence(
                        new Cutscene.Move(Direction.LEFT, 0),
                        Mode.CHASE),
                    new Sequence(
                        new Cutscene.Move(Direction.RIGHT, 0.8f),
                        Mode.FRIGHTENED),
                }));
        css.put(
            Integer.valueOf(2),
            new Cutscene(
                new StartPoint(70.2f, 9),
                new Sequence[] {
                    new Sequence(
                        new Cutscene.Move(Direction.LEFT, 0.78f * 0.8f * 2),
                        Mode.CHASE),
                    new Sequence(
                        new Cutscene.Move(Direction.LEFT, 0.1f * 0.8f),
                        Mode.CHASE),
                    new Sequence(
                        new Cutscene.Move(Direction.LEFT, 0),
                        Mode.CHASE),
                    new Sequence(
                        new Cutscene.Move(Direction.LEFT, 0),
                        Mode.STUCK),
                    new Sequence(
                        new Cutscene.Move(Direction.LEFT, 0),
                        Mode.STUCK)
                }));
        css.put(
            Integer.valueOf(3),
            new Cutscene(
                new StartPoint(70.2f, 9),
                new Sequence[] {
                    new Sequence(
                        new Cutscene.Move(Direction.LEFT, 0.78f * 0.8f * 2),
                        Mode.TORN_CLOTH),
                    new Sequence(
                        new Cutscene.Move(Direction.RIGHT, 0.78f * 0.8f * 2),
                        Mode.BUG)
                }));

        CUTSCENES = Collections.unmodifiableMap(css);
    }
    
    private Mode mode;
    private Presentation el = new ActorPresentation();
    
    CutsceneBlinky(PacmanGame g) {
        super(g);
    }
    

    @Override
    Presentation getEl() {
        return el;
    }
    
    @Override
    Cutscene getCutscene() {
        return CUTSCENES.get(Integer.valueOf(g.getCutsceneId()));
    }

    @Override
    void setMode(Cutscene cutscene) {
        Sequence s =
            (CutsceneBlinky.Sequence) cutscene.sequences[g.getCutsceneSequenceId()]; 
        mode = s.mode;
        
    }

    // モンスターの表示画像決定
    @Override
    int[] getImagePos() {
        int b = 0;
        int c = 0;
        switch (this.mode) {
        case FRIGHTENED:
            // ブルーモード.ただし、食べられてはいない
            b = 0;
            c = 8;
            // ブルーモード時間切れ間近の青白明滅
            if (g.getFrightModeTime() < g.getLevels().getFrightTotalTime() - g.getLevels().getFrightTime()
                    && FloatMath.floor(g.getFrightModeTime() / g.getTiming()[1]) % 2 == 0)
                b += 2;
    
            b += (int) (Math.floor(g.getGlobalTime() / 16) % 2); // ブルーモードの画像切り替え
            break;
        case TORN_CLOTH:
            b = 6;
            c = 8;
            b += (int) (Math.floor(g.getGlobalTime() / 16) % 2);
            break;
        case BUG:
            b = 6;
            c = 9;
            c += (int) (Math.floor(g.getGlobalTime() / 16) % 2);
            break;
        case STUCK:
            b = g.getCutsceneSequenceId() == 3 ? 6 : 7;
            c = 11;
            break;
        default: // 通常時の画像表示
            switch (getDir()) {
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
            c = 4;
            if (getSpeed() > 0 || g.getGameplayMode() != GameplayMode.CUTSCENE)
                b += (int) (Math.floor(g.getGlobalTime() / 16) % 2);
        }
        return new int[] { c, b };
    }
    
}
