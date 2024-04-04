package com.esame;
/**
 * Questa classe rappresenta un power-up che aumenta il raggio delle bombe di un giocatore.
 * Implementa l'interfaccia PowerUpBehaviour.
 */
public class PowerUpBlast implements PowerUpBehaviour{

    /**
     * Applica il power-up al modello del giocatore, aumentando il raggio delle bombe.
     *
     * @param playerModel il modello del giocatore a cui applicare il power-up
     */
    @Override
    public void applyPowerUp(PlayerModel playerModel){
        playerModel.increaseBombRadius();
    }

}
