package com.lucandia;
/**
 * PlayerModel represents the state and behavior of a player in the game.
 */
public class EnemyModel extends EntityModel {
    /**
     * Constructs a new PlayerModel with the specified initial position.
     * 
     * @param initialPosition The starting position of the player in the game world.
     */
    public EnemyModel(int initialX, int initialY, StageModel stage) {
        super(initialX, initialY, new int[] {15, 15}, new int[] {8, 17}, 100, stage);
    }

    @Override
    public EntityModel checkCollision(int dx, int dy) {
        EntityModel occupant = super.checkCollision(dx, dy);
        if (occupant instanceof PlayerModel) {
            occupant.loseLife(1);
        }
        return occupant;
    }

    @Override
    public void update(double elapsedTime) {
        super.update(elapsedTime);
        if (!this.isMoving) {
            this.startMoving(new int[] {-lastDirection[0], -lastDirection[1]});
        }
    }
}

