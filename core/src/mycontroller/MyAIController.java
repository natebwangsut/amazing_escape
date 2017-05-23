package mycontroller;

import controller.CarController;
import world.Car;

public class MyAIController extends CarController{

    public MyAIController(Car car) {
        super(car);
    }

    Action state = null;
    Action backgroundState = new FollowWallAction();
    ActionHandler deh = new DeadEndHandler();
    ActionHandler th = new DiscreteTrapStrategy();

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
