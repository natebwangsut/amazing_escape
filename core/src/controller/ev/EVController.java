package controller.ev;

import controller.CarController;
import tiles.TrapTile;
import world.Car;

import java.util.LinkedList;

public class EVController extends CarController {

    public EVController(Car car) {
        super(car);
    }

    {
        utils = new FOVUtils(this);
        backgroundState = new FollowAction(this, t->t.getName().equals("Wall"));
        deh = new DeadEndHandler();
        th = new DiscreteTrapStrategy(this);
        aq = new LinkedList<>();
        this.aq.add(this.backgroundState);
    }

    Action state = null;
    Action backgroundState;
    LinkedList<Action> aq;
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
            } else if(utils.isInVicinity(t->t instanceof TrapTile)){
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
