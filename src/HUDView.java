import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;

public class HUDView {
    private HBox hudPane;
    private Label scoreLabel;
    private Label livesLabel;
    private Label bombCapacityLabel;
    private Label bombRadiusLabel;

    public HUDView(PlayerModel playerModel) {
        hudPane = new HBox(10);
        // hudPane.getStylesheets().add(getClass().getResource("resources/styles/styles.css").toExternalForm());
        hudPane.setAlignment(Pos.TOP_CENTER);
        
        scoreLabel = new Label();
        livesLabel = new Label();
        bombCapacityLabel = new Label();
        bombRadiusLabel = new Label();
        
        // scoreLabel.setFont(new Font("Pixelify Sans Regular", 14));
        // livesLabel.setFont(new Font("Pixelify Sans Regular", 14));
        // bombCapacityLabel.setFont(new Font("Pixelify Sans Regular", 14));
        // bombRadiusLabel.setFont(new Font("Pixelify Sans Regular", 14));

        scoreLabel.textProperty().bind(playerModel.scoreProperty().asString("Score: %d"));
        livesLabel.textProperty().bind(playerModel.lifeProperty().asString("life: %d"));
        bombCapacityLabel.textProperty().bind(playerModel.bombCapacityProperty().asString("Bombs: %d"));
        bombRadiusLabel.textProperty().bind(playerModel.bombRadiusProperty().asString("Radius: %d"));

        hudPane.getChildren().addAll(scoreLabel, livesLabel, bombCapacityLabel, bombRadiusLabel);
    }

    public HBox getHudPane() {
        return hudPane;
    }
}
