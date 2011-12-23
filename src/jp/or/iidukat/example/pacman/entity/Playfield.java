package jp.or.iidukat.example.pacman.entity;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.or.iidukat.example.pacman.Direction;
import jp.or.iidukat.example.pacman.PacmanGame;
import jp.or.iidukat.example.pacman.PacmanGame.GameplayMode;
import jp.or.iidukat.example.pacman.entity.Playfield.PathElement.Dot;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.FloatMath;

public class Playfield extends BaseEntity {

    private static class Path {
        final int x;
        final int y;
        final int w;
        final int h;
        final boolean tunnel;

        private Path(int x, int y, int w, int h) {
            this(x, y, w, h, false);
        }

        private Path(int x, int y, int w, int h, boolean tunnel) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
            this.tunnel = tunnel;
        }

        static Path createHorizontalPath(int x, int y, int w) {
            return new Path(x, y, w, 0);
        }

        static Path createVerticalPath(int x, int y, int h) {
            return new Path(x, y, 0, h);
        }

        static Path createTunnelPath(int x, int y, int w) {
            return new Path(x, y, w, 0, true);
        }
    }

    // a element of the path array has either (x, y, w) or (x, y, h).
    // the element has w field's value: horizontal path
    // the element has h field's value: vertical path
    // (x, y) is a coordinate of start point.
    // Both h and w represent a length of path.
    // [exception] If the type field's value is 1, the path is a warp tunnel.
    //
    // upper left:(5, 1), lower left:(5, 15),
    // upper right:(60, 1), lower right:(60, 15).
    private static final Path[] PATHS = {
        Path.createHorizontalPath(5, 1, 56),
        Path.createHorizontalPath(5, 4, 5),
        Path.createVerticalPath(5, 1, 4),
        Path.createVerticalPath(9, 1, 12),
        Path.createVerticalPath(5, 12, 4),
        Path.createVerticalPath(10, 12, 4),
        Path.createHorizontalPath(5, 15, 16),
        Path.createHorizontalPath(5, 12, 31),
        Path.createVerticalPath(60, 1, 4),
        Path.createVerticalPath(54, 1, 4),
        Path.createVerticalPath(19, 1, 12),
        Path.createHorizontalPath(19, 4, 26),
        Path.createHorizontalPath(13, 5, 7),
        Path.createVerticalPath(13, 5, 4),
        Path.createHorizontalPath(13, 8, 3),
        Path.createVerticalPath(56, 4, 9),
        Path.createHorizontalPath(48, 4, 13),
        Path.createVerticalPath(48, 1, 12),
        Path.createVerticalPath(60, 12, 4),
        Path.createHorizontalPath(44, 15, 17),
        Path.createVerticalPath(54, 12, 4),
        Path.createHorizontalPath(44, 12, 17),
        Path.createVerticalPath(44, 1, 15),
        Path.createHorizontalPath(41, 13, 4),
        Path.createVerticalPath(41, 13, 3),
        Path.createVerticalPath(38, 13, 3),
        Path.createHorizontalPath(38, 15, 4),
        Path.createHorizontalPath(35, 10, 10),
        Path.createVerticalPath(35, 1, 15),
        Path.createHorizontalPath(35, 13, 4),
        Path.createVerticalPath(21, 12, 4),
        Path.createVerticalPath(24, 12, 4),
        Path.createHorizontalPath(24, 15, 12),
        Path.createVerticalPath(27, 4, 9),
        Path.createHorizontalPath(52, 9, 5),
        Path.createTunnelPath(56, 8, 10),
        Path.createTunnelPath(1, 8, 9),
    };

    private static final Path[] PATHS_HAVING_NO_DOT = {
        Path.createHorizontalPath(1, 8, 8),
        Path.createHorizontalPath(57, 8, 9),
        Path.createVerticalPath(44, 2, 10),
        Path.createVerticalPath(35, 5, 7),
        Path.createHorizontalPath(36, 4, 8),
        Path.createHorizontalPath(36, 10, 8),
        Path.createHorizontalPath(39, 15, 2),
    };

    public static class Position {
        final int x;
        final int y;

        Position(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }

    private static final Position[] ENERGIZER_POSITIONS = {
        new Position(5, 15),
        new Position(5, 3),
        new Position(15, 8),
        new Position(60, 3),
        new Position(60, 15),
    };

    // warp tunnel
    static final Position[] TUNNEL_POS = {
        new Position(2, 8),
        new Position(63, 8),
    };

    static final int[] PEN_ENTRANCE = {32, 312}; // the entrance position of the ghost's nest
    static final int[] FRUIT_POSITION = { 80, 312 };

    public static class PathElement {
        public static enum Dot {
            NONE(0), FOOD(1), ENERGIZER(2);
            
            private int dot;
            
            Dot(int dot) {
                this.dot = dot;
            }
            
            public int getDot() {
                return dot;
            }
        }
        
        private boolean path;
        private Dot dot;
        private boolean intersection;
        private boolean tunnel;
        private EnumSet<Direction> allowedDirs;
        
        void setAllowedDir(EnumSet<Direction> allowedDir) {
            this.allowedDirs = allowedDir;
        }
        
        public Dot getDot() {
            return dot;
        }
        
        void setDot(Dot dot) {
            this.dot = dot;
        }

        boolean isIntersection() {
            return intersection;
        }
        
        void setIntersection(boolean intersection) {
            this.intersection = intersection;
        }

        boolean isPath() {
            return path;
        }
        
        void setPath(boolean path) {
            this.path = path;
        }

        boolean isTunnel() {
            return tunnel;
        }
        
        void setTunnel(boolean tunnel) {
            this.tunnel = tunnel;
        }
        
        boolean allow(Direction dir) {
            return allowedDirs.contains(dir);
        }
        
        boolean allowOnlyOpposite(Direction dir) {
            return !allowedDirs.contains(dir)
                    && allowedDirs.contains(dir.getOpposite())
                    && allowedDirs.size() == 1;
        }
    }
    
    private final PacmanGame game;

    private int playfieldWidth;
    private int playfieldHeight;
    private int dotsRemaining;
    private int dotsEaten;
    private Map<Integer, Map<Integer, PathElement>> playfield;

    private Pacman pacman;
    private Ghost[] ghosts;

    private Door door;
    private Map<Integer, Map<Integer, DotElement>> foods;
    private Energizer[] energizers;
    private Fruit fruit;
    private Ready ready;
    private GameOver gameover;

    public Playfield(Bitmap sourceImage, PacmanGame game) {
        super(sourceImage, true);
        this.game = game;
    }

    public Map<Integer, Map<Integer, PathElement>> getPlayfield() {
        return playfield;
    }

    public void init() {
        Appearance a = getAppearance();
        a.setTop(16);
        a.setLeft(0);
        a.setWidth(464);
        a.setHeight(136);
        a.setOrder(99);
    }

    public void reset() {
        clearChildren();
        dotsRemaining = 0;
        dotsEaten = 0;
        getAppearance().prepareBkPos(256, 0);
        determinePlayfieldDimensions();
        preparePlayfield();
        preparePaths();
        prepareAllowedDirections();
        createPlayfieldElements();
    }

    private void determinePlayfieldDimensions() {
        playfieldWidth = 0;
        playfieldHeight = 0;
        for (Path p : PATHS) {
            if (p.w > 0) {
                int x = p.x + p.w - 1;
                if (x > playfieldWidth)
                    playfieldWidth = x;
            } else {
                int y = p.y + p.h - 1;
                if (y > playfieldHeight)
                    playfieldHeight = y;
            }
        }
    }

    private void preparePlayfield() {
        playfield = new HashMap<Integer, Map<Integer, PathElement>>();
        for (int y = 0; y <= playfieldHeight + 1; y++) {
            Map<Integer, PathElement> row = new HashMap<Integer, PathElement>();
            for (int x = -2; x <= playfieldWidth + 1; x++) {
                PathElement p = new PathElement();
                p.setPath(false);
                p.setDot(Dot.NONE);
                p.setIntersection(false);
                row.put(Integer.valueOf(x * 8), p);
            }
            playfield.put(Integer.valueOf(y * 8), row);
        }
    }

    private void preparePaths() {
        for (Path p : PATHS) {
            if (p.w > 0) {
                int y = p.y * 8;
                for (int x = p.x * 8; x <= (p.x + p.w - 1) * 8; x += 8) {
                    PathElement pe = playfield.get(Integer.valueOf(y)).get(Integer.valueOf(x));
                    pe.setPath(true);
                    if (pe.getDot() == Dot.NONE) {
                        pe.setDot(Dot.FOOD);
                        dotsRemaining++;
                    }
                    pe.setTunnel(
                            !p.tunnel || x != p.x * 8 && x != (p.x + p.w - 1) * 8
                                ? p.tunnel
                                : false);
                }
                playfield.get(Integer.valueOf(y)).get(Integer.valueOf(p.x * 8))
                        .setIntersection(true);
                playfield.get(Integer.valueOf(y))
                        .get(Integer.valueOf((p.x + p.w - 1) * 8))
                        .setIntersection(true);
            } else {
                int x = p.x * 8;
                for (int y = p.y * 8; y <= (p.y + p.h - 1) * 8; y += 8) {
                    PathElement pe = playfield.get(Integer.valueOf(y)).get(Integer.valueOf(x));
                    if (pe.isPath()) {
                        pe.setIntersection(true);
                    }
                    pe.setPath(true);
                    if (pe.getDot() == Dot.NONE) {
                        pe.setDot(Dot.FOOD);
                        dotsRemaining++;
                    }
                    pe.setTunnel(
                            !p.tunnel || y != p.y * 8 && y != (p.y + p.h - 1) * 8
                                ? p.tunnel
                                : false);
                }
                playfield.get(Integer.valueOf(p.y * 8)).get(Integer.valueOf(x))
                        .setIntersection(true);
                playfield.get(Integer.valueOf((p.y + p.h - 1) * 8))
                        .get(Integer.valueOf(x)).setIntersection(true);
            }
        }
        for (Path p : PATHS_HAVING_NO_DOT) {
            if (p.w != 0) {
                for (int x = p.x * 8; x <= (p.x + p.w - 1) * 8; x += 8) {
                    playfield.get(Integer.valueOf(p.y * 8))
                                .get(Integer.valueOf(x))
                                .setDot(Dot.NONE);
                    dotsRemaining--;
                }
            } else {
                for (int y = p.y * 8; y <= (p.y + p.h - 1) * 8; y += 8) {
                    playfield.get(Integer.valueOf(y))
                                .get(Integer.valueOf(p.x * 8))
                                .setDot(Dot.NONE);
                    dotsRemaining--;
                }
            }
        }
    }

    private void prepareAllowedDirections() {
        for (int y = 8; y <= playfieldHeight * 8; y += 8) {
            for (int x = 8; x <= playfieldWidth * 8; x += 8) {
                PathElement pe = playfield.get(Integer.valueOf(y))
                                        .get(Integer.valueOf(x));
                EnumSet<Direction> allowedDir = EnumSet.noneOf(Direction.class);
                if (playfield.get(Integer.valueOf(y - 8))
                        .get(Integer.valueOf(x)).isPath()) {
                    allowedDir.add(Direction.UP);
                }
                if (playfield.get(Integer.valueOf(y + 8))
                        .get(Integer.valueOf(x)).isPath()) {
                    allowedDir.add(Direction.DOWN);
                }
                if (playfield.get(Integer.valueOf(y))
                        .get(Integer.valueOf(x - 8)).isPath()) {
                    allowedDir.add(Direction.LEFT);
                }
                if (playfield.get(Integer.valueOf(y))
                        .get(Integer.valueOf(x + 8)).isPath()) {
                    allowedDir.add(Direction.RIGHT);
                }
                pe.setAllowedDir(allowedDir);
            }
        }
    }

    private void createDotElements() {
        foods = new HashMap<Integer, Map<Integer, DotElement>>();
        for (int y = 8; y <= playfieldHeight * 8; y += 8) {
            Map<Integer, DotElement> row = new HashMap<Integer, DotElement>();
            foods.put(Integer.valueOf(y), row);
            for (int x = 8; x <= playfieldWidth * 8; x += 8) {
                if (playfield.get(Integer.valueOf(y))
                            .get(Integer.valueOf(x)).getDot()
                        != PathElement.Dot.NONE) {
                    DotElement dot = new Food(getAppearance().getSourceImage());
                    dot.init(x, y);
                    dot.setParent(this);
                    row.put(Integer.valueOf(x), dot);
                }
            }
        }
    }

    private void createEnergizerElements() {
        List<Energizer> es = new ArrayList<Energizer>();
        for (Position c : ENERGIZER_POSITIONS) {
            int x = c.x * 8;
            int y = c.y * 8;
            DotElement removed = removeDotElement(x, y);
            if (removed == null) {
                continue;
            }
            
            removeChild(removed);

            Energizer e = new Energizer(getAppearance().getSourceImage());
            e.init(x, y);
            e.setParent(this);
            putDotElement(x, y, e);
            es.add(e);
            
            playfield.get(Integer.valueOf(y))
                        .get(Integer.valueOf(x))
                        .setDot(Dot.ENERGIZER);
        }
        energizers = es.toArray(new Energizer[0]);
    }

    public DotElement getDotElement(final int x, final int y) {
        return handleDotRow(y, new DotRowHandler() {
            @Override
            public DotElement handle(Map<Integer, DotElement> row) {
                return row.get(Integer.valueOf(x)); 
            }
        }); 
    }

    private DotElement removeDotElement(final int x, final int y) {
        return handleDotRow(y, new DotRowHandler() {
            @Override
            public DotElement handle(Map<Integer, DotElement> row) {
                return row.remove(Integer.valueOf(x)); 
            }
        }); 
    }

    private DotElement putDotElement(final int x, final int y, final DotElement d) {
        return handleDotRow(y, new DotRowHandler() {
            @Override
            public DotElement handle(Map<Integer, DotElement> row) {
                return row.put(Integer.valueOf(x), d); 
            }
        }); 
    }
    
    private DotElement handleDotRow(int y, DotRowHandler h) {
        Map<Integer, DotElement> row = foods.get(Integer.valueOf(y));
        if (row == null) {
            return null;
        }
        return h.handle(row);
        
    }
    
    private static interface DotRowHandler {
        DotElement handle(Map<Integer, DotElement> row);
    }

    private void createFruitElement() {
        fruit = new Fruit(getAppearance().getSourceImage(),
                            game.getLevels().getFruit());
        fruit.initOnPlayfield(FRUIT_POSITION[1], FRUIT_POSITION[0]);
        fruit.setParent(this);
    }
    
    private void createDoorElement() {
        door = new Door(getAppearance().getSourceImage());
        door.init();
        door.setVisibility(false);
        door.setParent(this);
    }

    private void createActorElements() {
        pacman = new Pacman(
                        getAppearance().getSourceImage(),
                        game);
        pacman.init();
        pacman.setParent(this);

        {
            List<Ghost> gs = new ArrayList<Ghost>();
            gs.add(
                new Blinky(
                    getAppearance().getSourceImage(),
                    game));
            gs.add(
                new Pinky(
                    getAppearance().getSourceImage(),
                    game));
            gs.add(
                new Inky(
                    getAppearance().getSourceImage(),
                    game));
            gs.add(
                new Clyde(
                    getAppearance().getSourceImage(),
                    game));

            ghosts = gs.toArray(new Ghost[0]);
            for (Ghost ghost : ghosts) {
                ghost.init();
                ghost.setParent(this);
            }
        }
    }

    private void createPlayfieldElements() {
        createDotElements();
        createEnergizerElements();
        createFruitElement();
        createDoorElement();
        createActorElements();
    }
    
    public void createReadyElement() {
        ready = new Ready(getAppearance().getSourceImage());
        ready.init();
        ready.setParent(this);
    }
    
    public void createGameOverElement() {
        gameover = new GameOver(getAppearance().getSourceImage());
        gameover.init();
        gameover.setParent(this);
    }

    public int getDotsRemaining() {
        return dotsRemaining;
    }

    public void decrementDotsRemaining() {
        dotsRemaining--;
    }

    public int getDotsEaten() {
        return dotsEaten;
    }

    public void incrementDotsEaten() {
        dotsEaten++;
    }

    public PathElement getPathElement(int x, int y) {
        return playfield.get(Integer.valueOf(y)).get(Integer.valueOf(x));
    }

    public void clearDot(int x, int y) {
        DotElement d = getDotElement(x, y);
        d.setEaten(true);
        d.setVisibility(false);
        playfield.get(Integer.valueOf(y))
                    .get(Integer.valueOf(x))
                    .setDot(Dot.NONE);
    }

    public void blinkEnergizers(GameplayMode gameplayMode,
                                long globalTime,
                                float interval) {
        for (Energizer e : energizers) {
            e.update(gameplayMode, globalTime, interval);
        }
    }

    private int killScreenTileX;
    private int killScreenTileY;
    
    public void killScreen() {
        createKillScreenElement(272, 0, 200, 80, false);
        createKillScreenElement(280, 80, 192, 56, false);
        killScreenTileX = 80;
        killScreenTileY = 0;
        for (int x = 280; x <= 472; x += 8) {
            for (int y = 0; y <= 136; y += 8) {
                if (game.rand() < 0.03) {
                    killScreenTileX = (int) FloatMath.floor(game.rand() * 25) * 10;
                    killScreenTileY = (int) FloatMath.floor(game.rand() * 2) * 10;
                }
                createKillScreenElement(x, y, 8, 8, true);
            }
        }
    }

    private void createKillScreenElement(
                                    int x,
                                    int y,
                                    int width,
                                    int height,
                                    boolean bgImage) {
        KillScreenTile tile =
                new KillScreenTile(getAppearance().getSourceImage());
        tile.init(x, y, width, height);
        if (bgImage) {
            tile.setBgPos(killScreenTileX, killScreenTileY);
            killScreenTileY += 8;
        } else {
            tile.setBgColor(0x000000);
        }
        tile.setParent(this);
    }
    
    public void blink(float gameplayModeTime, float interval) {
        if (FloatMath.floor(gameplayModeTime / (interval / 8)) % 2 == 0) {
            getAppearance().changeBkPos(322, 2, false);
        } else {
            getAppearance().changeBkPos(322, 138, false);
        }
    }

    @Override
    void doDraw(Canvas canvas) {
        getAppearance().drawBitmap(canvas);
    }

    public Pacman getPacman() {
        return pacman;
    }
    
    public Ghost[] getGhosts() {
        return ghosts;
    }
    
    public Ghost getBlinky() {
        return ghosts[0];
    }
    
    public Ghost getPinky() {
        return ghosts[1];
    }
    
    public Ghost getInky() {
        return ghosts[2];
    }

    public Ghost getClyde() {
        return ghosts[3];
    }
    
    public Door getDoor() {
        return door;
    }

    public void setDoor(Door door) {
        this.door = door;
    }
    
    public Fruit getFruit() {
        return fruit;
    }

    public void setFruit(Fruit fruit) {
        this.fruit = fruit;
    }

    public Ready getReady() {
        return ready;
    }

    public void setReady(Ready ready) {
        this.ready = ready;
    }
    
    public void removeReady() {
        removeChild(ready);
        ready = null;
    }

    public GameOver getGameover() {
        return gameover;
    }

    public void setGameover(GameOver gameover) {
        this.gameover = gameover;
    }

    public void removeGameover() {
        removeChild(gameover);
        gameover = null;
    }

    public abstract static class DotElement extends BaseEntity {

        private boolean eaten;

        public DotElement(Bitmap sourceImage) {
            super(sourceImage);
        }

        void init(int x, int y) {
            Appearance a = getAppearance();
            a.setLeft(x + -32);
            a.setTop(y + 0);
        }
        
        @Override
        public final void doDraw(Canvas canvas) {
            if (eaten || !isVisible())
                return;

            drawDot(canvas);
        }
        
        abstract void drawDot(Canvas canvas);

        public final void setEaten(boolean eaten) {
            this.eaten = eaten;
        }
    }

    static class Food extends DotElement {

        public Food(Bitmap sourceImage) {
            super(sourceImage);
        }

        @Override
        void init(int x, int y) {
            super.init(x, y);
            Appearance a = getAppearance();
            a.setLeftOffset(3);
            a.setTopOffset(3);
            a.setWidth(2);
            a.setHeight(2);
            a.setBgColor(0xf8b090);
            a.setOrder(100);
        }

        @Override
        void drawDot(Canvas canvas) {
            getAppearance().drawRectShape(canvas);
        }
    }
    
    static class Energizer extends DotElement {
        
        public Energizer(Bitmap sourceImage) {
            super(sourceImage);
        }

        @Override
        void init(int x, int y) {
            super.init(x, y);
            Appearance a = getAppearance();
            a.setWidth(8);
            a.setHeight(8);
            a.prepareBkPos(0, 144);
            a.setOrder(101);
        }

        void update(GameplayMode gameplayMode, long globalTime, float interval) {
            switch (gameplayMode) {
            case NEWGAME_STARTING:
            case NEWGAME_STARTED:
            case GAME_RESTARTING:
            case GAME_RESTARTED:
            case LEVEL_BEING_COMPLETED:
            case LEVEL_COMPLETED:
            case TRANSITION_INTO_NEXT_SCENE:
                setVisibility(true);
                break;
            case GAMEOVER:
            case KILL_SCREEN:
                setVisibility(false);
                break;
            default:
                blink(globalTime, interval);
                break;
            }
        }

        private void blink(long globalTime, float interval) {
            if (globalTime % (interval * 2) == 0) {
                setVisibility(true);
            } else if (globalTime % (interval * 2) == interval) {
                setVisibility(false);
            }
        }

        @Override
        void drawDot(Canvas canvas) {
            getAppearance().drawBitmap(canvas);
        }
    }

    public static class Door extends BaseEntity {
        
        public Door(Bitmap sourceImage) {
            super(sourceImage);
        }
        
        void init() {
            Appearance a = getAppearance();
            a.setWidth(19);
            a.setHeight(2);
            a.setLeft(279);
            a.setTop(46);
            a.setBgColor(0xffaaa5);
        }
        
        @Override
        void doDraw(Canvas canvas) {
            getAppearance().drawRectShape(canvas);
        }
    }
    
    public static class Ready extends BaseEntity {

        public Ready(Bitmap sourceImage) {
            super(sourceImage);
        }

        public void init() {
            Appearance a = getAppearance();
            a.setWidth(48);
            a.setHeight(8);
            a.setLeft(264);
            a.setTop(80);
            a.prepareBkPos(160, 0);
            a.setOrder(120);
        }

        @Override
        void doDraw(Canvas canvas) {
            getAppearance().drawBitmap(canvas);
        }
    }

    public static class GameOver extends BaseEntity {

        public GameOver(Bitmap sourceImage) {
            super(sourceImage);
        }

        public void init() {
            Appearance a = getAppearance();
            a.setWidth(80);
            a.setHeight(8);
            a.setLeft(248);
            a.setTop(80);
            a.prepareBkPos(8, 152);
            a.setOrder(120);
        }

        @Override
        void doDraw(Canvas canvas) {
            getAppearance().drawBitmap(canvas);
        }
    }

    public static class KillScreenTile extends BaseEntity {

        private boolean bgImage;

        public KillScreenTile(Bitmap sourceImage) {
            super(sourceImage);
        }

        void init(int x, int y, int width, int height) {
            Appearance a = getAppearance();
            a.setLeft(x);
            a.setTop(y);
            a.setWidth(width);
            a.setHeight(height);
            a.setOrder(119);
        }

        void setBgPos(int x, int y) {
            bgImage = true;
            Appearance a = getAppearance();
            a.setBgPosX(x);
            a.setBgPosY(y);

        }

        void setBgColor(int color) {
            getAppearance().setBgColor(color);
        }

        @Override
        void doDraw(Canvas canvas) {
            Appearance a = getAppearance();
            if (bgImage) {
                a.drawBitmap(canvas);
            } else {
                a.drawRectShape(canvas);
            }
        }
    }

}
