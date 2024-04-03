package com.lucandia;

/**
 * Questa classe rappresenta una casella vuota (empty tile) nel gioco.
 * Estende la classe Tile e implementa le funzionalità specifiche
 * di una casella vuota.
 */
public class EmptyTile extends Tile{

    /*
     * Il modello dell'entità che occupa la casella.
     */
    private EntityModel occupant = null;

    /**
     * Costruttore della classe EmptyTile.
     * 
     * @param x La coordinata x della casella.
     * @param y La coordinata y della casella.
     */
    public EmptyTile(int x, int y) {
        super(x, y, true, false, true);
    }

    /**
     * Verifica se la casella è occupata.
     * 
     * @return true se la casella è occupata, false altrimenti.
     */
    public boolean isOccupied() {
        return occupant != null;
    }

    /**
     * Verificase la casella è detonabile.
     * 
     * @return true se la casella è occupata, false altrimenti.
     */
    public boolean isDetonable() {
        return isOccupied();
    }

    /**
     * Ottenere il modello dell'entita' che occupa la casella.
     * 
     * @return Il modello dell'entita' che occupa la casella.
     */
    public EntityModel getOccupant() {
        return occupant;
    }

    /**
     * Metodo per impostare il modello dell'entita' che occupa la casella.
     * 
     * @param occupant Il modello dell'entita' che occupa la casella.
     */
    public void setOccupant(EntityModel occupant) {
        this.occupant = occupant;
    }
}