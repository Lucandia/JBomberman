package com.esame;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import javafx.animation.AnimationTimer;
// import stuff to have a dialog box
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Alert.AlertType;
import java.util.Optional;

/**
 * La classe GameApp rappresenta l'applicazione principale del gioco JBomberman.
 * Estende la classe Application e gestisce l'inizializzazione del gioco, la creazione della scena di gioco,
 * l'aggiornamento del gioco e la gestione degli eventi.
 */
public class GameApp extends Application {
    
    /**
     * I dati del giocatore.
     */
    private PlayerData data;

    /**
     * La vista dello stage.
     */
    private StageView stageView;

    /**
     * Il controller del giocatore.
     */
    private PlayerController playerController;

    /**
     * Il controller delle bombe.
     */
    private BombController bombController;

    /**
     * Il modello del giocatore.
     */
    private PlayerModel playerModel;

    /**
     * Il controller degli avversari.
     */
    private EnemiesController enemiesController;

    /**
     * Il numero di nemici.
     */
    private int numberOfEnemies;

    /**
     * La musica di sottofondo.
     */
    private BackgroundMusic backgroundMusic = new BackgroundMusic();

    /**
        * Inizializza il gioco con i dati del giocatore e il numero di nemici.
        * 
        * @param data I dati del giocatore
        * @param numberOfEnemies Il numero di nemici
        * @return null
        */
    public Void initializeGame(PlayerData data, int numberOfEnemies) {
        this.data = data;
        this.numberOfEnemies = numberOfEnemies;
        return null;
    }

