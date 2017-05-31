package mycontroller;

import controller.CarController;
import mycontroller.handler.TrapHandler;
import tiles.*;
import utilities.Coordinate;

import java.util.Map;

/**
 * Created by Kolatat on 23/5/17.
 */
public class DiscreteTrapStrategy implements IActionHandler {

    CarController con;
    public DiscreteTrapStrategy(CarController con) {
        this.con=con;
    }

    public Action getAction(Map<Coordinate, MapTile> view) {
        return new FollowAction(con, t->t instanceof TrapTile);
//        TrapTile closest = FOVUtils.getClosest(view, TrapTile.class);
//        assert(closest!=null);
//        TrapHandler handler = getHandlerFor(closest.getClass());
//        return handler.getAction(view);
        // return null;
    }

    private TrapHandler getHandlerFor(Class<? extends TrapTile> type) {
        // TODO: Trap handler
//        switch(type) {
//            case tiles.GrassTrap.class:
//                return new GrassHandler();
//            case tiles.LavaTrap.class:
//                return new LavaHandler();
//            case tiles.MudTrap.class:
//                return new MudHandler();
//            default:
//                // unhandled
//                assert(false);
//                return null;
//        }
        return null;
    }
}
