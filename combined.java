import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import javafx.scene.layout.Pane;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class BombController {
    private LinkedHashMap<BombModel, BombView> bombMap = new LinkedHashMap<>();
    private Pane pane;
    private StageModel stage;
    private IntegerProperty currentX = new SimpleIntegerProperty();
    private IntegerProperty currentY = new SimpleIntegerProperty();
    private int[] bombPlayerYOffset;
    private IntegerProperty maxBombs = new SimpleIntegerProperty();
    private IntegerProperty bombRadius = new SimpleIntegerProperty();

    public BombController(PlayerModel playerModel, Pane pane) {
        currentX.bind(playerModel.xProperty());
        currentY.bind(playerModel.yProperty());
        maxBombs.bind(playerModel.bombCapacityProperty());
        bombRadius.bind(playerModel.bombRadiusProperty());
        bombPlayerYOffset = playerModel.getBoundingOffset();
        this.pane = pane;
        this.stage = playerModel.getStage();
    }

    public void input() {
        if (bombMap.size() < maxBombs.get()) {
            int[] startPosition = stage.getTileStartCoordinates(currentX.get() + bombPlayerYOffset[0], currentY.get() + bombPlayerYOffset[1]);
            if (stage.addBombAtPosition(startPosition[0], startPosition[1], bombRadius.get())) {
                bombMap.put(stage.getBombAtPosition(startPosition[0], startPosition[1]), new BombView(stage.getBombAtPosition(startPosition[0], startPosition[1]), pane, stage));
            }
        }
    }

    public boolean destroyTile(BombModel bomb, int x, int y) {
        // uso le stringe perche' se uso int[], equals non funziona bene (cerca il riferimento)
        Tile tile = stage.getTile(x, y);
        if (tile == null) return true; // Tile is out of bounds
        // the tile is destructible
        else if (!tile.isDestructible()) {
            return true;
        }
        else if (tile.isDetonable()) {
            bomb.addDetonatePosition(x, y);
            if (tile instanceof BombModel) {
                // Se la bomba e' gia' stata esaminata, non la esplodo
                DetonateBomb((BombModel) tile);
                return true;
            }
            else {
                stage.destroyTile(x, y);
                return true;
            }
        }
        bomb.addDetonatePosition(x, y);
        return false;
    }

    public void DetonateBomb(BombModel bomb) {
        int blast = bomb.getBlastRadius();
        bomb.explode();
        int tileX = (int) bomb.getX() / stage.getTileSize();
        int tileY = (int) bomb.getY() / stage.getTileSize();
        stage.destroyTile(tileX, tileY);
        for (int x = -1; x >= -blast; x--) {
            if (destroyTile(bomb, tileX + x, tileY)) break;
        }
        for (int x = 1; x <= blast; x++) {
            if (destroyTile(bomb, tileX + x, tileY)) break;
        }
        for (int y = -1; y >= -blast; y--) {
            if (destroyTile(bomb, tileX, tileY + y)) break;
        }
        for (int y = 1; y <= blast; y++) {
            if (destroyTile(bomb, tileX, tileY + y)) break;
        }
        bombMap.get(bomb).update();
    }

    public void update(double elapsed) {
        List<BombModel> toRemove = new ArrayList<>();
        for (BombModel bomb : bombMap.keySet()) {
            bomb.update(elapsed);
            if (!bomb.isActive()) {
                DetonateBomb(bomb);
                toRemove.add(bomb); // Mark this bomb for removal
            }
        }
        for (BombModel bomb : toRemove) {
            bombMap.remove(bomb);
        }
    }
}
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.BooleanProperty;
import java.util.ArrayList;
import java.util.List;

public class BombModel extends EmptyTile{
    private List<String> detonatePositions = new ArrayList<>();
    private int blastRadius = 1; // Default blast radius
    private BooleanProperty active = new SimpleBooleanProperty(true);
    private double timer = 5.0; // Bomb timer in seconds
    private double walkableTime = 1.0; // time on which you can walk on the bomb

    public BombModel(int x, int y, int radius) {
        super(x, y);
        blastRadius = radius;
    }

    public BombModel(int x, int y, int radius, double time) {
        super(x, y);
        blastRadius = radius;
        time = timer;
    }

    public boolean isDetonable() {
        return true;
    }

    public int getBlastRadius() {
        return blastRadius;
    }

    public BooleanProperty activeProperty() {
        return active;
    }

    public boolean isActive() {
        return active.get();
    }

    public void explode() {
        active.set(false);
        setTimer(0);
    }

    public double getTimer() {
        return timer;
    }

    public void setTimer(double time) {
        timer = time;
    }

    // Method to decrement the timer, call this method every second
    public void update(double elapsed) {
        if (isActive()) {
            timer -= elapsed;
            walkableTime -= elapsed;
            if (walkableTime <= 0) {
                setWalkable(false);
            }
            if (timer <= 0) {
                // Bomb should explode
                explode();
            }
        }
    }

    public List<String> getDetonatePositions() {
        return detonatePositions;
    }

    public void addDetonatePosition(int x, int y) {
        detonatePositions.add(x + "," + y);
    }

    public boolean containsDetonatePosition(int x, int y) {
        return detonatePositions.contains(x + "," + y);
    }
}
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Rectangle2D;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.BooleanProperty;

public class BombView {
    private ImageView bombSprite;
    private Timeline bombAnimation = null;
    private StageModel stage;
    private BooleanProperty active = new SimpleBooleanProperty(false);
    private Pane pane;
    private BombModel model;

    public BombView(BombModel bombModel, Pane pane, StageModel stage) {
        this.pane = pane;
        this.model = bombModel;
        this.stage = stage;
        active.bind(bombModel.activeProperty());
        // Assuming the sprite sheet is in the same package as the BombView
        Image image = new Image(getClass().getResourceAsStream("resources/sprites/bomb.png"));
        bombSprite = new ImageView(image);
        // Set the initial viewport to show the first sprite
        bombSprite.setViewport(new Rectangle2D(0, 0, 16, 16));
        // Bind the ImageView's position to the model's position
        bombSprite.layoutXProperty().set(bombModel.getX());
        bombSprite.layoutYProperty().set(bombModel.getY());

        // Animation timer to cycle through the sprites
        bombAnimation = new Timeline(
            new KeyFrame(Duration.seconds(0.3), e -> bombSprite.setViewport(new Rectangle2D(0, 0, 16, 16))),
            new KeyFrame(Duration.seconds(0.6), e -> bombSprite.setViewport(new Rectangle2D(17, 0, 16, 16))),
            new KeyFrame(Duration.seconds(0.9), e -> bombSprite.setViewport(new Rectangle2D(34, 0, 16, 16))),
            new KeyFrame(Duration.seconds(1.2), e -> bombSprite.setViewport(new Rectangle2D(17, 0, 16, 16))) 
        );
        bombAnimation.setCycleCount(Animation.INDEFINITE);
        bombAnimation.play();
        addToPane();
    }

