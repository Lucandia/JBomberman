import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

/**
 * This class represents the abstract model for any entity in the game.
 * It includes common properties like position, which can be observed
 * for changes using JavaFX's property system.
 */
public abstract class EntityModel {

    // Properties
    private final IntegerProperty x = new SimpleIntegerProperty();
    private final IntegerProperty y = new SimpleIntegerProperty();
    private final DoubleProperty velocity = new SimpleDoubleProperty();
    private double timeSinceLastMove = 0.0;
    private final double delayMove = 0.07; // Time in seconds between moves
    protected StageModel stage;
    private final int[] boundingBox = {0, 0};
    private final int[] boundingOffset = {0, 0};
    private boolean isMoving = false;
    private int[] lastDirection = {0, 0};

    public EntityModel() {
        this(0, 0, 0.0, null);
    }

    public EntityModel(int x, int y, double velocity, StageModel stage) {
        this(0, 0, 0.0, null, null, null);
    }

    /**
     * Constructor for EntityModel with initial position.
     * 
     * @param initialPosition The initial position of the entity on the game board.
     */
    public EntityModel(int x, int y, double velocity, int[] boundingBox, int[] boundingOffset, StageModel stage) {
        this.x.set(x);
        this.y.set(y);
        this.velocity.set(velocity);
        this.stage = stage;
        if (boundingBox!=null && boundingOffset.length == 2){
            this.boundingBox[0] = boundingBox[0];
            this.boundingBox[1] = boundingBox[1];
        }
        if (boundingOffset!=null && boundingOffset.length == 2){
            this.boundingOffset[0] = boundingOffset[0];
            this.boundingOffset[1] = boundingOffset[1];
        }
    }


    /**
     * Gets the x property of the entity.
     * 
     * @return The x property of the entity.
     */
    public IntegerProperty xProperty() {
        return x;
    }

    /**
     * Gets the y property of the entity.
     * 
     * @return The y property of the entity.
     */
    public IntegerProperty yProperty() {
        return y;
    }

    /**
     * Gets the velocity property of the entity.
     * 
     * @return The velocity property of the entity.
     */
    public DoubleProperty velocityProperty() {
        return velocity;
    }

    public int[] getBoundingBox() {
        return boundingBox;
    }

    public int[] getBoundingOffset() {
        return boundingOffset;
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
        return new int[] {x.get() + boundingOffset[0], y.get() + boundingOffset[1]};
    }

    /**
     * Sets the position of the entity using an ObservablePoint2D object.
     * 
     * @param position The new position of the entity.
     */
    public void move(int dx, int dy) {
        int x_move = (int) Math.round(this.velocityProperty().get() * Double.valueOf(dx)); // explicit cast to int
        int y_move = (int) Math.round(this.velocityProperty().get() * Double.valueOf(dy)); // explicit cast to int
        if (canMoveTo(dx, dy)) {
            xProperty().set(xProperty().get() + x_move);
            yProperty().set(yProperty().get() + y_move);
        }
    }

    // Check if the entity can move to a new position
    private boolean canMoveTo(int dx, int dy) {
        int xSign = Integer.signum(dx);
        int ySign = Integer.signum(dy);
        int[] center = centerOfMass();
        int tileX = center[0];
        int tileY = center[1];
        if (dx != 0){
            int tileXCollision = (int) center[0] + xSign * boundingBox[0] / 2 + 2;
            // controlla la tile direttamente sull'asse x della bounding box
            if (stage.getTileAtPosition(tileXCollision, tileY) != null) return false;
            // scontro col bordo di una tile mentre sopra non c'e' nulla --> e' uno spigolo e bisogna fermarsi
            int tileYCollision = (int) center[1] - boundingBox[1] / 2;
            if (stage.getTileAtPosition(tileX, tileYCollision) == null && stage.getTileAtPosition(tileXCollision, tileYCollision) != null) return false;
            tileYCollision = (int) center[1] + boundingBox[1] / 2;
            if (stage.getTileAtPosition(tileX, tileYCollision) == null && stage.getTileAtPosition(tileXCollision, tileYCollision) != null) return false;


        }
        // stessa cosa se lo spostamento e' sull'asse y
        else if (dy != 0){
            int tileYCollision = (int) center[1] + ySign * boundingBox[1] / 2 + 2;
            if (stage.getTileAtPosition(tileX, tileYCollision) != null) return false;
            int tileXCollision = (int) center[0] - boundingBox[0] / 2;
            if (stage.getTileAtPosition(tileXCollision, tileY) == null && stage.getTileAtPosition(tileXCollision, tileYCollision) != null) return false;
            tileXCollision = (int) center[0] + boundingBox[0] / 2;
            if (stage.getTileAtPosition(tileXCollision, tileY) == null && stage.getTileAtPosition(tileXCollision, tileYCollision) != null) return false;
        }
        return true; // in tutte le direzioni della bouinding box non c'e' collisione
    }

    /**
     * Starts the walking animation for the entity in the specified direction.
     * 
     * @param direction The direction in which the entity should start walking.
     */
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
        timeSinceLastMove += elapsedTime;
        // il tempo di delay viene diviso per la velocita' in modo da diminuire se aumenta la velocita'
        if (isMoving && timeSinceLastMove >= delayMove / velocity.get()) {
            move(lastDirection[0], lastDirection[1]);
            timeSinceLastMove = 0.0; // Reset the timer
        }
    }

}
