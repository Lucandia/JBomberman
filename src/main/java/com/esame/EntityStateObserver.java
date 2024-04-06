package com.esame;

import javafx.beans.InvalidationListener;

/**
 * Questa interfaccia rappresenta un osservatore dello stato di una entita'.
 * Gli osservatori dello stato delle entita' possono essere registrati per ricevere
 * notifiche quando lo stato di un'entita' cambia.
 */
public interface EntityStateObserver extends InvalidationListener {
    
    /**
     * Metodo chiamato quando lo stato dell'entita' viene aggiornato.
     * 
     * @param entityModel l'entita' che ha aggiornato il suo stato
     */
    void update(EntityModel entityModel);
}