    public void playExplosionAnimation() { // Ensure to pass the correct pane where the game is rendered
        int radius = model.getBlastRadius();
        final int size = stage.getTileSize(); // Adjust according to your sprite size
        Image explosionImage = new Image(getClass().getResourceAsStream("resources/sprites/explosion.png"));
        // List to hold all explosion sprites
        List<ImageView> explosionSprites = new ArrayList<>();
        // Assuming the bomb is placed in the center of the tile
        int bombX = (int) bombSprite.layoutXProperty().get();
        int bombY = (int) bombSprite.layoutYProperty().get();
        int tileX = (int) bombX / size;
        int tileY = (int) bombY / size;

        // Generate explosion at the bomb position
        ImageView mainExplosionSprite = new ImageView(explosionImage);
        mainExplosionSprite.setViewport(new Rectangle2D(32, 32, size, size)); // Set to the initial frame
        mainExplosionSprite.layoutXProperty().set(bombX); // Centering the explosion sprite
        mainExplosionSprite.layoutYProperty().set(bombY);
        explosionSprites.add(mainExplosionSprite);
        pane.getChildren().add(mainExplosionSprite);

        // Generate explosion on the positive x axis
        int max_dx = radius;
        for (int dx = 1; dx <= radius; dx++) {
            if (model.containsDetonatePosition(tileX + dx, tileY)) {
                // Check the explosion to display
                if (!model.containsDetonatePosition(tileX + dx + 1, tileY)) {
                    max_dx = dx;
                }
                    ImageView explosionSprite = new ImageView(explosionImage);
                    int rect_x = 3;
                    int rect_y = 2;
                    if (dx == max_dx) {
                        rect_x = 4;
                    }
                    explosionSprite.setViewport(new Rectangle2D(rect_x * size, rect_y * size, size, size)); // Set to the initial frame
                    explosionSprite.layoutXProperty().set(bombX + dx * size); // Centering the explosion sprite
                    explosionSprite.layoutYProperty().set(bombY);
                    explosionSprites.add(explosionSprite);
                    pane.getChildren().add(explosionSprite);
                }
            else break;
            if (dx == max_dx) break;
        }

        // Generate explosion on the negative x axis
        int min_dx = -radius;
        for (int dx = -1; dx >= -radius; dx--) {
            // Check the explosion to display
            if (model.containsDetonatePosition(tileX + dx, tileY)) {
                if (!model.containsDetonatePosition(tileX + dx - 1, tileY)) {
                    min_dx = dx;
                }
                ImageView explosionSprite = new ImageView(explosionImage);
                int rect_x = 1;
                int rect_y = 2;
                if (dx == min_dx) {
                    rect_x = 0;
                }
                explosionSprite.setViewport(new Rectangle2D(rect_x * size, rect_y * size, size, size)); // Set to the initial frame
                explosionSprite.layoutXProperty().set(bombX + dx * size); // Centering the explosion sprite
                explosionSprite.layoutYProperty().set(bombY);
                explosionSprites.add(explosionSprite);
                pane.getChildren().add(explosionSprite);
            }
            else break;
            if (dx == min_dx) break;
        }

        // Generate explosion on the positive y axis
        int max_dy = radius;
        for (int dy = 1; dy <= radius; dy++) {
            // Check the explosion to display
            if (model.containsDetonatePosition(tileX, tileY + dy)) {
                if (!model.containsDetonatePosition(tileX, tileY + dy + 1)) {
                    max_dy = dy;
                }
                ImageView explosionSprite = new ImageView(explosionImage);
                int rect_x = 2;
                int rect_y = 3;
                if (dy == max_dy) {
                    rect_y = 4;
                }
                explosionSprite.setViewport(new Rectangle2D(rect_x * size, rect_y * size, size, size)); // Set to the initial frame
                explosionSprite.layoutXProperty().set(bombX); // Centering the explosion sprite
                explosionSprite.layoutYProperty().set(bombY + dy * size);
                explosionSprites.add(explosionSprite);
                pane.getChildren().add(explosionSprite);
            }
            else break;
            if (dy == max_dy) break;
        }

        // Generate explosion on the negative y axis
        int min_dy = -radius;
        for (int dy = -1; dy >= -radius; dy--) {
            // Check the explosion to display
            if (stage.canExplodeAtPosition(bombX, bombY + dy * size)) {
                if (!model.containsDetonatePosition(tileX, tileY + dy - 1)){
                    min_dy = dy;
                }
                ImageView explosionSprite = new ImageView(explosionImage);
                int rect_x = 2;
                int rect_y = 1;
                if (dy == min_dy) {
                    rect_y = 0;
                }
                explosionSprite.setViewport(new Rectangle2D(rect_x * size, rect_y * size, size, size)); // Set to the initial frame
                explosionSprite.layoutXProperty().set(bombX); // Centering the explosion sprite
                explosionSprite.layoutYProperty().set(bombY + dy * size);
                explosionSprites.add(explosionSprite);
                pane.getChildren().add(explosionSprite);
            }
            else break;
            if (dy == min_dy) break;
        }
    
        double frameTime = 0.07; // Time between each frame of the explosion animation
        // Create and play the explosion animation
        Timeline explosionAnimation = new Timeline();
        for (int frameIndex = 0; frameIndex < 5; frameIndex++) { // For each frame of the explosion
            final int index = frameIndex;
            explosionAnimation.getKeyFrames().add(new KeyFrame(Duration.seconds(index * frameTime), e -> {
                for (ImageView sprite : explosionSprites) {
                    Rectangle2D viewport = sprite.getViewport();
                    sprite.setViewport(new Rectangle2D(viewport.getMinX() + 16 * 5 * index, viewport.getMinY(), size, size));
                }
            }));
        }
        explosionAnimation.setCycleCount(2); // Play the animation 2 times
        explosionAnimation.setAutoReverse(true); // Reverse the animation
        explosionAnimation.setOnFinished(e -> explosionSprites.forEach(sprite -> pane.getChildren().remove(sprite))); // Remove sprites after animation
        explosionAnimation.play();
    }
    
    public ImageView getbombSprite() {
        return bombSprite;
    }

    public void addToPane() {
        pane.getChildren().add(getbombSprite());
    }

    public void update() {
        if (!active.get()) {
            playExplosionAnimation();
            pane.getChildren().remove(bombSprite);
            }
        }
}

public class EmptyTile extends Tile{
    private EntityModel occupant = null;

    public EmptyTile(int x, int y) {
        super(x, y, true, false, true);
    }

    // Method to check if the tile is occupied
    public boolean isOccupied() {
        return occupant != null;
    }

    public boolean isDetonable() {
        return isOccupied();
    }

    // Method to get the occupant
    public EntityModel getOccupant() {
        return occupant;
    }

    // Method to set the occupant
    public void setOccupant(EntityModel occupant) {
        this.occupant = occupant;
    }
}import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javafx.scene.layout.Pane;

public class EnemiesController {
    private List<EnemyModel> enemies = new ArrayList<EnemyModel>();
    private List<EntityView> views = new ArrayList<EntityView>();
    private List<int[]> directions = new ArrayList<int []>();

