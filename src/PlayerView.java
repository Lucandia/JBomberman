import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class PlayerView {
    private Rectangle playerSprite;
    private PlayerModel model;

    public PlayerView(PlayerModel model, Pane gamePane) {
        this.model = model;
        this.playerSprite = new Rectangle(10, 10, Color.BLUE); // A simple square to represent the player
        gamePane.getChildren().add(playerSprite);

        // Correctly bind the player's view position to the model's position
        // Listen for changes in the position property itself
        model.positionProperty().addListener((obs, oldPosition, newPosition) -> {
            playerSprite.xProperty().bind(newPosition.xProperty());
            playerSprite.yProperty().bind(newPosition.yProperty());
        });

        // Initialize sprite position
        updateView();
    }

    private void updateView() {
        // This method is now primarily for initial setup
        // The listener in the constructor handles dynamic updates
        playerSprite.setX(model.getPosition().getX());
        playerSprite.setY(model.getPosition().getY());
    }
}
