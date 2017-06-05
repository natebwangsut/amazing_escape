package mycontroller.actions;

import controller.CarController;
import mycontroller.actions.Action;
import utilities.Coordinate;
import world.WorldSpatial;

import java.util.function.Predicate;

public class FindWallAction extends Action {

    protected final Predicate<Coordinate> tileTest;
    boolean following = false;

    public FindWallAction(CarController controller, Predicate<Coordinate> tileTest) {
        super(controller);
        this.tileTest = tileTest;
    }

    private enum Phase {
        FINDING, COMPLETED
    }

    private Phase phase = Phase.FINDING;
    
    private void setPhase(Phase p) {
        phase = p;
        logger.info("Switching phase into {}", p.name());
    }
    
    @Override
    public boolean isCompleted() {
        return phase == Phase.COMPLETED;
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        switch(phase){
        
        case FINDING:
            if (controller.getVelocity() < CAR_SPEED) {
                controller.applyForwardAcceleration();
            }
            // Turn towards the north
            if (!controller.getOrientation().equals(WorldSpatial.Direction.NORTH)) {
                lastTurnDirection = WorldSpatial.RelativeDirection.LEFT;
                applyLeftTurn(controller.getOrientation(), delta);
            }
            if (utils.check(WorldSpatial.Direction.NORTH, tileTest)) {
                // Turn right until we go back to east!
                if (!controller.getOrientation().equals(WorldSpatial.Direction.EAST)) {
                    lastTurnDirection = WorldSpatial.RelativeDirection.RIGHT;
                    applyRightTurn(controller.getOrientation(), delta);
                } else {
                    following = true;
                    setPhase(Phase.COMPLETED);
                }
            }
            break;
            
        }
    
    }
}
