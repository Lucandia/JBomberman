package com.lucandia;

/**
 * Questa classe rappresenta una casella speciale nel gioco JBomberman.
 * Estende la classe EmptyTile.
 */
public class SpecialTile extends EmptyTile{
    protected SpecialTileType type;

    /**
     * Costruisce un oggetto SpecialTile con le coordinate specificate e il tipo di casella speciale.
     * 
     * @param x la coordinata x della casella
     * @param y la coordinata y della casella
     * @param type il tipo di casella speciale
     */
    public SpecialTile(int x, int y, SpecialTileType type) {
        super(x, y);
        setType(type);
        setDisplayable(true);
    }

    /**
     * Restituisce il tipo di casella speciale.
     * 
     * @return il tipo di casella speciale
     */
    public SpecialTileType getType() {
        return type;
    }

    /**
     * Imposta il tipo di casella speciale.
     * 
     * @param type il tipo di casella speciale
     */
    public void setType(SpecialTileType type) {
        this.type = type;
    }
}
