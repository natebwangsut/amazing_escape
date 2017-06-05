package mycontroller;

import controller.CarController;
import mycontroller.actions.Action;
import mycontroller.actions.FollowAction;
import mycontroller.actions.ReverseOutAction;
import mycontroller.handler.DeadEndHandler;
import mycontroller.handler.DiscreteTrapStrategy;
import mycontroller.handler.IHandler;
import tiles.GrassTrap;
import tiles.LavaTrap;
import tiles.MudTrap;
import tiles.TrapTile;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utilities.Coordinate;
import world.Car;

import java.util.LinkedList;
import java.util.Scanner;

/**
 * [SWEN30006] Software Modelling and Design
 * Semester 1, 2017
 * Project Part C - amazing-escape
 *
 * Group 107:
 * Nate Wangsutthitham          [755399]
 * Kolatat Thangkasemvathana    [780631]
 * Khai Mei Chin                [755332]
 *
 * Main controller of the car
 * EVController - Electric Vehicle Controller.
 * 2k17, cars can drive itself to avoid traps and deadends.
 *
 * The controller use Action based movement where Action is a representation of a set of movements.
 */
public class EVController extends CarController {

    private static Logger logger = LogManager.getLogger();

    public FOVUtils utils;
    private Action state = null;
    private Action backgroundState = null;
    private LinkedList<Action> aq;
    private DeadEndHandler deh;
    private DiscreteTrapStrategy th;
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

    /**
     * Update the position based on the
     * @param delta
     */
    @Override
    public void update(float delta) {
         // System.out.println("Current speed is: " + getVelocity());

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
                /*Coordinate c = getCoordinate();
                /*if(pv.get(c).tile instanceof LavaTrap && pv.get(c).logicalWall){
                    toDo = state = new ReverseOutAction(this, getView(), null);
                }*/
            } else/* if (getView().get(getCoordinate()) instanceof MudTrap) {
                toDo = state = th.getAction(getView(), "MudTrap");

            } else */if (getView().get(getCoordinate()) instanceof GrassTrap){
                toDo = state = th.getAction(getView(), "GrassTrap");
            } else  {
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


    /**
     * Get the coordinate position of the current car
     *
     * @return      car's coordinate
     */
    public Coordinate getCoordinate(){
        String coordinates = getPosition();
        Scanner scanner = new Scanner(coordinates);
        scanner.useDelimiter(",");
        int x = scanner.nextInt();
        int y = scanner.nextInt();
        return new Coordinate(x,y);
    }
}
