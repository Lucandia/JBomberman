import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * PlayerModel represents the state and behavior of a player in the game.
 */
public class EnemyModel extends EntityModel {
    private final IntegerProperty points = new SimpleIntegerProperty();

    /**
     * Constructs a new PlayerModel with the specified initial position.
     * 
     * @param initialPosition The starting position of the player in the game world.
     */
    public EnemyModel(int initialX, int initialY, double velocity, int[] boundingBox, int[] boundingOffset, StageModel stage, int points) {
        super(initialX, initialY, velocity, boundingBox, boundingOffset, stage);
        this.points.set(3); // Example default lives
    }

    /**
     * Gets the lives property of the player.
     * 
     * @return The lives property.
     */
    public IntegerProperty pointsProperty() {
        return this.points;
    }

    /**
     * Decreases the player's lives by one.
     * Trigger game over or other logic when lives reach zero.
     */
    public void loseLife(int loss) {
        this.points.set(this.points.get() - loss);
        if (this.points.get() <= 0) {
            
        }
    }

    @Override
    public void update(double elapsedTime) {
        super.update(elapsedTime);
        if (!this.isMoving) {
            this.startMoving(new int[] {-lastDirection[0], -lastDirection[1]});
        }
    }
}

