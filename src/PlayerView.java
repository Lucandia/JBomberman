import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.geometry.Rectangle2D;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import java.util.Map;


public class PlayerView {
    private final ImageView playerSprite;
    private PlayerModel model;
    private String lastDirection;
    // Create a fixed HashMap with keys and values
    private static final Map<String, Integer> directionSprite = Map.of("DOWN", 64, "LEFT", 112, "RIGHT", 160, "UP", 208);
    private Timeline walkAnimation = null;
    
    public PlayerView(PlayerModel model) {
        this.model = model;
        
        // Load the sprite image
        Image image = new Image(getClass().getResourceAsStream("resources/sprites/bomberman.png"));
        this.playerSprite = new ImageView(image);
        
        // Set the viewport to show only the part of the image you need
        playerSprite.setViewport(new Rectangle2D(64, 0, 15, 24)); // Adjust x, y, width, and height accordingly

        // Bind the ImageView's position to the model's position
        playerSprite.layoutXProperty().bind(model.xProperty());
        playerSprite.layoutYProperty().bind(model.yProperty());
    }

    public ImageView getPlayerSprite() {
        return playerSprite;
    }

    public void startWalking(String direction) {
        if (lastDirection != direction) {
            lastDirection = direction;
            playerSprite.setViewport(new Rectangle2D(directionSprite.get(direction), 0, 15, 24)); // Iniziliazza la direzione del bomberman
            // Stop the current animation if it's running
            if (walkAnimation != null && walkAnimation.getStatus() == Animation.Status.RUNNING) {
                walkAnimation.stop();
            }
            double animationFrameTime = 0.2 / model.velocityProperty().get();
            walkAnimation = new Timeline(
                new KeyFrame(Duration.seconds(animationFrameTime), e -> playerSprite.setViewport(new Rectangle2D(directionSprite.get(direction) - 16, 0, 15, 24))),
                new KeyFrame(Duration.seconds(2 * animationFrameTime), e -> playerSprite.setViewport(new Rectangle2D(directionSprite.get(direction), 0, 15, 24))),
                new KeyFrame(Duration.seconds(3 * animationFrameTime), e -> playerSprite.setViewport(new Rectangle2D(directionSprite.get(direction) + 16, 0, 15, 24))),
                new KeyFrame(Duration.seconds(4 * animationFrameTime), e -> playerSprite.setViewport(new Rectangle2D(directionSprite.get(direction), 0, 15, 24)))
            );
            walkAnimation.setCycleCount(Animation.INDEFINITE);
            walkAnimation.play();
        }
        walkAnimation.play();
    }

    public void stopWalking() {
        if (walkAnimation != null && walkAnimation.getStatus() == Animation.Status.RUNNING) {
            walkAnimation.stop();
        }
        playerSprite.setViewport(new Rectangle2D(directionSprite.get(lastDirection), 0, 15, 24));
    }
}
