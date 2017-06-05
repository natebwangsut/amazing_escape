package mycontroller.actions;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import controller.CarController;
import mycontroller.FOVUtils;
import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial.Direction;

public class ReverseOutAction extends DeadEndAction{

	boolean completed;
	boolean keepReversing;
	boolean forwardDirection;
	boolean leftBackSeen;
	boolean rightBackSeen;
	final static float EPSILON = 0.00001f;
	private static final float REVERSE_SPEED = 1.5f;
	
	Direction carDirection;
	Direction intoDirection;
	Coordinate currentCoordinate;
	Coordinate toReverseCoordinate;
	
	boolean reverseMode;

	private enum Phase {
		MOVING,
        REVERSING,
        STOP_REVERSE,
        TURNING,
        COMPLETED
    }
	
	private Phase phase = Phase.MOVING;
	
	private void setPhase(Phase p) {
        phase = p;
        logger.info("Switching phase into {}", p.name());
    }
	
	public ReverseOutAction(CarController con, Map<Coordinate, MapTile> view, FOVUtils.DeadEnd de) {
		super(con, view ,de);
		// TODO Auto-generated constructor stub
		this.completed = false;
		this.keepReversing = true;
		this.forwardDirection = true;
		this.leftBackSeen = false;
		this.rightBackSeen = false;
		this.reverseMode = false;
		

		// get car orientation (facing which direction)
		carDirection = controller.getOrientation();
		
		if(carDirection == Direction.NORTH){
			intoDirection = Direction.EAST;
		} else if (carDirection == Direction.EAST){
			intoDirection = Direction.SOUTH;
		} else if(carDirection == Direction.SOUTH){
			intoDirection = Direction.WEST;
		} else {
			intoDirection = Direction.NORTH;
		}
	}

	@Override
	public boolean isCompleted() {
		// TODO Auto-generated method stub
		return completed;
	}

	@Override
	public void update(float delta) {
		
		super.update(delta);
		
		
		switch(phase){
			//
		case MOVING:
			if(controller.getVelocity() > 0)
				controller.applyBrake();
			else
			    setPhase(Phase.REVERSING);
			break;
		case REVERSING:
			if(controller.getVelocity() < REVERSE_SPEED){
				controller.applyReverseAcceleration();
			}
			
			
			// current car coordinate
			String coordinates = controller.getPosition();
			Scanner scanner = new Scanner(coordinates);
			scanner.useDelimiter(",");
			int x = scanner.nextInt();
			int y = scanner.nextInt();
			currentCoordinate = new Coordinate(x,y);
			
			
			// trying to figure out the shape of maze
			if(!leftBackSeen && !rightBackSeen){
				// get the 7x7 view around car
				HashMap<Coordinate,MapTile> currentView = controller.getView();
				MapTile leftBackTile, rightBackTile;
				
				// check for leftBackSeen and rightBackSeen
				// (based on orientations)
				if(carDirection == Direction.NORTH){
					leftBackTile = currentView.get(new Coordinate(x-1, y-1));
					rightBackTile = currentView.get(new Coordinate(x+1, y-1));
					
					toReverseCoordinate = new Coordinate(x, y-1);
					
				} else if (carDirection == Direction.EAST){
					leftBackTile = currentView.get(new Coordinate(x-1, y+1));
					rightBackTile = currentView.get(new Coordinate(x-1, y-1));
					

					toReverseCoordinate = new Coordinate(x-1,y);
					
				} else if(carDirection == Direction.SOUTH){
					leftBackTile = currentView.get(new Coordinate(x+1, y+1));
					rightBackTile = currentView.get(new Coordinate(x-1, y+1));

					toReverseCoordinate = new Coordinate(x, y+1);
				} else {
					leftBackTile = currentView.get(new Coordinate(x+1, y-1));
					rightBackTile = currentView.get(new Coordinate(x+1, y+1));
				
					toReverseCoordinate = new Coordinate(x+1, y);
				}
				
				if(leftBackTile.getName().equals("Road")){
					leftBackSeen = true;
				}
				if(rightBackTile.getName().equals("Road")){
					rightBackSeen = true;
				}
			} else{
				
				
				
			}
			break;
		case STOP_REVERSE:
			if(controller.getVelocity() > 0){
				controller.applyBrake();
			} else {
				setPhase(Phase.TURNING);
			}
			break;
		case TURNING:
			if(controller.getOrientation() == intoDirection){
				
			    setPhase(Phase.COMPLETED);
				
			}
			controller.applyForwardAcceleration();
			applyRightTurn(carDirection, delta);
			
			break;

		
			
		}
	}
}




