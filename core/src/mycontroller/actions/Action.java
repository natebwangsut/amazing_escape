package mycontroller.actions;

import controller.CarController;
import mycontroller.EVController;
import mycontroller.FOVUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import world.WorldSpatial;

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
 * Action:
 * EVController is pivot and driven on Action.
 */
public abstract class Action implements IAction {

    protected Logger logger = LogManager.getLogger();

    // Car Speed to move at
    protected static final float CAR_SPEED = 3;
    // Offset used to differentiate between 0 and 360 degrees
    private static final int EAST_THRESHOLD = 3;
    protected static final float ANGLE_THRESHOLD = 10;

    protected final CarController controller;
    protected final FOVUtils utils;
    WorldSpatial.RelativeDirection lastTurnDirection = null;
    boolean turningLeft = false;
    boolean turningRight = false;
    private WorldSpatial.Direction previousState = null;

    /**
     * Check the angle between two values
     * @param t1
     * @param t2
     * @param threshold
     * @return
     */
    protected static boolean isAngleSimilar(float t1, float t2, float threshold) {
        float diff = normaliseAngle(t1 - t2);
        if (diff > 180) diff = 360 - diff;
        // now we have a range of 0..180 where 0 means t1 close to t2
        return diff <= threshold;
    }


    /**
     * Normalise angle to [0, 360) range
     *
     * @param theta
     * @return
     */
    protected static float normaliseAngle(float theta) {
        while (theta < 0) theta += 360;
        return theta % 360;
    }


    /**
     * Check if reversing or not
     *
     * @return
     */
    protected boolean isReversing() {
        // reversing if velocity vector opposite angle
        float car = controller.getAngle();
        float vel = controller.getRawVelocity().angle();
        return isAngleSimilar(car, vel + 180, ANGLE_THRESHOLD);
    }


    /**
     * Constructor
     *
     * @param controller
     */
    protected Action(CarController controller) {
        this.controller = controller;
        if (controller instanceof EVController) {
            utils = ((EVController) controller).utils;
        } else {
            utils = new FOVUtils(controller);
        }
    }

    /**
     * Update the car's movement
     *
     * @param delta
     */
    public void update(float delta) {
        checkStateChange();
    }


    /**
     * Check if the state has been changed -> do next action
     *
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
     *
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
     *
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
     *
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


    /**
     * Adjusting myself to the right wall
     *
     * @param orientation       which direction am I moving
     * @param delta             delta from last update
     */
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