    /**
     * Avvia il gioco. Questo metodo viene chiamato quando il gioco e' inizializzato.
     * Viene avviato il loop del gioco e vengono gestiti gli eventi di morte del giocatore e completamento del livello.
     * Una volta che il giocatore muore o completa il livello, viene visualizzata una finestra di dialogo
     * che chiede al giocatore se vuole riprovare, tornare al menu principale o uscire dal gioco.
     * 
     * @param primaryStage lo stage principale dell'applicazione
     */
    @Override
    public void start(Stage primaryStage) {        
        // Setup the game with the provided player data
        setupGame(primaryStage, data.getLastLevelInt());
        // Create and start the game loop using AnimationTimer
        AnimationTimer gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // Check for player death or level completion
                if (playerModel.isDead() || (enemiesController.getEnemies().isEmpty() && playerModel.isOnNextLevelDoor())) {
                    // Stop the game loop first to prevent any updates while the dialog is shown
                    playerModel.stopMoving(); 
                    backgroundMusic.stopMusic();
                    this.stop();
                    int current_level = data.getLastLevelInt();
                    // save the data
                    if (playerModel.isDead()) {
                        data.setLostGames(data.getLostGamesInt() + 1);
                        AudioUtils.playSoundEffect("GameOver.mp3");
                    } else {
                        data.setWinGames(data.getWinGamesInt() + 1);
                        AudioUtils.playSoundEffect("StageClear.mp3");
                        // Don't go to higher levels than 3
                        if (data.getLastLevelInt() < 3) {
                            data.setLastLevel(data.getLastLevelInt() + 1);
                        }
                        else {
                            numberOfEnemies ++ ;
                        }
                    }
                    data.setScore(playerModel.getScore());
                    data.setPlayedGames(data.getPlayedGamesInt() + 1);
                    savePlayerData();

                    // Use Platform.runLater to show the dialog after the current animation frame is processed
                    Platform.runLater(() -> {
                        Alert alert = new Alert(AlertType.CONFIRMATION);
                        // Applying the CSS file to the DialogPane
                        DialogPane dialogPane = alert.getDialogPane();
                        dialogPane.getStylesheets().add(getClass().getResource("/styles/styles.css").toExternalForm());
                        // Add buttons to the dialog
                        alert.setTitle(playerModel.isDead() ? "Game Over!" : "Level Complete!");
                        alert.getDialogPane().setGraphic(null);
                        alert.setHeaderText(playerModel.isDead() ? "Game over!" : "Level Complete!");
                        if (!playerModel.isDead()) {
                            if (current_level == 3) {
                                alert.setContentText("Congratulations! You have completed all levels! Let's try more enemies?");
                            } else {
                                alert.setContentText("Well done! Proceed to the next level?");
                            }
                        } else {
                            alert.setContentText("You can do it! Try again!");
                        }
                        ButtonType buttonRestartOrContinue = new ButtonType(playerModel.isDead() ? "Try again" : "Continue");
                        ButtonType buttonExit = new ButtonType("Exit to Main Menu");
                        ButtonType buttonQuit = new ButtonType("Quit");
                        
                        alert.getButtonTypes().setAll(buttonRestartOrContinue, buttonExit, buttonQuit);

                        Optional<ButtonType> result = alert.showAndWait();
                        if (result.isPresent() && result.get() == buttonRestartOrContinue) {
                            if (playerModel.isDead()) {
                                // Restart current level if the player died
                                playerModel = null;
                                setupGame(primaryStage, data.getLastLevelInt());
                            } else {
                                // Setup game for next level if the current one was completed
                                setupGame(primaryStage, data.getLastLevelInt());
                            }
                            // Restart the game loop
                            this.start();
                        } else if (result.isPresent() && result.get() == buttonExit) {
                            // Instead of closing the primary stage, re-use it for the pre-game setup
                            Platform.runLater(() -> {
                                MainMenu mainMenu = new MainMenu();
                                try {
                                    mainMenu.start(primaryStage);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });
                        }
                        else if (result.isPresent() && result.get() == buttonQuit) {
                            // Stop the background music
                            backgroundMusic.stopMusic();
                            AudioUtils.stopAll();
                            // Quit the application
                            Platform.exit();
                        }
                    });
                } else {
                    // Regular game update logic if no special conditions met
                    enemiesController.updateState(1.0 / 60.0);
                    playerController.updateState(1.0 / 60.0);
                    bombController.updateState(1.0 / 60.0);
                    stageView.updateView();
                }
            }
        };
        gameLoop.start();
    }
    

    /**
     * Imposta il gioco con la scena principale e i componenti necessari.
     * 
     * @param primaryStage lo stage principale del gioco
     * @param level il livello del gioco
     */
    private void setupGame(Stage primaryStage, int level) {
        AudioUtils.playSoundEffect("StageStart.mp3");
        // Use a StackPane as the root to allow layering of the map and the player
        StackPane root = new StackPane();
        Pane bombLayer = new Pane();
        Pane gameLayer = new Pane();
        BorderPane borderPane = new BorderPane();

        // Initialize the Stage
        StageModel stageModel = new StageModel();
        StageView stageView = new StageView(level, stageModel);
        this.stageView = stageView;

        Scene mainScene = new Scene(borderPane, 272, 224);
        mainScene.getStylesheets().add(getClass().getResource("/styles/styles.css").toExternalForm());
        primaryStage.setTitle("Jbomberan level " + level);
        primaryStage.setScene(mainScene);
        primaryStage.show();

        // initialize playerModel, view, and controller
        if (this.playerModel == null) {
            stageModel.setPlayer(playerModel);
            this.playerModel = new PlayerModel(32, 6, stageModel);
        }
        else {
            this.playerModel.setPosition(32, 6);
            this.playerModel.setStage(stageModel);
        }
        stageModel.setPlayer(playerModel);
        this.playerController = new PlayerController(playerModel, Integer.parseInt(data.getAvatar()));
        this.enemiesController = new EnemiesController(numberOfEnemies, stageModel, gameLayer, level);

        // Layer the map and the player on the StackPane
        root.getChildren().add(stageView.getPane()); // Map as the base layer
        gameLayer.getChildren().add(playerController.getView().getEntitySprite()); // Add Bomberman on top of the map
        root.getChildren().add(bombLayer); // Add the bomb layer to the root
        root.getChildren().add(gameLayer); // Add the game layer to the root

        // usa un BorderPane per posizionare l'HUD sopra il gioco
        HUDView hudView = new HUDView();
        PlayerSound playerSound = new PlayerSound(); // inizializza il playerSound
        // aggiungi HUD come osservatore del playerModel
        playerModel.addListener(hudView);
        playerModel.addListener(playerSound); // aggiungi playerSound come osservatore del playerModel
        hudView.update(playerModel); // initializza l'HUD con i valori iniziali
        borderPane.setCenter(root); // Set the game (map + player) as the center
        borderPane.setTop(hudView.getHudPane()); // Set the HUD at the top

        // Setup the controller with the scene
        this.bombController = new BombController(playerModel, bombLayer);
        new InputController(playerController, bombController, mainScene);
        backgroundMusic.playMusic("Background.mp3");
    }

    /**
        * Salva i dati del giocatore nel file savedGames.txt.
        */
    public void savePlayerData() {
        try {
            // Get the path to the JAR file
            String jarPath = new File(GameApp.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
            // Get the directory of the JAR file
            String dirPath = new File(jarPath).getParent();
            // Construct the path to the players.txt file in the same directory
            File file = new File(dirPath, "savedGames.txt");
            // Prepare data to save
            String dataToSave = data.getDataString();
            // Check if data for the player already exists and needs to be updated, or append new data
            List<String> lines = file.exists() ? Files.readAllLines(file.toPath()) : new ArrayList<>();
            lines.removeIf(String::isEmpty); // Remove any empty lines
            boolean dataExists = false;
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                String nickname = line.split(",")[0];
                if (nickname.equals(data.getNickname())) {
                    lines.set(i, dataToSave); // Update existing data
                    dataExists = true;
                    break;
                }
            }
            if (!dataExists && !dataToSave.isEmpty()) {
                lines.add(dataToSave); // Append new player data
            }
            // Write data to the file
            Files.write(file.toPath(), lines);
        } catch (URISyntaxException e) {
            System.err.println("Error parsing URI: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("IO error occurred: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
        }
    }

}
