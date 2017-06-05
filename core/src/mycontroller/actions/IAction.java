package mycontroller.actions;

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
 * Standard interface for Action.
 * This enables further implementation upon different systems.
 */

public interface IAction {

    /**
     * Tell the handler that whether the action taken is completed or not.
     *
     * @return          has the action been completed.
     */
    boolean isCompleted();


    /**
     * Update the car's movement based on the time passed (delta) from last frame.
     *
     * @param delta     how many ms has passed since last frame.
     */
    void update(float delta);
}
