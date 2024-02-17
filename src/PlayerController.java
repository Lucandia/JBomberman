import javafx.scene.Scene;
import javafx.scene.input.KeyCode;

public class PlayerController {
    private PlayerModel model;
    private Scene scene;

    public PlayerController(PlayerModel model, Scene scene) {
        this.model = model;
        this.scene = scene;
        attachEventListeners();
    }

    private void attachEventListeners() {
        scene.setOnKeyPressed(event -> {
            int xMove = 0;
            int yMove = 0;
            if (event.getCode() == KeyCode.UP)    yMove = -10;
            if (event.getCode() == KeyCode.DOWN)  yMove = +10;
            if (event.getCode() == KeyCode.RIGHT) xMove = +10;
            if (event.getCode() == KeyCode.LEFT)  xMove = -10;
            // Move player up
            ObservablePoint2D newPosition = model.positionProperty().get().add(xMove, yMove);
            model.positionProperty().set(newPosition);
        });
    }
}
