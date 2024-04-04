package com.esame;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;


/**
 * La classe HUDView rappresenta la vista dell'HUD (Heads-Up Display) del gioco.
 * Implementa l'interfaccia PlayerStateObserver per osservare lo stato del giocatore.
 */
public class HUDView implements PlayerStateObserver{
    private HBox hudPane;
    private Label scoreLabel;
    private Label livesLabel;
    private Label bombCapacityLabel;
    private Label bombRadiusLabel;

    /**
     * Costruttore della classe HUDView.
     * Inizializza gli elementi dell'HUD e li aggiunge al pannello principale.
     */
    public HUDView() {
        hudPane = new HBox(10);
        hudPane.getStylesheets().add(getClass().getResource("/styles/styles.css").toExternalForm());
        hudPane.setAlignment(Pos.TOP_CENTER);
        
        scoreLabel = new Label();
        livesLabel = new Label();
        bombCapacityLabel = new Label();
        bombRadiusLabel = new Label();

        /*
         * Codice opzionale, usa JavaFX puoi usare il binding per aggiornare i valori
         * invece del classico Observer pattern.
         * Se si usa il JavaFx, e' necessario avere il playerModel come parametro del costruttore
         */
        // bombCapacityLabel.textProperty().bind(playerModel.bombCapacityProperty().asString("Bombs: %d"));
        // bombRadiusLabel.textProperty().bind(playerModel.bombRadiusProperty().asString("Radius: %d"));
        // scoreLabel.textProperty().bind(playerModel.scoreProperty().asString("Score: %d"));
        // livesLabel.textProperty().bind(playerModel.lifeProperty().asString("Life: %d"));

        // // Aggiungi un listener per cambiare il colore del testo a seconda del valore della vita
        // playerModel.lifeProperty().addListener((observable, oldValue, newValue) -> {
        //     if (newValue.intValue() == 3) {
        //         livesLabel.setTextFill(Color.GREEN);
        //     } else if (newValue.intValue() == 2) {
        //         livesLabel.setTextFill(Color.INDIANRED);
        //     } else if (newValue.intValue() == 1) {
        //         livesLabel.setTextFill(Color.CRIMSON);
        //     }
        // });

        hudPane.getChildren().addAll(scoreLabel, livesLabel, bombCapacityLabel, bombRadiusLabel);
    }

    /**
     * Restituisce il pannello dell'HUD.
     * 
     * @return il pannello dell'HUD
     */
    public HBox getHudPane() {
        return hudPane;
    }

    /**
     * Metodo di aggiornamento chiamato quando lo stato del giocatore cambia.
     * Aggiorna i valori dell'HUD in base allo stato del giocatore.
     * 
     * @param playerModel il modello del giocatore
     */
    @Override
    public void update(PlayerModel playerModel) {
        scoreLabel.setText("Score: " + playerModel.getScore());
        bombCapacityLabel.setText("Bombs: " + playerModel.getBombCapacity());
        bombRadiusLabel.setText("Radius: " + playerModel.getBombRadius());
        livesLabel.setText("Life: " + playerModel.getLife());
        // Seleziona il colore della vita a seconda del valore
        if (playerModel.getLife() == 3) {
            livesLabel.setTextFill(Color.GREEN);
        } else if (playerModel.getLife() == 2) {
            livesLabel.setTextFill(Color.INDIANRED);
        } else if (playerModel.getLife() == 1) {
            livesLabel.setTextFill(Color.CRIMSON);
        }
    }
}
