package mycontroller;

import controller.CarController;
import mycontroller.actions.Action;
import mycontroller.actions.FollowAction;
import mycontroller.handler.DeadEndHandler;
import mycontroller.handler.IActionHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utilities.Coordinate;
import world.Car;

import java.util.LinkedList;

public class EVController extends CarController {

    private static Logger logger = LogManager.getLogger();

    public FOVUtils utils;
    private Action state = null;
    private Action backgroundState = null;
    private LinkedList<Action> aq;
    private DeadEndHandler deh;
    private IActionHandler th;
    private PersistentView pv;

    /**
     * Constructor
     *
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
        backgroundState = new FollowAction(this, c -> {
            PersistentView.Property p = pv.get(c);
            assert (p != null);
            return p.logicalWall;
        });
    }

    @Override
    public void update(float delta) {

        pv.update(getView());

        Action toDo = null;
        toDo = backgroundState;

        if (state == null || state.isCompleted()) {
            state = null;

            // fill dead end only if we are not working on a specific task and just following wall
            int kuy;
            if ((kuy = pv.fillDeadEnd(new Coordinate(getPosition()), getViewSquare())) > 0) {
                logger.info("Filled in {} dead ends.", kuy);
            }

            FOVUtils.DeadEnd de;
            if ((de=utils.deadEndAhead(pv))!=null) {
                toDo = state = deh.getAction(getView(),de);
            } else /*if (utils.isInVicinity(t->t instanceof TrapTile)) {
                toDo = state = th.getAction(getView());
            } else */ {
                toDo = backgroundState;
            }

        } else {
            // Use the previous instruction
            toDo = state;
        }

        // Not doing anything if null
        if (toDo != null)
            toDo.update(delta);
    }
}
