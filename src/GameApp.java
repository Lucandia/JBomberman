import javafx.application.Application;
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

public class GameApp extends Application {
    private int avatar;
    private PlayerData data;

    // public static void main(String[] args) {
    //     launch(args);
    // }

    public Void initializeGame(PlayerData data) {
        this.avatar = Integer.parseInt(data.getAvatar()) - 1;
        this.data = data;
        System.out.println("Game initialized with player data: " + data.getDataString());
        return null;
    }

    @Override
    public void start(Stage primaryStage) {
                // Use a StackPane as the root to allow layering of the map and the player
        StackPane root = new StackPane();
        Pane bombLayer = new Pane();
        Pane gameLayer = new Pane();

        // Initialize the map and HUD
        // MapModel mapModel = new MapModel(1, 300, 230); // Example dimensions
        StageModel stageModel = new StageModel();
        StageView stageView = new StageView(2, stageModel);
        // HUDView hudView = new HUDView(playerModel);

        // initialize playerModel, view, and controller
        PlayerModel playerModel = new PlayerModel(32, 6, 1.3, stageModel);
        stageModel.setPlayer(playerModel);
        EntityView playerView = new EntityView(playerModel, "bomberman", true, 3, avatar); // Pass a new Pane as the gamePane for player
        int numberOfEnemies = 7;
        String enemyType = "1";
        EnemiesController enemiesController = new EnemiesController(numberOfEnemies, enemyType, stageModel, gameLayer);
        // Layer the map and the player on the StackPane
        root.getChildren().add(stageView.getPane()); // Map as the base layer
        gameLayer.getChildren().add(playerView.getEntitySprite()); // Add Bomberman on top of the map
        root.getChildren().add(bombLayer); // Add the bomb layer to the root
        root.getChildren().add(gameLayer); // Add the game layer to the root

        // For the HUD, use a BorderPane as the outer container
        HUDView hudView = new HUDView(playerModel);
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(root); // Set the game (map + player) as the center
        borderPane.setTop(hudView.getHudPane()); // Set the HUD at the top

        Scene mainScene = new Scene(borderPane, 272, 208);
        primaryStage.setTitle("JBomberman");
        primaryStage.setScene(mainScene);
        primaryStage.show();

        // Setup the controller with the scene
        PlayerController playerController = new PlayerController(playerModel, playerView);
        BombController bombController = new BombController(playerModel, bombLayer);
        InputController inputController = new InputController(playerController, bombController, mainScene);
        
        // Create and start the game loop using AnimationTimer
        AnimationTimer gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // Update logic here
                // playerController.update(1.0 / 30.0); // Assuming 60 FPS for calculation
                if (playerModel.isDead()) {
                    savePlayerData();
                    System.out.println("Game Over");
                    stop();
                }
                enemiesController.update(1.0 / 60.0);
                playerController.update(1.0 / 60.0);
                bombController.update(1.0 / 60.0);
                stageView.updateView();
            }
        };
        gameLoop.start();
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
            if (!dataExists) {
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
