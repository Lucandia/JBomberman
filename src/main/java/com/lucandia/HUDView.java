package com.lucandia;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
public class HUDView {
    private HBox hudPane;
    private Label scoreLabel;
    private Label livesLabel;
    private Label bombCapacityLabel;
    private Label bombRadiusLabel;

    public HUDView(PlayerModel playerModel) {
        hudPane = new HBox(10);
        // hudPane.getStylesheets().add(getClass().getResource("/styles/styles.css").toExternalForm());
        hudPane.setAlignment(Pos.TOP_CENTER);
        
        scoreLabel = new Label();
        livesLabel = new Label();
        bombCapacityLabel = new Label();
        bombRadiusLabel = new Label();

        scoreLabel.textProperty().bind(playerModel.scoreProperty().asString("Score: %d"));
        livesLabel.textProperty().bind(playerModel.lifeProperty().asString("Life: %d"));

        // Set label color based on life value
        if (playerModel.getLife() == 3) {
            livesLabel.setTextFill(Color.GREEN);
        } else if (playerModel.getLife() == 2) {
            livesLabel.setTextFill(Color.INDIANRED);
        } else if (playerModel.getLife() == 1) {
            livesLabel.setTextFill(Color.CRIMSON);
        }

        // Add a listener to update the label color when the life value changes
        playerModel.lifeProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() == 3) {
                livesLabel.setTextFill(Color.GREEN);
            } else if (newValue.intValue() == 2) {
                livesLabel.setTextFill(Color.INDIANRED);
            } else if (newValue.intValue() == 1) {
                livesLabel.setTextFill(Color.CRIMSON);
            }
        });

        bombCapacityLabel.textProperty().bind(playerModel.bombCapacityProperty().asString("Bombs: %d"));
        bombRadiusLabel.textProperty().bind(playerModel.bombRadiusProperty().asString("Radius: %d"));

        hudPane.getChildren().addAll(scoreLabel, livesLabel, bombCapacityLabel, bombRadiusLabel);
    }

    public HBox getHudPane() {
        return hudPane;
    }
}
