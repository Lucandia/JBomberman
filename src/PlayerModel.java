import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * PlayerModel represents the state and behavior of a player in the game.
 */
public class PlayerModel extends EntityModel {
    private final IntegerProperty lives = new SimpleIntegerProperty();
    private final IntegerProperty score = new SimpleIntegerProperty();
    private final IntegerProperty bombCapacity = new SimpleIntegerProperty(1); 
    private final IntegerProperty bombRadius = new SimpleIntegerProperty(2);

    /**
     * Constructs a new PlayerModel with the specified initial position.
     * 
     * @param initialPosition The starting position of the player in the game world.
     */
    public PlayerModel(int initialX, int initialY, double velocity, StageModel stage) {
        super(initialX, initialY, velocity,  new int[] {13, 13}, new int[] {7, 17}, stage);
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

    public IntegerProperty bombCapacityProperty() {
        return this.bombCapacity;
    }

    public IntegerProperty bombRadiusProperty() {
        return this.bombRadius;
    }


    /**
     * Increases the player's score by a certain amount.
     * 
     * @param points The number of points to add to the score.
     */
    public void addScore(int points) {
        this.score.set(this.score.get() + points);
    }

    @Override
    public void update(double elapsedTime) {
        super.update(elapsedTime);
        // Example: Update the player's position based on velocity and elapsed time.
        
        // Example movement update could be applied here, handling user input, etc.
    }
    
    // Additional player-specific methods can be added below
}

