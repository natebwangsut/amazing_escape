package controller.ev;

import controller.CarController;
import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;

/**
 * Created by Kolatat on 23/5/17.
 */
public class FOVUtils {
    public static boolean isDeadEnd(Map<Coordinate, MapTile> view) {
        // TODO
        return false;
    }

    public static boolean isInVicinity(Map<Coordinate, MapTile> view, Class<? extends MapTile> type) {
        // from all the tiles in our view, check for the presence of any tile that is our type
        return false;
        // return view.entrySet().stream().filter(e -> type.isInstance(e.getValue())).findAny().isPresent();
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

    public boolean checkWallAhead(){
        return checkWall(con.getOrientation());
    }

    public static WorldSpatial.Direction directionalAdd(WorldSpatial.Direction dir, WorldSpatial.RelativeDirection rdir){
        ArrayList<WorldSpatial.Direction> dirs = new ArrayList<>(4);
        dirs.add(WorldSpatial.Direction.NORTH);
        dirs.add(WorldSpatial.Direction.EAST);
        dirs.add(WorldSpatial.Direction.SOUTH);
        dirs.add(WorldSpatial.Direction.WEST);
        int i = dirs.indexOf(dir);
        if(rdir == WorldSpatial.RelativeDirection.LEFT){
            i--;
        } else {
            i++;
        }
        i = (i+4)%4;
        return dirs.get(i);
    }

    public boolean checkFollowingWall(){
        return checkWall(directionalAdd(con.getOrientation(), WorldSpatial.RelativeDirection.LEFT));
    }
    private int wallSensitivity = 2;
    public boolean checkWall(WorldSpatial.Direction dir){
        for(int i=0; i<=wallSensitivity; i++){
            if(con.getView().get(relativeAdd(i, 0, dir)).getName().equals("Wall")){
                return true;
            }
        }
        return false;
    }
    private final CarController con;

    public FOVUtils(CarController con){
        this.con=con;
    }

    public Coordinate relativeAdd(int x, int y, WorldSpatial.Direction dir){
        int dx = 0, dy = 0;
        switch(dir) {
            // we are east oriented (theta=0)
            /*
            R = [ dx     [ cos t, -sin t
                  dy ] x   sin t, cos t  ]
             */
            case EAST:
                dx = x;
                dy = y;
                break;
            case NORTH:
                dy = x;
                dx = -y;
                break;
            case WEST:
                dx = -x;
                dy = -y;
                break;
            case SOUTH:
                dy = -x;
                dx = y;
                break;
        }
        Coordinate curPos = new Coordinate(con.getPosition());
        return new Coordinate(curPos.x+dx, curPos.y+dy);
    }

}
