package mycontroller;

import controller.CarController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    private static Logger logger = LogManager.getLogger();

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
            // skip tiles we already know
            if (masterView.containsKey(e.getKey())) continue;

            Property p = new Property();
            p.tile = e.getValue();
            p.coordinate = e.getKey();
            // condition for our tile to be considered a logical wall
            p.logicalWall = FOVUtils.IS_WALL.test(e.getValue()) || e.getValue() instanceof LavaTrap || e.getValue() instanceof MudTrap;

            masterView.put(e.getKey(), p);
        }
    }

    private static final int MAX_DE_GAP_SIZE = 3;
    private static final float R2_DONT_MESS = 4;

    /*
     * Detects dead-ends and replace them with logical walls.
     *
     * @return number of dead-ends replaced
     */
    protected int detectDeadEnd(Coordinate start) {
        int dec = 0; // dead end count
        Coordinate car = new Coordinate(con.getPosition());

        // repeat for all directions
        directional:
        for (WorldSpatial.Direction dir : WorldSpatial.Direction.values()) {

            /*
            DED Algorithm: X = wall (real and virtual), R = road, ? = any

            width=1     width=2     width=3
            ? X ?       ? X X ?     ? X X X ?
            X R X       X R R X     X R R R X

            replace the road R here with a virtual wall ^ so above becomes:
            ? X ?       ? X X ?     ? X X X ?
            X X X       X X X X     X X X X X

            Detection:
                x=  -1 (0..n-1) n
            y=1      ?    X     ?
            y=0      X    R     X

            And so we fill in all blocks at (x,y)=(0 to n-1, 0)

            variable names
            ?       c2      ?
            cLeft   c1      c1 at last iteration
             */

            Coordinate cLeft = FOVUtils.directionalCoordinateAdd(start, new Coordinate(-1, 0), dir);
            Property left = get(cLeft);
            if (left == null || !left.logicalWall) continue directional;
            int i;
            width:
            for (i = 0; i <= MAX_DE_GAP_SIZE; i++) {
                Coordinate c = FOVUtils.directionalCoordinateAdd(start, new Coordinate(i, 0), dir);
                // skip if our target is too close to car
                if (FOVUtils.dist2(car, c) < R2_DONT_MESS) continue directional;

                Property p = get(c);
                Coordinate c2 = FOVUtils.directionalCoordinateAdd(start, new Coordinate(i, 1), dir);
                Property p2 = get(c2);

                if (p == null || p2 == null) continue directional;

                /*
                 * As stated above, we require a logical wall right above a road
                 * and the road must also only be a road and not a logical wall
                 */
                if (p.tile.getName().equals("Road") && !p.logicalWall && p2.logicalWall) continue width;

                // we found the end (cRight)
                if (p.logicalWall) break;

                continue directional;
            }

            if (i > 0 && i <= MAX_DE_GAP_SIZE) {
                logger.info("Filling ");
                for (int j = 0; j < i; j++) {
                    // mark all roads in here as logical walls
                    Coordinate c = FOVUtils.directionalCoordinateAdd(start, new Coordinate(j, 0), dir);
                    Property p = get(c);
                    p.logicalWall = true;
                    logger.info("[{}]", c);
                }
                dec++;
            }
        }
        return dec;
    }

    public int fillDeadEnd(Coordinate cen, int limit) {
        // I will not fill dead ends when car is currently in a virtual wall
        Property p = get(new Coordinate(con.getPosition()));
        assert(p!=null);
        if (p.logicalWall && !p.tile.getName().equals("Wall")) return 0;

        int totalDef = 0;
        // indicates the number of dead ends filled in a single loop
        int def;
        // repeatedly fill in dead end until no more is fillable
        do {
            def = 0;
            // fills starting with tile within center +- limit
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
