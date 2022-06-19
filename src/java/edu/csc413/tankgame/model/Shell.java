package edu.csc413.tankgame.model;

import edu.csc413.tankgame.Constants;

public class Shell extends Entity {
    private static int uniqueId = 0;

    public Shell(String id, double x, double y, double angle) {
        super (id+"-shell-"+uniqueId, x, y, angle);
        uniqueId++;
    }

    @Override
    public double getXBound() {
        return getX() + Constants.SHELL_WIDTH;
    }

    @Override
    public double getYBound() {
        return getY() + Constants.SHELL_HEIGHT;
    }

    @Override
    public void move(GameWorld gameWorld) {
        moveForward(Constants.SHELL_MOVEMENT_SPEED);
    }

    @Override
    public void checkBounds() {}

    @Override
    public boolean checkShellBounds() {
        if (getX() < Constants.SHELL_X_LOWER_BOUND) {
            return true;
        }
        else if (getX() > Constants.SHELL_X_UPPER_BOUND) {
            return true;
        }
        else if (getY() < Constants.SHELL_Y_LOWER_BOUND) {
            return true;
        }
        else if(getY() > Constants.SHELL_Y_UPPER_BOUND) {
            return true;
        }
        return false;
    }
}
