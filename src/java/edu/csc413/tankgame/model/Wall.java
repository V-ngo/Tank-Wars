package edu.csc413.tankgame.model;

import edu.csc413.tankgame.Constants;

public class Wall extends Entity {
    private int life = 1;

    public Wall(String id, double x, double y, double angle) {
        super(id, x, y, angle);
    }

    public int getLife() {
        return life;
    }

    public void killLife() {
        life--;
    }

    @Override
    public String getId() {
        return super.getId();
    }

    @Override
    public double getAngle() {
        return super.getAngle();
    }

    @Override
    public double getXBound() {
        return getX() + Constants.WALL_WIDTH;
    }

    @Override
    public double getYBound() {
        return getY() + Constants.WALL_HEIGHT;
    }

    @Override
    public void move(GameWorld gameWorld) {}

    @Override
    public void checkBounds() {}

    @Override
    public boolean checkShellBounds() {
        return false;
    }
}