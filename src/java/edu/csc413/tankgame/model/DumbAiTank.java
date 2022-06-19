package edu.csc413.tankgame.model;

import edu.csc413.tankgame.Constants;
import java.util.Random;

public class DumbAiTank extends Tank {
    private static int life = 4;

    public DumbAiTank(String id, double x, double y, double angle) {
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
        Entity playerTank = gameWorld.getEntity(Constants.PLAYER_TANK_ID);
        double x = playerTank.getX() - getX();
        double y = playerTank.getY() - getY();
        double angleToPlayer = Math.atan2(y, x);
        double angleDifference = getAngle() - angleToPlayer;
        angleDifference -= Math.floor(angleDifference / Math.toRadians(360.0) + 0.5) * Math.toRadians(360);

        if (angleDifference < -Math.toRadians(3.0)) {
            turnRight(Constants.TANK_TURN_SPEED);
        } else if (angleDifference > Math.toRadians(3.0)) {
            turnLeft(Constants.TANK_TURN_SPEED);
        } else if (angleDifference < -Math.toRadians(3.0)) {
            turnRight(Constants.TANK_TURN_SPEED);
        } else if (angleDifference > Math.toRadians(3.0)) {
            turnRight(Constants.TANK_TURN_SPEED);
        }

        Random rand = new Random();
        if (rand.nextFloat() < 0.008) {
            fireShell(gameWorld);
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
        }}
}
