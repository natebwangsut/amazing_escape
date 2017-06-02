package mycontroller.actions;

/*
 * Nate Bhurinat W. (@natebwangsut | nate.bwangsut@gmail.com)
 * https://github.com/natebwangsut
 */

import controller.CarController;
import mycontroller.FOVUtils;
import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial;

import java.util.Map;

public class ThreePointsTurnAction extends DeadEndAction {

    private WorldSpatial.Direction target;

    private enum Phase {
        BRAKING,
        TURNING,
        ACCELERATING,
        COMPLETED
    }

    private Phase phase = Phase.BRAKING;

    ThreePointsTurnAction(CarController con, Map<Coordinate, MapTile> view, FOVUtils.DeadEnd de){
        super(con, view, de);
        target = FOVUtils.directionalAdd(con.getOrientation(), WorldSpatial.RelativeDirection.LEFT);
        target = FOVUtils.directionalAdd(target, WorldSpatial.RelativeDirection.LEFT);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        // TODO implement 3pt turn
    }

    @Override
    public boolean isCompleted() {
        return false;
    }
}
