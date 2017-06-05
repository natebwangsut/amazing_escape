package mycontroller.actions;

import controller.CarController;
import mycontroller.FOVUtils;
import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial;

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
 * UTurning with efficiency Action.
 */
public class EfficientUTurnAction extends DeadEndAction {

    private WorldSpatial.Direction target;

    private enum Phase {
        SETUP,
        TURNING,
        ACCELERATING,
        COMPLETED
    }

    private Phase phase = Phase.SETUP;


    /**
     * Constructor
     *
     * @param con
     * @param view
     * @param de
     */
    public EfficientUTurnAction(CarController con, Map<Coordinate, MapTile> view, FOVUtils.DeadEnd de) {
        super(con,view,de);
        target = FOVUtils.directionalAdd(con.getOrientation(), WorldSpatial.RelativeDirection.LEFT);
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
            //
            case SETUP:
                controller.applyReverseAcceleration();
                if (controller.getVelocity() < 1.5)
                    setPhase(Phase.TURNING);
                break;
            //
            case TURNING:
                if (controller.getVelocity() < 0.5)
                    controller.applyForwardAcceleration();
                applyRightTurn(controller.getOrientation(), delta);
                if (controller.getOrientation() == target)
                    setPhase(Phase.ACCELERATING);
                break;
            //
            case ACCELERATING:
                controller.applyForwardAcceleration();
                if (controller.getVelocity() >= CAR_SPEED)
                    setPhase(Phase.COMPLETED);
                break;
        default:
            break;
        }
    }


    /**
     * Tell the handler that the action taken is completed.
     * @return
     */
    @Override
    public boolean isCompleted() {
        return phase == Phase.COMPLETED;
    }
}
