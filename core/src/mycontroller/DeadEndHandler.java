package mycontroller;

/*
 * Nate Bhurinat W. (@natebwangsut | nate.bwangsut@gmail.com)
 * https://github.com/natebwangsut
 */

import controller.CarController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial;

import java.util.Map;

public class DeadEndHandler implements IActionHandler {

    private CarController controller;
    private static Logger logger = LogManager.getLogger();

    private boolean initUTurn = false;
    private boolean uTurnStopped = false;

    public DeadEndHandler(CarController controller) {
        this.controller = controller;
    }

    @Override
    public Action getAction(Map<Coordinate, MapTile> view) {
        int size = FOVUtils.getDeadEndSize(view);
        return getActionBasedOnSize(view, size);
    }

    private Action getActionBasedOnSize(Map<Coordinate, MapTile> view, int size) {
        if (size >= 3) {
            // u-turn
        } else if (size == 2) {
            // 3pt
        } else if (size == 1) {
            // reverse
        } else {
            // fuck you
        }
        return null;
    }

    public void uTurn(float delta) {
        // TODO stub
        logger.info("Doing uTurn");
        initUTurn = true;

        if (controller.getVelocity() > 0) {
            logger.info("Applying brake.");
            controller.applyReverseAcceleration();
        }
        else {
            uTurnStopped = true;
            logger.info("Car stopped.");
        }

        return;

        /*
        final int MAX_DEGREE = 360;

        float angle = controller.getAngle();
        float destinationAngle = (angle + MAX_DEGREE/2) % MAX_DEGREE;
        float halfwayAngle = destinationAngle/2;

        logger.info("Current angle: {}, halfway: {}, desination: {}", angle, halfwayAngle, destinationAngle);

        if(angle - halfwayAngle > 0) {
            logger.info("Turning half left.");
            controller.applyReverseAcceleration();
            controller.turnLeft(delta);
        }
        else if(angle > destinationAngle) {
            logger.info("Turning half right.");
            controller.applyForwardAcceleration();
            controller.turnRight(delta);
        }
        */
    }

    public void threePointTurn() {
        // TODO stub
    }

    public void reverseOut() {
        // TODO stub
    }

}
