package mycontroller.actions;

import controller.CarController;
import mycontroller.FOVUtils;
import tiles.MapTile;
import utilities.Coordinate;

import java.util.Map;

/**
 * [SWEN30006] Software Modelling and Design
 * Semester 1, 2017
 * Project Part C - amazing-escape
 *
 * Group 107:
 * Nate Wangsutthitham              [755399]
 * Kolatat Thangkasemvathana        [780631]
 * Khai Mei Chin                    [755332]
 *
 * DeadEnd based Action:
 * Action to deal with deadends if I have detected one.
 */
public abstract class DeadEndAction extends Action {

    final FOVUtils.DeadEnd deadEnd;
    protected final Map<Coordinate, MapTile> view;

    /**
     * Constructor
     *
     * @param con
     * @param view
     * @param de
     */
    DeadEndAction(CarController con, Map<Coordinate, MapTile> view, FOVUtils.DeadEnd de) {
        super(con);
        this.deadEnd = de;
        this.view = view;
    }
}
