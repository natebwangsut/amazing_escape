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
 * Standard interface for Handler.
 * This enables further implementation upon different systems.
 */
public interface IHandler {

    /**
     * Provide general instruction of the method.
     * Should return Action so that the controller could take it prior detecting the obstacles
     *
     * @param view
     * @return
     */
    Action getAction(Map<Coordinate, MapTile> view);
}
