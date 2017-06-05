package mycontroller.actions;

import controller.CarController;
import mycontroller.FOVUtils;
import tiles.GrassTrap;
import tiles.MapTile;
import tiles.MudTrap;
import utilities.Coordinate;

import java.util.Map;
import java.util.Scanner;

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
 * Lava-Trap Action:
 * Series of movements to execute when the car is near or in the Lava Trap
 */

public class LavaAction extends Action {

    Coordinate currentCoordinate;
    
    private enum Phase {
        DETECTED, IN_MUD, OUT_MUD, COMPLETED
    }

    private Phase phase = Phase.DETECTED;
    

    public LavaAction(CarController con) {
        super(con);
        

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

        System.out.println("Current speed is: " + controller.getVelocity());
        switch(phase){
            case DETECTED:

                // if Mud detected a few while still a few tiles away, increase acceleration as much as possible
                if(controller.getVelocity() < 5f){
                    controller.applyForwardAcceleration();
                }
                
                if(controller.getView().get(currentCoordinate) instanceof MudTrap){
                    setPhase(Phase.IN_MUD);
                }
                break;
                
            case IN_MUD:
                if(!(controller.getView().get(currentCoordinate) instanceof MudTrap)){
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
