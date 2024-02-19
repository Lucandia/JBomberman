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
    private String lastDirection = "DOWN"; // Default direction
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
        playerSprite.xProperty().bind(model.xProperty());
        playerSprite.yProperty().bind(model.yProperty());
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
            walkAnimation = new Timeline(
                new KeyFrame(Duration.seconds(0.3 / model.velocityProperty().get()), e -> playerSprite.setViewport(new Rectangle2D(directionSprite.get(direction) - 16, 0, 15, 24))),
                new KeyFrame(Duration.seconds(2 * 0.3 / model.velocityProperty().get()), e -> playerSprite.setViewport(new Rectangle2D(directionSprite.get(direction) + 16, 0, 15, 24)))
            );
            walkAnimation.setCycleCount(Animation.INDEFINITE);
            walkAnimation.play();
        }
        walkAnimation.play();
    }

    public void stopWalking() {
        walkAnimation.stop();
        playerSprite.setViewport(new Rectangle2D(directionSprite.get(lastDirection), 0, 15, 24));
    }
}

// Sprite position
// (0, 0)
// (16, 0)
// (32, 0)
// (48, 0)
// (64, 0)
// (80, 0)
// (96, 0)
// (112, 0)
// (128, 0)
// (144, 0)
// (160, 0)
// (176, 0)
// (192, 0)
// (208, 0)
// (224, 0)
// (240, 0)
// (256, 0)
// (272, 0)
// (288, 0)
// (304, 0)
// (320, 0)
// (336, 0)
// (352, 0)
// (368, 0)


