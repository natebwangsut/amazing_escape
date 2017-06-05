package mycontroller.handler;

import controller.CarController;
import mycontroller.FOVUtils;
import mycontroller.actions.Action;
import mycontroller.actions.FollowAction;
import tiles.*;
import utilities.Coordinate;

import java.util.Map;

/**
 * Created by Kolatat on 23/5/17.
 */
public class DiscreteTrapStrategy implements IHandler {

    CarController con;

    public DiscreteTrapStrategy(CarController con) {
        this.con = con;
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

    @Override
    public Action getAction(Map<Coordinate, MapTile> view) {
        // TODO Auto-generated method stub
        return null;
    }
}
