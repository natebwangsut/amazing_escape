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

    // Targeting direction after u-turn
    private WorldSpatial.Direction target;

    private enum Phase {
        BRAKING,
        TURNING,
        ACCELERATING,
        COMPLETED
    }

    private Phase phase = Phase.BRAKING;

    /**
     *
     * @param controller
     * @param view
     * @param de
     */
    public UTurnAction(CarController controller, Map<Coordinate, MapTile> view, FOVUtils.DeadEnd de) {
        super(controller, view, de);
        target = FOVUtils.directionalAdd(controller.getOrientation(), WorldSpatial.RelativeDirection.LEFT);
        target = FOVUtils.directionalAdd(target, WorldSpatial.RelativeDirection.LEFT);
    }


    /**
     * Set the phase
     *
     * @param p     phase to be set into
     */
    private void setPhase(Phase p) {
        phase = p;
        logger.info("Switching phase into {}", p.name());
    }


    /**
     * Update the car's movement
     *
     * @param delta
     */
    @Override
    public void update(float delta) {
        super.update(delta);
        switch(phase) {
            case BRAKING:
                controller.applyReverseAcceleration();
                if (controller.getVelocity() < 0.7) phase = Phase.TURNING;
                break;
            case TURNING:
                if (controller.getVelocity()<0.3) controller.applyForwardAcceleration();
                applyRightTurn(controller.getOrientation(), delta);
                if (controller.getOrientation() == target) phase = Phase.ACCELERATING;
                break;
            case ACCELERATING:
                controller.applyForwardAcceleration();
                if (controller.getVelocity()>=CAR_SPEED) phase = Phase.COMPLETED;
                break;
        }
    }


    /**
     * Tell the handler that the action taken is completed.
     * @return
     */
    @Override
    public boolean isCompleted() {
        return phase==Phase.COMPLETED;
    }
}
