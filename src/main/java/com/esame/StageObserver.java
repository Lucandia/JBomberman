package com.esame;
import javafx.beans.InvalidationListener;

/**
 * Questa interfaccia rappresenta un osservatore dello stato dello stage.
 */
public interface StageObserver extends InvalidationListener{
    
    /**
     * Metodo chiamato quando lo stato dello stage viene aggiornato.
     * 
     * @param stageModel lo stage che ha aggiornato il suo stato
     */
    void update(StageModel stageModel);
}
