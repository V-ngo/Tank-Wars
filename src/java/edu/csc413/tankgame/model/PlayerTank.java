package edu.csc413.tankgame.model;

import edu.csc413.tankgame.Constants;
import edu.csc413.tankgame.KeyboardReader;

public class PlayerTank extends Tank {
    private int coolDown = 0;
    private static int life = 4;

    public PlayerTank(String id, double x, double y, double angle) {
        super(id, x, y, angle);
    }

    public static int getLife() {
        return life;
    }

    public void killLife() {
        life--;
    }

    public static void setLife(int l) {
        life = l;
    }

    @Override
    public void move(GameWorld gameWorld) {
        KeyboardReader keyboard = KeyboardReader.instance();
        if (keyboard.upPressed()) {
            moveForward(Constants.TANK_MOVEMENT_SPEED);
        }
        if (keyboard.downPressed()) {
            moveBackward(Constants.TANK_MOVEMENT_SPEED);
        }
        if (keyboard.leftPressed()) {
            turnLeft(Constants.TANK_TURN_SPEED);
        }
        if (keyboard.rightPressed()) {
            turnRight(Constants.TANK_TURN_SPEED);
        }
        if (keyboard.spacePressed()) {
            if(coolDown == 0) {
                fireShell(gameWorld);
                coolDown = 50;
            }
        }
        if (coolDown > 0) {
            coolDown--;
        }
    }

    @Override
    public void checkBounds() {
        if (getX() < Constants.TANK_X_LOWER_BOUND) {
            setX(Constants.TANK_X_LOWER_BOUND);
        }
        else if (getX() > Constants.TANK_X_UPPER_BOUND) {
            setX(Constants.TANK_X_UPPER_BOUND);
        }
        else if (getY() < Constants.TANK_Y_LOWER_BOUND) {
            setY(Constants.TANK_Y_LOWER_BOUND);
        }
        else if(getY() > Constants.TANK_Y_UPPER_BOUND) {
            setY(Constants.TANK_Y_UPPER_BOUND);
        }
    }
}
