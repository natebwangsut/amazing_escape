package mycontroller.handler;

import controller.CarController;
import mycontroller.actions.Action;
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
 * Handler for LavaTrap.
 */
public class LavaHandler extends TrapHandler {

    private CarController controller;


    /**
     * Constructor
     *
     * @param controller
     */
    public LavaHandler(CarController controller) {
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
        //TODO
        return null;
    }
}
