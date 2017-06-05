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
 * Nate Wangsutthitham          [755399]
 * Kolatat Thangkasemvathana    [780631]
 * Khai Mei Chin                [755332]
 *
 * Handler for MudTraps
 */
public class MudHandler extends TrapHandler {

    private CarController controller;


    /**
     * Constructor
     *
     * @param controller
     */
    public MudHandler(CarController controller) {
        this.controller = controller;
    }


    /**
     * Return Action to dealt with LavaTrap
     *
     * @param view
     * @return
     */
    @Override
    public Action getAction(Map<Coordinate, MapTile> view) {
        return new MudAction(controller, view);
    }
}