    public EnemiesController(int numberOfEnemies, StageModel stageModel, Pane gameLayer, int level) {
        List<int[]> freeTileIndex = stageModel.getFreeTileIndex();
        Random random = new Random();
        int i = 1;
        while (i <= numberOfEnemies) {
            int randomIndex = random.nextInt(freeTileIndex.size());
            int[] tileIndex = freeTileIndex.get(randomIndex);
            if (((EmptyTile) stageModel.getTile(tileIndex[0], tileIndex[1])).getOccupant() != null) {
                continue;
            }
            EnemyModel enemyModel;
            EntityView enemyView;
            if ((i % (numberOfEnemies / level )) != 0) { // alternate between enemy types
                enemyModel = new EnemyModel(tileIndex[0] * stageModel.getTileSize(), tileIndex[1] * stageModel.getTileSize() - 10,  stageModel);
                enemyView = new EntityView(enemyModel, "enemy1");
            }
            else {
                enemyModel = new EnemyModel2(tileIndex[0] * stageModel.getTileSize(), tileIndex[1] * stageModel.getTileSize() - 10, stageModel);
                enemyView = new EntityView(enemyModel, "enemy2", true, 6);
            }
            if (stageModel.getTile(tileIndex[0], tileIndex[1] - 1) instanceof EmptyTile || stageModel.getTile(tileIndex[0], tileIndex[1] + 1) instanceof EmptyTile) {
                enemyModel.startMoving("UP");
            } else {
                enemyModel.startMoving("RIGHT");
            }
            gameLayer.getChildren().add(enemyView.getEntitySprite());
            addEnemy(enemyModel, enemyView);
            i++;
        }
    }

    public void addEnemy(EnemyModel enemy, EntityView enemyView) {
        enemies.add(enemy);
        views.add(enemyView);
        directions.add(enemy.getLastDirection());
    }

    public void removeEnemy(EnemyModel enemy) {
        int index = enemies.indexOf(enemy);
        if (index != -1) {
            enemies.remove(index);
            views.remove(index);
            directions.remove(index);
        }
    }

    public List<EnemyModel> getEnemies() {
        return enemies;
    }

    public void update(double elapsed) {
        for (int i = enemies.size()-1; i >= 0; i--) {
            EnemyModel enemy = enemies.get(i);
            if (enemy.isDead()) {
                views.get(i).update(elapsed);
                removeEnemy(enemy);
                continue;
            }
            enemy.update(elapsed);
            int[] lastDirection = enemies.get(i).getLastDirection();
            if (enemy.getLastDirection()[0] != lastDirection[0] || enemy.getLastDirection()[1] != lastDirection[1]) {
                directions.set(i, lastDirection);
            }
            views.get(i).update(elapsed);
        }
    }
}
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
        super(initialX, initialY, 0.8, new int[] {15, 15}, new int[] {8, 17}, 100, stage);
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
        this.life.set(200); // Initial life
        this.setBoundingBox(new int[] {15, 15});
        this.setBoundingOffset(new int[] {8, 17});
    }

    @Override
    public void loseLife(int amount) {
        super.loseLife(amount);
        if (isDead()) return;
        boolean new_pos = false;
        // Set the current tile's occupant to null
        ((EmptyTile) getStage().getTileAtPosition((int) this.centerOfMass()[0], (int) this.centerOfMass()[1])).setOccupant(null);
        while (!new_pos) {
            int[] randomXY = getStage().getRandomFreeTile();
            if (getStage().getTile(randomXY[0], randomXY[1]) instanceof EmptyTile && ((EmptyTile) getStage().getTile(randomXY[0], randomXY[1])).getOccupant() == null) {
                this.xProperty().set(randomXY[0] * getStage().getTileSize());
                this.yProperty().set(randomXY[1] * getStage().getTileSize());
                new_pos = true;
            }
        }
    }

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

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import java.util.ArrayList;

/**
 * This class represents the abstract model for any entity in the game.
 * It includes common properties like position, which can be observed
 * for changes using JavaFX's property system.
 */
public abstract class EntityModel extends XYModel{

    // Properties
    protected final IntegerProperty life = new SimpleIntegerProperty(100);
    protected final DoubleProperty velocity = new SimpleDoubleProperty();
    protected double timeSinceLastMove = 0.0;
    protected double delayMove = 0.05; // Time in seconds between moves
    protected final int[] boundingBox = {0, 0};
    protected final int[] boundingOffset = {0, 0};
    protected boolean isMoving = false;
    protected int[] lastDirection = {0, 0};
    protected StageModel stage;
    protected ArrayList<EmptyTile> occupiedTiles = new ArrayList<>();
        


    public EntityModel() {
        this(0, 0, 0.0, null);
    }

    public EntityModel(int x, int y, double velocity, StageModel stage) {
        this(x, y, velocity, new int[] {16, 16}, new int[] {16, 16}, 100, null);
    }

    /**
     * Constructor for EntityModel with initial position.
     * 
     * @param initialPosition The initial position of the entity on the game board.
     */
    public EntityModel(int x, int y, double velocity, int[] boundingBox, int[] boundingOffset,  int life, StageModel stage) {
        super(x, y);
        this.stage = stage;
        this.velocity.set(velocity);
        this.life.set(life);
        if (boundingBox!=null && boundingOffset.length == 2){
            this.boundingBox[0] = boundingBox[0];
            this.boundingBox[1] = boundingBox[1];
        }
        if (boundingOffset!=null && boundingOffset.length == 2){
            this.boundingOffset[0] = boundingOffset[0];
            this.boundingOffset[1] = boundingOffset[1];
        }
        setOccupiedTiles();
    }

    public void loseLife(int amount) {
        this.life.set(getLife() - amount);
        if (isDead()) {
            clearOccupiedTiles();
        }
    }

    public boolean isDead() {
        return getLife() <= 0;
    }

    public void setLife(int amount) {
        this.life.set(amount);
    }

    public int getLife() {
        return life.get();
    }

    /**
     * Gets the velocity property of the entity.
     * 
     * @return The velocity property of the entity.
     */
    public DoubleProperty velocityProperty() {
        return velocity;
    }

    public StageModel getStage() {
        return stage;
    }

    public int[] getBoundingBox() {
        return boundingBox;
    }

    public int[] getBoundingOffset() {
        return boundingOffset;
    }

    public int[] getLastDirection() {
        return lastDirection;
    }

    public String getLastDirectionString() {
        if (lastDirection[0] == 0 && lastDirection[1] == -1) return "UP";
        else if (lastDirection[0] == 0 && lastDirection[1] == 1) return "DOWN";
        else if (lastDirection[0] == -1 && lastDirection[1] == 0) return "LEFT";
        else if (lastDirection[0] == 1 && lastDirection[1] == 0) return "RIGHT";
        else return "";
    }

    public boolean isMoving() {
        return isMoving;
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
        return new int[] {getX() + boundingOffset[0], getY() + boundingOffset[1]};
    }

    public void clearOccupiedTiles() {
        for (EmptyTile tile : occupiedTiles) {
            tile.setOccupant(null);
        }
        occupiedTiles.clear();
    }

