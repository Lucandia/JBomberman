package com.lucandia;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import java.util.ArrayList;

/**
 * This class represents the abstract model for any entity in the game.
 * It includes common properties like position, which can be observed
 * for changes using JavaFX's property system.
 */
public abstract class EntityModel extends XYModel{

    // Properties
    protected final IntegerProperty life = new SimpleIntegerProperty(100);
    protected final DoubleProperty velocity = new SimpleDoubleProperty(1);
    protected double timeSinceLastMove = 0.0;
    protected double delayMove = 0.05; // Time in seconds between moves
    protected final int[] boundingBox = {0, 0};
    protected final int[] boundingOffset = {0, 0};
    protected boolean isMoving = false;
    protected int[] lastDirection = {0, 0};
    protected StageModel stage;
    protected ArrayList<EmptyTile> occupiedTiles = new ArrayList<>();

    public EntityModel(int x, int y, StageModel stage) {
        this(x, y, new int[] {16, 16}, new int[] {16, 16}, 100, null);
    }

    /**
     * Constructor for EntityModel with initial position.
     * 
     * @param initialPosition The initial position of the entity on the game board.
     */
    public EntityModel(int x, int y, int[] boundingBox, int[] boundingOffset,  int life, StageModel stage) {
        super(x, y);
        this.stage = stage;
        this.life.set(life);
        if (boundingBox!=null && boundingOffset.length == 2){
            this.boundingBox[0] = boundingBox[0];
            this.boundingBox[1] = boundingBox[1];
        }
        if (boundingOffset!=null && boundingOffset.length == 2){
            this.boundingOffset[0] = boundingOffset[0];
            this.boundingOffset[1] = boundingOffset[1];
        }
        setOccupiedTiles();
    }

    public void loseLife(int amount) {
        this.life.set(getLife() - amount);
        if (isDead()) {
            clearOccupiedTiles();
        }
    }

    public boolean isDead() {
        return getLife() <= 0;
    }

    public void setLife(int amount) {
        this.life.set(amount);
    }

    public int getLife() {
        return life.get();
    }

    /**
     * Gets the velocity property of the entity.
     * 
     * @return The velocity property of the entity.
     */
    public DoubleProperty velocityProperty() {
        return velocity;
    }

    public double getVelocity() {
        return velocity.get();
    }

    public void setVelocity(double velocity) {
        this.velocity.set(velocity);
    }

    public StageModel getStage() {
        return stage;
    }

    public int[] getBoundingBox() {
        return boundingBox;
    }

    public int[] getBoundingOffset() {
        return boundingOffset;
    }

    public int[] getLastDirection() {
        return lastDirection;
    }

    public String getLastDirectionString() {
        if (lastDirection[0] == 0 && lastDirection[1] == -1) return "UP";
        else if (lastDirection[0] == 0 && lastDirection[1] == 1) return "DOWN";
        else if (lastDirection[0] == -1 && lastDirection[1] == 0) return "LEFT";
        else if (lastDirection[0] == 1 && lastDirection[1] == 0) return "RIGHT";
        else return "";
    }

    public boolean isMoving() {
        return isMoving;
    }

    public void setBoundingBox(int[] boundingBox) {
        this.boundingBox[0] = boundingBox[0];
        this.boundingBox[1] = boundingBox[1];
    }

    public void setBoundingOffset(int[] boundingOffset) {
        this.boundingOffset[0] = boundingOffset[0];
        this.boundingOffset[1] = boundingOffset[1];
    }

    public void setStage(StageModel stage) {
        this.stage = stage;
    }

    public int[] centerOfMass() {
        return new int[] {getX() + boundingOffset[0], getY() + boundingOffset[1]};
    }

    public void clearOccupiedTiles() {
        for (EmptyTile tile : occupiedTiles) {
            tile.setOccupant(null);
        }
        occupiedTiles.clear();
    }

