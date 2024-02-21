import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;

public class PlayerController {
    private final Set<KeyCode> keysPressed = ConcurrentHashMap.newKeySet(); // per far muovere il player appena si preme un tasto
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
            KeyCode code = event.getCode();
            if (!keysPressed.contains(code)) { // Check to prevent repeated calls for the same key press
                keysPressed.add(code);
                updateMovement(); // Move as soon as the key is pressed
            }
        });
        scene.setOnKeyReleased(event -> {
            keysPressed.remove(event.getCode());
            updateMovement();
        });
    }
    
    private void updateMovement() {
        int moveAmount = 2;
        boolean moved = false; // il player e' fermo di default
    
        if (keysPressed.contains(KeyCode.UP)) {
            model.startMoving("UP");
            view.startWalking("UP");
            moved = true;
        }
        if (keysPressed.contains(KeyCode.DOWN)) {
            model.startMoving("DOWN");
            view.startWalking("DOWN");
            moved = true;
        }
        if (keysPressed.contains(KeyCode.LEFT)) {
            model.startMoving("LEFT");
            view.startWalking("LEFT");
            moved = true;
        }
        if (keysPressed.contains(KeyCode.RIGHT)) {
            model.startMoving("RIGHT");
            view.startWalking("RIGHT");
            moved = true;
        }
        if (keysPressed.contains(KeyCode.DIGIT1)) {
            model.velocityProperty().set(model.velocityProperty().get() + 0.1);
            moved = true;
        }
        if (keysPressed.contains(KeyCode.DIGIT2)) {
            model.velocityProperty().set(model.velocityProperty().get() - 0.1);
            moved = true;
        }
        if (!moved) {
            model.stopMoving();
            view.stopWalking(); // ferma il player
        }
    }
    
}
