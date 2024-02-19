import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class HUDView {
    private HBox hudPane = new HBox(10);
    private Text scoreText = new Text();
    private Text livesText = new Text();

    public HUDView(PlayerModel model) {
        hudPane.getChildren().addAll(scoreText, livesText);

        // Bind the HUD elements to the model
        scoreText.textProperty().bind(model.scoreProperty().asString("Score: %d"));
        livesText.textProperty().bind(model.livesProperty().asString("Lives: %d"));
    }

    public HBox getHudPane() {
        return hudPane;
    }

    // Methods to update the view
}
