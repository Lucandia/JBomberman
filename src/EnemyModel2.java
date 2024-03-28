/**
 * PlayerModel represents the state and behavior of a player in the game.
 */
public class EnemyModel2 extends EnemyModel {

    /**
     * Constructs a new PlayerModel with the specified initial position.
     * 
     * @param initialPosition The starting position of the player in the game world.
     */
    public EnemyModel2(int initialX, int initialY, StageModel stage) {
        super(initialX, initialY, stage);
        this.life.set(400); // Initial life
        this.setBoundingBox(new int[] {15, 15});
        this.setBoundingOffset(new int[] {8, 15});
    }

    @Override
    public void loseLife(int amount) {
        super.loseLife(amount);
        if (isDead()) return;
        boolean new_pos = false;
        while (!new_pos) {
            int[] randomXY = getStage().getRandomFreeTile();
            if (getStage().getTile(randomXY[0], randomXY[1]) instanceof EmptyTile && ((EmptyTile) getStage().getTile(randomXY[0], randomXY[1])).getOccupant() == null) {
                this.xProperty().set(randomXY[0] * getStage().getTileSize());
                this.yProperty().set(randomXY[1] * getStage().getTileSize());
                new_pos = true;
            }
        }
    }

    // @Override
    // protected boolean canMoveTo(int dx, int dy) {
    //     int xSign = Integer.signum(dx);
    //     int ySign = Integer.signum(dy);
    //     int[] center = centerOfMass();
    //     int tileX;
    //     int tileY;
    //     int directionOffset = 2;
    //     Tile collisionTile;
    //     if (dx != 0){
    //         tileX = (int) center[0] + xSign * boundingBox[0] / 2 + xSign * directionOffset;
    //     }
    //     else  tileX = center[0];
    //     if (dy != 0){
    //         tileY = (int) center[1] + ySign * boundingBox[1] / 2 + ySign * directionOffset;
    //     }
    //     else tileY = center[1];
    //     collisionTile = stage.getTileAtPosition(tileX, tileY);
    //     if (collisionTile.isDestructible()) {
    //         return true;
    //     }
    //     return false;
    //    }

    @Override
    public void update(double elapsedTime) {
        super.update(elapsedTime);
        int lastX = lastDirection[0];
        int lastY = lastDirection[1];
        int randomDirection = (int) (Math.random() * 4); // Generate a random number between 0 and 3
        if (randomDirection == 0 && canMoveTo(1, 0) && lastX != -1) {
            lastDirection[0] = 1;
            lastDirection[1] = 0;
        }
        else if (randomDirection == 1 && canMoveTo(-1, 0) && lastX != 1) {
            lastDirection[0] = -1;
            lastDirection[1] = 0;
        }
        else if (randomDirection == 2 && canMoveTo(0, -1) && lastY != 1) {
            lastDirection[0] = 0;
            lastDirection[1] = -1;
        }
        else if (randomDirection == 3 && canMoveTo(0, 1) && lastY != -1) {
            lastDirection[0] = 0;
            lastDirection[1] = 1;
        }
    }
}

