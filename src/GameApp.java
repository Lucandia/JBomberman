import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class GameApp extends Application {
    
    private PlayerModel playerModel;
    
    @Override
    public void start(Stage primaryStage) {
        Pane root = new Pane();
        Scene scene = new Scene(root, 800, 600);

        // Initialize the player model, view, and controller
        playerModel = new PlayerModel(new ObservablePoint2D(200, 200), 100);
        PlayerView playerView = new PlayerView(playerModel, root);
        new PlayerController(playerModel, scene);

        // Create and start the game loop using AnimationTimer
        AnimationTimer gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // Calculate elapsed time since last update
                // For simplicity, we're not calculating delta time here
                // In a real game, you would pass the elapsed time since the last frame to the update method
                playerModel.update(1.0 / 60.0); // Assuming 60 FPS for calculation
            }
        };
        gameLoop.start();

        primaryStage.setTitle("MVC Game Demo");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
