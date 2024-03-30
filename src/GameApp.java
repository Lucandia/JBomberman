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

    public Void initializeGame(PlayerData data) {
        this.avatar = Integer.parseInt(data.getAvatar()) - 1;
        this.data = data;
        System.out.println("Game initialized with player data: " + data.getDataString());
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
                    this.stop();

                    // Use Platform.runLater to show the dialog after the current animation frame is processed
                    Platform.runLater(() -> {
                        Alert alert = new Alert(AlertType.CONFIRMATION);
                        alert.setTitle(playerModel.isDead() ? "Game Over!" : "Level Complete!");
                        alert.setHeaderText(playerModel.isDead() ? "You died! Try again?" : "Proceed to the next level?");
                        ButtonType buttonRestartOrContinue = new ButtonType(playerModel.isDead() ? "Restart Level" : "Continue");
                        ButtonType buttonExit = new ButtonType("Exit to Main Menu");
                        alert.getButtonTypes().setAll(buttonRestartOrContinue, buttonExit);

                        Optional<ButtonType> result = alert.showAndWait();
                        if (result.isPresent() && result.get() == buttonRestartOrContinue) {
                            if (playerModel.isDead()) {
                                // Restart current level if the player died
                                playerModel = null;
                                setupGame(primaryStage, data.getLastLevelInt());
                            } else {
                                // Setup game for next level if the current one was completed
                                data.setLastLevel(Integer.toString(data.getLastLevelInt() + 1));
                                savePlayerData();
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
        // Use a StackPane as the root to allow layering of the map and the player
        StackPane root = new StackPane();
        Pane bombLayer = new Pane();
        Pane gameLayer = new Pane();
        BorderPane borderPane = new BorderPane();

        // Initialize the Stage
        StageModel stageModel = new StageModel();
        StageView stageView = new StageView(level, stageModel);
        this.stageView = stageView;

        Scene mainScene = new Scene(borderPane, 272, 232);
        mainScene.getStylesheets().add(getClass().getResource("resources/styles/styles.css").toExternalForm());
        primaryStage.setTitle("JBomberman");
        primaryStage.setScene(mainScene);
        primaryStage.show();

        // initialize playerModel, view, and controller
        if (this.playerModel == null) {
            stageModel.setPlayer(playerModel);
            this.playerModel = new PlayerModel(32, 6, 1.3, stageModel);
        }
        else {
            this.playerModel.setPosition(32, 6);
            this.playerModel.setStage(stageModel);
        }
        EntityView playerView = new EntityView(playerModel, "bomberman", true, 3, avatar);
        int numberOfEnemies = level; // + level * 2;
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
