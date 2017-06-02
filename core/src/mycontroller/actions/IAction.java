package mycontroller.actions;

/*
 * Nate Bhurinat W. (@natebwangsut | nate.bwangsut@gmail.com)
 * https://github.com/natebwangsut
 */

public interface IAction {

    boolean isCompleted();

    void update(float delta);
}
