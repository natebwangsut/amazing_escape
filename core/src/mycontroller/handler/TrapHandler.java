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
 * Abstract trap-handler:
 * Returns a specific Action for the car to execute 
 * based on the type of handler
 */

public abstract class TrapHandler implements IHandler {
    
    /**
     * Returns an Action class based on the type of trap
     */
    public abstract Action getAction(Map<Coordinate, MapTile> view);
}
