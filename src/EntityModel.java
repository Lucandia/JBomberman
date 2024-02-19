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

    /**
     * Default constructor for EntityModel.
     */
    public EntityModel() {
        // Initialize with default values if necessary
        this(0, 0, 0);
    }

    /**
     * Constructor for EntityModel with initial position.
     * 
     * @param initialPosition The initial position of the entity on the game board.
     */
    public EntityModel(int x, int y, double velocity) {
        this.x.set(x);
        this.y.set(x);
        this.velocity.set(velocity);
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

    /**
     * Sets the position of the entity using an ObservablePoint2D object.
     * 
     * @param position The new position of the entity.
     */
    public void move(int dx, int dy) {
        int x_move = (int) Math.round(this.velocityProperty().get() * Double.valueOf(dx)); // explicit cast to int
        int y_move = (int) Math.round(this.velocityProperty().get() * Double.valueOf(dy)); // explicit cast to int
        xProperty().set(xProperty().get() + x_move);
        yProperty().set(yProperty().get() + y_move);
    }

    /**
     * Abstract method to update the entity's state. This should be called
     * on every frame or update interval.
     * 
     * @param elapsedTime The time elapsed since the last update.
     */
    public abstract void update(double elapsedTime);

    // Additional common methods for game entities would go here
}
