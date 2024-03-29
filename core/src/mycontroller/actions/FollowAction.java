package mycontroller.actions;

import controller.CarController;
import tiles.MudTrap;
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
 * Action to Stick to the wall.
 */
public class FollowAction extends Action {

    protected final Predicate<Coordinate> tile;
    protected boolean following = false;


    /**
     * Constructor
     *
     * @param controller
     * @param tile
     */
    public FollowAction(CarController controller, Predicate<Coordinate> tile) {
        super(controller);
        this.tile = tile;
    }

    protected boolean doInit = true;


    /**
     * Update the car's movement
     *
     * @param delta
     */
    @Override
    public void update(float delta) {
        super.update(delta);
        

        // If a Mud trap is spotted ahead, increase acceleration as much as possible
        if(utils.checkTileAhead(t->t instanceof MudTrap)){
            if(controller.getVelocity() < 5f){
                controller.applyForwardAcceleration();
            }
        }
        

        // If you are not following a wall initially, find a wall to stick to!
        if (!following && doInit) {
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
                }
            }
        }
        // Once the car is already stuck to a wall, apply the following logic
        else {

            // Readjust the car if it is misaligned.
            readjust(lastTurnDirection, delta);

            if (turningRight) {
                applyRightTurn(controller.getOrientation(), delta);
            } else if (turningLeft) {
                // Apply the left turn if you are not currently near a wall.
                if (!utils.checkFollowing(tile)) {
                    applyLeftTurn(controller.getOrientation(), delta);
                } else {
                    turningLeft = false;
                }
            }
            // Try to determine whether or not the car is next to a wall.
            else if (utils.checkFollowing(tile)) {
                // Maintain some velocity
                if (controller.getVelocity() < CAR_SPEED) {
                    controller.applyForwardAcceleration();
                }
                // If there is wall ahead, turn right!
                if (utils.checkAhead(tile)) {
                    lastTurnDirection = WorldSpatial.RelativeDirection.RIGHT;
                    turningRight = true;

                }

            }
            // This indicates that I can do a left turn if I am not turning right
            else {
                lastTurnDirection = WorldSpatial.RelativeDirection.LEFT;
                turningLeft = true;
            }
        }
    }


    /**
     * Tell the handler that the action taken is completed.
     * @return
     */
    @Override
    public boolean isCompleted() {
        return false;
    }
}
