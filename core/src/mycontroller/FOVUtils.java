package mycontroller;

import tiles.MapTile;
import utilities.Coordinate;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;

/**
 * Created by Kolatat on 23/5/17.
 */
public class FOVUtils {
    public static boolean isDeadEnd(Map<Coordinate, MapTile> view) {
        // TODO
    }

    public static boolean isInVicinity(Map<Coordinate, MapTile> view, Class<? extends MapTile> type) {
        // from all the tiles in our view, check for the presence of any tile that is our type
        return view.entrySet().stream().filter(e -> type.isInstance(e.getValue())).findAny().isPresent();
    }

    public static int getDeadEndSize(Map<Coordinate, MapTile> view) {
        // TODO
    }

    public static class MapEntry<T extends MapTile> {
        private final Coordinate coord;
        private final T tile;

        private MapEntry(Coordinate coord, T tile) {
            this.coord = cood;
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
            return new MapEntry<>(candidate.get().getKey(), candidate.get().getValue());
        } else {
            return null;
        }
    }
}
