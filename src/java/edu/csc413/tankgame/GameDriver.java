package edu.csc413.tankgame;

import edu.csc413.tankgame.model.*;
import edu.csc413.tankgame.view.*;

import java.awt.desktop.SystemEventListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class GameDriver {
    private final MainView mainView;
    private final RunGameView runGameView;
    private final GameWorld gameWorld;
    private KeyboardReader keyboardReader;
    private Sounds cutePewPew;

    public GameDriver() {
        mainView = new MainView(this::startMenuActionPerformed);
        runGameView = mainView.getRunGameView();
        gameWorld = new GameWorld();
    }

    public void start() {
        mainView.setScreen(MainView.Screen.START_GAME_SCREEN);
    }

    private void startMenuActionPerformed(ActionEvent actionEvent) {
        switch (actionEvent.getActionCommand()) {
            case StartMenuView.START_BUTTON_ACTION_COMMAND -> runGame();
            case StartMenuView.EXIT_BUTTON_ACTION_COMMAND -> mainView.closeGame();
            default -> throw new RuntimeException("Unexpected action command: " + actionEvent.getActionCommand());
        }
    }

    private void runGame() {
        mainView.setScreen(MainView.Screen.RUN_GAME_SCREEN);
        Runnable gameRunner = () -> {
            setUpGame();
            while (updateGame()) {
                runGameView.repaint();
                try {
                    Thread.sleep(10L);
                } catch (InterruptedException exception) {
                    throw new RuntimeException(exception);
                }
                keyboardReader = KeyboardReader.instance();
                if (keyboardReader.escapePressed() || PewPewDead()) {
                    break;
                }
            }
            mainView.setScreen(MainView.Screen.END_MENU_SCREEN);
            resetGame();
        };
        new Thread(gameRunner).start();
    }

    /**
     * setUpGame is called once at the beginning when the game is started. Entities that are present from the start
     * should be initialized here, with their corresponding sprites added to the RunGameView.
     */
    private void setUpGame() {
        // TODO: Implement.
        Entity playerTank = new PlayerTank(
                Constants.PLAYER_TANK_ID,
                Constants.PLAYER_TANK_INITIAL_X,
                Constants.PLAYER_TANK_INITIAL_Y,
                Constants.PLAYER_TANK_INITIAL_ANGLE);
        Entity aiTank1 = new DumbAiTank(
                Constants.AI_TANK_1_ID,
                Constants.AI_TANK_1_INITIAL_X,
                Constants.AI_TANK_1_INITIAL_Y,
                Constants.AI_TANK_1_INITIAL_ANGLE);
        Entity aiTank2 = new SmartAiTank(
                Constants.AI_TANK_2_ID,
                Constants.AI_TANK_2_INITIAL_X,
                Constants.AI_TANK_2_INITIAL_Y,
                Constants.AI_TANK_2_INITIAL_ANGLE);

        gameWorld.addEntity(playerTank);
        gameWorld.addEntity(aiTank1);
        gameWorld.addEntity(aiTank2);

        runGameView.addSprite(
                playerTank.getId(),
                RunGameView.PLAYER_TANK_IMAGE_FILE,
                playerTank.getX(),
                playerTank.getY(),
                playerTank.getAngle());
        runGameView.addSprite(
                aiTank1.getId(),
                RunGameView.AI_TANK_IMAGE_FILE,
                aiTank1.getX(),
                aiTank1.getY(),
                aiTank1.getAngle());
        runGameView.addSprite(
                aiTank2.getId(),
                RunGameView.AI_TANK_IMAGE_FILE,
                aiTank2.getX(),
                aiTank2.getY(),
                aiTank2.getAngle());

        int index = 0;

        for(WallInformation wall: WallInformation.readWalls()) {
            Wall newWall = new Wall(
                    "Wall" + index,
                    wall.getX(),
                    wall.getY(),
                    0);
            gameWorld.addEntity(newWall);
            runGameView.addSprite(
                    "Wall" + index,
                    wall.getImageFile(),
                    wall.getX(),
                    wall.getY(),
                    0);
            index++; 
        }
    }

    /**
     * updateGame is repeatedly called in the gameplay loop. The code in this method should run a single frame of the
     * game. As long as it returns true, the game will continue running. If the game should stop for whatever reason
     * (e.g. the player tank being destroyed, escape being pressed), it should return false.
     */
    private boolean updateGame() {
        // TODO: Implement.
        List<Entity> newShell = gameWorld.getShells();
        List<Entity> deleteShells = new ArrayList<>();

        // Move entities
        for (Entity entity: new ArrayList<>(gameWorld.getEntities())) {
            entity.move(gameWorld);
        }

        // Add new shells
        for (Entity newShellEntity: newShell) {
            runGameView.addSprite(
                    newShellEntity.getId(),
                    RunGameView.SHELL_IMAGE_FILE,
                    newShellEntity.getX(),
                    newShellEntity.getY(),
                    newShellEntity.getAngle());
        }

        for (Entity entity: newShell) {
            gameWorld.addEntity(entity);
        }

        newShell.removeAll(newShell);

        // Check bound
        for (Entity entity: gameWorld.getEntities()) {
            entity.checkBounds();
        }

        // Check shell bounds and delete shells
        for (Entity entity: gameWorld.getEntities()) {
            if(entity.checkShellBounds()) {
                deleteShells.add(entity);
            }
        }

        for (Entity entity: deleteShells) {
            gameWorld.remove(entity);
            gameWorld.removeEntity(entity.getId());
            runGameView.removeSprite(entity.getId());
        }

        // Collision thingy
        for (int i = 0; i < gameWorld.getEntities().size(); i++) {
            for (int j = i + 1; j < gameWorld.getEntities().size(); j++) {
                if (entitiesOverLap(gameWorld.getEntities().get(i), gameWorld.getEntities().get(j))) {
                    collisionThingy(gameWorld.getEntities().get(i), gameWorld.getEntities().get(j));
                }
            }
        }

        // Update entity
        for (Entity entity: gameWorld.getEntities()) {
            runGameView.setSpriteLocationAndAngle(
                    entity.getId(),
                    entity.getX(),
                    entity.getY(),
                    entity.getAngle());
        }

        return true;
    }

    /**
     * resetGame is called at the end of the game once the gameplay loop exits. This should clear any existing data from
     * the game so that if the game is restarted, there aren't any things leftover from the previous run.
     */
    private void resetGame() {
        // TODO: Implement.
        runGameView.reset();
        gameWorld.reset();

        // Reset life
        PlayerTank.setLife(4);
        SmartAiTank.setLife(4);
        DumbAiTank.setLife(4);
    }

    /** Method Overlapping **/
    private boolean entitiesOverLap(Entity EnwiwiWan, Entity EnwiwiTu) {
        return EnwiwiWan.getX() < EnwiwiTu.getXBound() &&
               EnwiwiWan.getXBound() > EnwiwiTu.getX() &&
               EnwiwiWan.getY() < EnwiwiTu.getYBound() &&
               EnwiwiWan.getYBound() > EnwiwiTu.getY();
    }

    /** Collision Thingy **/
    public void collisionThingy(Entity EnwiwiWan, Entity EnwiwiTu) {
        // A tank colliding with a tank
        if (EnwiwiWan instanceof Tank && EnwiwiTu instanceof Tank) {
            TankCollide(EnwiwiWan, EnwiwiTu);
        }

        // A tank colliding with a wall
        if (EnwiwiWan instanceof Tank && EnwiwiTu instanceof Wall) {
            TankWallCollide(EnwiwiWan, EnwiwiTu);
        }
        else if (EnwiwiWan instanceof Wall && EnwiwiTu instanceof Tank) {
            TankWallCollide(EnwiwiTu, EnwiwiWan);
        }

        // A shell colliding with a wall
        if (EnwiwiWan instanceof Shell && EnwiwiTu instanceof Wall) {
            ShellWallCollide(EnwiwiWan, EnwiwiTu);
        }
        else if (EnwiwiWan instanceof Wall && EnwiwiTu instanceof Shell) {
            ShellWallCollide(EnwiwiTu, EnwiwiWan);
        }

        // A shell colliding with a player tank
        if (EnwiwiWan instanceof PlayerTank && EnwiwiTu instanceof Shell) {
            ShellPlayerTankCollide(EnwiwiWan, EnwiwiTu);
        }
        else if (EnwiwiWan instanceof Shell && EnwiwiTu instanceof PlayerTank) {
            ShellPlayerTankCollide(EnwiwiTu, EnwiwiWan);
        }

        // A shell colliding with a smart AI tank
        if (EnwiwiWan instanceof SmartAiTank && EnwiwiTu instanceof Shell) {
            ShellSmartTankCollide(EnwiwiWan, EnwiwiTu);
        }
        else if (EnwiwiWan instanceof Shell && EnwiwiTu instanceof SmartAiTank) {
            ShellSmartTankCollide(EnwiwiTu, EnwiwiWan);
        }

        // A shell colliding with a dumb AI tank
        if (EnwiwiWan instanceof DumbAiTank && EnwiwiTu instanceof Shell) {
            ShellDumbTankCollide(EnwiwiWan, EnwiwiTu);
        }
        else if (EnwiwiWan instanceof Shell && EnwiwiTu instanceof DumbAiTank) {
            ShellDumbTankCollide(EnwiwiTu, EnwiwiWan);
        }


        // A shell colliding with a shell
        if (EnwiwiWan instanceof Shell && EnwiwiTu instanceof Shell) {
            ShellCollide(EnwiwiWan, EnwiwiTu);
        }
    }

    /** A Tank Colliding with a Tank **/
    public void TankCollide(Entity EnwiwiWan, Entity EnwiwiTu) {
        double nambaWan = EnwiwiWan.getXBound() - EnwiwiTu.getX();
        double nambaTu = EnwiwiTu.getXBound() - EnwiwiWan.getX();
        double nambaTee = EnwiwiWan.getYBound() - EnwiwiTu.getY();
        double nambaPho = EnwiwiTu.getYBound() - EnwiwiWan.getY();
        double [] findMin = {nambaWan, nambaTu, nambaTee, nambaPho};
        double smollestNamba = nambaWan;

        // Find smallest number
        for (int i = 0; i < 4; i++) {
            if (smollestNamba > findMin[i]) {
                smollestNamba = findMin[i];
            }
        }

        // If number 1 is smallest
        if (nambaWan == smollestNamba) {
            EnwiwiWan.setX(EnwiwiWan.getX() - smollestNamba / 2);
            EnwiwiTu.setX(EnwiwiTu.getX() + smollestNamba / 2);
        }

        // If number 2 is smallest
        if (nambaTu == smollestNamba) {
            EnwiwiWan.setX(EnwiwiWan.getX() + smollestNamba / 2);
            EnwiwiTu.setX(EnwiwiTu.getX() - smollestNamba / 2);
        }

        // If number 3 is smallest
        if (nambaTee == smollestNamba) {
            EnwiwiWan.setY(EnwiwiWan.getY() - smollestNamba / 2);
            EnwiwiTu.setY(EnwiwiTu.getY() + smollestNamba / 2);
        }

        // If number 4 is smallest
        if (nambaPho == smollestNamba) {
            EnwiwiWan.setY(EnwiwiWan.getY() + smollestNamba / 2);
            EnwiwiTu.setY(EnwiwiTu.getY() - smollestNamba / 2);
        }
    }

    /** A Tank Colliding with a Wall **/
    public void TankWallCollide(Entity EnwiwiWan, Entity EnwiwiTu) {
        double nambaWan = EnwiwiWan.getXBound() - EnwiwiTu.getX();
        double nambaTu = EnwiwiTu.getXBound() - EnwiwiWan.getX();
        double nambaTee = EnwiwiWan.getYBound() - EnwiwiTu.getY();
        double nambaPho = EnwiwiTu.getYBound() - EnwiwiWan.getY();
        double [] findMin = {nambaWan, nambaTu, nambaTee, nambaPho};
        double smollestNamba = nambaWan;

        // Find smallest number
        for (int i = 0; i < 4; i++) {
            if (smollestNamba > findMin[i]) {
                smollestNamba = findMin[i];
            }
        }

        // If number 1 is smallest
        if (nambaWan == smollestNamba) {
            EnwiwiWan.setX(EnwiwiWan.getX() - smollestNamba);
        }

        // If number 2 is smallest
        if (nambaTu == smollestNamba) {
            EnwiwiWan.setX(EnwiwiWan.getX() + smollestNamba);
        }

        // If number 3 is smallest
        if (nambaTee == smollestNamba) {
            EnwiwiWan.setY(EnwiwiWan.getY() - smollestNamba);
        }

        // If number 4 is smallest
        if (nambaPho == smollestNamba) {
            EnwiwiWan.setY(EnwiwiWan.getY() + smollestNamba);
        }
    }

    /** A shell colliding with a Wall **/
    public void ShellWallCollide(Entity EnwiwiWan, Entity EnwiwiTu) {
        List<Entity> entitiesToRemove = new ArrayList<>();
        Wall wall = (Wall) EnwiwiTu;

        entitiesToRemove.add(EnwiwiWan);

        if(wall.getLife() == 1) {
            entitiesToRemove.add(EnwiwiTu);
            runGameView.addAnimation(
                    RunGameView.BIG_EXPLOSION_ANIMATION,
                    RunGameView.BIG_EXPLOSION_FRAME_DELAY,
                    EnwiwiWan.getX(),
                    EnwiwiWan.getY());
        }
        else {
            wall.killLife();
        }

        for (Entity entity: entitiesToRemove) {
            gameWorld.removeEntity(entity.getId());
            runGameView.removeSprite(entity.getId());
        }

        runGameView.addAnimation(
                RunGameView.SHELL_EXPLOSION_ANIMATION,
                RunGameView.SHELL_EXPLOSION_FRAME_DELAY,
                EnwiwiWan.getX(),
                EnwiwiWan.getY());

    }

    /** A shell colliding with a Player Tank **/
    public void ShellPlayerTankCollide(Entity EnwiwiWan, Entity EnwiwiTu) {
        List<Entity> entitiesToRemove = new ArrayList<>();
        PlayerTank playerTank = (PlayerTank) EnwiwiWan;

        entitiesToRemove.add(EnwiwiTu);

        if (playerTank.getLife() == 0) {
            entitiesToRemove.add(EnwiwiWan);
            runGameView.addAnimation(
                    RunGameView.BIG_EXPLOSION_ANIMATION,
                    RunGameView.BIG_EXPLOSION_FRAME_DELAY,
                    EnwiwiTu.getX(),
                    EnwiwiTu.getY());
            cutePewPew.PewPewSound();
        }
        else if (playerTank.getLife() > 0) {
            playerTank.killLife();
        }

        for (Entity entity: entitiesToRemove) {
            gameWorld.removeEntity(entity.getId());
            runGameView.removeSprite(entity.getId());
        }
    }

    /** A shell colliding with a Smart AI Tank **/
    public void ShellSmartTankCollide(Entity EnwiwiWan, Entity EnwiwiTu) {
        List<Entity> entitiesToRemove = new ArrayList<>();
        SmartAiTank smartTank = (SmartAiTank) EnwiwiWan;

        entitiesToRemove.add(EnwiwiTu);

        if (smartTank.getLife() == 0) {
            entitiesToRemove.add(EnwiwiWan);
            runGameView.addAnimation(
                    RunGameView.BIG_EXPLOSION_ANIMATION,
                    RunGameView.BIG_EXPLOSION_FRAME_DELAY,
                    EnwiwiTu.getX(),
                    EnwiwiTu.getY());
            cutePewPew.PewPewSound();
        }
        else if (smartTank.getLife() > 0) {
            smartTank.killLife();
        }

        for (Entity entity: entitiesToRemove) {
            gameWorld.removeEntity(entity.getId());
            runGameView.removeSprite(entity.getId());
        }
    }

    /** A shell colliding with a Dumb AI Tank **/
    public void ShellDumbTankCollide(Entity EnwiwiWan, Entity EnwiwiTu) {
        List<Entity> entitiesToRemove = new ArrayList<>();
        DumbAiTank dumbTank = (DumbAiTank) EnwiwiWan;

        entitiesToRemove.add(EnwiwiTu);

        if (dumbTank.getLife() == 0) {
            entitiesToRemove.add(EnwiwiWan);
            runGameView.addAnimation(
                    RunGameView.BIG_EXPLOSION_ANIMATION,
                    RunGameView.BIG_EXPLOSION_FRAME_DELAY,
                    EnwiwiTu.getX(),
                    EnwiwiTu.getY());
            cutePewPew.PewPewSound();
        }
        else if (dumbTank.getLife() > 0) {
            dumbTank.killLife();
        }

        for (Entity entity: entitiesToRemove) {
            gameWorld.removeEntity(entity.getId());
            runGameView.removeSprite(entity.getId());
        }
    }

    /** A Shell Colliding with a Shell **/
    public void ShellCollide(Entity EnwiwiWan, Entity EnwiwiTu) {
        List<Entity> entitiesToRemove = new ArrayList<>();

        entitiesToRemove.add(EnwiwiWan);
        entitiesToRemove.add(EnwiwiTu);

        for (Entity entity: entitiesToRemove) {
            gameWorld.remove(entity);
            gameWorld.removeEntity(entity.getId());
            runGameView.removeSprite(entity.getId());
        }

        runGameView.addAnimation(
                RunGameView.SHELL_EXPLOSION_ANIMATION,
                RunGameView.SHELL_EXPLOSION_FRAME_DELAY,
                EnwiwiWan.getX(),
                EnwiwiWan.getY());
        runGameView.addAnimation(
                RunGameView.SHELL_EXPLOSION_ANIMATION,
                RunGameView.SHELL_EXPLOSION_FRAME_DELAY,
                EnwiwiTu.getX(),
                EnwiwiTu.getY());
    }

    /** Check if Tank Died **/
    public boolean PewPewDead(){
        if (PlayerTank.getLife() == 0 || SmartAiTank.getLife() == 0 && DumbAiTank.getLife() == 0) {
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        GameDriver gameDriver = new GameDriver();
        gameDriver.start();
    }
}