    public void setOccupiedTiles() {
        clearOccupiedTiles();     
        int xCenter = centerOfMass()[0];
        int yCenter = centerOfMass()[1];
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                // skip the cornern tiles {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
                if (x != 0 && y !=0 && (x == y || x == -y)) continue;
                int tileX = xCenter + x * boundingBox[0] / 2;
                int tileY = yCenter + y * boundingBox[1] / 2;
                EmptyTile tile = stage.getEmptyTileAtPosition(tileX, tileY);
                tile.setOccupant(this);
                occupiedTiles.add(tile);
            }
        }
    }

    /**
     * Sets the position of the entity using an ObservablePoint2D object.
     * 
     * @param position The new position of the entity.
     */
    public void move(int dx, int dy) {
        int x_move = (int) Math.round(this.velocityProperty().get() * Double.valueOf(dx)); // explicit cast to int
        int y_move = (int) Math.round(this.velocityProperty().get() * Double.valueOf(dy)); // explicit cast to int
        if (canMoveTo(dx, dy) && (checkCollision(dx, dy) == null || checkCollision(dx, dy) == this)) {
            xProperty().set(getX() + x_move);
            yProperty().set(getY() + y_move);
            // Update tiles occupancy
            setOccupiedTiles();
        }
        else isMoving = false;
    }

    public EntityModel checkCollision(int dx, int dy) {
        int xSign = Integer.signum(dx);
        int ySign = Integer.signum(dy);
        int directionOffset = 3;
        int tileXCollision = (int) centerOfMass()[0] + xSign * boundingBox[0] / 2 + xSign * directionOffset;
        int tileYCollision = (int) centerOfMass()[1] + ySign * boundingBox[1] / 2 + ySign * directionOffset;
        return stage.getEmptyTileAtPosition(tileXCollision, tileYCollision).getOccupant();
    }

    // Check if the entity can move to a new position
    protected boolean canMoveTo(int dx, int dy) {
        int xSign = Integer.signum(dx);
        int ySign = Integer.signum(dy);
        int[] center = centerOfMass();
        int tileX = center[0];
        int tileY = center[1];
        int directionOffset = 2;
        Tile collisionTile;
        if (dx != 0){
            int tileXCollision = (int) center[0] + xSign * boundingBox[0] / 2 + xSign * directionOffset;
            collisionTile = stage.getTileAtPosition(tileXCollision, tileY);
            // controlla la tile direttamente sull'asse x della bounding box
            if (!collisionTile.isWalkable()) return false;
            // scontro col bordo di una tile mentre sopra non c'e' nulla --> e' uno spigolo e bisogna fermarsi
            int tileYCollision = (int) center[1] - boundingBox[1] / 2;
            collisionTile = stage.getTileAtPosition(tileXCollision, tileYCollision);
            if (stage.getTileAtPosition(tileX, tileYCollision) instanceof EmptyTile && !collisionTile.isWalkable()) return false;
            tileYCollision = (int) center[1] + boundingBox[1] / 2;
            collisionTile = stage.getTileAtPosition(tileXCollision, tileYCollision);
            if (stage.getTileAtPosition(tileX, tileYCollision) instanceof EmptyTile && !collisionTile.isWalkable()) return false;
        }
        // stessa cosa se lo spostamento e' sull'asse y
        else if (dy != 0){
            int tileYCollision = (int) center[1] + ySign * boundingBox[1] / 2 + ySign * directionOffset;
            collisionTile = stage.getTileAtPosition(tileX, tileYCollision);
            if (!collisionTile.isWalkable()) return false;
            int tileXCollision = (int) center[0] - boundingBox[0] / 2;
            collisionTile = stage.getTileAtPosition(tileXCollision, tileYCollision);
            if (stage.getTileAtPosition(tileXCollision, tileY) instanceof EmptyTile && !collisionTile.isWalkable()) return false;
            tileXCollision = (int) center[0] + boundingBox[0] / 2;
            collisionTile = stage.getTileAtPosition(tileXCollision, tileYCollision);
            if (stage.getTileAtPosition(tileXCollision, tileY) instanceof EmptyTile && !collisionTile.isWalkable()) return false;
        }
        return true; // in tutte le direzioni della bouinding box non c'e' collisione
    }

    /**
     * Starts the walking animation for the entity in the specified direction.
     * 
     * @param direction The direction in which the entity should start walking.
     */
    public void startMoving(int[] direction) {
            lastDirection[0] = direction[0];
            lastDirection[1] = direction[1];
            isMoving = true;
            if (direction[0] == 0 && direction[1] == 0) {
                isMoving = false;
            }
    }

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
        if (isDead()) {
            return;
        }
        timeSinceLastMove += elapsedTime;
        // il tempo di delay viene diviso per la velocita' in modo da diminuire se aumenta la velocita'
        if (isMoving && timeSinceLastMove >= delayMove / velocity.get()) {
            move(lastDirection[0], lastDirection[1]);
            timeSinceLastMove = 0.0; // Reset the timer
        }
    }

}
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.geometry.Rectangle2D;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;


public class EntityView {
    private final ImageView EntitySprite;
    private EntityModel model;
    private String lastDirection;
    // Create a fixed HashMap with keys and values
    private Map<String, Integer> directionSprite = new HashMap<>();
    private Timeline walkAnimation = null;
    private boolean autoReverse = false;
    // private String spriteName;
    private int frames = 4;
    int row = 0;

    public EntityView(EntityModel model, String spriteName)
    {
        this(model, spriteName, false, 4, 0);
    }

    public EntityView(EntityModel model, String spriteName, boolean autoReverse, int frames)
    {
        this(model, spriteName, autoReverse, frames, 0);
    }
    
    public EntityView(EntityModel model, String spriteName, boolean autoReverse, int frames, int row) {
        this.model = model;
        this.autoReverse = autoReverse;
        this.frames = frames;
        this.row = row;
        // this.spriteName = spriteName;
        this.directionSprite = Map.of(
            "DOWN",  16 + 16 * frames * 0,
            "LEFT",  16 + 16 * frames * 1,
            "RIGHT", 16 + 16 * frames * 2,
            "UP",    16 + 16 * frames * 3
        );
        
        // Load the sprite image
        Image image = new Image(getClass().getResourceAsStream("resources/sprites/" + spriteName + ".png"));
        this.EntitySprite = new ImageView(image);
        
        // Set the default sprite for the entity
        EntitySprite.setViewport(new Rectangle2D(directionSprite.get("DOWN") + 16, 24 * row, 16, 24)); 

        // Bind the ImageView's position to the model's position
        EntitySprite.layoutXProperty().bind(model.xProperty());
        EntitySprite.layoutYProperty().bind(model.yProperty());
    }

    public ImageView getEntitySprite() {
        return EntitySprite;
    }

    public void startWalking(String direction) {
        if (lastDirection != direction) {
            lastDirection = direction;
            EntitySprite.setViewport(new Rectangle2D(directionSprite.get(direction) + 16, 24 * row, 16, 24)); // Iniziliazza la direzione della view
            // Stop the current animation if it's running
            if (walkAnimation != null && walkAnimation.getStatus() == Animation.Status.RUNNING) {
                walkAnimation.stop();
            }
            double animationFrameTime = 0.2 / model.velocityProperty().get();
            walkAnimation = new Timeline();
            IntStream.range( 0, frames).forEach(i -> {
                walkAnimation.getKeyFrames().add(
                    new KeyFrame(Duration.seconds(animationFrameTime * i), e -> EntitySprite.setViewport(new Rectangle2D(directionSprite.get(direction) + 16 * i, 24 * row, 16, 24)))
                );
            });
            walkAnimation.setAutoReverse(autoReverse);
            walkAnimation.setCycleCount(Animation.INDEFINITE);
            walkAnimation.play();
        }
        if (walkAnimation.getStatus() !=  Animation.Status.RUNNING){
            walkAnimation.play();
        }
    }

