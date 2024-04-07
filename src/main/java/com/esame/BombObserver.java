package com.esame;
import javafx.beans.InvalidationListener;

/**
 * Questa interfaccia rappresenta un osservatore dello stato della bomba
 */
public interface BombObserver extends InvalidationListener{
    
    /**
     * Metodo chiamato quando lo stato della bomba viene aggiornato.
     * 
     * @param bombModel la bomba che ha aggiornato il suo stato
     */
    void update(BombModel bombModel);
}
