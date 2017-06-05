package mycontroller.handler;

import controller.CarController;
import mycontroller.actions.Action;
import mycontroller.FOVUtils;
import mycontroller.actions.EfficientUTurnAction;
import mycontroller.actions.ReverseOutAction;
import mycontroller.actions.ThreePointTurnAction;
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
            // fuck you
            // holy sheeeeeeeeeeeet
        }
        return new ThreePointTurnAction(controller, view, deadEnd);
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