    public void stopWalking() {
        if (walkAnimation != null) {
            walkAnimation.stop();
        }
        if (lastDirection != null) {
            EntitySprite.setViewport(new Rectangle2D(directionSprite.get(lastDirection) + 16, 24 * row, 16, 24));
        }
    }

    public void setFrames(int frames) {
        this.frames = frames;
    }

    public void update(double elapsed) {
        if (model.isDead()) {
            EntitySprite.setVisible(false);
            return;
        }
        if (model.isMoving()) {
            startWalking(model.getLastDirectionString());
        }
        else {
            stopWalking();
        }
    }
}
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import javafx.animation.AnimationTimer;

public class GameApp extends Application {
    private int avatar;
    private PlayerData data;
    private StageView stageView;
    private PlayerController playerController;
    private BombController bombController;
    private PlayerModel playerModel;
    private EnemiesController enemiesController;

    public Void initializeGame(PlayerData data) {
        this.avatar = Integer.parseInt(data.getAvatar()) - 1;
        this.data = data;
        System.out.println("Game initialized with player data: " + data.getDataString());
        return null;
    }

    @Override
    public void start(Stage primaryStage) {        
        // Setup the game with the provided player data
        setupGame(primaryStage, data.getLastLevelInt());

        // Create and start the game loop using AnimationTimer
        AnimationTimer gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // Check for player death
                if (playerModel.isDead()) {
                    savePlayerData();
                    System.out.println("Game Over");
                    stop();
                }
                 // Check for level completion
                 if (enemiesController.getEnemies().isEmpty() && playerModel.isOnNextLevelDoor()) {
                    savePlayerData(); // Save current game state
                    setupGame(primaryStage, data.getLastLevelInt() + 1); // Setup game for next level
                    data.setLastLevel(Integer.toString(data.getLastLevelInt() + 1));
                }
                enemiesController.update(1.0 / 60.0);
                playerController.update(1.0 / 60.0);
                bombController.update(1.0 / 60.0);
                stageView.updateView();
            }
        };
        gameLoop.start();
    }

    private void setupGame(Stage primaryStage, int level) {
        // Use a StackPane as the root to allow layering of the map and the player
        StackPane root = new StackPane();
        Pane bombLayer = new Pane();
        Pane gameLayer = new Pane();
        BorderPane borderPane = new BorderPane();

        // Initialize the Stage
        StageModel stageModel = new StageModel();
        StageView stageView = new StageView(level, stageModel);
        this.stageView = stageView;

        Scene mainScene = new Scene(borderPane, 272, 232);
        primaryStage.setTitle("JBomberman");
        primaryStage.setScene(mainScene);
        primaryStage.show();

        // initialize playerModel, view, and controller
        if (this.playerModel == null) {
            stageModel.setPlayer(playerModel);
            this.playerModel = new PlayerModel(32, 6, 1.3, stageModel);
        }
        else {
            this.playerModel.setPosition(32, 6);
            this.playerModel.setStage(stageModel);
        }
        EntityView playerView = new EntityView(playerModel, "bomberman", true, 3, avatar);
        int numberOfEnemies = 1 + level * 2;
        this.enemiesController = new EnemiesController(numberOfEnemies, stageModel, gameLayer, level);

        // Layer the map and the player on the StackPane
        root.getChildren().add(stageView.getPane()); // Map as the base layer
        gameLayer.getChildren().add(playerView.getEntitySprite()); // Add Bomberman on top of the map
        root.getChildren().add(bombLayer); // Add the bomb layer to the root
        root.getChildren().add(gameLayer); // Add the game layer to the root

        // For the HUD, use a BorderPane as the outer container
        HUDView hudView = new HUDView(playerModel);
        borderPane.setCenter(root); // Set the game (map + player) as the center
        borderPane.setTop(hudView.getHudPane()); // Set the HUD at the top

        // Setup the controller with the scene
        this.playerController = new PlayerController(playerModel, playerView);
        this.bombController = new BombController(playerModel, bombLayer);
        new InputController(playerController, bombController, mainScene);
    }


    public void savePlayerData() {
        try {
            // Get the path to the JAR file
            String jarPath = new File(GameApp.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
            // Get the directory of the JAR file
            String dirPath = new File(jarPath).getParent();
            // Construct the path to the players.txt file in the same directory
            File file = new File(dirPath, "savedGames.txt");
            // Prepare data to save
            String dataToSave = data.getDataString();
            // Check if data for the player already exists and needs to be updated, or append new data
            List<String> lines = file.exists() ? Files.readAllLines(file.toPath()) : new ArrayList<>();
            boolean dataExists = false;
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                String nickname = line.split(",")[0];
                if (nickname.equals(data.getNickname())) {
                    lines.set(i, dataToSave); // Update existing data
                    dataExists = true;
                    break;
                }
            }
            if (!dataExists) {
                lines.add(dataToSave); // Append new player data
            }
            // Write data to the file
            Files.write(file.toPath(), lines);
        } catch (URISyntaxException e) {
            System.err.println("Error parsing URI: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("IO error occurred: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
        }
    }


}
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;

public class HUDView {
    private HBox hudPane;
    private Label scoreLabel;
    private Label livesLabel;
    private Label bombCapacityLabel;
    private Label bombRadiusLabel;

    public HUDView(PlayerModel playerModel) {
        hudPane = new HBox(10);
        hudPane.setAlignment(Pos.TOP_CENTER);
        
        scoreLabel = new Label();
        livesLabel = new Label();
        bombCapacityLabel = new Label();
        bombRadiusLabel = new Label();
        
        scoreLabel.setFont(new Font("Pixelify Sans Regular", 14));
        livesLabel.setFont(new Font("Pixelify Sans Regular", 14));
        bombCapacityLabel.setFont(new Font("Pixelify Sans Regular", 14));
        bombRadiusLabel.setFont(new Font("Pixelify Sans Regular", 14));

        scoreLabel.textProperty().bind(playerModel.scoreProperty().asString("Score: %d"));
        livesLabel.textProperty().bind(playerModel.lifeProperty().asString("life: %d"));
        bombCapacityLabel.textProperty().bind(playerModel.bombCapacityProperty().asString("Bombs: %d"));
        bombRadiusLabel.textProperty().bind(playerModel.bombRadiusProperty().asString("Radius: %d"));

        hudPane.getChildren().addAll(scoreLabel, livesLabel, bombCapacityLabel, bombRadiusLabel);
    }

    public HBox getHudPane() {
        return hudPane;
    }
}
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;

public class InputController {
    private final Set<KeyCode> keysPressed = ConcurrentHashMap.newKeySet(); // per far muovere il player appena si preme un tasto
    private Scene scene;
    private PlayerController player;
    private BombController bomb;

    public InputController(PlayerController player, BombController bomb,  Scene scene) {
        this.player = player;
        this.bomb = bomb;
        this.scene = scene;
        attachEventListeners();
    }

    private void attachEventListeners() {
        scene.setOnKeyPressed(event -> {
            KeyCode code = event.getCode();
            if (!keysPressed.contains(code)) { // Check to prevent repeated calls for the same key press
                keysPressed.add(code);
                pressedController(); // Move as soon as the key is pressed
            }
        });
        scene.setOnKeyReleased(event -> {
            keysPressed.remove(event.getCode());
            releaseController();
        });
    }
    
