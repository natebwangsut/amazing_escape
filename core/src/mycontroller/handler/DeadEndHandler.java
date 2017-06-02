package mycontroller.handler;

/*
 * Nate Bhurinat W. (@natebwangsut | nate.bwangsut@gmail.com)
 * https://github.com/natebwangsut
 */

import controller.CarController;
import mycontroller.actions.Action;
import mycontroller.FOVUtils;
import mycontroller.actions.EfficientUTurnAction;
import mycontroller.actions.ThreePointTurnAction;
import tiles.MapTile;
import utilities.Coordinate;

import java.util.Map;

public class DeadEndHandler implements IHandler {

    private CarController controller;

    public DeadEndHandler(CarController controller) {
        this.controller = controller;
    }

    public Action getAction(Map<Coordinate, MapTile> view) {
        // When handling deadends, only view is not enough
        return null;
    }

    public Action getAction(Map<Coordinate, MapTile> view, FOVUtils.DeadEnd deadEnd) {
        return getActionBasedOnSize(view, deadEnd);
    }

    private Action getActionBasedOnSize(Map<Coordinate, MapTile> view, FOVUtils.DeadEnd deadEnd) {
        int size = deadEnd.turnSize;
        if (size >= 3) {
            // u-turn
        } else if (size == 2) {
            // 3pt
            // return new ThreePointsTurnAction(controller, view, deadEnd);
        } else if (size == 1) {
            // reverse
            // return new ReverseAction(controller, view, deadEnd);
        } else {
            // fuck you
            // holy sheeeeeeeeeeeet
        }
        return new ThreePointTurnAction(controller, view, deadEnd);
    }
}
