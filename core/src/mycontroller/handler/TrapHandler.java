package mycontroller.handler;

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
 * Abstract class TrapHandler
 *
 * This class is an abstraction of the handlers for each particular traps
 * Handler for Traps.
 */
public abstract class TrapHandler implements IHandler {

    /**
     * Provide general instruction of the method.
     * Should return Action so that the controller could take it prior detecting the traps
     *
     * @param view
     * @return
     */
    public abstract Action getAction(Map<Coordinate, MapTile> view);
}
