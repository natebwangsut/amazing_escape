package mycontroller.handler;

import mycontroller.actions.Action;
import mycontroller.actions.GrassAction;
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
 * Grass-Traps Handler:
 * Returns a GrassAction for the car to execute
 */

public class GrassHandler extends TrapHandler {

    private CarController controller;

    public GrassHandler(CarController controller) {
        this.controller = controller;
    }

    /**
     * Returns a GrassAction to handle Grass Traps
     */
    @Override
    public Action getAction(Map<Coordinate, MapTile> view) {
        return new GrassAction(controller, view);
    }
}
