package controller.ev;

import controller.CarController;
import tiles.TrapTile;
import world.Car;

public class EVController extends CarController {

    public EVController(Car car) {
        super(car);
    }

    {
        utils = new FOVUtils(this);
        backgroundState = new FollowWallAction(this);
        deh = new DeadEndHandler();
        th = new DiscreteTrapStrategy();
    }

    Action state = null;
    Action backgroundState;
    IActionHandler deh;
    IActionHandler th;
    FOVUtils utils;

    @Override
    public void update(float delta) {

        Action toDo;

        if(state==null || state.isCompleted()){
            state = null;

            if(FOVUtils.isDeadEnd(getView())){
                toDo = state = deh.getAction(getView());
            } else if(FOVUtils.isInVicinity(getView(), TrapTile.class)){
                toDo = state = th.getAction(getView());
            } else {
                toDo = backgroundState;
            }

        } else {
            toDo = state;
        }

        toDo.update(delta);
    }
}
