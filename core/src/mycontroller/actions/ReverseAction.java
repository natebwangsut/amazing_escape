package mycontroller.actions;

/*
 * Nate Bhurinat W. (@natebwangsut | nate.bwangsut@gmail.com)
 * https://github.com/natebwangsut
 */

import controller.CarController;
import mycontroller.FOVUtils;
import tiles.MapTile;
import utilities.Coordinate;

import java.util.Map;

public class ReverseAction extends DeadEndAction {

    public ReverseAction(CarController con, Map<Coordinate, MapTile> view, FOVUtils.DeadEnd de) {
        super(con, view, de);
    }

    @Override
    public boolean isCompleted() {
        return false;
    }

    @Override
    public void update(float delta) {

    }
}