    private void pressedController() {
        if (keysPressed.contains(KeyCode.SPACE)) {
            bomb.input();
        } 
        if (keysPressed.contains(KeyCode.UP)) {
            player.input("UP");
        } else if (keysPressed.contains(KeyCode.DOWN)) {
            player.input("DOWN");
        } else if (keysPressed.contains(KeyCode.LEFT)) {
            player.input("LEFT");
        } else if (keysPressed.contains(KeyCode.RIGHT)) {
            player.input("RIGHT");
        } else {
            player.input(null);
        }
    }
    
    public void releaseController() {
        if (keysPressed.contains(KeyCode.UP)) {
            player.input("UP");
        } else if (keysPressed.contains(KeyCode.DOWN)) {
            player.input("DOWN");
        } else if (keysPressed.contains(KeyCode.LEFT)) {
            player.input("LEFT");
        } else if (keysPressed.contains(KeyCode.RIGHT)) {
            player.input("RIGHT");
        } else {
            player.input(null);
        }
    }
}
public class PlayerController {
    private PlayerModel model;
    private EntityView view;

    public PlayerController(PlayerModel model, EntityView view) {
        this.model = model;
        this.view = view;
    }

    public void input(String directionString) {    
        if (directionString != null) {
            model.startMoving(directionString);
        }
        else {
            model.stopMoving();
        }
    }
    
    public PlayerModel getModel() {
        return model;
    }

    public void update(double elapsed) {
        model.update(elapsed);
        view.update(elapsed);
    }

}
public class PlayerData {
    private String nickname;
    private String avatar;
    private String lastLevel;
    private String playedGames;
    private String winGames;
    private String lostGames;
    private String score;

