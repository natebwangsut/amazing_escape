package mycontroller;

import controller.CarController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * [SWEN30006] Software Modelling and Design
 * Semester 1, 2017
 * Project Part C - amazing-escape
 *
 * Group 107:
 * Nate Wangsutthitham          [755399]
 * Kolatat Thangkasemvathana    [780631]
 * Khai Mei Chin                [755332]
 *
 * Handling the car's FOV
 */
public class FOVUtils {

    private static Logger logger = LogManager.getLogger();

    public static final Predicate<MapTile> IS_WALL = t -> t.getName().equals("Wall");
    private final CarController con;
    private int wallSensitivity = 2;


    /**
     * FOVUtils Constructor
     *
     * @param con
     */
    public FOVUtils(CarController con) {
        this.con = con;
    }


    public class DeadEnd {
        Coordinate origin;
        WorldSpatial.Direction direction;
        int ahead;
        int left;
        int right;
        public WorldSpatial.RelativeDirection recommendedTurn;
        public int turnSize;
    }


    public static int getDeadEndSize(Map<Coordinate, MapTile> view) {
        // TODO
        return -1;
    }


    public static Coordinate getCenter(Map<Coordinate, MapTile> view) {
        if (view.isEmpty()) return null;
        Coordinate center = new Coordinate(0, 0);
        // center is the average of all coordinates?
        // would be incorrect at edges of maps...
        view.entrySet().stream().forEach(e -> {
            center.x += e.getKey().x;
            center.y += e.getKey().y;
        });
        center.x /= view.size();
        center.y /= view.size();
        return center;
    }

    public static double dist2(Coordinate u, Coordinate v) {
        double dx = u.x - v.x;
        double dy = u.y - v.y;
        return dx * dx + dy * dy;
    }

    public static double dist(Coordinate u, Coordinate v) {
        return Math.sqrt(dist2(u, v));
    }

    public static <T extends MapTile> MapEntry<T> getClosest(Map<Coordinate, MapTile> view, Class<T> type) {
        Coordinate center = getCenter(view);
        Optional<Map.Entry<Coordinate, MapTile>> candidate = view.entrySet().stream()
                // select only specified tile type
                .filter(e -> type.isInstance(e.getValue()))
                // finds the closest one to center
                .min(Comparator.comparingDouble(e -> dist2(center, e.getKey())));
        if (candidate.isPresent()) {
//            return new MapEntry<T>(candidate.get().getKey(), candidate.get().getValue());
            return null;
        } else {
            return null;
        }
    }

    public static WorldSpatial.Direction directionalAdd(
            WorldSpatial.Direction dir,
            WorldSpatial.RelativeDirection rdir) {
        ArrayList<WorldSpatial.Direction> dirs = new ArrayList<>(4);

        dirs.add(WorldSpatial.Direction.NORTH);
        dirs.add(WorldSpatial.Direction.EAST);
        dirs.add(WorldSpatial.Direction.SOUTH);
        dirs.add(WorldSpatial.Direction.WEST);

        int i = dirs.indexOf(dir);
        if (rdir == WorldSpatial.RelativeDirection.LEFT) {
            i--;
        } else {
            i++;
        }
        i = (i + 4) % 4;
        return dirs.get(i);
    }

    public static Coordinate directionalCoordinateAdd(Coordinate u, Coordinate v, WorldSpatial.Direction dir) {
        int dx = 0, dy = 0;
        switch (dir) {
            // we are east oriented (theta=0)
            /*
            R = [ dx     [ cos t, -sin t
                  dy ] x   sin t,  cos t  ]
             */
            case EAST:
                dx = v.x;
                dy = v.y;
                break;
            case NORTH:
                dy = v.x;
                dx = -v.y;
                break;
            case WEST:
                dx = -v.x;
                dy = -v.y;
                break;
            case SOUTH:
                dy = -v.x;
                dx = v.y;
                break;
        }

        return new Coordinate(u.x + dx, u.y + dy);
    }


