import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * PlayerModel represents the state and behavior of a player in the game.
 */
public class PlayerModel extends EntityModel {
    private final IntegerProperty lives = new SimpleIntegerProperty();
    private final IntegerProperty score = new SimpleIntegerProperty();

    /**
     * Constructs a new PlayerModel with the specified initial position.
     * 
     * @param initialPosition The starting position of the player in the game world.
     */
    public PlayerModel(ObservablePoint2D initialPosition, double velocity) {
        super(initialPosition, velocity);
        // Set default values for lives and score or any additional setup.
        this.lives.set(3); // Example default lives
        this.score.set(0); // Initial score
    }

    /**
     * Gets the lives property of the player.
     * 
     * @return The lives property.
     */
    public IntegerProperty livesProperty() {
        return this.lives;
    }

    /**
     * Gets the score property of the player.
     * 
     * @return The score property.
     */
    public IntegerProperty scoreProperty() {
        return this.score;
    }

    /**
     * Increases the player's score by a certain amount.
     * 
     * @param points The number of points to add to the score.
     */
    public void addScore(int points) {
        this.score.set(this.score.get() + points);
    }

    /**
     * Decreases the player's lives by one.
     * Trigger game over or other logic when lives reach zero.
     */
    public void loseLife() {
        this.lives.set(this.lives.get() - 1);
        if (this.lives.get() <= 0) {
            // Trigger game over or other logic.
        }
    }

    @Override
    public void update(double elapsedTime) {
        // Update the player's state. This method would be called each frame or update cycle.

        // Example: Update the player's position based on velocity
        ObservablePoint2D velocity = new ObservablePoint2D(this.getVelocity(), getVelocity());
        ObservablePoint2D newPosition = this.getPosition().add(velocity.multiply(elapsedTime));
        this.setPosition(newPosition.getX(), newPosition.getY());
        
        // Example movement update could be applied here, handling user input, etc.
    }
    
    // Additional player-specific methods can be added below
}