    public void setOccupiedTiles() {
        clearOccupiedTiles();     
        int xCenter = centerOfMass()[0];
        int yCenter = centerOfMass()[1];
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                // skip the cornern tiles {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
                if (x != 0 && y !=0 && (x == y || x == -y)) continue;
                int tileX = xCenter + x * boundingBox[0] / 2;
                int tileY = yCenter + y * boundingBox[1] / 2;
                EmptyTile tile = stage.getEmptyTileAtPosition(tileX, tileY);
                tile.setOccupant(this);
                occupiedTiles.add(tile);
            }
        }
    }

    /**
     * Sets the position of the entity using an ObservablePoint2D object.
     * 
     * @param position The new position of the entity.
     */
    public void move(int dx, int dy) {
        int x_move = (int) Math.round(this.velocityProperty().get() * Double.valueOf(dx)); // explicit cast to int
        int y_move = (int) Math.round(this.velocityProperty().get() * Double.valueOf(dy)); // explicit cast to int
        if (canMoveTo(dx, dy) && (checkCollision(dx, dy) == null || checkCollision(dx, dy) == this)) {
            xProperty().set(getX() + x_move);
            yProperty().set(getY() + y_move);
            // Update tiles occupancy
            setOccupiedTiles();
        }
        else isMoving = false;
    }

    public EntityModel checkCollision(int dx, int dy) {
        int xSign = Integer.signum(dx);
        int ySign = Integer.signum(dy);
        int directionOffset = 3;
        int tileXCollision = (int) centerOfMass()[0] + xSign * boundingBox[0] / 2 + xSign * directionOffset;
        int tileYCollision = (int) centerOfMass()[1] + ySign * boundingBox[1] / 2 + ySign * directionOffset;
        return stage.getEmptyTileAtPosition(tileXCollision, tileYCollision).getOccupant();
    }

    // Check if the entity can move to a new position
    protected boolean canMoveTo(int dx, int dy) {
        int xSign = Integer.signum(dx);
        int ySign = Integer.signum(dy);
        int[] center = centerOfMass();
        int tileX = center[0];
        int tileY = center[1];
        int directionOffset = 2;
        Tile collisionTile;
        if (dx != 0){
            int tileXCollision = (int) center[0] + xSign * boundingBox[0] / 2 + xSign * directionOffset;
            collisionTile = stage.getTileAtPosition(tileXCollision, tileY);
            // controlla la tile direttamente sull'asse x della bounding box
            if (!collisionTile.isWalkable()) return false;
            // scontro col bordo di una tile mentre sopra non c'e' nulla --> e' uno spigolo e bisogna fermarsi
            int tileYCollision = (int) center[1] - boundingBox[1] / 2;
            collisionTile = stage.getTileAtPosition(tileXCollision, tileYCollision);
            if (stage.getTileAtPosition(tileX, tileYCollision) instanceof EmptyTile && !collisionTile.isWalkable()) return false;
            tileYCollision = (int) center[1] + boundingBox[1] / 2;
            collisionTile = stage.getTileAtPosition(tileXCollision, tileYCollision);
            if (stage.getTileAtPosition(tileX, tileYCollision) instanceof EmptyTile && !collisionTile.isWalkable()) return false;
        }
        // stessa cosa se lo spostamento e' sull'asse y
        else if (dy != 0){
            int tileYCollision = (int) center[1] + ySign * boundingBox[1] / 2 + ySign * directionOffset;
            collisionTile = stage.getTileAtPosition(tileX, tileYCollision);
            if (!collisionTile.isWalkable()) return false;
            int tileXCollision = (int) center[0] - boundingBox[0] / 2;
            collisionTile = stage.getTileAtPosition(tileXCollision, tileYCollision);
            if (stage.getTileAtPosition(tileXCollision, tileY) instanceof EmptyTile && !collisionTile.isWalkable()) return false;
            tileXCollision = (int) center[0] + boundingBox[0] / 2;
            collisionTile = stage.getTileAtPosition(tileXCollision, tileYCollision);
            if (stage.getTileAtPosition(tileXCollision, tileY) instanceof EmptyTile && !collisionTile.isWalkable()) return false;
        }
        return true; // in tutte le direzioni della bouinding box non c'e' collisione
    }

    /**
     * Starts the walking animation for the entity in the specified direction.
     * 
     * @param direction The direction in which the entity should start walking.
     */
    public void startMoving(int[] direction) {
            lastDirection[0] = direction[0];
            lastDirection[1] = direction[1];
            isMoving = true;
            if (direction[0] == 0 && direction[1] == 0) {
                isMoving = false;
            }
    }

    public void startMoving(String direction) {
        switch (direction) {
            case "UP":
                lastDirection[0] = 0;
                lastDirection[1] = -1;
                isMoving = true;
                break;
            case "DOWN":
                lastDirection[0] = 0;
                lastDirection[1] = 1;
                isMoving = true;
                break;
            case "LEFT":
                lastDirection[0] = -1;
                lastDirection[1] = 0;
                isMoving = true;
                break;
            case "RIGHT":
                lastDirection[0] = 1;
                lastDirection[1] = 0;
                isMoving = true;
                break;
            default:
                isMoving = false;
                break;
        }
    }

    /**
     * Stops the walking animation for the entity.
     */
    public void stopMoving() {
        isMoving = false;
    }

    /**
     * Method to update the entity's state. This should be called
     * on every frame or update interval.
     * 
     * @param elapsedTime The time elapsed since the last update.
     */
    public void update(double elapsedTime){
        if (isDead()) {
            return;
        }
        timeSinceLastMove += elapsedTime;
        // il tempo di delay viene diviso per la velocita' in modo da diminuire se aumenta la velocita'
        if (isMoving && timeSinceLastMove >= delayMove / velocity.get()) {
            move(lastDirection[0], lastDirection[1]);
            timeSinceLastMove = 0.0; // Reset the timer
        }
    }

}