package mycontroller;

import controller.CarController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import world.WorldSpatial;

/**
 * Created by Kolatat on 23/5/17.
 */
public abstract class Action implements IAction {

    protected Logger logger = LogManager.getLogger();

    // Car Speed to move at
    public static final float CAR_SPEED = 3;
    // Offset used to differentiate between 0 and 360 degrees
    private static final int EAST_THRESHOLD = 3;
    protected final CarController controller;
    final FOVUtils utils;
    WorldSpatial.RelativeDirection lastTurnDirection = null;
    boolean turningLeft = false;
    boolean turningRight = false;
    WorldSpatial.Direction previousState = null;
    protected Action(CarController controller) {
        this.controller = controller;
        if (controller instanceof EVController) {
            utils = ((EVController) controller).utils;
        } else {
            utils = new FOVUtils(controller);
        }
    }

    public void update(float delta) {
        checkStateChange();
    }

    /**
     * Checks whether the car's state has changed or not, stops turning if it
     * already has.
     */
    private void checkStateChange() {
        if (previousState == null) {
            previousState = controller.getOrientation();
        } else {
            if (previousState != controller.getOrientation()) {
                if (turningLeft) {
                    turningLeft = false;
                }
                if (turningRight) {
                    turningRight = false;
                }
                previousState = controller.getOrientation();
            }
        }
    }

    /**
     * Turn the car counter clock wise (think of a compass going counter clock-wise)
     */
    protected void applyLeftTurn(WorldSpatial.Direction orientation, float delta) {
        switch (orientation) {
            case EAST:
                if (!controller.getOrientation().equals(WorldSpatial.Direction.NORTH)) {
                    controller.turnLeft(delta);
                }
                break;
            case NORTH:
                if (!controller.getOrientation().equals(WorldSpatial.Direction.WEST)) {
                    controller.turnLeft(delta);
                }
                break;
            case SOUTH:
                if (!controller.getOrientation().equals(WorldSpatial.Direction.EAST)) {
                    controller.turnLeft(delta);
                }
                break;
            case WEST:
                if (!controller.getOrientation().equals(WorldSpatial.Direction.SOUTH)) {
                    controller.turnLeft(delta);
                }
                break;
            default:
                break;

        }

    }

    /**
     * Turn the car clock wise (think of a compass going clock-wise)
     */
    protected void applyRightTurn(WorldSpatial.Direction orientation, float delta) {
        switch (orientation) {
            case EAST:
                if (!controller.getOrientation().equals(WorldSpatial.Direction.SOUTH)) {
                    controller.turnRight(delta);
                }
                break;
            case NORTH:
                if (!controller.getOrientation().equals(WorldSpatial.Direction.EAST)) {
                    controller.turnRight(delta);
                }
                break;
            case SOUTH:
                if (!controller.getOrientation().equals(WorldSpatial.Direction.WEST)) {
                    controller.turnRight(delta);
                }
                break;
            case WEST:
                if (!controller.getOrientation().equals(WorldSpatial.Direction.NORTH)) {
                    controller.turnRight(delta);
                }
                break;
            default:
                break;

        }

    }

    /**
     * Readjust the car to the orientation we are in.
     *
     * @param lastTurnDirection
     * @param delta
     */
    void readjust(WorldSpatial.RelativeDirection lastTurnDirection, float delta) {
        if (lastTurnDirection != null) {
            if (!turningRight && lastTurnDirection.equals(WorldSpatial.RelativeDirection.RIGHT)) {
                adjustRight(controller.getOrientation(), delta);
            } else if (!turningLeft && lastTurnDirection.equals(WorldSpatial.RelativeDirection.LEFT)) {
                adjustLeft(controller.getOrientation(), delta);
            }
        }

    }

    /**
     * Try to orient myself to a degree that I was supposed to be at if I am
     * misaligned.
     */
    private void adjustLeft(WorldSpatial.Direction orientation, float delta) {

        switch (orientation) {
            case EAST:
                if (controller.getAngle() > WorldSpatial.EAST_DEGREE_MIN + EAST_THRESHOLD) {
                    controller.turnRight(delta);
                }
                break;
            case NORTH:
                if (controller.getAngle() > WorldSpatial.NORTH_DEGREE) {
                    controller.turnRight(delta);
                }
                break;
            case SOUTH:
                if (controller.getAngle() > WorldSpatial.SOUTH_DEGREE) {
                    controller.turnRight(delta);
                }
                break;
            case WEST:
                if (controller.getAngle() > WorldSpatial.WEST_DEGREE) {
                    controller.turnRight(delta);
                }
                break;

            default:
                break;
        }

    }

    private void adjustRight(WorldSpatial.Direction orientation, float delta) {
        switch (orientation) {
            case EAST:
                if (controller.getAngle() > WorldSpatial.SOUTH_DEGREE && controller.getAngle() < WorldSpatial.EAST_DEGREE_MAX) {
                    controller.turnLeft(delta);
                }
                break;
            case NORTH:
                if (controller.getAngle() < WorldSpatial.NORTH_DEGREE) {
                    controller.turnLeft(delta);
                }
                break;
            case SOUTH:
                if (controller.getAngle() < WorldSpatial.SOUTH_DEGREE) {
                    controller.turnLeft(delta);
                }
                break;
            case WEST:
                if (controller.getAngle() < WorldSpatial.WEST_DEGREE) {
                    controller.turnLeft(delta);
                }
                break;

            default:
                break;
        }
    }
}
