package com.esame;

import javafx.beans.Observable;

/**
 * Questa classe implementa l'interfaccia EntityStateObserver e gestisce la riproduzione dei suoni del giocatore.
 */
public class PlayerSound implements EntityStateObserver {

    /**
     * Il ritardo tra i suoni del movimento del giocatore.
     */
    private int audioDelay = 120;

    /**
     * Metodo chiamato quando un'osservabile è stato invalidato.
     * 
     * @param observable l'osservabile invalidato
     */
    @Override
    public void invalidated(Observable observable) {
        AudioUtils.stopAll();      
    }

    /**
     * Aggiorna il suono del giocatore in base allo stato del modello del giocatore.
     *
     * @param playerEntityModel il modello dell'entità del giocatore
     */
    @Override
    public void update(EntityModel playerEntityModel) {
        PlayerModel playerModel = (PlayerModel) playerEntityModel; // fai il casting dell'entità a PlayerModel
        if (playerModel.justLostLife()) { // Assume this method exists and tracks if a life was recently lost
            AudioUtils.playSoundEffect("LoseLife.mp3");
        }
        if (playerModel.justGainedPowerUp()) { // Similarly, assume this checks for recent power-up acquisition
            AudioUtils.playSoundEffect("ItemGet.mp3");
        }
        if (playerModel.isMoving()) {
            audioDelay -= 1;
            if (audioDelay <= 0) {
                AudioUtils.playSoundEffect("Walking.mp3");
                audioDelay = 120;
            }
        }
    }
}
