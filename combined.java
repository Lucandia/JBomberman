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
            System.out.println("Max bomb" + maxBombs.get());
            System.out.println("Current bomb" + bombMap.size());
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
    
        // Create and play the explosion animation
        Timeline explosionAnimation = new Timeline();
        for (int frameIndex = 0; frameIndex < 5; frameIndex++) { // For each frame of the explosion
            final int index = frameIndex;
            explosionAnimation.getKeyFrames().add(new KeyFrame(Duration.seconds(index * 0.1), e -> {
                for (ImageView sprite : explosionSprites) {
                    Rectangle2D viewport = sprite.getViewport();
                    sprite.setViewport(new Rectangle2D(viewport.getMinX() + 16 * 5 * index, viewport.getMinY(), size, size));
                }
            }));
        }
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

    public EnemiesController(int numberOfEnemies, String enemyType, StageModel stageModel, Pane gameLayer) {
        List<int[]> freeTileIndex = stageModel.getFreeTileIndex();
        Random random = new Random();
        for (int i = 0; i < numberOfEnemies; i++) {
            int randomIndex = random.nextInt(freeTileIndex.size());
            int[] tileIndex = freeTileIndex.get(randomIndex);
            EnemyModel enemyModel = new EnemyModel(tileIndex[0] * stageModel.getTileSize(), tileIndex[1] * stageModel.getTileSize() - 10, 0.8, new int[] {15, 15}, new int[] {8, 17}, stageModel, Integer.parseInt(enemyType) * 100);
            if (stageModel.getTile(tileIndex[0], tileIndex[1] - 1) instanceof EmptyTile || stageModel.getTile(tileIndex[0], tileIndex[1] + 1) instanceof EmptyTile) {
                enemyModel.startMoving("UP");
            } else {
                enemyModel.startMoving("RIGHT");
            }
            EntityView enemyView = new EntityView(enemyModel, "enemy" + enemyType, 4);
            gameLayer.getChildren().add(enemyView.getEntitySprite());
            addEnemy(enemyModel, enemyView);
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

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * This class represents the abstract model for any entity in the game.
 * It includes common properties like position, which can be observed
 * for changes using JavaFX's property system.
 */
public abstract class EntityModel extends XYModel{

    // Properties
    private final IntegerProperty life = new SimpleIntegerProperty(100);
    protected final DoubleProperty velocity = new SimpleDoubleProperty();
    protected double timeSinceLastMove = 0.0;
    protected final double delayMove = 0.05; // Time in seconds between moves
    protected final int[] boundingBox = {0, 0};
    protected final int[] boundingOffset = {0, 0};
    protected boolean isMoving = false;
    protected int[] lastDirection = {0, 0};
    protected StageModel stage;


    public EntityModel() {
        this(0, 0, 0.0, null);
    }

    public EntityModel(int x, int y, double velocity, StageModel stage) {
        this(0, 0, 0.0, null, null, null);
    }

    /**
     * Constructor for EntityModel with initial position.
     * 
     * @param initialPosition The initial position of the entity on the game board.
     */
    public EntityModel(int x, int y, double velocity, int[] boundingBox, int[] boundingOffset, StageModel stage) {
        super(x, y);
        this.stage = stage;
        this.velocity.set(velocity);
        if (boundingBox!=null && boundingOffset.length == 2){
            this.boundingBox[0] = boundingBox[0];
            this.boundingBox[1] = boundingBox[1];
        }
        if (boundingOffset!=null && boundingOffset.length == 2){
            this.boundingOffset[0] = boundingOffset[0];
            this.boundingOffset[1] = boundingOffset[1];
        }
        stage.getEmptyTileAtPosition(centerOfMass()[0], centerOfMass()[1]).setOccupant(this);
    }

    public void loseLife() {
        this.life.set(this.life.get() - 100);
    }

    public void loseLife(int amount) {
        this.life.set(this.life.get() - amount);
    }

    public boolean isDead() {
        return this.life.get() <= 0;
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

    /**
     * Sets the position of the entity using an ObservablePoint2D object.
     * 
     * @param position The new position of the entity.
     */
    public void move(int dx, int dy) {
        EmptyTile oldTile = stage.getEmptyTileAtPosition(centerOfMass()[0], centerOfMass()[1]);
        int x_move = (int) Math.round(this.velocityProperty().get() * Double.valueOf(dx)); // explicit cast to int
        int y_move = (int) Math.round(this.velocityProperty().get() * Double.valueOf(dy)); // explicit cast to int
        if (canMoveTo(dx, dy) && (checkCollision(dx, dy) == null || checkCollision(dx, dy) == this)) {
            xProperty().set(getX() + x_move);
            yProperty().set(getY() + y_move);
            // Update tiles occupancy
            oldTile.setOccupant(null);
            stage.getEmptyTileAtPosition(centerOfMass()[0], centerOfMass()[1]).setOccupant(this);
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
    private boolean canMoveTo(int dx, int dy) {
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
            stage.getEmptyTileAtPosition(centerOfMass()[0], centerOfMass()[1]).setOccupant(null);
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
    private String spriteName;
    private int frames = 0;
    
    public EntityView(EntityModel model, String spriteName, int frames) {
        this.model = model;
        this.frames = frames;
        this.spriteName = spriteName;
        this.directionSprite = Map.of(
            "DOWN", 16 * frames * 1 + 1,
            "LEFT", 16 * frames * 2 + 1,
            "RIGHT", 16 * frames * 3 + 1,
            "UP", 16 * frames * 4 + 1
        );
        
        // Load the sprite image
        Image image = new Image(getClass().getResourceAsStream("resources/sprites/" + spriteName + ".png"));
        this.EntitySprite = new ImageView(image);
        
        // Set the viewport to show only the part of the image you need
        EntitySprite.setViewport(new Rectangle2D(64, 0, 16, 24)); // Adjust x, y, width, and height accordingly

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
            EntitySprite.setViewport(new Rectangle2D(directionSprite.get(direction), 0, 15, 24)); // Iniziliazza la direzione del bomberman
            // Stop the current animation if it's running
            if (walkAnimation != null && walkAnimation.getStatus() == Animation.Status.RUNNING) {
                walkAnimation.stop();
            }
            double animationFrameTime = 0.2 / model.velocityProperty().get();
            // walkAnimation = new Timeline(
            //     new KeyFrame(Duration.seconds(animationFrameTime), e -> EntitySprite.setViewport(new Rectangle2D(directionSprite.get(direction) - 16, 0, 16, 24))),
            //     new KeyFrame(Duration.seconds(2 * animationFrameTime), e -> EntitySprite.setViewport(new Rectangle2D(directionSprite.get(direction), 0, 16, 24))),
            //     new KeyFrame(Duration.seconds(3 * animationFrameTime), e -> EntitySprite.setViewport(new Rectangle2D(directionSprite.get(direction) + 16, 0, 16, 24))),
            //     new KeyFrame(Duration.seconds(4 * animationFrameTime), e -> EntitySprite.setViewport(new Rectangle2D(directionSprite.get(direction), 0, 16, 24)))
            // );
            walkAnimation = new Timeline();
            IntStream.range(0, frames).forEach(i -> {
                walkAnimation.getKeyFrames().add(
                    new KeyFrame(Duration.seconds(animationFrameTime * (i + 1)), e -> EntitySprite.setViewport(new Rectangle2D(directionSprite.get(direction) + 16 * i, 0, 15, 24)))
                );
            });

            // reverse the animation for the bomberman
            if (spriteName == "bomberman"){
                IntStream.range(-frames+2, 0).forEach(i -> {
                    walkAnimation.getKeyFrames().add(
                        new KeyFrame(Duration.seconds(animationFrameTime * (frames * 2 + i - 1)), e -> EntitySprite.setViewport(new Rectangle2D(directionSprite.get(direction) - 16 * i, 0, 15, 24)))
                    );
                });
            }
            walkAnimation.setCycleCount(Animation.INDEFINITE);
            walkAnimation.play();
        }
        walkAnimation.play();
    }

    public void stopWalking() {
        if (walkAnimation != null) {
            walkAnimation.stop();
        }
        if (lastDirection != null) {
            EntitySprite.setViewport(new Rectangle2D(directionSprite.get(lastDirection) + 16, 0, 15, 24));
        }
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
import javafx.animation.AnimationTimer;

public class GameApp extends Application {

    private PlayerModel playerModel;

    @Override
    public void start(Stage primaryStage) {
                // Use a StackPane as the root to allow layering of the map and the player
        StackPane root = new StackPane();
        Pane bombLayer = new Pane();
        Pane gameLayer = new Pane();

        // Initialize the map and HUD
        // MapModel mapModel = new MapModel(1, 300, 230); // Example dimensions
        StageModel stageModel = new StageModel();
        StageView stageView = new StageView(2, stageModel);
        // HUDView hudView = new HUDView(playerModel);

        // initialize playerModel, view, and controller
        playerModel = new PlayerModel(32, 6, 1.3, stageModel);
        EntityView playerView = new EntityView(playerModel, "bomberman", 3); // Pass a new Pane as the gamePane for player
        int numberOfEnemies = 7;
        String enemyType = "1";
        EnemiesController enemiesController = new EnemiesController(numberOfEnemies, enemyType, stageModel, gameLayer);
        // Layer the map and the player on the StackPane
        root.getChildren().add(stageView.getPane()); // Map as the base layer
        gameLayer.getChildren().add(playerView.getEntitySprite()); // Add Bomberman on top of the map
        root.getChildren().add(bombLayer); // Add the bomb layer to the root
        root.getChildren().add(gameLayer); // Add the game layer to the root

        // For the HUD, use a BorderPane as the outer container
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(root); // Set the game (map + player) as the center
        // borderPane.setTop(hudView.getHudPane()); // Set the HUD at the top

        Scene mainScene = new Scene(borderPane, 272, 208);
        primaryStage.setTitle("JBomberman");
        primaryStage.setScene(mainScene);
        primaryStage.show();

        // Setup the controller with the scene
        PlayerController playerController = new PlayerController(playerModel, playerView);
        BombController bombController = new BombController(playerModel, bombLayer);
        InputController inputController = new InputController(playerController, bombController, mainScene);

        // Create and start the game loop using AnimationTimer
        AnimationTimer gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // Update logic here
                // playerController.update(1.0 / 30.0); // Assuming 60 FPS for calculation
                if (playerModel.isDead()) {
                    System.out.println("Game Over");
                    stop();
                }
                enemiesController.update(1.0 / 60.0);
                playerController.update(1.0 / 60.0);
                bombController.update(1.0 / 60.0);
                stageView.updateView();
            }
        };
        gameLoop.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class HUDView {
    private HBox hudPane = new HBox(10);
    private Text scoreText = new Text();
    private Text livesText = new Text();

    public HUDView(PlayerModel model) {
        hudPane.getChildren().addAll(scoreText, livesText);

        // Bind the HUD elements to the model
        scoreText.textProperty().bind(model.scoreProperty().asString("Score: %d"));
        livesText.textProperty().bind(model.livesProperty().asString("Lives: %d"));
    }

    public HBox getHudPane() {
        return hudPane;
    }

    // Methods to update the view
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
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * PlayerModel represents the state and behavior of a player in the game.
 */
public class PlayerModel extends EntityModel {
    private final IntegerProperty lives = new SimpleIntegerProperty();
    private final IntegerProperty score = new SimpleIntegerProperty();
    private final IntegerProperty bombCapacity = new SimpleIntegerProperty(1); 
    private final IntegerProperty bombRadius = new SimpleIntegerProperty(1);

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
        this.velocity.set(this.velocity.get() + 0.2);
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

public abstract class PowerUp extends EmptyTile{
    private PowerUpType type;
    private boolean applied = false;

    public PowerUp(int x, int y, PowerUpType type) {
        super(x, y);
        this.type = type;
        setDisplayable(true);
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

    public abstract void applyPowerUp(PlayerModel playerModel);
 
    public PowerUpType getType() {
        return type;
    }
}
public class PowerUpBlast extends PowerUp{

    public PowerUpBlast(int x, int y) {
        super(x, y, PowerUpType.blast);
    }

    @Override
    public void applyPowerUp(PlayerModel playerModel){
        playerModel.increaseBombRadius();
    }

}
public class PowerUpBomb extends PowerUp{

    public PowerUpBomb(int x, int y) {
        super(x, y, PowerUpType.bomb);
    }

    @Override
    public void applyPowerUp(PlayerModel playerModel){
        playerModel.increaseBombCapacity();
    }

}
public class PowerUpSpeed extends PowerUp{

    public PowerUpSpeed(int x, int y) {
        super(x, y, PowerUpType.speed);
    }

    @Override
    public void applyPowerUp(PlayerModel playerModel){
        playerModel.increaseSpeed();
    }

}
enum PowerUpType {
  blast,
  bomb,
  speed,
}import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StageModel {
    private final int width = 17;
    private final int height = 13;
    private double powerUpProbability = 0.2; // 10% chance of adding a PowerUp tile
    private final int tileSize = 16; // Assuming each tile is 16x16 pixels
    private final List<int[]> freeTileIndex = new ArrayList<>();
    private Tile[][] tiles = new Tile[width][height];
    private Random rand = new Random();
    

    public StageModel() {
        this(0.3, 0.05); // Default destructible and non-destructible percentages
    }

    public StageModel(double destructiblePercentage, double nonDestructiblePercentage) {
        final int freeSlots = 110; // Number of free slots in the stage
        // Calculate the total number of free positions
        int destructibleTilesCount = (int) (freeSlots * destructiblePercentage);
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
        return new EmptyTile(x, y); // Out of bounds
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
            if (!tiles[x][y].isDestructible()) return true;
            if ((tiles[x][y] instanceof EmptyTile || tiles[x][y] instanceof BombModel) && ((EmptyTile) tiles[x][y]).isOccupied()) {
                EmptyTile occupiedTile = (EmptyTile) tiles[x][y];
                EntityModel occupant = occupiedTile.getOccupant();
                System.out.println(occupant.getLife());
                occupant.loseLife();
            }
            // Randomly add a PowerUp tile
            else if (!(tiles[x][y] instanceof EmptyTile)){
                if (rand.nextDouble() < powerUpProbability) { 
                    setTile(x, y, new PowerUpBlast(x, y));
                }
                else if (rand.nextDouble() < powerUpProbability) { 
                    setTile(x, y, new PowerUpBomb(x, y));
                }
                else if (rand.nextDouble() < powerUpProbability / 2) { 
                    setTile(x, y, new PowerUpSpeed(x, y));
                }
                else setTile(x, y, new EmptyTile(x, y));
            }
            else setTile(x, y, new EmptyTile(x, y));
            freeTileIndex.add(new int[] {x, y});
            return true;
        }
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
        if (!(tiles[tileX][tileY] instanceof EmptyTile)) {
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
                    if (tile instanceof PowerUp) {
                        PixelReader powerUpReader = new Image(getClass().getResourceAsStream("resources/sprites/pup_" + ((PowerUp) tile).getType().toString() + ".png")).getPixelReader();
                        writer.setPixels(x * tileSize, y * tileSize, tileSize, tileSize, powerUpReader, 0, 0);
                    }
                    else {
                        int srcX = tile.isDestructible() ? 17 : 0;
                        writer.setPixels(x * tileSize, y * tileSize, tileSize, tileSize, tilesReader, srcX, 0);
                    }
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
}