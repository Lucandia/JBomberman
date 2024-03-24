import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * PlayerModel represents the state and behavior of a player in the game.
 */
public class EnemyModel2 extends EntityModel {
    private final IntegerProperty points = new SimpleIntegerProperty();

    /**
     * Constructs a new PlayerModel with the specified initial position.
     * 
     * @param initialPosition The starting position of the player in the game world.
     */
    public EnemyModel2(int initialX, int initialY, double velocity, int[] boundingBox, int[] boundingOffset, StageModel stage, int points) {
        super(initialX, initialY, velocity, boundingBox, boundingOffset, stage);
        this.points.set(3); // Example default lives
    }

    public EntityModel checkCollision(int dx, int dy) {
        EntityModel occupant = super.checkCollision(dx, dy);
        if (occupant instanceof PlayerModel) {
            occupant.loseLife(1);
        }
        return occupant;
    }

    /**
     * Gets the lives property of the player.
     * 
     * @return The lives property.
     */
    public IntegerProperty pointsProperty() {
        return this.points;
    }

    @Override
    public void update(double elapsedTime) {
        super.update(elapsedTime);
        if (!this.isMoving) {
            this.startMoving(new int[] {-lastDirection[0], -lastDirection[1]});
        }
    }
}

