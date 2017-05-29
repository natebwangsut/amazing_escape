package controller.ev;

import controller.CarController;
import tiles.TrapTile;
import world.Car;

public class EVController extends CarController {

    public EVController(Car car) {
        super(car);
    }

    Action state = null;
    Action backgroundState = new FollowWallAction();
    IActionHandler deh = new DeadEndHandler();
    IActionHandler th = new DiscreteTrapStrategy();

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
