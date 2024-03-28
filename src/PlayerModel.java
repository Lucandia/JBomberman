import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * PlayerModel represents the state and behavior of a player in the game.
 */
public class PlayerModel extends EntityModel {
    private final IntegerProperty score = new SimpleIntegerProperty(0);
    private final IntegerProperty bombCapacity = new SimpleIntegerProperty(1); 
    private final IntegerProperty bombRadius = new SimpleIntegerProperty(1);
    private int recoveryTime = 10;
    private boolean recovering = false;

    /**
     * Constructs a new PlayerModel with the specified initial position.
     * 
     * @param initialPosition The starting position of the player in the game world.
     */
    public PlayerModel(int initialX, int initialY, double velocity, StageModel stage) {
        super(initialX, initialY, velocity,  new int[] {13, 13}, new int[] {7, 17}, 3, stage);
        // Set default values for lives and score or any additional setup.
        this.score.set(0); // Initial score
    }

    /**
     * Gets the lives property of the player.
     * 
     * @return The lives property.
     */
    public IntegerProperty lifeProperty() {
        return this.life;
    }

    @Override
    public void loseLife(int amount) {
        if (this.recovering) return;
        this.life.set(life.get() - 1);
        this.recovering = true;
    }

    @Override
    public EntityModel checkCollision(int dx, int dy) {
        EntityModel occupant = super.checkCollision(dx, dy);
        if (occupant instanceof EnemyModel) {
            loseLife(1);
        }
        return occupant;
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

    public void increaseBombCapacity() {
        this.bombCapacity.set(this.bombCapacity.get() + 1);
    }

    public IntegerProperty bombRadiusProperty() {
        return this.bombRadius;
    }

    public void increaseBombRadius() {
        this.bombRadius.set(this.bombRadius.get() + 1);
    }

    public void increaseSpeed() {
        // this.velocity.set(this.velocity.get() + 0.3);
        if (this.delayMove > 0.01) this.delayMove -= 0.01;
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
        if (this.recovering) {
            this.recoveryTime--;
            if (this.recoveryTime == 0) {
                this.recovering = false;
                this.recoveryTime = 10;
            }
        }
    }
    
}

