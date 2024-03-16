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
