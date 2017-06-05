package mycontroller.handler;

import mycontroller.actions.Action;
import mycontroller.actions.GrassAction;
import tiles.MapTile;
import utilities.Coordinate;

import java.util.Map;

import controller.CarController;

/**
 * Created by Kolatat on 23/5/17.
 */
public class GrassHandler extends TrapHandler {

    private CarController controller;
    
    public GrassHandler(CarController controller){
        this.controller = controller;
    }
    
    
    @Override
    public Action getAction(Map<Coordinate, MapTile> view) {
        return new GrassAction(controller, view);
    }
}
