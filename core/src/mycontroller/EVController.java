package mycontroller;

import controller.CarController;
import tiles.TrapTile;
import world.Car;

import java.util.LinkedList;

public class EVController extends CarController {

    private Action state = null;
    private Action backgroundState = null;
    private LinkedList<Action> aq;
    private IActionHandler deh;
    private IActionHandler th;

    FOVUtils utils;

    /**
     * Constructor
     * @param car
     */
    public EVController(Car car) {

        super(car);  // uses CarController init()

        utils = new FOVUtils(this);
        backgroundState = new FollowAction(this, t->t.getName().equals("Wall"));

        deh = new DeadEndHandler(this);
        th = new DiscreteTrapStrategy(this);

        aq = new LinkedList<>();
        this.aq.add(this.backgroundState);
    }

    @Override
    public void update(float delta) {

        Action toDo = null;

        if (state == null || state.isCompleted()) {
            state = null;

            if (FOVUtils.isDeadEnd(getView())) {
                toDo = state = deh.getAction(getView());
            } else if (utils.isInVicinity(t->t instanceof TrapTile)) {
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
