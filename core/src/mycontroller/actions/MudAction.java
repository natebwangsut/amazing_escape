package mycontroller.actions;

import controller.CarController;
import mycontroller.FOVUtils;
import tiles.GrassTrap;
import tiles.MapTile;
import tiles.MudTrap;
import utilities.Coordinate;

import java.util.Map;
import java.util.Scanner;

public class MudAction extends Action {

    protected final Map<Coordinate, MapTile> view;
    Coordinate currentCoordinate;
    
    private enum Phase {
        DETECTED, IN_MUD, OUT_MUD, COMPLETED
    }

    private Phase phase = Phase.DETECTED;
    

    public MudAction(CarController con, Map<Coordinate, MapTile> view) {
        super(con);
        this.view = view;
        

        logger.info("New Mud Action");
    }
    
    private void setPhase(Phase p) {
        phase = p;
        logger.info("Switching phase into {}", p.name());
    }
    
    @Override
    public void update(float delta) {
     // current car coordinate
        String coordinates = controller.getPosition();
        Scanner scanner = new Scanner(coordinates);
        scanner.useDelimiter(",");
        int x = scanner.nextInt();
        int y = scanner.nextInt();
        currentCoordinate = new Coordinate(x,y);
    //    System.out.println("In?: " + currentCoordinate);
        switch(phase){
        
            case DETECTED:

                if(controller.getVelocity() < 3){
                    controller.applyForwardAcceleration();
                }
                
             
                
                if(controller.getView().get(currentCoordinate) instanceof MudTrap){
                    setPhase(Phase.IN_MUD);
                }
                break;
                
            case IN_MUD:
                if(!(controller.getView().get(currentCoordinate) instanceof MudTrap)){
                    setPhase(Phase.OUT_MUD);
                }
                break;
                
            case OUT_MUD:
                
                if(controller.getVelocity() < 3){
                    controller.applyForwardAcceleration();
                } else {
                    setPhase(Phase.COMPLETED);
                }
                break;
                
            default:
                break;
            
        }
    }

    @Override
    public boolean isCompleted() {

        return phase == Phase.COMPLETED;
    }
}
