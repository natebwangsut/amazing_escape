package mycontroller.handler;

/*
 * Nate Bhurinat W. (@natebwangsut | nate.bwangsut@gmail.com)
 * https://github.com/natebwangsut
 */

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
 * Nate Wangsutthitham [755399]
 * Kolatat Thangkasemvathana [780631]
 * Khai Mei Chin [755332]
 *
 * Determines the strategy to take when encountering a dead end of a particular size
 * Enables future modifications to the strategy taken for dead ends
 */

public class DeadEndHandler implements IHandler {

    private CarController controller;

    public DeadEndHandler(CarController controller) {
        this.controller = controller;
    }

    public Action getAction(Map<Coordinate, MapTile> view) {
        // When handling deadends, only view is not enough
        // Method overloading, the variant of getAction() below
        return null;
    }

    public Action getAction(Map<Coordinate, MapTile> view, FOVUtils.DeadEnd deadEnd) {
        return getActionBasedOnSize(view, deadEnd);
    }

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
}
