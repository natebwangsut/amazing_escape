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
 * Nate Wangsutthitham [755399]
 * Kolatat Thangkasemvathana [780631]
 * Khai Mei Chin [755332]
 *
 * Standard interface for Handlers.
 * This enables further implementation upon different systems.
 */


public interface IHandler {
    
    /**
     * Returns the particular action for the controller to take,
     * when met with certain situations (dead ends or traps)
     * @param view      The current view of car
     * @return          a series of movements, as an Action-type class, for the controller to execute
     */
    Action getAction(Map<Coordinate, MapTile> view);
}
