package mycontroller;

import controller.CarController;
import tiles.LavaTrap;
import tiles.MapTile;
import tiles.MudTrap;
import utilities.Coordinate;
import world.WorldSpatial;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kolatat on 1/6/17.
 */
public class PersistentView {
    final CarController con;
    final FOVUtils utils;
    protected Map<Coordinate, Property> masterView = new HashMap<>();

    public PersistentView(CarController con) {
        this.con = con;
        if (con instanceof EVController) {
            utils = ((EVController) con).utils;
        } else {
            utils = new FOVUtils(con);
        }
    }

    public void update(Map<Coordinate, MapTile> view) {
        for (Map.Entry<Coordinate, MapTile> e : view.entrySet()) {
            if (masterView.containsKey(e.getKey())) continue;
            Property p = new Property();
            p.tile = e.getValue();
            p.coordinate = e.getKey();
            p.logicalWall = FOVUtils.IS_WALL.test(e.getValue()) || e.getValue() instanceof LavaTrap || e.getValue() instanceof MudTrap;
            masterView.put(e.getKey(), p);
        }
    }

    /*
     * Detects dead-ends and replace them with logical walls.
     *
     * @return number of dead-ends replaced
     */
    protected int detectDeadEnd(Coordinate start) {
        int dec = 0;
        Coordinate car = new Coordinate(con.getPosition());
        directional:
        for (WorldSpatial.Direction dir : WorldSpatial.Direction.values()) {
            Coordinate cLeft = FOVUtils.directionalCoordinateAdd(start, new Coordinate(-1, 0), dir);
            Property left = get(cLeft);
            if (left == null || !left.logicalWall) continue directional;
            int i;
            width:
            for (i = 0; i < 4; i++) {
                Coordinate c = FOVUtils.directionalCoordinateAdd(start, new Coordinate(i, 0), dir);
                if (FOVUtils.dist2(car, c) < 4) continue directional;

                Property p = get(c);
                Coordinate c2 = FOVUtils.directionalCoordinateAdd(start, new Coordinate(i, 1), dir);
                Property p2 = get(c2);

                if (p == null || p2 == null) continue directional;

                if (p.tile.getName().equals("Road") && !p.logicalWall && p2.logicalWall) continue width;

                if (p.logicalWall) break;

                continue directional;
            }
            if (i > 0 && i < 4) {
                System.out.printf("Filling ");
                for (int j = 0; j < i; j++) {
                    Coordinate c = FOVUtils.directionalCoordinateAdd(start, new Coordinate(j, 0), dir);
                    Property p = get(c);
                    p.logicalWall = true;
                    System.out.printf("[%s]", c);
                }
                System.out.println();
                dec++;
            }
        }
        return dec;
    }

    public int fillDeadEnd(Coordinate cen, int limit) {
        int totalDef = 0;
        int def;
        do {
            def = 0;
            Coordinate c = new Coordinate(cen.x - limit, cen.y - limit);
            for (; c.x <= cen.x + limit; c.x++)
                for (c.y = cen.y - limit; c.y <= cen.y + limit; c.y++) {
                    def += detectDeadEnd(c);
                }
            totalDef += def;
        } while (def > 0);
        return totalDef;
    }

    public Property get(Coordinate c) {
        return masterView.get(c);
    }

    public static class Property {
        MapTile tile;
        Coordinate coordinate;
        boolean logicalWall;
    }
}
