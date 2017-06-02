package mycontroller.handler;

import mycontroller.actions.Action;
import tiles.MapTile;
import utilities.Coordinate;

import java.util.Map;

/**
 * Created by Kolatat on 23/5/17.
 */
public abstract class TrapHandler {
    public abstract Action getAction(Map<Coordinate, MapTile> view);
}
