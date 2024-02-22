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
