package mycontroller;

import world.WorldSpatial;

/**
 * Created by Kolatat on 23/5/17.
 */
public class FollowWallAction extends Action {
    boolean followingWall = false;

    private static final int CAR_SPEED = 3;

    @Override
    public void update(float delta) {
        super.update(delta);

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
            if(FOVUtils.checkAhead()){
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
                if(!FOVUtils.checkFollowingWall(controller.getOrientation(),currentView)){
                    applyLeftTurn(controller.getOrientation(),delta);
                }
                else{
                    turningLeft = false;
                }
            }
            // Try to determine whether or not the car is next to a wall.
            else if(FOVUtils.checkFollowingWall(controller.getOrientation(),currentView)){
                // Maintain some velocity
                if(controller.getVelocity() < CAR_SPEED){
                    controller.applyForwardAcceleration();
                }
                // If there is wall ahead, turn right!
                if(FOVUtils.checkAhead(controller.getOrientation(),currentView)){
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
