package com.lucandia;
/**
 * Questa classe rappresenta un power-up che aumenta la velocità di un giocatore.
 * Implementa l'interfaccia PowerUpBehaviour.
 */
public class PowerUpSpeed implements PowerUpBehaviour{
    
    /**
     * Applica il power-up di aumento della velocità al modello del giocatore.
     * 
     * @param playerModel il modello del giocatore a cui applicare il power-up
     */
    @Override
    public void applyPowerUp(PlayerModel playerModel){
        playerModel.increaseSpeed();
    }

}
