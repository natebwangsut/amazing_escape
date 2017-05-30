package controller.ev;

import controller.CarController;
import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial;

import java.util.Map;

/**
 * Created by Kolatat on 23/5/17.
 */
public class FollowWallAction extends Action {

    public FollowWallAction(CarController controller){
        super(controller);
    }

    boolean followingWall = false;

    @Override
    public boolean isCompleted() {
        return false;
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        Map<Coordinate, MapTile> currentView = controller.getView();
        // If you are not following a wall initially, find a wall to stick to!
        if(!followingWall){
            if(controller.getVelocity() < CAR_SPEED){
                controller.applyForwardAcceleration();
            }
            // Turn towards the north
            if(!controller.getOrientation().equals(WorldSpatial.Direction.NORTH)){
                lastTurnDirection = WorldSpatial.RelativeDirection.LEFT;
                applyLeftTurn(controller.getOrientation(),delta);
            }
            if(utils.checkWall(WorldSpatial.Direction.NORTH)){
                // Turn right until we go back to east!
                if(!controller.getOrientation().equals(WorldSpatial.Direction.EAST)){
                    lastTurnDirection = WorldSpatial.RelativeDirection.RIGHT;
                    applyRightTurn(controller.getOrientation(),delta);
                }
                else{
                    followingWall = true;
                }
            }
        }
        // Once the car is already stuck to a wall, apply the following logic
        else{

            // Readjust the car if it is misaligned.
            readjust(lastTurnDirection,delta);

            if(turningRight){
                applyRightTurn(controller.getOrientation(),delta);
            }
            else if(turningLeft){
                // Apply the left turn if you are not currently near a wall.
                if(!utils.checkFollowingWall()){
                    applyLeftTurn(controller.getOrientation(),delta);
                }
                else{
                    turningLeft = false;
                }
            }
            // Try to determine whether or not the car is next to a wall.
            else if(utils.checkFollowingWall()){
                // Maintain some velocity
                if(controller.getVelocity() < CAR_SPEED){
                    controller.applyForwardAcceleration();
                }
                // If there is wall ahead, turn right!
                if(utils.checkWallAhead()){
                    lastTurnDirection = WorldSpatial.RelativeDirection.RIGHT;
                    turningRight = true;

                }

            }
            // This indicates that I can do a left turn if I am not turning right
            else{
                lastTurnDirection = WorldSpatial.RelativeDirection.LEFT;
                turningLeft = true;
            }
        }
    }
}
