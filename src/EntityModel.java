import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

/**
 * This class represents the abstract model for any entity in the game.
 * It includes common properties like position, which can be observed
 * for changes using JavaFX's property system.
 */
public abstract class EntityModel {

    // Properties
    private final ObjectProperty<ObservablePoint2D> position = new SimpleObjectProperty<>();
    private final DoubleProperty velocity = new SimpleDoubleProperty();

    /**
     * Default constructor for EntityModel.
     */
    public EntityModel() {
        // Initialize with default values if necessary
        this(new ObservablePoint2D(0, 0), 0);
    }

    /**
     * Constructor for EntityModel with initial position.
     * 
     * @param initialPosition The initial position of the entity on the game board.
     */
    public EntityModel(ObservablePoint2D initialPosition, double velocity) {
        this.position.set(initialPosition);
        this.velocity.set(velocity);
    }

    /**
     * Gets the position property of the entity.
     * 
     * @return The position property of the entity.
     */
    public ObjectProperty<ObservablePoint2D> positionProperty() {
        return position;
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
     * Gets the velocity of the entity.
     * 
     * @return The velocity of the entity.
     */
    public double getVelocity() {
        return velocity.get();
    }
    

    /**
     * Gets the position of the entity.
     * 
     * @return The position of the entity.
     */
    public ObservablePoint2D getPosition() {
        return position.get();
    }

    /**
     * Gets the X component of the entity's position.
     * 
     * @return The X component of the position property.
     */
    public double getX() {
        return position.get().getX();
    }

    /**
     * Gets the Y component of the entity's position.
     * 
     * @return The Y component of the position property.
     */
    public double getY() {
        return position.get().getY();
    }

    /**
     * Sets the entity's position using x and y coordinates.
     * 
     * @param x The X coordinate of the new position.
     * @param y The Y coordinate of the new position.
     */
    public void setPosition(double x, double y) {
        position.set(new ObservablePoint2D(x, y));
    }

    /**
     * Sets the velocity of the entity.
     * 
     * @param velocity The velocity value to set.
     */
    public void setVelocity(double velocity) {
        this.velocity.set(velocity);
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
