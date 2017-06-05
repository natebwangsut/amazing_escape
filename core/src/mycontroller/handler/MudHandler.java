package mycontroller.handler;

import mycontroller.actions.Action;
import mycontroller.actions.MudAction;
import tiles.MapTile;
import utilities.Coordinate;

import java.util.Map;

import controller.CarController;

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
 * Mud-Traps Handler:
 * Returns a MudAction for the car to execute
 */

public class MudHandler extends TrapHandler {
    
    private CarController controller;
    
    public MudHandler(CarController controller) {
        this.controller = controller;
    }

    /**
     * Returns MudAction to handle a Mud Trap
     */
    @Override
    public Action getAction(Map<Coordinate, MapTile> view) {
        return new MudAction(controller, view);
    }
}
