package mycontroller.handler;

import mycontroller.actions.Action;
import mycontroller.actions.MudAction;
import tiles.MapTile;
import utilities.Coordinate;

import java.util.Map;

import controller.CarController;

/**
 * Created by Kolatat on 23/5/17.
 */
public class MudHandler extends TrapHandler {
    
    private CarController controller;
    
    public MudHandler(CarController controller) {
        this.controller = controller;
    }

    
    @Override
    public Action getAction(Map<Coordinate, MapTile> view) {
        return new MudAction(controller, view);
    }
}
