package mycontroller.actions;

import com.badlogic.gdx.math.Vector2;
import controller.CarController;
import mycontroller.FOVUtils;
import mycontroller.actions.DeadEndAction;
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
 * Nate Wangsutthitham [755399]
 * Kolatat Thangkasemvathana [780631]
 * Khai Mei Chin [755332]
 *
 * 3-Point Turn Action
 * Series of movements to execute the 3-point turn Action
 */

public class ThreePointTurnAction extends DeadEndAction {

    private WorldSpatial.Direction incomingDir;

    private static boolean isAngleSimilar(float t1, float t2, float threshold) {
        float diff = normaliseAngle(t1 - t2);
        if (diff > 180) diff = 360 - diff;
        // now we have a range of 0..180 where 0 means t1 close to t2
        return diff <= threshold;
    }

    private static float normaliseAngle(float theta) {
        while (theta < 0) theta += 360;
        return theta % 360;
    }

    public ThreePointTurnAction(CarController con, Map<Coordinate, MapTile> view, FOVUtils.DeadEnd de) {
        super(con, view, de);
        incomingDir = con.getOrientation();
        incoming = con.getAngle();
        float step = 60; // degrees
        if (de.recommendedTurn == WorldSpatial.RelativeDirection.RIGHT)
            step = -step;
        target1 = incoming + step; 
        target2 = incoming - step;  
        target3 = incoming + 180;   

        // the angle to turn to is in the opposite direction
        target2 -= 180;
        
        target1 = normaliseAngle(target1);
        target2 = normaliseAngle(target2);
        target3 = normaliseAngle(target3);
        
        //logger.info("Target 1: {}, Target 2: {}, Target 3: {}", target1, target2, target3);
    }

    float incoming, target1, target2, target3;

    private enum Phase {
        DECELERATE, POINT1_T, POINT1_B, POINT2_R, POINT2_B, POINT3_A, ACCELERATION, COMPLETED
    }

    private Phase phase = Phase.DECELERATE;
    private static final float T_THRESHOLD = 10;

    private boolean isReversing() {
        // reversing if velocity vector opposite angle
        float car = controller.getAngle();
        float vel = controller.getRawVelocity().angle();
        return isAngleSimilar(car, vel + 180, T_THRESHOLD);
    }

    private void setPhase(Phase p) {
        phase = p;
        logger.info("Switching phase into {}", p.name());
    }

    @Override
    public void update(float delta) {

        incomingDir = controller.getOrientation();
        super.update(delta);
        switch (phase) {

            case DECELERATE:
                if (controller.getVelocity() < 0.8) {
                    controller.applyForwardAcceleration();
                } else if (controller.getVelocity() > 1) {
                    controller.applyReverseAcceleration();
                } else {
                    setPhase(Phase.POINT1_T);
                }
                break;

            case POINT1_T:
                if (deadEnd.recommendedTurn == WorldSpatial.RelativeDirection.LEFT) {
                    applyLeftTurn(incomingDir, delta);
                } else {
                    applyRightTurn(incomingDir, delta);
                }
               // logger.info("Current angle: {}", controller.getAngle());
                if (isAngleSimilar(controller.getAngle(), target1, T_THRESHOLD))
                    setPhase(Phase.POINT1_B);
                break;

            case POINT1_B:
                controller.applyReverseAcceleration();
                
                if (controller.getVelocity() < 0.1 || isReversing()) {
                    setPhase(Phase.POINT2_R);
                }
                break;

            case POINT2_R:
                
                if (deadEnd.recommendedTurn == WorldSpatial.RelativeDirection.LEFT) {
                    applyLeftTurn(incomingDir, delta);
                } else {
                    applyRightTurn(incomingDir, delta);
                }
                if (controller.getVelocity()<1)
                    controller.applyReverseAcceleration();

                //logger.info("Current angle: {}", controller.getAngle());
                if (isAngleSimilar(controller.getAngle(), target2, T_THRESHOLD))
                    setPhase(Phase.POINT2_B);
                break;

            case POINT2_B:
                controller.applyForwardAcceleration();
                if (controller.getVelocity() > 0.1 && !isReversing()) phase = Phase.POINT3_A;
                break;

            case POINT3_A:
                if (deadEnd.recommendedTurn == WorldSpatial.RelativeDirection.LEFT) {
                    applyLeftTurn(incomingDir, delta);
                } else {
                    applyRightTurn(incomingDir, delta);
                }
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

    @Override
    public boolean isCompleted() {
        return phase == Phase.COMPLETED;
    }
}
