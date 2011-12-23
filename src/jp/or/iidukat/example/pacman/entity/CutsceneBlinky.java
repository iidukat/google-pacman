package jp.or.iidukat.example.pacman.entity;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import jp.or.iidukat.example.pacman.Direction;
import jp.or.iidukat.example.pacman.PacmanGame;
import jp.or.iidukat.example.pacman.PacmanGame.GameplayMode;
import jp.or.iidukat.example.pacman.entity.CutsceneActor.Cutscene.Move;
import jp.or.iidukat.example.pacman.entity.CutsceneActor.Cutscene.StartPoint;
import android.graphics.Bitmap;
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
    
    CutsceneBlinky(Bitmap sourceImage, PacmanGame game) {
        super(sourceImage, game);
    }
    
    @Override
    Cutscene getCutscene() {
        return CUTSCENES.get(Integer.valueOf(game.getCutsceneId()));
    }

    @Override
    void setMode(Cutscene cutscene) {
        Sequence s =
            (CutsceneBlinky.Sequence) cutscene.sequences[game.getCutsceneSequenceId()]; 
        mode = s.mode;
        
    }

    // determine the display image of Blinky
    @Override
    int[] getImagePos() {
        int x = 0;
        int y = 0;
        switch (this.mode) {
        case FRIGHTENED:
            // in the frighten mode, however, not eaten.
            x = 0;
            y = 8;
            // blinking before the end of the frighten mode
            if (game.getFrightModeTime()
                    < game.getLevels().getFrightTotalTime()
                        - game.getLevels().getFrightTime()
                && FloatMath.floor(
                        game.getFrightModeTime() / game.getTiming()[1])
                        % 2 == 0) {
                x += 2;
            }
            x += (int) (Math.floor(game.getGlobalTime() / 16) % 2); // switching the display image in the frighten mode
            break;
        case TORN_CLOTH:
            x = 6;
            y = 8;
            x += (int) (Math.floor(game.getGlobalTime() / 16) % 2);
            break;
        case BUG:
            x = 6;
            y = 9;
            y += (int) (Math.floor(game.getGlobalTime() / 16) % 2);
            break;
        case STUCK:
            x = game.getCutsceneSequenceId() == 3 ? 6 : 7;
            y = 11;
            break;
        default: // display the image in the ordinary way
            switch (dir) {
            case LEFT:
                x = 4;
                break;
            case RIGHT:
                x = 6;
                break;
            case UP:
                x = 0;
                break;
            case DOWN:
                x = 2;
                break;
            }
            y = 4;
            if (speed > 0 || game.getGameplayMode() != GameplayMode.CUTSCENE) {
                x += (int) (Math.floor(game.getGlobalTime() / 16) % 2);
            }
        }
        return new int[] { y, x };
    }
    
}
