package mycontroller;

import tiles.MapTile;
import utilities.Coordinate;

/**
 * Created by Kolatat on 23/5/17.
 */
public abstract class TrapHandler {
    public abstract Action getAction(Map<Coordinate, MapTile> view);
}
