import javafx.scene.Scene;
import javafx.scene.input.KeyCode;

public class PlayerController {
    private PlayerModel model;
    private PlayerView view;
    private Scene scene;

    public PlayerController(PlayerModel model, PlayerView view,  Scene scene) {
        this.model = model;
        this.scene = scene;
        this.view = view;
        attachEventListeners();
    }

    private void attachEventListeners() {
        scene.setOnKeyPressed(event -> {
            int moveAmount = 2;
            KeyCode code = event.getCode();
            switch (code) {
                case UP:
                    view.startWalking("UP");
                    model.move(0, -moveAmount); 
                    break;
                case DOWN:
                   view.startWalking("DOWN");
                    model.move(0, moveAmount); 
                    break;
                case LEFT:
                  view.startWalking("LEFT");
                    model.move(-moveAmount, 0); 
                    break;
                case RIGHT:
                    view.startWalking("RIGHT");
                    model.move(moveAmount, 0); 
                    break;
                case DIGIT1:
                    model.velocityProperty().set(model.velocityProperty().get() - 0.1);
                    break;
                case DIGIT2:
                    model.velocityProperty().set(model.velocityProperty().get() + 0.1);
                    break;
                default:
                    view.stopWalking(); // This will stop any walking animation if a key is pressed that is not for movement
                    break;
            }
        });
        scene.setOnKeyReleased(event -> view.stopWalking());
    }
}
