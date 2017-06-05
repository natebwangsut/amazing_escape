package mycontroller.handler;

import controller.CarController;
import mycontroller.actions.Action;
import tiles.*;
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
 * Handles each particular trap.
 */
public class DiscreteTrapStrategy implements IHandler {

    private CarController controller;

    /**
     * Constructor
     *
     * @param con
     */
    public DiscreteTrapStrategy(CarController con) {
        this.controller = con;
    }


    /**
     * Get Action based on Trap
     *
     * @param view
     * @param type
     * @return
     */
    public Action getAction(Map<Coordinate, MapTile> view, String type) {
        //return new FollowAction(controller, t -> controller.getView().get(t) instanceof TrapTile);
        //TrapTile closest = FOVUtils.getClosest(view, TrapTile.class);
        //assert(closest!=null);
        TrapHandler handler = getHandlerFor(type);
        assert handler != null;

        return handler.getAction(view);
    }


    /**
     * Assign Handler for particular Trap
     *
     * @param type
     * @return
     */
    private TrapHandler getHandlerFor(String type) {
        switch(type) {
            case "GrassTrap":
                return new GrassHandler(controller);
            case "MudTrap":
                return new MudHandler(controller);
//            default:
//                // unhandled
//                assert(false);
//                return null;
        }
        return null;
    }


    /**
     * Required by IHandler
     *
     * @param view
     * @return
     */
    public Action getAction(Map<Coordinate, MapTile> view) {
        // When handling traps, only view is not enough
        return null;
    }
}
