package mycontroller.actions;

import java.util.HashMap;
import java.util.Map;

import controller.CarController;
import mycontroller.EVController;
import mycontroller.FOVUtils;
import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial.Direction;

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
 * Action to reverse out.
 * Use this when the road is too small to do a uturn or 3point-turn.
 */

public class ReverseOutAction extends DeadEndAction{

    private boolean completed;
    private boolean keepReversing;
    private boolean forwardDirection;
    private boolean leftBackSeen;
    private boolean rightBackSeen;

    final static float EPSILON = 0.00001f;
    private static final float REVERSE_SPEED = 1.5f;

    private Direction carDirection;
    private Direction intoDirection;
    private Coordinate currentCoordinate;
    private Coordinate toReverseCoordinate;

    private boolean reverseMode;

    private enum Phase {
        MOVING,
        REVERSING,
        STOP_REVERSE,
        TURNING,
        COMPLETED
    }

    private Phase phase = Phase.MOVING;

    /**
     * Constructor
     *
     * @param controller
     * @param view
     * @param de
     */
    public ReverseOutAction(CarController controller, Map<Coordinate, MapTile> view, FOVUtils.DeadEnd de) {
        super(controller, view ,de);
        // TODO Auto-generated constructor stub
        this.completed = false;
        this.keepReversing = true;
        this.forwardDirection = true;
        this.leftBackSeen = false;
        this.rightBackSeen = false;
        this.reverseMode = false;


        // get car orientation (facing which direction)
        carDirection = this.controller.getOrientation();

        if (carDirection == Direction.NORTH) {
            intoDirection = Direction.EAST;
        } else if (carDirection == Direction.EAST) {
            intoDirection = Direction.SOUTH;
        } else if (carDirection == Direction.SOUTH) {
            intoDirection = Direction.WEST;
        } else {
            intoDirection = Direction.NORTH;
        }
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

        switch(phase) {
            case MOVING:
                if (controller.getVelocity() > 0)
                    controller.applyBrake();
                else
                    setPhase(Phase.REVERSING);
                break;

            case REVERSING:
                if (controller.getVelocity() < REVERSE_SPEED) {
                    controller.applyReverseAcceleration();
                }

                // trying to figure out the shape of maze
                if (!leftBackSeen && !rightBackSeen) {
                    // get the 7x7 view around car
                    HashMap<Coordinate,MapTile> currentView = controller.getView();
                    MapTile leftBackTile, rightBackTile;

                    // check for leftBackSeen and rightBackSeen
                    // (based on orientations)
                    if (carDirection == Direction.NORTH) {
                        leftBackTile = currentView.get(
                                new Coordinate(currentCoordinate.x-1, currentCoordinate.y-1));
                        rightBackTile = currentView.get(
                                new Coordinate(currentCoordinate.x+1, currentCoordinate.y-1));

                        toReverseCoordinate = new Coordinate(currentCoordinate.x, currentCoordinate.y-1);
                    }
                    else if (carDirection == Direction.EAST) {
                        leftBackTile = currentView.get(
                                new Coordinate(currentCoordinate.x-1, currentCoordinate.y+1));
                        rightBackTile = currentView.get(
                                new Coordinate(currentCoordinate.x-1, currentCoordinate.y-1));

                        toReverseCoordinate = new Coordinate(currentCoordinate.x-1,currentCoordinate.y);
                    }
                    else if (carDirection == Direction.SOUTH) {
                        leftBackTile = currentView.get(
                                new Coordinate(currentCoordinate.x+1, currentCoordinate.y+1));
                        rightBackTile = currentView.get(
                                new Coordinate(currentCoordinate.x-1, currentCoordinate.y+1));

                        toReverseCoordinate = new Coordinate(currentCoordinate.x, currentCoordinate.y+1);
                    }
                    else { //carDirection == Direction.WEST
                        leftBackTile = currentView.get(
                                new Coordinate(currentCoordinate.x+1, currentCoordinate.y-1));
                        rightBackTile = currentView.get(
                                new Coordinate(currentCoordinate.x+1, currentCoordinate.y+1));

                        toReverseCoordinate = new Coordinate(currentCoordinate.x+1, currentCoordinate.y);
                    }

                    if (leftBackTile.getName().equals("Road"))
                        leftBackSeen = true;
                    if (rightBackTile.getName().equals("Road"))
                        rightBackSeen = true;
                }
                else {
                    // TODO something
                }
                break;

            case STOP_REVERSE:
                if (controller.getVelocity() > 0)
                    controller.applyBrake();
                else
                    setPhase(Phase.TURNING);
                break;

            case TURNING:
                if (controller.getOrientation() == intoDirection)
                    setPhase(Phase.COMPLETED);

                controller.applyForwardAcceleration();
                applyRightTurn(carDirection, delta);
                break;
            }
    }


    /**
     * Tell the handler that the action taken is completed.
     * @return
     */
    @Override
    public boolean isCompleted() {
        return false;
    }
}
