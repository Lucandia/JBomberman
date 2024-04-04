package com.esame;

/**
 * Questa interfaccia rappresenta il comportamento di un power-up.
 * Un power-up è un potere specialo dato al Bomberman
 * e ha un effetto specifico quando viene utilizzato.
 */
public interface PowerUpBehaviour {
    /**
     * Applica il power-up al modello del giocatore specificato.
     *
     * @param playerModel il modello del giocatore a cui applicare il power-up
     */
    void applyPowerUp(PlayerModel playerModel);
}