    public PlayerData(String nickname, String avatar, String lastLevel, String playedGames, String winGames, String lostGames, String score) {
        if (nickname == "") nickname = "Default";
        this.nickname = nickname;
        this.avatar = avatar;
        this.lastLevel = lastLevel;
        this.playedGames = playedGames;
        this.winGames = winGames;
        this.lostGames = lostGames;
        this.score = score;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getLastLevel() {
        return lastLevel;
    }

    public int getLastLevelInt() {
        return Integer.parseInt(lastLevel);
    }

    public void setLastLevel(String lastLevel) {
        this.lastLevel = lastLevel;
    }

    public String getPlayedGames() {
        return playedGames;
    }

    public void setPlayedGames(String playedGames) {
        this.playedGames = playedGames;
    }

    public String getWinGames() {
        return winGames;
    }

    public void setWinGames(String winGames) {
        this.winGames = winGames;
    }

    public String getLostGames() {
        return lostGames;
    }

    public void setLostGames(String lostGames) {
        this.lostGames = lostGames;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getDataString() {
        return nickname + "," + avatar + "," + lastLevel + "," + playedGames + "," + winGames + "," + lostGames + "," + score + "\n";
    }


}
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

    public boolean isOnNextLevelDoor() {
        for (EmptyTile tile : occupiedTiles) {
            if (tile instanceof SpecialTile && ((SpecialTile) tile).getType() == SpecialTileType.nextLevelDoor) {
                return true;
            }
        }
        return false;
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

public class PowerUp extends SpecialTile{
    private boolean applied = false;
    private PowerUpBehaviour behaviour;

    public PowerUp(int x, int y, SpecialTileType type) {
        super(x, y, type);
        if (type == SpecialTileType.pupBlast) {
            this.behaviour = new PowerUpBlast();
        }
        else if (type == SpecialTileType.pupBomb) {
            this.behaviour = new PowerUpBomb();
        }
        else if (type == SpecialTileType.pupSpeed) {
            this.behaviour = new PowerUpSpeed();
        }
    }

    @Override
    public void setOccupant(EntityModel occupant) {
        super.setOccupant(occupant);
        if (applied) return;
        if (occupant instanceof PlayerModel) {
            applyPowerUp((PlayerModel) occupant);
        }
        setDisplayable(false);
        applied = true;
    }

    @Override
    public boolean isDetonable() {
        return true;
    }

    public void applyPowerUp(PlayerModel playerModel) {
        behaviour.applyPowerUp(playerModel);
    };
}
public interface PowerUpBehaviour {
    void applyPowerUp(PlayerModel playerModel);
}public class PowerUpBlast implements PowerUpBehaviour{

    @Override
    public void applyPowerUp(PlayerModel playerModel){
        playerModel.increaseBombRadius();
    }

}
public class PowerUpBomb implements PowerUpBehaviour{
    
    @Override
    public void applyPowerUp(PlayerModel playerModel){
        playerModel.increaseBombCapacity();
    }
}
public class PowerUpSpeed implements PowerUpBehaviour{
    
    @Override
    public void applyPowerUp(PlayerModel playerModel){
        playerModel.increaseSpeed();
    }

}
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PreGameSetup extends Application {
    private Map<String, PlayerData> playerDataMap = new HashMap<>();
    private ImageView avatarPreview = new ImageView();

    @Override
    public void start(Stage primaryStage) throws Exception {
        readPlayerData();

        TextField nicknameField = new TextField();
        ComboBox<String> avatarComboBox = new ComboBox<>();
        avatarComboBox.getItems().addAll("1", "2", "3", "4"); // Assuming these are your avatar options
        avatarComboBox.setValue("1"); // Default to the first avatar
        updateAvatarPreview("1"); // Update the avatar preview based on the default value (1)
        avatarComboBox.setOnAction(e -> updateAvatarPreview(avatarComboBox.getValue()));

        Button startGameButton = new Button("Start Game");
        startGameButton.setOnAction(e -> {
            String nickname = nicknameField.getText();
            String avatar = avatarComboBox.getValue();
            // Make sure to handle the case where the nickname or avatar is not selected properly
            PlayerData data = playerDataMap.getOrDefault(nickname, new PlayerData(nickname, avatar, "1", "0", "0", "0", "0")); 
            try {
                GameApp gameApp = new GameApp();
                gameApp.initializeGame(data); // Adjust GameApp to accept these parameters
                gameApp.start(new Stage()); // This may need adjustment based on your GameApp class
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            primaryStage.close(); // Close the setup window
        });

        VBox layout = new VBox(10, new Label("Nickname:"), nicknameField, new Label("Choose Avatar:"), avatarComboBox, avatarPreview, startGameButton);
        Scene scene = new Scene(layout, 400, 400);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void readPlayerData() {
        // List<PlayerData> playerDataList = new ArrayList<>();
        try {
            // Get the path to the JAR file
            String jarPath = new File(PreGameSetup.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
            // Get the directory of the JAR file
            String dirPath = new File(jarPath).getParent();
            // Construct the path to the players.txt file in the same directory
            File file = new File(dirPath, "savedGames.txt");

            if (file.exists()) {
                // Read all lines from the players.txt file
                List<String> lines = Files.readAllLines(file.toPath());
                // Parse each line into PlayerData objects
                for (String line : lines) {
                    String[] parts = line.split(",");
                    if (parts.length == 6) {
                        String nickname = parts[0];
                        String avatarNumber = parts[1];
                        String lastLevel = parts[2];
                        String playedGames = parts[3];
                        String winGames = parts[4];
                        String lostGames = parts[5];
                        String score = parts[6];
                        PlayerData playerData = new PlayerData(nickname, avatarNumber, lastLevel, playedGames, winGames, lostGames, score);
                        playerDataMap.put(nickname, playerData);
                    }
                }
            }
        } catch (URISyntaxException e) {
            System.err.println("Error parsing URI: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("IO error occurred: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
        }
    }

    private void updateAvatarPreview(String avatarNumber) {
        Image avatarImage = new Image(getClass().getResourceAsStream("resources/sprites/bomberman.png"));
        int avatarIndex = Integer.parseInt(avatarNumber) - 1; // Assuming avatarNumber starts at 1
        avatarPreview.setImage(avatarImage); // avatarPreview should be an ImageView
        avatarPreview.setViewport(new Rectangle2D(0, 24 * avatarIndex, 47, 24)); // Update this to match your sprite sheet
    }

    public static void main(String[] args) {
        launch(args);
    }
}
public class SpecialTile extends EmptyTile{
    protected SpecialTileType type;

    public SpecialTile(int x, int y, SpecialTileType type) {
        super(x, y);
        setType(type);
        setDisplayable(true);
    }

    public SpecialTileType getType() {
        return type;
    }

    public void setType(SpecialTileType type) {
        this.type = type;
    }
}
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

enum SpecialTileType {
  pupBlast,
  pupBomb,
  pupSpeed,
  nextLevelDoor;

  public static SpecialTileType getRandomPowerUpType() {
      // Convert array to a modifiable list
      List<SpecialTileType> values = new ArrayList<>(Arrays.asList(values()));
      values.remove(SpecialTileType.nextLevelDoor); // Now this operation is supported
      int size = values.size();
      Random random = new Random();
      return values.get(random.nextInt(size));
  }

}import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StageModel {
    private final int width = 17;
    private final int height = 13;
    private double powerUpProbability = 0.8; // 10% chance of adding a PowerUp tile
    private final int tileSize = 16; // Assuming each tile is 16x16 pixels
    final int freeSlots = 110; // Number of free slots in the stage
    private final int destructibleTilesStart;
    private int destructedTiles = 0;
    private final List<int[]> freeTileIndex = new ArrayList<>();
    private Tile[][] tiles = new Tile[width][height];
    private Random rand = new Random();
    private PlayerModel player;
    private int damage = 100;
    private SpecialTile nextLevelDoor;
    

    public StageModel() {
        this(0.3, 0.05); // Default destructible and non-destructible percentages
    }

    public StageModel(double destructiblePercentage, double nonDestructiblePercentage) {
        // Calculate the total number of free positions
        int destructibleTilesCount = (int) (freeSlots * destructiblePercentage);
        destructibleTilesStart = destructibleTilesCount;
        int nonDestructibleTilesCount = (int) ((freeSlots-destructibleTilesCount) * nonDestructiblePercentage);

        // Fill the stage with non-walkable borders and predefined tiles
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (x < 2 || x > width - 3 || y == 0 || y == height - 1 || (x % 2 == 1 && y % 2 == 0)) {
                    tiles[x][y] = new Tile(x * tileSize, y * tileSize, false, false); // Non-destructible and not displayable
                }
            }
        }
        // lascia la posizione in alto a sinsitra per il giocatore
        tiles[2][1] = new EmptyTile(2, 1);
        tiles[2][2] = new EmptyTile(2, 2);
        tiles[3][1] = new EmptyTile(3, 1);
        for (int x = 2; x < width - 2; x++) {
            for (int y = 1; y < height - 1; y++) {
                // lascia le posizioni nell'angolo in alto a sinistra libere per far muovere il giocatore
                if (tiles[x][y] == null && !(x == 2 && y == 1) && !(x == 2 && y == 2) && !(x == 3 && y == 1)) {
                    freeTileIndex.add(new int[] {x, y});
                }   
            }
        }
    
        // Randomly place destructible and non-destructible tiles
        for (int i = 0; i < destructibleTilesCount; i++) {
            int[] position = freeTileIndex.remove(rand.nextInt(freeTileIndex.size()));
            tiles[position[0]][position[1]] = new Tile(position[0] * tileSize, position[1] * tileSize, true);
        }
        for (int i = 0; i < nonDestructibleTilesCount; i++) {
            int[] position = freeTileIndex.remove(rand.nextInt(freeTileIndex.size()));
            tiles[position[0]][position[1]] = new Tile(position[0] * tileSize, position[1] * tileSize, false);
        }

        // in all the other freeTileIndex, add empty tiles
        for (int[] position : freeTileIndex) {
            tiles[position[0]][position[1]] = new EmptyTile(position[0], position[1]);
        }
    }

    public void setPlayer(PlayerModel player) {
        this.player = player;
    }

    public Tile getTile(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            return tiles[x][y];
        }
        return null; // Out of bounds
    }

    public EmptyTile getEmptyTile(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            if (tiles[x][y] instanceof EmptyTile)
            return (EmptyTile) tiles[x][y];
        }
        return new EmptyTile(x, y); // return a dummy empty tile
    }

    public Tile getTileAtPosition(int x, int y) {
        int tileX = (int) (x / tileSize);
        int tileY = (int) (y / tileSize);
        return getTile(tileX, tileY);
    }

    public EmptyTile getEmptyTileAtPosition(int x, int y) {
        int tileX = (int) (x / tileSize);
        int tileY = (int) (y / tileSize);
        return getEmptyTile(tileX, tileY);
    }

    public List<int[]> getFreeTileIndex() {
        return freeTileIndex;
    }

    public SpecialTile getNextLevelDoor() {
        return nextLevelDoor;
    }

    public void setTile(int x, int y, Tile tile) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            tiles[x][y] = tile;
        }
    }

    public void setTileAtPosition(int x, int y, Tile tile) {
        int tileX = (int) (x / tileSize);
        int tileY = (int) (y / tileSize);
        setTile(tileX, tileY, tile);
    }

    public BombModel getBombAtPosition(int x, int y) {
        int tileX = (int) (x / tileSize);
        int tileY = (int) (y / tileSize);
        if (getTile(tileX, tileY) instanceof BombModel) {
            return (BombModel) getTile(tileX, tileY);
        }
        return null;
    }

    public int[] getTileStartCoordinates(int x, int y) {
        int tileX = (int) (x / tileSize);
        int tileY = (int) (y / tileSize);
        return new int[] {tileX * tileSize, tileY * tileSize};
    }

    public boolean isBorder(int x, int y) {
        return x < 2 * tileSize || x >= (width - 3) * tileSize || y < tileSize || y >= (height - 2) * tileSize;
    }

    public boolean destroyTile(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height && tiles[x][y] != null) {
            // the tile is not destructible
            if (!tiles[x][y].isDestructible()) return true;
            // the tile is empty but occupied
            if ((tiles[x][y] instanceof EmptyTile) && ((EmptyTile) tiles[x][y]).isOccupied()) {
                EmptyTile occupiedTile = (EmptyTile) tiles[x][y];
                EntityModel occupant = occupiedTile.getOccupant();
                occupant.loseLife(damage);
                if (tiles[x][y] instanceof BombModel) {
                    setTile(x, y, new EmptyTile(x, y));
                    ((EmptyTile) tiles[x][y]).setOccupant(occupant);
                }
                if (player != null && !(occupant instanceof PlayerModel)) {
                    player.addScore(damage);
                }
            }
            // the tile is a special tile (but not a next level door)
            else if (tiles[x][y] instanceof PowerUp) {
                setTile(x, y, new EmptyTile(x, y));
            }
            // the tiles is destructible tile, in case add powerup or next level door
            else if (!(tiles[x][y] instanceof EmptyTile)){
                double nextLevelDoorProbability = (double) destructedTiles / destructibleTilesStart;
                if (this.nextLevelDoor == null && rand.nextDouble() < nextLevelDoorProbability) {
                        setTile(x, y, new SpecialTile(x, y, SpecialTileType.nextLevelDoor)); // Create a new instance of NextLevelDoor
                        this.nextLevelDoor = (SpecialTile) tiles[x][y];
                } 
                else if (rand.nextDouble() < powerUpProbability) {
                    setTile(x, y, new PowerUp(x, y, SpecialTileType.getRandomPowerUpType())); // Create a new instance of PowerUp
                }
                else setTile(x, y, new EmptyTile(x, y));
                destructedTiles++;
            }
            // the tile was simply empty
            else {
                setTile(x, y, new EmptyTile(x, y));
            }
            freeTileIndex.add(new int[] {x, y});
            // tile destroyed
            return true;
        }
        // tile out of bounds
        return false;
    }

