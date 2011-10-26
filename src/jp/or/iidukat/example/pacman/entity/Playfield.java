package jp.or.iidukat.example.pacman.entity;

import static jp.or.iidukat.example.pacman.PacmanGame.GameplayMode;
import static jp.or.iidukat.example.pacman.entity.Playfield.PathElement.Dot;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.or.iidukat.example.pacman.Direction;
import jp.or.iidukat.example.pacman.PacmanGame;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.FloatMath;

public class Playfield extends BaseEntity {

    // パスの配列.左上:(5, 1), 左下:(5, 15), 右上:(60, 1), 右下:(60, 15).
    // 配列要素のオブジェクトのプロパティは(x, y, w) もしくは(x, y, h)
    // 要素のオブジェクトにwプロパティがあり:横方向, 要素のオブジェクトにhプロパティがあり:縦方向
    // x, yはパスの始点の座標
    // h, wは各々パスの長さを表現
    // [例外] typeプロパティを値1でもつパスはワープつき
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

    private static final Path[] n = {
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

    // エサの存在しないパス
    // 左上:(5, 1), 左下:(5, 15), 右上:(60, 1), 右下:(60, 15).
    private static final Path[] o = {
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

    // パワーエサ
    private static final Position[] p = {
        new Position(5, 15),
        new Position(5, 3),
        new Position(15, 8),
        new Position(60, 3),
        new Position(60, 15),
    };

    // ワープトンネル
    private static final Position[] q = {
        new Position(2, 8),
        new Position(63, 8),
    };

    public static Position[] getQ() {
        return q;
    }

    private static final int[] v = { 80, 312 }; // フルーツ出現位置

    public static int[] getV() {
        return v;
    }

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
        private EnumSet<Direction> allowedDir;
        
        public EnumSet<Direction> getAllowedDir() {
            return allowedDir;
        }
        
        public void setAllowedDir(EnumSet<Direction> allowedDir) {
            this.allowedDir = allowedDir;
        }
        
        public Dot getDot() {
            return dot;
        }
        
        public void setDot(Dot dot) {
            this.dot = dot;
        }

        public boolean isIntersection() {
            return intersection;
        }
        
        public void setIntersection(boolean intersection) {
            this.intersection = intersection;
        }

        public boolean isPath() {
            return path;
        }
        
        public void setPath(boolean path) {
            this.path = path;
        }

        public boolean isTunnel() {
            return tunnel;
        }
        
        public void setTunnel(boolean tunnel) {
            this.tunnel = tunnel;
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

    // private final List<Entity> entities = new ArrayList<Entity>();

    public Playfield(Bitmap sourceImage, PacmanGame game) {
        super(sourceImage, true);
        this.game = game;
    }

    public Map<Integer, Map<Integer, PathElement>> getPlayfield() {
        return playfield;
    }

    public void init() {
        Presentation p = getPresentation();
        p.setLeft(45);
        p.setWidth(464);
        p.setHeight(136);
        p.setOrder(99);
    }

    public void reset() {
        clearDrawQueue();
        dotsRemaining = 0;
        dotsEaten = 0;
        getPresentation().prepareBkPos(256, 0);
        determinePlayfieldDimensions();
        preparePlayfield();
        preparePaths();
        prepareAllowedDirections();
        createPlayfieldElements();
    }

    private void determinePlayfieldDimensions() {
        playfieldWidth = 0;
        playfieldHeight = 0;
        for (Path c : n) {
            if (c.w > 0) {
                int x = c.x + c.w - 1;
                if (x > playfieldWidth)
                    playfieldWidth = x;
            } else {
                int y = c.y + c.h - 1;
                if (y > playfieldHeight)
                    playfieldHeight = y;
            }
        }
    }

    private void preparePlayfield() {
        playfield = new HashMap<Integer, Map<Integer, PathElement>>();
        for (int b = 0; b <= playfieldHeight + 1; b++) {
            Map<Integer, PathElement> row = new HashMap<Integer, PathElement>();
            for (int c = -2; c <= playfieldWidth + 1; c++) {
                PathElement p = new PathElement();
                p.setPath(false);
                p.setDot(Dot.NONE);
                p.setIntersection(false);
                row.put(Integer.valueOf(c * 8), p);
            }
            playfield.put(Integer.valueOf(b * 8), row);
        }
    }

    private void preparePaths() {
        for (Path c : n) {
            boolean d = c.tunnel;
            if (c.w > 0) {
                int f = c.y * 8;
                for (int h = c.x * 8; h <= (c.x + c.w - 1) * 8; h += 8) {
                    PathElement pe = playfield.get(Integer.valueOf(f)).get(Integer.valueOf(h));
                    pe.setPath(true);
                    if (pe.getDot() == Dot.NONE) {
                        pe.setDot(Dot.FOOD);
                        dotsRemaining++;
                    }
                    pe.setTunnel(!d || h != c.x * 8 && h != (c.x + c.w - 1) * 8 ? d : false);
                }
                playfield.get(Integer.valueOf(f)).get(Integer.valueOf(c.x * 8))
                        .setIntersection(true);
                playfield.get(Integer.valueOf(f))
                        .get(Integer.valueOf((c.x + c.w - 1) * 8))
                        .setIntersection(true);
            } else {
                int h = c.x * 8;
                for (int f = c.y * 8; f <= (c.y + c.h - 1) * 8; f += 8) {
                    PathElement pe = playfield.get(Integer.valueOf(f)).get(Integer.valueOf(h));
                    if (pe.isPath())
                        pe.setIntersection(true);
                    pe.setPath(true);
                    if (pe.getDot() == Dot.NONE) {
                        pe.setDot(Dot.FOOD);
                        dotsRemaining++;
                    }
                    pe.setTunnel(!d || f != c.y * 8 && f != (c.y + c.h - 1) * 8 ? d : false);
                }
                playfield.get(Integer.valueOf(c.y * 8)).get(Integer.valueOf(h))
                        .setIntersection(true);
                playfield.get(Integer.valueOf((c.y + c.h - 1) * 8))
                        .get(Integer.valueOf(h)).setIntersection(true);
            }
        }
        for (Path p : o)
            if (p.w != 0)
                for (int h = p.x * 8; h <= (p.x + p.w - 1) * 8; h += 8) {
                    playfield.get(Integer.valueOf(p.y * 8))
                                .get(Integer.valueOf(h))
                                .setDot(Dot.NONE);
                    dotsRemaining--;
                }
            else
                for (int f = p.y * 8; f <= (p.y + p.h - 1) * 8; f += 8) {
                    playfield.get(Integer.valueOf(f))
                                .get(Integer.valueOf(p.x * 8))
                                .setDot(Dot.NONE);
                    dotsRemaining--;
                }
    }

    private void prepareAllowedDirections() {
        for (int b = 8; b <= playfieldHeight * 8; b += 8)
            for (int c = 8; c <= playfieldWidth * 8; c += 8) {
                PathElement pe = playfield.get(Integer.valueOf(b)).get(
                        Integer.valueOf(c));
                EnumSet<Direction> allowedDir = EnumSet.noneOf(Direction.class);
                if (playfield.get(Integer.valueOf(b - 8))
                        .get(Integer.valueOf(c)).isPath())
                    allowedDir.add(Direction.UP);
                if (playfield.get(Integer.valueOf(b + 8))
                        .get(Integer.valueOf(c)).isPath())
                    allowedDir.add(Direction.DOWN);
                if (playfield.get(Integer.valueOf(b))
                        .get(Integer.valueOf(c - 8)).isPath())
                    allowedDir.add(Direction.LEFT);
                if (playfield.get(Integer.valueOf(b))
                        .get(Integer.valueOf(c + 8)).isPath())
                    allowedDir.add(Direction.RIGHT);
                pe.setAllowedDir(allowedDir);
            }
    }

    // エサを作成
    private void createDotElements() {
        foods = new HashMap<Integer, Map<Integer, DotElement>>();
        for (int b = 8; b <= playfieldHeight * 8; b += 8) {
            Map<Integer, DotElement> row = new HashMap<Integer, DotElement>();
            foods.put(Integer.valueOf(b), row);
            for (int c = 8; c <= playfieldWidth * 8; c += 8) {
                if (playfield.get(Integer.valueOf(b))
                            .get(Integer.valueOf(c)).getDot()
                        != PathElement.Dot.NONE) {
                    DotElement dot = new Food(getPresentation().getSourceImage());
                    dot.init(c, b);
                    dot.setParent(this);
                    row.put(Integer.valueOf(c), dot);
                }
            }
        }
    }

    // パワーエサを作成
    private void createEnergizerElements() {
        List<Energizer> es = new ArrayList<Energizer>();
        for (Position c : p) {
            int x = c.x * 8;
            int y = c.y * 8;
            DotElement removed = removeDotElement(x, y);
            if (removed == null) {
                continue;
            }
            
            removeFromDrawQueue(removed);

            Energizer e = new Energizer(getPresentation().getSourceImage());
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
        return iterateDotElements(x, y, new DotElementHandler() {
            @Override
            public DotElement handle(Map<Integer, DotElement> row) {
                return row.get(Integer.valueOf(x)); 
            }
        }); 
    }

    private DotElement removeDotElement(final int x, final int y) {
        return iterateDotElements(x, y, new DotElementHandler() {
            @Override
            public DotElement handle(Map<Integer, DotElement> row) {
                return row.remove(Integer.valueOf(x)); 
            }
        }); 
    }

    private DotElement putDotElement(final int x, final int y, final DotElement f) {
        return iterateDotElements(x, y, new DotElementHandler() {
            @Override
            public DotElement handle(Map<Integer, DotElement> row) {
                return row.put(Integer.valueOf(x), f); 
            }
        }); 
    }
    
    private DotElement iterateDotElements(int x, int y, DotElementHandler h) {
        Map<Integer, DotElement> row = foods.get(Integer.valueOf(y));
        if (row == null) {
            return null;
        }
        return h.handle(row);
        
    }
    
    private static interface DotElementHandler {
        DotElement handle(Map<Integer, DotElement> row);
    }

    private void createFruitElement() {
        fruit = new Fruit(getPresentation().getSourceImage(),
                            game.getLevels().getFruit());
        fruit.initOnPlayfield(v);
        fruit.setParent(this);
    }
    
    private void createDoorElement() {
        door = new Door(getPresentation().getSourceImage());
        door.init();
        door.setVisibility(false);
        door.setParent(this);
    }

    private void createActorElements() {
        int cnt = 0;
        pacman = new Pacman(
                        getPresentation().getSourceImage(),
                        cnt++,
                        game);
        pacman.init();
        pacman.setParent(this);

        {
            List<Ghost> gs = new ArrayList<Ghost>();
            gs.add(
                new Blinky(
                        getPresentation().getSourceImage(),
                        cnt++,
                        game));
            gs.add(
                new Pinky(
                        getPresentation().getSourceImage(),
                        cnt++,
                        game));
            gs.add(
                new Inky(
                        getPresentation().getSourceImage(),
                        cnt++,
                        game));
            gs.add(
                new Clyde(
                        getPresentation().getSourceImage(),
                        cnt++,
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
        ready = new Ready(getPresentation().getSourceImage());
        ready.init();
        ready.setParent(this);
    }
    
    public void createGameOverElement() {
        gameover = new GameOver(getPresentation().getSourceImage());
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
        for (int b = 280; b <= 472; b += 8) {
            for (int c = 0; c <= 136; c += 8) {
                if (game.rand() < 0.03) {
                    killScreenTileX = (int) FloatMath.floor(game.rand() * 25) * 10;
                    killScreenTileY = (int) FloatMath.floor(game.rand() * 2) * 10;
                }
                createKillScreenElement(b, c, 8, 8, true);
            }
        }
    }

    private void createKillScreenElement(
                                int b, int c, int d, int f, boolean h) {
        KillScreenTile tile =
                new KillScreenTile(getPresentation().getSourceImage());
        tile.init(b, c, d, f);
        if (h) {
            // j.style.background = "url(src/pacman10-hp-sprite-2.png) -" + killScreenTileX + "px -" + killScreenTileY + "px no-repeat";
            tile.setBgPos(killScreenTileX, killScreenTileY);
            killScreenTileY += 8;
        } else {
            tile.setBgColor(0x000000); // j.style.background = "black";
        }
        tile.setParent(this); // playfieldEl.appendChild(j)
    }
    
    public void blink(float gameplayModeTime, float interval) {
        if (FloatMath.floor(gameplayModeTime / (interval / 8)) % 2 == 0)
            getPresentation().changeBkPos(322, 2, false);
        else
            getPresentation().changeBkPos(322, 138, false);
    }

    public static float getPlayfieldX(float b) {
        return b + -32;
    }

    public static float getPlayfieldY(float b) {
        return b + 0;
    }

    public static float getDistance(int[] b, int[] c) {
        return FloatMath.sqrt((c[1] - b[1]) * (c[1] - b[1]) + (c[0] - b[0]) * (c[0] - b[0]));
    }

    public static float getDistance(float[] b, float[] c) {
        return FloatMath.sqrt((c[1] - b[1]) * (c[1] - b[1]) + (c[0] - b[0]) * (c[0] - b[0]));
    }

    void doDraw(Canvas c) {
        getPresentation().drawBitmap(c);
    }

    public Pacman getPacman() {
        return pacman;
    }
    
    public Ghost[] getGhosts() {
        return ghosts;
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
        removeFromDrawQueue(ready);
        ready = null;
    }

    public GameOver getGameover() {
        return gameover;
    }

    public void setGameover(GameOver gameover) {
        this.gameover = gameover;
    }

    public void removeGameover() {
        removeFromDrawQueue(gameover);
        gameover = null;
    }

    public abstract static class DotElement extends BaseEntity {

        private boolean eaten;

        public DotElement(Bitmap sourceImage) {
            super(sourceImage);
        }

        void init(int x, int y) {
            Presentation p = getPresentation();
            p.setLeft(x + -32);
            p.setTop(y + 0);
        }
        
        @Override
        public void doDraw(Canvas c) {
            if (eaten || !isVisible())
                return;

            drawDot(c);
        }
        
        abstract void drawDot(Canvas c);

        public boolean isEaten() {
            return eaten;
        }

        public void setEaten(boolean eaten) {
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
            Presentation p = getPresentation();
            p.setLeftOffset(3);// margin-left: 3
            p.setTopOffset(3); // margint-top: 3
            p.setWidth(2);
            p.setHeight(2);
            p.setBgColor(0xf8b090);
            p.setOrder(100);
        }

        @Override
        void drawDot(Canvas c) {
            getPresentation().drawRectShape(c);
        }
    }
    
    static class Energizer extends DotElement {
        
        public Energizer(Bitmap sourceImage) {
            super(sourceImage);
        }

        @Override
        void init(int x, int y) {
            super.init(x, y);
            Presentation p = getPresentation();
            p.setWidth(8);
            p.setHeight(8);
            p.prepareBkPos(0, 144);
            p.setOrder(101);
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
        void drawDot(Canvas c) {
            getPresentation().drawBitmap(c);
        }
    }

    public static class Door extends BaseEntity {
        
        public Door(Bitmap sourceImage) {
            super(sourceImage);
        }
        
        void init() {
            Presentation p = getPresentation();
            p.setWidth(19);
            p.setHeight(2);
            p.setLeft(279);
            p.setTop(46);
            p.setBgColor(0xffaaa5);
        }
        
        @Override
        void doDraw(Canvas c) {
            getPresentation().drawRectShape(c);
        }
    }
    
    public static class Ready extends BaseEntity {

        public Ready(Bitmap sourceImage) {
            super(sourceImage);
        }

        public void init() {
            Presentation p = getPresentation();
            p.setWidth(48);
            p.setHeight(8);
            p.setLeft(264);
            p.setTop(80);
            p.prepareBkPos(160, 0);
            p.setOrder(120);
        }

        @Override
        void doDraw(Canvas c) {
            getPresentation().drawBitmap(c);
        }
    }

    public static class GameOver extends BaseEntity {

        public GameOver(Bitmap sourceImage) {
            super(sourceImage);
        }

        public void init() {
            Presentation p = getPresentation();
            p.setWidth(80);
            p.setHeight(8);
            p.setLeft(248);
            p.setTop(80);
            p.prepareBkPos(8, 152);
            p.setOrder(120);
        }

        @Override
        void doDraw(Canvas c) {
            getPresentation().drawBitmap(c);
        }
    }

    public static class KillScreenTile extends BaseEntity {

        private boolean bgImage;

        public KillScreenTile(Bitmap sourceImage) {
            super(sourceImage);
        }

        void init(int left, int top, int width, int height) {
            Presentation p = getPresentation();
            p.setLeft(left);
            p.setTop(top);
            p.setWidth(width);
            p.setHeight(height);
            p.setOrder(119);
        }

        void setBgPos(int x, int y) {
            bgImage = true;
            Presentation p = getPresentation();
            p.setBgPosX(x);
            p.setBgPosY(y);

        }

        void setBgColor(int color) {
            getPresentation().setBgColor(color);
        }

        @Override
        void doDraw(Canvas c) {
            Presentation p = getPresentation();
            if (bgImage) {
                p.drawBitmap(c);
            } else {
                p.drawRectShape(c);
            }
        }
    }

}