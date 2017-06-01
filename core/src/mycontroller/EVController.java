package mycontroller;

import controller.CarController;
import tiles.TrapTile;
import utilities.Coordinate;
import world.Car;

import java.util.LinkedList;

public class EVController extends CarController {

    private Action state = null;
    private Action backgroundState = null;
    private LinkedList<Action> aq;
    private IActionHandler deh;
    private IActionHandler th;
    private PersistentView pv;

    FOVUtils utils;

    /**
     * Constructor
     * @param car
     */
    public EVController(Car car) {

        super(car);  // uses CarController init()

        utils = new FOVUtils(this);

        deh = new DeadEndHandler(this);
        th = new DiscreteTrapStrategy(this);

        aq = new LinkedList<>();
        this.aq.add(this.backgroundState);

        pv = new PersistentView(this);
        backgroundState = new FollowAction(this, c->{
            PersistentView.Property p = pv.get(c);
            assert(p!=null);
            return p.logicalWall;
        });
    }

    @Override
    public void update(float delta) {
        pv.update(getView());
        int kuy;
        if((kuy=pv.fillDeadEnd(new Coordinate(getPosition()), getViewSquare()))>0){
            System.out.printf("Filled in %d dead ends.%n", kuy);
        }

        Action toDo = null;
        toDo = backgroundState;

        if (state == null || state.isCompleted()) {
            state = null;

            /* if (utils.isDeadEnd(FOVUtils.IS_WALL)) {
                toDo = state = deh.getAction(getView());
            } else /*if (utils.isInVicinity(t->t instanceof TrapTile)) {
                toDo = state = th.getAction(getView());
            } else */{
                toDo = backgroundState;
            }

        } else {
            toDo = state;
        }

        toDo.update(delta);
    }
}