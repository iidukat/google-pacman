package jp.or.iidukat.example.pacman.entity;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import jp.or.iidukat.example.pacman.Direction;
import jp.or.iidukat.example.pacman.PacmanGame;
import jp.or.iidukat.example.pacman.entity.CutsceneActor.Cutscene.Sequence;
import jp.or.iidukat.example.pacman.entity.CutsceneActor.Cutscene.StartPoint;

public class CutsceneSteak extends CutsceneActor {

    private static final Map<Integer, Cutscene> CUTSCENES;
    static {
        Map<Integer, Cutscene> css = new HashMap<Integer, Cutscene>();
        css.put(
            Integer.valueOf(2),
            new Cutscene(
                new StartPoint(32, 9.5f),
                new Sequence[] {
                    new Sequence(
                        new Cutscene.Move(Direction.NONE, 0)),
                    new Sequence(
                        new Cutscene.Move(Direction.NONE, 0)),
                    new Sequence(
                        new Cutscene.Move(Direction.NONE, 0)),
                    new Sequence(
                        new Cutscene.Move(Direction.NONE, 0)),
                    new Sequence(
                        new Cutscene.Move(Direction.NONE, 0)),
                }));
        CUTSCENES = Collections.unmodifiableMap(css);
    }
    
    CutsceneSteak(PacmanGame g) {
        super(g);
    }

    @Override
    Cutscene getCutscene() {
        return CUTSCENES.get(Integer.valueOf(g.getCutsceneId()));
    }

    @Override
    void setMode(Cutscene cutscene) {
    }

    @Override
    public int[] getImagePos() {
        int b = g.getCutsceneSequenceId() == 1
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
        int c = 13;
        
        return new int[] { c, b };
    }
}
