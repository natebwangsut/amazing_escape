package mycontroller;

/*
 * Nate Bhurinat W. (@natebwangsut | nate.bwangsut@gmail.com)
 * https://github.com/natebwangsut
 */

import controller.CarController;
import tiles.MapTile;
import utilities.Coordinate;

import java.util.Map;

public class DeadEndHandler implements IActionHandler {

    private CarController controller;

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

    public void uTurn() {
        // TODO stub
    }

    public void threePointTurn() {
        // TODO stub
    }

    public void reverseOut() {
        // TODO stub
    }

}