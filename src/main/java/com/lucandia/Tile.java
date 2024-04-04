package com.lucandia;
/**
 * Questa classe rappresenta una casella nel gioco Bomberman.
 * Estende la classe XYModel e contiene informazioni sulla sua posizione e proprietà.
 */
public class Tile extends XYModel{
    protected boolean isDestructible;
    protected boolean isDisplayable = true;
    protected boolean isWalkable = false;

    /**
     * Costruttore della classe Tile.
     * 
     * @param x la coordinata x della casella
     * @param y la coordinata y della casella
     * @param isDestructible indica se la casella è distruttibile o meno
     */
    public Tile(int x, int y, boolean isDestructible) {
        super(x, y);
        this.isDestructible = isDestructible;
    }

    /**
     * Costruttore della classe Tile.
     * 
     * @param x la coordinata x della casella
     * @param y la coordinata y della casella
     * @param isDestructible indica se la casella è distruttibile o meno
     * @param isDisplayable indica se la casella è visualizzabile o meno
     */
    public Tile(int x, int y, boolean isDestructible, boolean isDisplayable) {
        super(x, y);
        this.isDestructible = isDestructible;
        this.isDisplayable = isDisplayable;
    }

    /**
     * Costruttore della classe Tile.
     * 
     * @param x la coordinata x della casella
     * @param y la coordinata y della casella
     * @param isDestructible indica se la casella è distruttibile o meno
     * @param isDisplayable indica se la casella è visualizzabile o meno
     * @param isWalkable indica se la casella è attraversabile o meno
     */
    public Tile(int x, int y, boolean isDestructible, boolean isDisplayable, boolean isWalkable) {
        super(x, y);
        this.isDestructible = isDestructible;
        this.isDisplayable = isDisplayable;
        this.isWalkable = isWalkable;
    }

    /**
     * Verifica se la casella è distruttibile.
     * 
     * @return true se la casella è distruttibile, false altrimenti
     */
    public boolean isDestructible() {
        return isDestructible;
    }

    /**
     * Verifica se la casella è attraversabile.
     * 
     * @return true se la casella è attraversabile, false altrimenti
     */
    public boolean isWalkable() {
        return isWalkable;
    }

    /**
     * Verifica se la casella è detonabile da una bomba.
     * 
     * @return true se la casella è distruttibile e visualizzabile, false altrimenti
     */
    public boolean isDetonable() {
        return isDestructible && isDisplayable;
    }

    /**
     * Imposta se la casella è attraversabile o meno.
     * 
     * @param walkable true se la casella è attraversabile, false altrimenti
     */
    public void setWalkable(boolean walkable) {
        isWalkable = walkable;
    }

    /**
     * Verifica se la casella è visualizzabile.
     * 
     * @return true se la casella è visualizzabile, false altrimenti
     */
    public boolean isDisplayable() {
        return isDisplayable;
    }

    /**
     * Imposta se la casella è visualizzabile o meno.
     * 
     * @param displayable true se la casella è visualizzabile, false altrimenti
     */
    public void setDisplayable(boolean displayable) {
        isDisplayable = displayable;
    }

}
