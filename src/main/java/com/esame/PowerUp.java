package com.esame;

/**
 * Questa classe rappresenta un power-up nel gioco JBomberman.
 * Un power-up è una casella speciale che può essere applicata al Bomberman per ottenere abilità speciali.
 * Ogni power-up ha un comportamento specifico che viene applicato al giocatore quando ci passa sopra.
 */
public class PowerUp extends SpecialTile{

    /*
     * Se il power-up è stato applicato.
     */
    private boolean applied = false;

    /*
     * Il comportamento del power-up.
     */
    private PowerUpBehaviour behaviour;

    /**
     * Crea un nuovo oggetto PowerUp con le coordinate specificate e il tipo di casella speciale (SpecialTileType).
     * In base al tipo di SpecialTileType, viene assegnato un comportamento specifico al power-up.
     * 
     * @param x    la coordinata x del power-up
     * @param y    la coordinata y del power-up
     * @param type il tipo di casella speciale del power-up
     */
    public PowerUp(int x, int y, SpecialTileType type) {
        super(x, y, type);
        if (type == SpecialTileType.pupBlast) {
            this.behaviour = new PowerUpBlast();
        }
        else if (type == SpecialTileType.pupBomb) {
            this.behaviour = new PowerUpBomb();
        }
        else if (type == SpecialTileType.pupSpeed) {
            this.behaviour = new PowerUpSpeed();
        }
    }

    /**
     * Imposta l'entita' che occupa la casella.
     * Se l'occupante è un giocatore, viene applicato il power-up al giocatore.
     * Dopo l'applicazione del power-up, la casella speciale viene resa non visualizzabile.
     * 
     * @param occupant l'enità che occupa la casella
     */
    @Override
    public void setOccupant(EntityModel occupant) {
        super.setOccupant(occupant);
        if (applied) return;
        if (occupant instanceof PlayerModel) {
            applyPowerUp((PlayerModel) occupant);
        }
        setDisplayable(false);
        applied = true;
    }

    /**
     * Verifica se il power-up può essere detonato.
     * Un power-up può essere detonato se non è stato ancora applicato.
     * Se il power-up è stato applicato, si comporta come una casella vuota normale.
     * 
     * @return true se il power-up può essere detonato, false altrimenti
     */
    @Override
    public boolean isDetonable() {
        if (!applied) return true; // Il power-up può essere detonato se non è stato applicato
        return super.isDetonable(); // Il power-up è stato applicato, quindi si comporta come una casella vuota normale
    }

    /**
     * Applica il power-up al giocatore specificato con il comportamento specifico del tipo di power-up.
     * 
     * @param playerModel il giocatore a cui applicare il power-up
     */
    public void applyPowerUp(PlayerModel playerModel) {
        behaviour.applyPowerUp(playerModel);
    };
}