    public DeadEnd deadEndAhead(PersistentView view) {
        // number of tiles till wall in front
        int frontWall = -1;
        for (int x = 1; x <= con.getViewSquare(); x++) {
            Coordinate c = relativeAdd(x, 0, con.getOrientation());
            PersistentView.Property p = view.get(c);
            if (p == null) break;
            if (p.logicalWall) {
                frontWall = x;
                break;
            }
        }
        // if not found then no dead end
        if (frontWall == -1) return null;

        /*
        ? X X X X ?
        ?????|?????  ^
        ?????|?????  | we test left and right wall (from car's trajectory) up to front wall
        ?????C?????
         */
        int leftWall = Integer.MAX_VALUE;
        int rightWall = Integer.MAX_VALUE;
        // reverse from wall down to car because there might not be walls closer to us
        for (int x = frontWall - 1; x >= 0; x--) {
            boolean foundLeft = false;
            boolean foundRight = false;
            // loop through all tiles in the current row
            for (int y = -con.getViewSquare(); y <= con.getViewSquare(); y++) {
                Coordinate c = relativeAdd(x, y, con.getOrientation());
                PersistentView.Property p = view.get(c);
                if (p == null) continue;
                if (p.logicalWall) {
                    if (y >= 0) {
                        foundLeft = true;
                        leftWall = Math.min(leftWall, y);
                    }
                    if (y <= 0) {
                        foundRight = true;
                        rightWall = Math.min(rightWall, -y);
                    }
                }
            }
            // mustve found at least one of each otherwise it is not a dead end
            if (!(foundLeft && foundRight)) return null;
        }
        if (rightWall>=3) return null; // big enough room to turn so no
        logger.info("Found dead end at {} at {} in front having L-R: {}-{}", con.getPosition(), frontWall, -leftWall, rightWall);
        DeadEnd de = new DeadEnd();
        de.ahead = frontWall;
        de.direction = con.getOrientation();
        de.left = leftWall;
        de.right = rightWall;
        de.origin = new Coordinate(con.getPosition());
        // turn to the side with larger space
        if (leftWall > rightWall) {
            de.recommendedTurn = WorldSpatial.RelativeDirection.LEFT;
            de.turnSize = de.left;
        } else {
            de.recommendedTurn = WorldSpatial.RelativeDirection.RIGHT;
            de.turnSize = de.right;
        }
        return de;
    }

    public boolean isInVicinity(Predicate<MapTile> wantTile) {
        Map<Coordinate, MapTile> view = con.getView();
        for (int i = 0; i <= 1; i++) {
            for (int j = 0; j <= 1; j++) {
                Coordinate nc = relativeAdd(i, j, con.getOrientation());
                if (wantTile.test(view.get(nc))) {
                    logger.info("Found TrapTile at {}", nc);

                    return true;
                }
            }
        }
        return false;
    }

    public boolean checkAhead(Predicate<Coordinate> p) {
        return check(con.getOrientation(), p);
    }

    public boolean checkFollowing(Predicate<Coordinate> p) {
        return check(directionalAdd(con.getOrientation(), WorldSpatial.RelativeDirection.LEFT), p);
    }

    public boolean check(WorldSpatial.Direction dir, Predicate<Coordinate> p) {
        for (int i = 0; i <= wallSensitivity; i++) {
            if (p.test(relativeAdd(i, 0, dir))) {
                return true;
            }
        }
        return false;
    }

    public Coordinate relativeAdd(int x, int y, WorldSpatial.Direction dir) {
        return directionalCoordinateAdd(new Coordinate(con.getPosition()), new Coordinate(x, y), dir);
    }

    public static class MapEntry<T extends MapTile> {
        private final Coordinate coord;
        private final T tile;

        private MapEntry(Coordinate coord, T tile) {
            this.coord = coord;
            this.tile = tile;
        }

        public Coordinate getCoordinate() {
            return coord;
        }

        public T getTile() {
            return tile;
        }
    }

}
