package mycontroller.actions;

import controller.CarController;
import mycontroller.FOVUtils;
import tiles.GrassTrap;
import tiles.LavaTrap;
import tiles.MapTile;
import tiles.MudTrap;
import utilities.Coordinate;
import world.WorldSpatial;
import world.WorldSpatial.Direction;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Predicate;

/**
 * [SWEN30006] Software Modelling and Design
 * Semester 1, 2017
 * Project Part C - amazing-escape
 *
 * Group 107:
 * Nate Wangsutthitham [755399]
 * Kolatat Thangkasemvathana [780631]
 * Khai Mei Chin [755332]
 *
 * Grass-Trap Action:
 * Series of movements to execute when the car is in a Grass Trap
 */

public class GrassAction extends Action {

    protected final Map<Coordinate, MapTile> view;

    Predicate<Coordinate> tileTest;
    boolean following = true;

    Coordinate currentCoordinate;
    Coordinate lastWallSeen;

    boolean findWallTurn;
    Direction facingLeftDirection;
    Direction lastTurn = null;
    Direction turnDirectionAfterFoundWall = null;

    private static boolean isAngleSimilar(float t1, float t2, float threshold) {
        float diff = normaliseAngle(t1 - t2);
        if (diff > 180) diff = 360 - diff;
        // now we have a range of 0..180 where 0 means t1 close to t2
        return diff <= threshold;
    }

    private static float normaliseAngle(float theta) {
        while (theta < 0) theta += 360;
        return theta % 360;
    }


    private static final int SNAP_THRESHOLD = 10;
    Direction beforeSnap = null;
    Direction snapTo = null;

    private enum Phase {
        DETECTED, IN_GRASS, REVERSE_GRASS, REVERSE_OUT, OUT_GRASS, FINDING_WALL, COMPLETED
    }

    private Phase phase = Phase.DETECTED;


    public GrassAction(CarController con, Map<Coordinate, MapTile> view) {
        super(con);
        this.view = view;
        this.findWallTurn = false;

        if (con.getOrientation() == Direction.NORTH) {

            this.facingLeftDirection = Direction.WEST;
        } else if (con.getOrientation() == Direction.EAST) {
            this.facingLeftDirection = Direction.NORTH;
        } else if (con.getOrientation() == Direction.SOUTH) {
            this.facingLeftDirection = Direction.EAST;
        } else {
            this.facingLeftDirection = Direction.SOUTH;
        }

        logger.info("New Grass Action");
    }

    private void setPhase(Phase p) {
        phase = p;
        logger.info("Switching phase into {}", p.name());
    }

