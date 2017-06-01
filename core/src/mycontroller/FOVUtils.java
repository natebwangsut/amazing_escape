package mycontroller;

import controller.CarController;
import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Created by Kolatat on 23/5/17.
 */
public class FOVUtils {

    public static final Predicate<MapTile> IS_WALL = t->t.getName().equals("Wall");

    public boolean isDeadEnd(Predicate<Coordinate> isWall) {
        Map<Coordinate, MapTile> view = con.getView();
        Coordinate curpos = new Coordinate(con.getPosition());
        WorldSpatial.Direction left = directionalAdd(con.getOrientation(), WorldSpatial.RelativeDirection.LEFT);

        // first we check dead end on the left so car will not dumbly turn into one
        if(!checkFollowing(isWall)){
            int wallpos[] = {-1,-1,-1,-1};
            int gapWidth = -1;
            int gapDepth = -1;
            // we start at -1 to check if previously following wall
            // if not then this loop is meaningless
            forwardLoop: for(int j = -1; j<4; j++) {
                //find the closest wall not directly to the left
                leftLoop: for (int i = j==0?wallSensitivity:0; i <= con.getViewSquare(); i++) {
                    // east oriented, so left is positive y
                    if (isWall.test(relativeAdd(j, i, con.getOrientation()))) {
                        if(j==-1&&i<=wallSensitivity){

                        }
                        wallpos[j] = i;
                        if(j!=0&&i<=wallSensitivity){
                            gapWidth = j;
                            break forwardLoop;
                        } else {
                            gapDepth = Math.max(gapDepth, i);
                            break leftLoop;
                        }
                        //System.out.printf("Wall is %s %d steps away.%n",con.getOrientation().name(), i);
                    }
                }
            }
            if(gapWidth>0) {
                System.out.println(con.getOrientation().name());
                System.out.printf("Found gap of width:%d depth:%d wall=%d,%d,%d,%d %n", gapWidth, gapDepth, wallpos[0], wallpos[1], wallpos[2], wallpos[3]);
                try {
                    Thread.sleep(2000);
                } catch(InterruptedException ex){

                }
                System.exit(0);
            }
        }
        // TODO
        return false;
    }

    public boolean isInVicinity(Predicate<MapTile> wantTile) {
        Map<Coordinate, MapTile> view = con.getView();
        for(int i=0; i<=wallSensitivity; i++) {
            for(int j=-wallSensitivity; j<=wallSensitivity; j++) {
                Coordinate nc = relativeAdd(i, j, con.getOrientation());
                if (wantTile.test(view.get(nc))) {
                    return true;
                }
            }
        }
        return false;
    }

    public static int getDeadEndSize(Map<Coordinate, MapTile> view) {
        // TODO
        return -1;
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

    public boolean checkAhead(Predicate<Coordinate> p) {
        return check(con.getOrientation(),p);
    }

    public static WorldSpatial.Direction directionalAdd(
            WorldSpatial.Direction dir,
            WorldSpatial.RelativeDirection rdir)
    {
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
        i = (i+4)%4;
        return dirs.get(i);
    }

    public boolean checkFollowing(Predicate<Coordinate> p) {
        return check(directionalAdd(con.getOrientation(), WorldSpatial.RelativeDirection.LEFT), p);
    }
    private int wallSensitivity = 2;
    public boolean check(WorldSpatial.Direction dir, Predicate<Coordinate> p) {
        for(int i=0; i<=wallSensitivity; i++) {
            if (p.test(relativeAdd(i, 0, dir))) {
                return true;
            }
        }
        return false;
    }
    private final CarController con;

    public FOVUtils(CarController con) {
        this.con=con;
    }


    public Coordinate relativeAdd(int x, int y, WorldSpatial.Direction dir) {
        return directionalCoordinateAdd(new Coordinate(con.getPosition()), new Coordinate(x,y), dir);
    }

    public static Coordinate directionalCoordinateAdd(Coordinate u, Coordinate v, WorldSpatial.Direction dir){
        int dx = 0, dy = 0;
        switch(dir) {
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

        return new Coordinate(u.x+dx, u.y+dy);
    }

}
