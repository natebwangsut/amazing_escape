package mycontroller;

import controller.CarController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tiles.LavaTrap;
import tiles.MapTile;
import tiles.TrapTile;
import utilities.Coordinate;
import world.WorldSpatial;

import java.util.HashMap;
import java.util.Map;

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
 * Handling the car's view
 */

public class PersistentView {

    private static Logger logger = LogManager.getLogger();

    private final CarController con;
    private final FOVUtils utils;

    private Map<Coordinate, Property> masterView = new HashMap<>();

    private static final int    MAX_DE_GAP_SIZE     = 3;
    private static final float  R2_DONT_MESS        = 4;


    private Coordinate lastDone = new Coordinate(-1,-1);


    /**
     * Sub class to provide property of the view.
     */
    public static class Property {
        MapTile tile;
        Coordinate coordinate;
        boolean logicalWall;
    }


    /**
     * PersistentView constructor
     *
     * @param con
     */
    public PersistentView(CarController con) {
        this.con = con;
        if (con instanceof EVController) {
            utils = ((EVController) con).utils;
        } else {
            utils = new FOVUtils(con);
        }
    }


    private void dumbTrapDetect(Coordinate start, int lim){
        Coordinate c, cs[];
        cs = new Coordinate[4];

        for(int i=-lim;i<lim;i++){
            for(int j=-lim;j<lim;j++){
                c=new Coordinate(start.x+i, start.y+j);
                cs[0] = new Coordinate(c.x, c.y);
                cs[1] = new Coordinate(c.x, c.y+1);
                cs[2] = new Coordinate(c.x+1, c.y);
                cs[3] = new Coordinate(c.x+1, c.y+1);
                boolean allLava = true;
                boolean hasLava = false;
                for(int k=0;k<4&&allLava;k++){
                    Property p =get(cs[k]);
                    if(p==null){
                        allLava = false;
                        break;
                    }
                    if(p.tile instanceof LavaTrap){
                        if(!p.logicalWall) hasLava = true;
                    } else if(p.logicalWall){
                        // Ok
                        allLava = false;
                        break;
                    } else {
                        allLava = false;
                        break;
                    }
                }
                if(allLava && hasLava){
                    for(int k=0;k<4;k++){
                        get(cs[k]).logicalWall=true;
                    }
                    logger.info("Filled in lava block at [{}]x[{}]", cs[0], cs[3]);
                }
            }
        }

        WorldSpatial.Direction dirs[] = new WorldSpatial.Direction[3];
        dirs[0] = con.getOrientation();
        dirs[1] = FOVUtils.directionalAdd(dirs[0], WorldSpatial.RelativeDirection.LEFT);
        dirs[2] = FOVUtils.directionalAdd(dirs[0], WorldSpatial.RelativeDirection.RIGHT);
        for(WorldSpatial.Direction dir : dirs) {
            // for some reason car riperino when also checking left right
            if(dir!=con.getOrientation()) continue;
            int s = -1;
            int e = -1;
            /*boolean lava = true;

            for(int i=1;i<=con.getViewSquare();i++){
                c = FOVUtils.directionalCoordinateAdd(start, new Coordinate(i,0), dir);
                if(get(c)==null || !(get(c).tile instanceof LavaTrap)){
                    lava = false;
                    break;
                }
            }

            if(lava){
                s=1;
                e=con.getViewSquare();
            } else */{
                for (int i = 1; i <= lim; i++) {
                    c = FOVUtils.directionalCoordinateAdd(start, new Coordinate(i, 0), dir);
                    if (get(c) == null) {
                        s = -1;
                        break;
                    }
                    if (!get(c).logicalWall) {
                        if (get(c).tile instanceof TrapTile) {
                            if (s == -1) {
                                s = i;
                            }
                        } else {
                            e = -1;
                            s = -1;
                        }
                    } else {
                        e = i;
                        break;
                    }
                }
            }
            if (s != -1 && e != -1) {
                for (int i = s; i <= e; i++) {
                    c = FOVUtils.directionalCoordinateAdd(start, new Coordinate(i, 0), dir);
                    get(c).logicalWall = true;
                    logger.info("dum trap at {}", c);
                }
            }
        }
    }


    /**
     * Update the view based on the given map
     *
     * @param view
     */
    public void update(Map<Coordinate, MapTile> view) {
        for (Map.Entry<Coordinate, MapTile> e : view.entrySet()) {
            // skip tiles we already know
            if (masterView.containsKey(e.getKey())) continue;

            Property p = new Property();
            p.tile = e.getValue();
            p.coordinate = e.getKey();
            // condition for our tile to be considered a logical wall
            // if LavaTrap and MudTrap tiles are added as 'logical walls', the car may get stuck; 
            // thus not have any path to go through
            p.logicalWall = FOVUtils.IS_WALL.test(e.getValue());// || e.getValue() instanceof LavaTrap || e.getValue() instanceof MudTrap;

            masterView.put(e.getKey(), p);
        }
    }

    @Deprecated
    private boolean dtdl(Coordinate start, Class<? extends TrapTile> type, int x, int y){
        Coordinate rs = new Coordinate(start.x-x, start.y-y);
        if(get(rs) != null && get(rs).logicalWall) return false;
        for(rs.x+=x, rs.y+=y; get(rs)!=null&&type.isInstance(get(rs).tile)&&!get(rs).logicalWall; rs.x+=x, rs.y+=y){}
        return get(rs)==null||!get(rs).logicalWall;
    }


    /**
     * Detects dead-ends and replace them with logical walls.
     *
     * @param start         coordinate of the starting detection coordinate
     * @return              number of dead ends found
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

    /**
     * Can the deadend be filled ?
     *
     * @param cen
     * @param limit
     * @return
     */
    public int fillDeadEnd(Coordinate cen, int limit) {
        if(cen==lastDone) return 0;
        lastDone = new Coordinate(cen.x, cen.y);

        // I will not fill dead ends when car is currently in a virtual wall
        Property p = get(new Coordinate(con.getPosition()));
        assert(p!=null);
        if (p.logicalWall && !p.tile.getName().equals("Wall")) return 0;

        dumbTrapDetect(cen, limit);

        int totalDef = 0;
        // indicates the number of dead ends filled in a single loop
        int def;
        // repeatedly fill in dead end until no more is fill-able
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


    /**
     * Return the property given the coordinate
     *
     * @param c     coordinate to show the property
     * @return      property
     */
    public Property get(Coordinate c) {
        return masterView.get(c);
    }
}
