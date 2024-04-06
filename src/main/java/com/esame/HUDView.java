package com.esame;
import javafx.beans.Observable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;


/**
 * La classe HUDView rappresenta la vista dell'HUD (Heads-Up Display) del gioco.
 * Implementa l'interfaccia EntityStateObserver per osservare lo stato del giocatore.
 */
public class HUDView implements EntityStateObserver{

    /**
     * Il pannello dell'HUD.
     */
    private HBox hudPane;

    /**
     * Etichetta per visualizzare il punteggio del giocatore.
     */
    private Label scoreLabel;

    /**
     * Etichetta per visualizzare le vite del giocatore.
     */
    private Label livesLabel;

    /**
     * Etichetta per visualizzare la capacità delle bombe del giocatore.
     */
    private Label bombCapacityLabel;

    /**
     * Etichetta per visualizzare il raggio delle bombe del giocatore.
     */
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
     * Metodo chiamato quando un'osservabile è stato invalidato.
     * 
     * @param observable l'osservabile invalidato
     */
    @Override
    public void invalidated(Observable observable) {
        scoreLabel.setText("Score: /");
        bombCapacityLabel.setText("Bombs: /");
        bombRadiusLabel.setText("Radius: /");
        livesLabel.setText("Life: /");      
    }
    

    /**
     * Metodo di aggiornamento chiamato quando lo stato del giocatore cambia.
     * Aggiorna i valori dell'HUD in base allo stato del giocatore.
     * 
     * @param playerEntityModel il modello dell'entità del giocatore
     */
    @Override
    public void update(EntityModel playerEntityModel) {
        PlayerModel playerModel = (PlayerModel) playerEntityModel;
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
