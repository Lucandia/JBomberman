package com.lucandia;

/**
 * Questa interfaccia rappresenta un osservatore dello stato del giocatore.
 * Gli osservatori dello stato del giocatore possono essere registrati per ricevere
 * notifiche quando lo stato del giocatore viene aggiornato.
 */
public interface PlayerStateObserver {
    /**
     * Metodo chiamato quando lo stato del giocatore viene aggiornato.
     * 
     * @param playerModel il modello del giocatore aggiornato
     */
    void update(PlayerModel playerModel);
}
