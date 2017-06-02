package mycontroller.actions;

import controller.CarController;
import mycontroller.FOVUtils;
import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial;

import java.util.Map;

/**
 * Created by Kolatat on 2/6/17.
 */
public class UTurnAction extends DeadEndAction {

    public UTurnAction(CarController con, Map<Coordinate, MapTile> view, FOVUtils.DeadEnd de){
        super(con,view,de);
        target = FOVUtils.directionalAdd(con.getOrientation(), WorldSpatial.RelativeDirection.LEFT);
        target = FOVUtils.directionalAdd(target, WorldSpatial.RelativeDirection.LEFT);
    }

    private enum Phase {
        BRAKING, TURNING, ACCELERATING, COMPLETED
    };

    private Phase phase = Phase.BRAKING;

    WorldSpatial.Direction target;

    @Override
    public void update(float delta) {
        super.update(delta);
        switch(phase){
            case BRAKING:
                controller.applyReverseAcceleration();
                if(controller.getVelocity() < 0.7) phase = Phase.TURNING;
                break;
            case TURNING:
                if(controller.getVelocity()<0.3) controller.applyForwardAcceleration();
                applyRightTurn(controller.getOrientation(), delta);
                if(controller.getOrientation() == target) phase = Phase.ACCELERATING;
                break;
            case ACCELERATING:
                controller.applyForwardAcceleration();
                if(controller.getVelocity()>=CAR_SPEED) phase = Phase.COMPLETED;
                break;
        }
    }

    @Override
    public boolean isCompleted() {
        return phase==Phase.COMPLETED;
    }
}
