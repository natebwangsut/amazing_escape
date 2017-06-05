package mycontroller.actions;

import controller.CarController;
import utilities.Coordinate;
import world.WorldSpatial;

import java.util.function.Predicate;

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
 * Action to find nearest wall.
 */
public class FindWallAction extends Action {

    protected final Predicate<Coordinate> tile;
    boolean following = false;

    private enum Phase {
        FINDING, COMPLETED
    }
    private Phase phase = Phase.FINDING;

    /**
     * Constructor
     *
     * @param controller
     * @param tile
     */
    public FindWallAction(CarController controller, Predicate<Coordinate> tile) {
        super(controller);
        this.tile = tile;
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

        case FINDING:
            if (controller.getVelocity() < CAR_SPEED) {
                controller.applyForwardAcceleration();
            }
            // Turn towards the north
            if (!controller.getOrientation().equals(WorldSpatial.Direction.NORTH)) {
                lastTurnDirection = WorldSpatial.RelativeDirection.LEFT;
                applyLeftTurn(controller.getOrientation(), delta);
            }
            if (utils.check(WorldSpatial.Direction.NORTH, tile)) {
                // Turn right until we go back to east!
                if (!controller.getOrientation().equals(WorldSpatial.Direction.EAST)) {
                    lastTurnDirection = WorldSpatial.RelativeDirection.RIGHT;
                    applyRightTurn(controller.getOrientation(), delta);
                } else {
                    following = true;
                    setPhase(Phase.COMPLETED);
                }
            }
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
