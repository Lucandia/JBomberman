import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.animation.AnimationTimer;

public class GameApp extends Application {

    private PlayerModel playerModel;

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
        playerModel = new PlayerModel(32, 6, 1.3, stageModel);
        EntityView playerView = new EntityView(playerModel, "bomberman", 3); // Pass a new Pane as the gamePane for player
        int numberOfEnemies = 7;
        String enemyType = "1";
        EnemiesController enemiesController = new EnemiesController(numberOfEnemies, enemyType, stageModel, gameLayer);
        // Layer the map and the player on the StackPane
        root.getChildren().add(stageView.getPane()); // Map as the base layer
        gameLayer.getChildren().add(playerView.getEntitySprite()); // Add Bomberman on top of the map
        root.getChildren().add(bombLayer); // Add the bomb layer to the root
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
        PlayerController playerController = new PlayerController(playerModel, playerView);
        playerModel.bombCapacityProperty().set(7);
        BombController bombController = new BombController(playerModel, bombLayer);
        InputController inputController = new InputController(playerController, bombController, mainScene);

        // Create and start the game loop using AnimationTimer
        AnimationTimer gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // Update logic here
                // playerController.update(1.0 / 30.0); // Assuming 60 FPS for calculation
                if (playerModel.isDead()) {
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

    public static void main(String[] args) {
        launch(args);
    }
}
