package mycontroller.handler;

import controller.CarController;
import mycontroller.actions.*;
import mycontroller.FOVUtils;
import org.apache.logging.log4j.LogManager;
import tiles.MapTile;
import utilities.Coordinate;

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
 * Handle deadends
 */

public class DeadEndHandler implements IHandler {

    private CarController controller;


    /**
     * Constructor
     *
     * @param controller
     */
    public DeadEndHandler(CarController controller) {
        this.controller = controller;
    }


    /**
     * Return Action to deal with current deadend
     *
     * @param view
     * @param deadEnd
     * @return
     */
    public Action getAction(Map<Coordinate, MapTile> view, FOVUtils.DeadEnd deadEnd) {
        return getActionBasedOnSize(view, deadEnd);
    }


    /**
     * Tell which Action to take for the size given.
     *
     * @param view
     * @param deadEnd
     * @return
     */
    private Action getActionBasedOnSize(Map<Coordinate, MapTile> view, FOVUtils.DeadEnd deadEnd) {
        int size = deadEnd.turnSize;
        System.out.println("Size of dead end is: "+size);
        if (size >= 3) {
            // u-turn
            return new EfficientUTurnAction(controller, view, deadEnd);
        } else if (size == 2) {
            // 3pt
            return new ThreePointTurnAction(controller, view, deadEnd);
        } else if (size == 1) {
            // reverse
            // this should probably never happen
            return new ReverseOutAction(controller, view, deadEnd);
        } else {

            // nothing to see here ...
        }

        // if we reach here then we are in a 0-width dead end and also in deep ****
        // could be due to detecting logical walls as real wall
        // so we will use a more basic follow wall action following only physical
        // wall, completing when delta > xxx
        LogManager.getLogger().warn("In deep ****.");
        return new FollowAction(controller, c->FOVUtils.IS_WALL.test(view.get(c))){

            {
                doInit = false;
            }

            float sumDelta = 0;

            @Override
            public void update(float delta) {
                sumDelta+=delta;

                if(!following){
                    if(controller.getVelocity()<CAR_SPEED){
                        controller.applyForwardAcceleration();
                    }
                    if(utils.checkAhead(tile)) following=true;
                }
                super.update(delta);

            }

            @Override
            public boolean isCompleted() {
                logger.info("basic wall delta={}", sumDelta);
                return sumDelta>0.5;
            }
        };
    }


    /**
     * Required by IHandler
     *
     * @param view
     * @return
     */
    public Action getAction(Map<Coordinate, MapTile> view) {
        // When handling deadends, only view is not enough
        return null;
    }

}
