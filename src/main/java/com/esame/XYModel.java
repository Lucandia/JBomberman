package com.esame;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Questa classe astratta rappresenta un oggetto con coordinate x e y
 */
public abstract class XYModel {

    /**
     * La coordinata x.
     */
    protected IntegerProperty x = new SimpleIntegerProperty();

    /**
     * La coordinata y.
     */
    protected IntegerProperty y = new SimpleIntegerProperty();

    /**
     * Costruisce un nuovo oggetto XYModel con coordinate (0, 0).
     */
    public XYModel() {
        this(0, 0);
    }

    /**
     * Costruisce un nuovo oggetto XYModel con le coordinate specificate.
     *
     * @param x la coordinata x
     * @param y la coordinata y
     */
    public XYModel(int x, int y) {
        this.x.set(x);
        this.y.set(y);
    }

    /**
     * Restituisce la proprietà x.
     *
     * @return la proprietà x
     */
    public IntegerProperty xProperty() {
        return x;
    }

    /**
     * Restituisce la proprietà y.
     *
     * @return la proprietà y
     */
    public IntegerProperty yProperty() {
        return y;
    }

    /**
     * Restituisce la coordinata x.
     *
     * @return la coordinata x
     */
    public int getX() {
        return x.get();
    }

    /**
     * Restituisce la coordinata y.
     *
     * @return la coordinata y
     */
    public int getY() {
        return y.get();
    }

    /**
     * Imposta la coordinata x.
     *
     * @param x la nuova coordinata x
     */
    public void setX(int x) {
        this.x.set(x);
    }

    /**
     * Imposta la coordinata y.
     *
     * @param y la nuova coordinata y
     */
    public void setY(int y) {
        this.y.set(y);
    }

    /**
     * Imposta la posizione con le coordinate specificate.
     *
     * @param x la nuova coordinata x
     * @param y la nuova coordinata y
     */
    public void setPosition(int x, int y) {
        this.x.set(x);
        this.y.set(y);
    }
}