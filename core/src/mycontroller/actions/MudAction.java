package mycontroller.actions;

import controller.CarController;
import mycontroller.EVController;
import tiles.MapTile;
import tiles.MudTrap;
import utilities.Coordinate;

import java.util.Map;

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
 * Action to dealt with MudTraps.
 */
public class MudAction extends Action {

    private final int VELOCITY_THRESHOLD = 3;
    protected final Map<Coordinate, MapTile> view;
    Coordinate currentCoordinate;

    private enum Phase {
        DETECTED, IN_MUD, OUT_MUD, COMPLETED
    }

    private Phase phase = Phase.DETECTED;


    /**
     * Constructor
     *
     * @param con
     * @param view
     */
    public MudAction(CarController con, Map<Coordinate, MapTile> view) {
        super(con);
        this.view = view;
        logger.info("New Mud Action");
    }


    /**
     * Set the phase
     *
     * @param p     phase to be set into
     */
    private void setPhase(Phase p) {
        phase = p;
        logger.info("Switching phase into {}", p.name());
    }


    /**
     * Update the car's movement
     *
     * @param delta
     */
    @Override
    public void update(float delta) {

        super.update(delta);
        // current car coordinate
        EVController ev = (EVController) controller;
        currentCoordinate = ev.getCoordinate();

        //System.out.println("In?: " + currentCoordinate);
       // logger.info("Current orientation is: {}", controller.getOrientation());
      //  logger.info("Current angle is: {}", controller.getAngle());
        switch(phase) {

            case DETECTED:
                if (controller.getVelocity() < VELOCITY_THRESHOLD)
                    controller.applyForwardAcceleration();

                if (controller.getView().get(currentCoordinate) instanceof MudTrap)
                    setPhase(Phase.IN_MUD);
                break;

            case IN_MUD:
                if (!(controller.getView().get(currentCoordinate) instanceof MudTrap))
                    setPhase(Phase.OUT_MUD);
                break;

            case OUT_MUD:
                if (controller.getVelocity() < VELOCITY_THRESHOLD)
                    controller.applyForwardAcceleration();
                else
                    setPhase(Phase.COMPLETED);
                break;

            default:
                break;
        }
    }


    /**
     * Tell the handler that the action taken is completed.
     * @return
     */
    @Override
    public boolean isCompleted() {
        return phase == Phase.COMPLETED;
    }
}
