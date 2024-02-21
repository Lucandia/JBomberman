import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.animation.AnimationTimer;

public class GameApp extends Application {

    private PlayerModel playerModel;

    @Override
    public void start(Stage primaryStage) {
        // initialize playerModel
        playerModel = new PlayerModel(32, 0, 1);
        
        // Use a StackPane as the root to allow layering of the map and the player
        StackPane root = new StackPane();
        Pane gameLayer = new Pane();

        // Initialize the map and HUD
        // MapModel mapModel = new MapModel(1, 300, 230); // Example dimensions
        StageModel stageModel = new StageModel();
        StageView stageView = new StageView(1, stageModel);
        // HUDView hudView = new HUDView(playerModel);

        // Initialize the player view, and controller
        PlayerView playerView = new PlayerView(playerModel); // Pass a new Pane as the gamePane for player

        // Layer the map and the player on the StackPane
        root.getChildren().add(stageView.getPane()); // Map as the base layer
        gameLayer.getChildren().add(playerView.getPlayerSprite()); // Add Bomberman on top of the map
        root.getChildren().add(gameLayer); // Add the game layer to the root

        // For the HUD, use a BorderPane as the outer container
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(root); // Set the game (map + player) as the center
        // borderPane.setTop(hudView.getHudPane()); // Set the HUD at the top

        Scene mainScene = new Scene(borderPane, 272, 208);
        primaryStage.setTitle("JBomberman");
        primaryStage.setScene(mainScene);
        primaryStage.show();

        // Setup the controller with the scene
        new PlayerController(playerModel, playerView, mainScene);

        // Create and start the game loop using AnimationTimer
        AnimationTimer gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // Update logic here
                playerModel.update(1.0 / 30.0); // Assuming 60 FPS for calculation
            }
        };
        gameLoop.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
