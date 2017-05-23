package mycontroller;

import tiles.MapTile;
import tiles.TrapTile;
import utilities.Coordinate;

import java.util.Map;

/**
 * Created by Kolatat on 23/5/17.
 */
public class DiscreteTrapStrategy {
    public Action getAction(Map<Coordinate, MapTile> view){
        TrapTile closest = FOVUtils.getClosest(view, TrapTile.class);
        assert(closest!=null);
        TrapHandler handler = getHandlerFor(closest.getClass());
        return handler.getAction(view);
    }

    private TrapHandler getHandlerFor(Class<? extends TrapTile> type){
        switch(type){
            case GrassTrap.class:
                return new GrassHandler();
            case LavaTrap.class:
                return new LavaHandler();
            case MudTrap.class:
                return new MudHandler();
            default:
                // unhandled
                assert(false);
                return null;
        }
    }
}