    public boolean destroyTileAtPosition(int x, int y) {
        int tileX = (int) (x / tileSize);
        int tileY = (int) (y / tileSize);
        return destroyTile(tileX, tileY);
    }

    public boolean canExplodeAtPosition(int x, int y) {
        Tile tile = getTileAtPosition(x, y);
        return tile == null || tile.isDestructible();
    }

    public boolean addBombAtPosition(int x, int y, int bombRadius) {
        int tileX = (int) (x / tileSize);
        int tileY = (int) (y / tileSize);
        Tile tile = tiles[tileX][tileY];
        if (!(tile instanceof EmptyTile) || tile instanceof BombModel || tile == this.nextLevelDoor) {
            return false;
        }
        EntityModel previousOccupant = ((EmptyTile) tiles[tileX][tileY]).getOccupant();
        tiles[tileX][tileY] = new BombModel(tileX * tileSize, tileY * tileSize, bombRadius);
        ((EmptyTile) tiles[tileX][tileY]).setOccupant(previousOccupant);
        return true;
    }

    public int getTileSize() {
        return tileSize;
    }

    // Getters for width and height if needed
    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int[] getRandomFreeTile() {
        if (freeTileIndex.isEmpty()) {
            return null; // No more free tiles available
        }
        int randomIndex = rand.nextInt(freeTileIndex.size());
        return freeTileIndex.remove(randomIndex);
    }

    // Additional methods as needed for game logic
}
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;

public class StageView {
    private Pane pane = new Pane(); // Pane to hold the stage and tiles
    private Image stageImage; // Background image of the stage
    private Image tilesImage; // Image containing tile sprites
    private StageModel stage;
    private ImageView combinedView;

    public StageView(int levelNumber, StageModel stage) {
        this.stage = stage;
        // Load stage and tiles images based on the level number
        stageImage = new Image(getClass().getResourceAsStream("resources/sprites/level" + levelNumber + "_stage.png"));
        tilesImage = new Image(getClass().getResourceAsStream("resources/sprites/level" + levelNumber + "_tiles.png"));
        combinedView = new ImageView();
        pane.getChildren().add(combinedView); // Add the combined image view to the pane
        updateView(); // Initial update
    }

    public void updateView() {
        int tileSize = stage.getTileSize();
        int width = stage.getWidth() * tileSize;
        int height = stage.getHeight() * tileSize;
        WritableImage combinedImage = new WritableImage(width, height);
        PixelWriter writer = combinedImage.getPixelWriter();

        // Copy the stage background to the combined image
        PixelReader stageReader = stageImage.getPixelReader();
        writer.setPixels(0, 0, width, height, stageReader, 0, 0);

        // Iterate over the tiles and add them to the combined image
        PixelReader tilesReader = tilesImage.getPixelReader();
        for (int x = 0; x < stage.getWidth(); x++) {
            for (int y = 0; y < stage.getHeight(); y++) {
                Tile tile = stage.getTile(x, y);
                if (tile.isDisplayable()) {
                    if (tile instanceof SpecialTile) {
                        PixelReader powerUpReader = new Image(getClass().getResourceAsStream("resources/sprites/" + ((SpecialTile) tile).getType().toString() + ".png")).getPixelReader();
                        writer.setPixels(x * tileSize, y * tileSize, tileSize, tileSize, powerUpReader, 0, 0);
                    }
                    else {
                        int srcX = tile.isDestructible() ? 16 : 0;
                        writer.setPixels(x * tileSize, y * tileSize, tileSize, tileSize, tilesReader, srcX, 0);
                    }
                }
                else if (tile instanceof EmptyTile && !stage.getTile(x, y - 1).isDestructible()){
                    // if the tile is not displayable, but the tile above it is not destructible, display the shadow tile
                    writer.setPixels(x * tileSize, y * tileSize, tileSize, tileSize, tilesReader, 32, 0);
                }
            }
        }

        combinedView.setImage(combinedImage);
    }

    public Pane getPane() {
        return pane;
    }
}
public class Tile extends XYModel{
    protected boolean isDestructible;
    protected boolean isDisplayable = true;
    protected boolean isWalkable = false;

    public Tile(int x, int y, boolean isDestructible) {
        super(x, y);
        this.isDestructible = isDestructible;
    }

    public Tile(int x, int y, boolean isDestructible, boolean isDisplayable) {
        super(x, y);
        this.isDestructible = isDestructible;
        this.isDisplayable = isDisplayable;
    }

    public Tile(int x, int y, boolean isDestructible, boolean isDisplayable, boolean isWalkable) {
        super(x, y);
        this.isDestructible = isDestructible;
        this.isDisplayable = isDisplayable;
        this.isWalkable = isWalkable;
    }

    // ignora le tile che non sono mostrate
    public boolean isDestructible() {
        return isDestructible;
    }

    public boolean isWalkable() {
        return isWalkable;
    }

    public boolean isDetonable() {
        return isDestructible && isDisplayable;
    }

    public void setWalkable(boolean walkable) {
        isWalkable = walkable;
    }

    public boolean isDisplayable() {
        return isDisplayable;
    }

    public void setDisplayable(boolean displayable) {
        isDisplayable = displayable;
    }

    public void update() {
    }; // Implement according to your rendering logic
}
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * This class represents the abstract model for any entity in the game.
 * It includes common properties like position, which can be observed
 * for changes using JavaFX's property system.
 */
public abstract class XYModel {

    // Properties
    private final IntegerProperty x = new SimpleIntegerProperty();
    private final IntegerProperty y = new SimpleIntegerProperty();

    public XYModel() {
        this(0, 0);
    }

    public XYModel(int x, int y) {
        this.x.set(x);
        this.y.set(y);
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

    public int getX() {
        return x.get();
    }

    public int getY() {
        return y.get();
    }   

    public void setX(int x) {
        this.x.set(x);
    }

    public void setY(int y) {
        this.y.set(y);
    }

    public void setPosition(int x, int y) {
        this.x.set(x);
        this.y.set(y);
    }
}