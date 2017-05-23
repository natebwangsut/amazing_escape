package mycontroller;

import controller.CarController;
import world.Car;

public class MyAIController extends CarController{

    public MyAIController(Car car) {
        super(car);
    }

    @Override
    public void update(float delta) {
        // TODO Auto-generated method stub

    }

    public class DeadEndHandler {

        public DeadEndHandler() {
            // TODO stub
        }

        public void uTurn() {
            // TODO stub
        }

        public void threePointTurn() {
            // TODO stub
        }

        public void reverseOut() {
            // TODO stub
        }

    }
}
