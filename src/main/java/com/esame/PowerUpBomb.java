package com.esame;

/**
 * Questa classe rappresenta un power-up che aumenta il numero di bombe che un giocatore può piazzare.
 * Implementa l'interfaccia PowerUpBehaviour.
 */
public class PowerUpBomb implements PowerUpBehaviour{
    
    /**
     * Applica il power-up al modello del giocatore, aumentando il numero di bombe che può piazzare.
     * 
     * @param playerModel il modello del giocatore a cui applicare il power-up
     */
    @Override
    public void applyPowerUp(PlayerModel playerModel){
        playerModel.increaseBombCapacity();
    }
}