    @Override
    public void update(float delta) {

        super.update(delta);
        // current car coordinate
        String coordinates = controller.getPosition();
        Scanner scanner = new Scanner(coordinates);
        scanner.useDelimiter(",");
        int x = scanner.nextInt();
        int y = scanner.nextInt();
        currentCoordinate = new Coordinate(x,y);
     //   System.out.println("In?: " + currentCoordinate);

        System.out.println("Current orientation is: " + controller.getOrientation());
        System.out.println("Current angle is: " + controller.getAngle());
        switch(phase) {

            case DETECTED:


                if (controller.getView().get(currentCoordinate) instanceof GrassTrap) {
                    setPhase(Phase.IN_GRASS);
                }
                break;

            case IN_GRASS:
                if (controller.getVelocity() < 3) {
                    controller.applyForwardAcceleration();
                }

                Coordinate ahead;
                if (controller.getOrientation() == Direction.NORTH) {
                    ahead = new Coordinate(currentCoordinate.x, currentCoordinate.y +1);

                } else if (controller.getOrientation() == Direction.EAST) {
                    ahead = new Coordinate(currentCoordinate.x +1, currentCoordinate.y );


                } else if (controller.getOrientation() == Direction.SOUTH) {
                    ahead = new Coordinate(currentCoordinate.x , currentCoordinate.y -1);
                } else {
                    ahead = new Coordinate(currentCoordinate.x -1,  currentCoordinate.y );
                }

                if (controller.getView().get(ahead).getName().equals("Wall")) {
                    setPhase(Phase.REVERSE_GRASS);
                    break;
                }


                if (!(controller.getView().get(currentCoordinate) instanceof GrassTrap)) {
                    setPhase(Phase.OUT_GRASS);
                }
                break;
            case REVERSE_GRASS:
                // bad alignment of car in grass

                beforeSnap = controller.getOrientation();

                if (isAngleSimilar(controller.getAngle(), 0, SNAP_THRESHOLD )) {
                    snapTo = Direction.EAST;
                } else if (isAngleSimilar(controller.getAngle(), 90, SNAP_THRESHOLD)) {
                    snapTo = Direction.NORTH;
                } else if (isAngleSimilar(controller.getAngle(), 180, SNAP_THRESHOLD)) {
                    snapTo = Direction.WEST;
                } else {
                    snapTo = Direction.SOUTH;
                }

                if (controller.getView().get(currentCoordinate) instanceof GrassTrap) {
                    controller.applyReverseAcceleration();
                }

                if (!(controller.getView().get(currentCoordinate) instanceof GrassTrap)) {
                    setPhase(Phase.REVERSE_OUT);
                }

                break;

            case REVERSE_OUT:

                if (controller.getVelocity() < CAR_SPEED) {
                    controller.applyForwardAcceleration();
                }

                if (controller.getOrientation() != snapTo) {

                    if ((beforeSnap == Direction.WEST && snapTo == Direction.SOUTH) || (beforeSnap == Direction.SOUTH && snapTo == Direction.EAST) || (beforeSnap == Direction.EAST && snapTo == Direction.NORTH) || (beforeSnap == Direction.NORTH && snapTo == Direction.WEST))
                        applyLeftTurn(controller.getOrientation(),delta);
                    else
                        applyRightTurn(controller.getOrientation(), delta);
                } else {
                    setPhase(Phase.IN_GRASS);
                }

                break;

            case OUT_GRASS:
                System.out.println("Current coordinate is: "+ currentCoordinate);
                if (!(controller.getView().get(currentCoordinate) instanceof LavaTrap) && !(controller.getView().get(currentCoordinate) instanceof MudTrap)) {
                    setPhase(Phase.FINDING_WALL);
                }
                else {
                    setPhase(Phase.COMPLETED);
                }
                break;

            case FINDING_WALL:

             // if after leaving grass, the car is not following a wall, then try:
             // turn left, go straight until find another wall, then turn right, continue following

                Coordinate left_1, left_2, ahead_1, ahead_2;
                Map<Coordinate, MapTile> carView = controller.getView();
                if (controller.getOrientation() == Direction.NORTH) {
                    left_1 = new Coordinate(currentCoordinate.x -1, currentCoordinate.y);
                    left_2 = new Coordinate(currentCoordinate.x -2, currentCoordinate.y);
                    ahead_1 = new Coordinate(currentCoordinate.x, currentCoordinate.y +1);
                    ahead_2 = new Coordinate(currentCoordinate.x, currentCoordinate.y +2);
                } else if (controller.getOrientation() == Direction.EAST) {
                    left_1 = new Coordinate(currentCoordinate.x, currentCoordinate.y +1);
                    left_2 = new Coordinate(currentCoordinate.x, currentCoordinate.y +2);
                    ahead_1 = new Coordinate(currentCoordinate.x +1, currentCoordinate.y);
                    ahead_2 = new Coordinate(currentCoordinate.x +2, currentCoordinate.y);

                } else if (controller.getOrientation() == Direction.SOUTH) {
                    left_1 = new Coordinate(currentCoordinate.x +1, currentCoordinate.y);
                    left_2 = new Coordinate(currentCoordinate.x +2, currentCoordinate.y);
                    ahead_1 = new Coordinate(currentCoordinate.x, currentCoordinate.y -1);
                    ahead_2 = new Coordinate(currentCoordinate.x, currentCoordinate.y -2);
                } else {
                    left_1 = new Coordinate(currentCoordinate.x, currentCoordinate.y -1);
                    left_2 = new Coordinate(currentCoordinate.x, currentCoordinate.y -2);
                    ahead_1 = new Coordinate(currentCoordinate.x -1, currentCoordinate.y);
                    ahead_2 = new Coordinate(currentCoordinate.x -2, currentCoordinate.y);
                }


                if (carView.get(left_1).getName().equals("Wall") || carView.get(left_2).getName().equals("Wall")) {
                    following = true;
                    setPhase(Phase.COMPLETED);
                    break;
                }

                if (carView.get(ahead_1).getName().equals("Wall") || carView.get(ahead_2).getName().equals("Wall")) {
                    if (controller.getOrientation() == Direction.NORTH) {
                        turnDirectionAfterFoundWall = Direction.EAST;
                    } else if (controller.getOrientation() == Direction.EAST) {
                        turnDirectionAfterFoundWall = Direction.SOUTH;
                    } else if (controller.getOrientation() == Direction.SOUTH) {
                        turnDirectionAfterFoundWall = Direction.WEST;
                    } else {
                        turnDirectionAfterFoundWall = Direction.NORTH;
                    }
                }



                if (controller.getVelocity() < CAR_SPEED) {
                    controller.applyForwardAcceleration();
                }

                if (turnDirectionAfterFoundWall != null) {
                    if (controller.getOrientation() != turnDirectionAfterFoundWall) {
                        applyRightTurn(controller.getOrientation(), delta);
                    }
                }


                if (controller.getOrientation() != facingLeftDirection) {
                    applyLeftTurn(controller.getOrientation(), delta);
                }




            break;


        }
    }

    @Override
    public boolean isCompleted() {

        return phase == Phase.COMPLETED;
    }
}
