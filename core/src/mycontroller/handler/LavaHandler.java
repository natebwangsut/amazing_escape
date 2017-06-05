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
 * Lave-Traps Handler:
 * Returns a LavaAction for the car to execute
 */

public class LavaHandler extends TrapHandler {

    /**
     * Returns a LavaAction to handle Lava Traps
     */
    @Override
    public Action getAction(Map<Coordinate, MapTile> view) {
        //TODO
        return null;
    }
}
