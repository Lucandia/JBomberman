package com.esame;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;

/**
 * Questa classe gestisce l'input da tastiera per muovere il player e piazzare le bombe.
 */
public class InputController {

    /**
     * L'insieme dei tasti premuti.
     */
    private final Set<KeyCode> keysPressed = ConcurrentHashMap.newKeySet(); // per far muovere il player appena si preme un tasto

    /**
     * La scena in cui si verificano gli eventi di input.
     */
    private Scene scene;

    /**
     * Il controller del player.
     */
    private PlayerController player;

    /**
     * Il controller delle bombe.
     */
    private BombController bomb;


    /**
     * Costruttore della classe InputController.
     * Crea un oggetto InputController con il player, la bomba e la scena specificati.
     *
     * @param player il controller del player
     * @param bomb il controller della bomba
     * @param scene la scena in cui si verificano gli eventi di input
     */
    public InputController(PlayerController player, BombController bomb,  Scene scene) {
        this.player = player;
        this.bomb = bomb;
        this.scene = scene;
        attachEventListeners();
    }


    /**
     * Attiva gli "event listeners" per la scena: gestisce gli input da tastiera per muovere il player e piazzare le bombe.
     */
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
    
    /**
     * Gestisce gli input e li invia al player e alla bomba.
     */
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
    
    /**
     * Gestiisce il rilascio degli input e li invia al player.
     */
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
