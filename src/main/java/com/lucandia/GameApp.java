package com.lucandia;
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

public class GameApp extends Application {
    private int avatar;
    private PlayerData data;
    private StageView stageView;
    private PlayerController playerController;
    private BombController bombController;
    private PlayerModel playerModel;
    private EnemiesController enemiesController;
    private int numberOfEnemies;
    private BackgroundMusic backgroundMusic = new BackgroundMusic();

    public Void initializeGame(PlayerData data, int numberOfEnemies) {
        this.avatar = Integer.parseInt(data.getAvatar()) - 1;
        this.data = data;
        this.numberOfEnemies = numberOfEnemies;
        return null;
    }

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
                    // save the data
                    if (playerModel.isDead()) {
                        data.setLostGames(data.getLostGamesInt() + 1);
                        // AudioUtils.playSoundEffect("BombermanDies.mp3");
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
                            if (data.getLastLevelInt() == 3) {
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
                                    // Assume you have this method in PreGameSetup
                                    // This method should setup and show the pre-game scene
                                    mainMenu.start(primaryStage);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });
                        }
                        else if (result.isPresent() && result.get() == buttonQuit) {
                            // Quit the application
                            Platform.exit();
                        }
                    });
                } else {
                    // Regular game update logic if no special conditions met
                    enemiesController.update(1.0 / 60.0);
                    playerController.update(1.0 / 60.0);
                    bombController.update(1.0 / 60.0);
                    stageView.updateView();
                }
            }
        };
        gameLoop.start();
    }
    

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
        primaryStage.setTitle("JBomberman");
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
        EntityView playerView = new EntityView(playerModel, "bomberman", true, 3, avatar);
        this.enemiesController = new EnemiesController(numberOfEnemies, stageModel, gameLayer, level);

        // Layer the map and the player on the StackPane
        root.getChildren().add(stageView.getPane()); // Map as the base layer
        gameLayer.getChildren().add(playerView.getEntitySprite()); // Add Bomberman on top of the map
        root.getChildren().add(bombLayer); // Add the bomb layer to the root
        root.getChildren().add(gameLayer); // Add the game layer to the root

        // For the HUD, use a BorderPane as the outer container
        HUDView hudView = new HUDView(playerModel);
        borderPane.setCenter(root); // Set the game (map + player) as the center
        borderPane.setTop(hudView.getHudPane()); // Set the HUD at the top

        // Setup the controller with the scene
        this.playerController = new PlayerController(playerModel, playerView);
        this.bombController = new BombController(playerModel, bombLayer);
        new InputController(playerController, bombController, mainScene);
        backgroundMusic.playMusic("Background.mp3");
    }

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
