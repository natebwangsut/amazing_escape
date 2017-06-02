package mycontroller.actions;

import controller.CarController;
import mycontroller.FOVUtils;
import tiles.MapTile;
import utilities.Coordinate;

import java.util.Map;

/**
 * Created by Kolatat on 2/6/17.
 */
public abstract class DeadEndAction extends Action {
    protected final FOVUtils.DeadEnd deadEnd;
    protected final Map<Coordinate, MapTile> view;
    DeadEndAction(CarController con, Map<Coordinate, MapTile> view, FOVUtils.DeadEnd de){
        super(con);
        this.deadEnd = de;
        this.view = view;
    }
}
