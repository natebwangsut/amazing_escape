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
 * ThreePointTurn Action
 * Action to do a uturn with limited space.
 *
 * Advanced technique for driver.
 */

public class ThreePointTurnAction extends DeadEndAction {

    private WorldSpatial.Direction incomingDir;
    private float incoming, target1, target2, target3;

    private enum Phase {
        DECELERATE,
        POINT1_T,
        POINT1_B,
        POINT2_R,
        POINT2_B,
        POINT3_A,
        ACCELERATION,
        COMPLETED
    }

    private Phase phase = Phase.DECELERATE;
    private static final float T_THRESHOLD = 10;


    /**
     * Constructor
     *
     * @param controller
     * @param view
     * @param de
     */
    public ThreePointTurnAction(CarController controller, Map<Coordinate, MapTile> view, FOVUtils.DeadEnd de) {

        super(controller, view, de);
        incomingDir = controller.getOrientation();
        incoming = controller.getAngle();

        float step = 60; // degrees

        if (de.recommendedTurn == WorldSpatial.RelativeDirection.RIGHT)
            step = -step;

        target1 = incoming + step;
        target2 = incoming - step;
        target3 = incoming + 180;

        // The angle to turn to is in the opposite direction
        target2 -= 180;

        // Normalise angles
        target1 = normaliseAngle(target1);
        target2 = normaliseAngle(target2);
        target3 = normaliseAngle(target3);
        //logger.info("Target 1: {}, Target 2: {}, Target 3: {}", target1, target2, target3);
    }


    /**
     * Check the angle between two values
     * @param t1
     * @param t2
     * @param threshold
     * @return
     */
    private static boolean isAngleSimilar(float t1, float t2, float threshold) {
        float diff = normaliseAngle(t1 - t2);
        if (diff > 180) diff = 360 - diff;
        // now we have a range of 0..180 where 0 means t1 close to t2
        return diff <= threshold;
    }


    /**
     * Normallise angle to [0, 360) range
     *
     * @param theta
     * @return
     */
    private static float normaliseAngle(float theta) {
        while (theta < 0) theta += 360;
        return theta % 360;
    }


    /**
     * Check if reversing or not
     *
     * @return
     */
    private boolean isReversing() {
        // reversing if velocity vector opposite angle
        float car = controller.getAngle();
        float vel = controller.getRawVelocity().angle();
        return isAngleSimilar(car, vel + 180, T_THRESHOLD);
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
        incomingDir = controller.getOrientation();

        switch (phase) {
            case DECELERATE:
                if (controller.getVelocity() < 0.8)
                    controller.applyForwardAcceleration();
                else if (controller.getVelocity() > 1)
                    controller.applyReverseAcceleration();
                else
                    setPhase(Phase.POINT1_T);
                break;

            case POINT1_T:
                if (deadEnd.recommendedTurn == WorldSpatial.RelativeDirection.LEFT)
                    applyLeftTurn(incomingDir, delta);
                else
                    applyRightTurn(incomingDir, delta);

               // logger.info("Current angle: {}", controller.getAngle());
                if (isAngleSimilar(controller.getAngle(), target1, T_THRESHOLD))
                    setPhase(Phase.POINT1_B);
                break;

            case POINT1_B:
                controller.applyReverseAcceleration();

                if (controller.getVelocity() < 0.1 || isReversing())
                    setPhase(Phase.POINT2_R);
                break;

            case POINT2_R:
                if (deadEnd.recommendedTurn == WorldSpatial.RelativeDirection.LEFT)
                    applyLeftTurn(incomingDir, delta);
                else
                    applyRightTurn(incomingDir, delta);

                if (controller.getVelocity() < 1)
                    controller.applyReverseAcceleration();

                //logger.info("Current angle: {}", controller.getAngle());
                if (isAngleSimilar(controller.getAngle(), target2, T_THRESHOLD))
                    setPhase(Phase.POINT2_B);
                break;

            case POINT2_B:
                controller.applyForwardAcceleration();
                if (controller.getVelocity() > 0.1 && !isReversing())
                    phase = Phase.POINT3_A;
                break;

            case POINT3_A:
                if (deadEnd.recommendedTurn == WorldSpatial.RelativeDirection.LEFT)
                    applyLeftTurn(incomingDir, delta);
                else
                    applyRightTurn(incomingDir, delta);

                if (controller.getVelocity() < 1)
                    controller.applyForwardAcceleration();

               // logger.info("Current angle: {}", controller.getAngle());
                if (isAngleSimilar(controller.getAngle(), target3, T_THRESHOLD))
                    phase = Phase.ACCELERATION;
                break;

            case ACCELERATION:
                controller.applyForwardAcceleration();
                if (controller.getVelocity() > CAR_SPEED)
                    phase = Phase.COMPLETED;
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
