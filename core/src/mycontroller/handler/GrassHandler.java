package mycontroller.handler;

import mycontroller.actions.Action;
import tiles.MapTile;
import utilities.Coordinate;

import java.util.Map;

/**
 * Created by Kolatat on 23/5/17.
 */
public class GrassHandler extends TrapHandler {
    @Override
    public Action getAction(Map<Coordinate, MapTile> view) {
        return null;
    }
}
