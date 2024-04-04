package com.lucandia;

/**
 * Questa classe implementa l'interfaccia PlayerStateObserver e gestisce la riproduzione dei suoni del giocatore.
 */
public class PlayerSound implements PlayerStateObserver {

    private int audioDelay = 30;

    /**
     * Aggiorna il suono del giocatore in base allo stato del modello del giocatore.
     *
     * @param playerModel il modello del giocatore
     */
    @Override
    public void update(PlayerModel playerModel) {
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
                audioDelay = 30;
            }
        }
    }
}
