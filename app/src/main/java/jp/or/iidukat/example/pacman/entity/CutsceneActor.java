package jp.or.iidukat.example.pacman.entity;

import jp.or.iidukat.example.pacman.Direction;
import jp.or.iidukat.example.pacman.Direction.Move;
import jp.or.iidukat.example.pacman.PacmanGame;
import jp.or.iidukat.example.pacman.entity.CutsceneActor.Cutscene.StartPoint;
import android.graphics.Bitmap;

public abstract class CutsceneActor extends Actor {

    double speed;

    CutsceneActor(Bitmap sourceImage, PacmanGame game) {
        super(sourceImage, game);
    }

    @Override
    public void init() {
        super.init();
        double[] start = getStartPoint(getCutscene());
        pos = new double[] { start[0] * 8, start[1] * 8 };
    }

    final double[] getStartPoint(Cutscene cutscene) {
        StartPoint p = cutscene.startPoint;
        return new double[] { p.y, p.x };
    }

    @Override
    public final void move() {
        Move mv = dir.getMove();
        pos[mv.getAxis()] += mv.getIncrement() * speed;
        updateAppearance();
    }

    @Override
    final boolean canAppear() {
        return true;
    }
    
    @Override
    public final double getFieldX() {
        return PacmanGame.getFieldX(pos[1]);
    }
    
    @Override
    public final double getFieldY() {
        return PacmanGame.getFieldY(pos[0]);
    }
    
    abstract Cutscene getCutscene();

    public final void setupSequence() {
        Cutscene c = getCutscene();
        setMove(c);
        setMode(c);
    }

    private void setMove(Cutscene cutscene) {
        dir = cutscene.sequences[game.getCutsceneSequenceId()].move.dir;
        speed = cutscene.sequences[game.getCutsceneSequenceId()].move.speed;
    }

    abstract void setMode(Cutscene cutscene);

    static class Cutscene {
        final StartPoint startPoint;
        final Sequence[] sequences;

        Cutscene(StartPoint startPoint, Sequence[] sequences) {
            this.startPoint = startPoint;
            this.sequences = sequences;
        }

        static class StartPoint {
            final double x;
            final double y;

            StartPoint(double x, double y) {
                this.x = x;
                this.y = y;
            }
        }

        static class Sequence {
            final Move move;

            Sequence(Move move) {
                this.move = move;
            }
        }

        static class Move {
            final Direction dir;
            final double speed;

            Move(Direction dir, double speed) {
                this.dir = dir;
                this.speed = speed;
            }
        }

    }
}
