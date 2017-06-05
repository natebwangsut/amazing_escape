package mycontroller.handler;

import controller.CarController;
import mycontroller.FOVUtils;
import mycontroller.actions.Action;
import mycontroller.actions.FollowAction;
import tiles.*;
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
 * Determines the strategy to take when encountering a particular type of trap
 * Enables future modifications to the strategy taken for the traps
 */

public class DiscreteTrapStrategy implements IHandler {

    CarController con;

    public DiscreteTrapStrategy(CarController con) {
        this.con = con;
    }
    
    @Override
    public Action getAction(Map<Coordinate, MapTile> view) {
        // Method overloading
        // Variant of getAction() below
        return null;
    }

    public Action getAction(Map<Coordinate, MapTile> view, String type) {
 //       return new FollowAction(con, t -> con.getView().get(t) instanceof TrapTile);
 //       TrapTile closest = FOVUtils.getClosest(view, TrapTile.class);
//        assert(closest!=null);
        TrapHandler handler = getHandlerFor(type);
        return handler.getAction(view);
        // return null;
    }

    private TrapHandler getHandlerFor(String type) {
        // TODO: Trap handler
        switch(type) {
            case "GrassTrap":
                return new GrassHandler(con);
//            case tiles.LavaTrap.class:
//                return new LavaHandler();
            case "MudTrap":
                return new MudHandler(con);
//            default:
//                // unhandled
//                assert(false);
//                return null;
        }
        return null;
    }

    
}
