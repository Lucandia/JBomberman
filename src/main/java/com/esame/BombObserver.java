package com.esame;

/**
 * Questa interfaccia rappresenta un osservatore dello stato della bomba
 */
public interface BombObserver {
    
    /**
     * Metodo chiamato quando lo stato della bomba viene aggiornato.
     * 
     * @param bombModel la bomba che ha aggiornato il suo stato
     */
    void update(BombModel bombModel);
}
