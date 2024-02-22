import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Rectangle2D;

public class BombView {
    private ImageView bombSprite;
    private Timeline bombAnimation = null;
    private Pane pane;

    public BombView(BombModel bombModel, Pane pane) {
        this.pane = pane;
        // Assuming the sprite sheet is in the same package as the BombView
        Image image = new Image(getClass().getResourceAsStream("resources/sprites/bomb.png"));
        bombSprite = new ImageView(image);
        // Set the initial viewport to show the first sprite
        bombSprite.setViewport(new Rectangle2D(0, 0, 16, 16));
        // Bind the ImageView's position to the model's position
        bombSprite.layoutXProperty().bind(bombModel.xProperty());
        bombSprite.layoutYProperty().bind(bombModel.yProperty());

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

    public ImageView getbombSprite() {
        return bombSprite;
    }

    public void addToPane() {
        pane.getChildren().add(getbombSprite());
    }

    public void removeFromPane() {
        pane.getChildren().remove(bombSprite);
    }
}
