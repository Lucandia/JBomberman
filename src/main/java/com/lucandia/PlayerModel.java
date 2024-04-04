package com.lucandia;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;


/**
 * Questa classe rappresenta il modello del giocatore nel gioco Bomberman.
 * Estende la classe EntityModel e implementa il pattern Observer per notificare gli osservatori
 * sullo stato del giocatore.
 */
public class PlayerModel extends EntityModel {

    // Observer pattern
    private List<PlayerStateObserver> observers = new ArrayList<>();

    private final IntegerProperty score = new SimpleIntegerProperty(0);
    private final IntegerProperty bombCapacity = new SimpleIntegerProperty(1); 
    private final IntegerProperty bombRadius = new SimpleIntegerProperty(1);
    private boolean justLostLife = false;
    private boolean justGainedPowerUp = false;
    private int recoveryTime = 10;
    private boolean recovering = false;

    /**
     * Costruttore della classe PlayerModel.
     * 
     * @param initialX la coordinata x iniziale del giocatore
     * @param initialY la coordinata y iniziale del giocatore
     * @param stage il modello dello stage di gioco
     */
    public PlayerModel(int initialX, int initialY, StageModel stage) {
        super(initialX, initialY, new int[] {13, 13}, new int[] {7, 17}, 3, stage);
        // Set default values for lives and score or any additional setup.
        this.score.set(0); // Initial score
    }

    /**
     * Aggiunge un osservatore dello stato del giocatore.
     * 
     * @param observer l'osservatore da aggiungere
     */
    public void addObserver(PlayerStateObserver observer) {
        observers.add(observer);
    }

    /**
     * Rimuove un osservatore dello stato del giocatore.
     * 
     * @param observer l'osservatore da rimuovere
     */
    public void removeObserver(PlayerStateObserver observer) {
        observers.remove(observer);
    }

    /**
     * Notifica gli osservatori sullo stato del giocatore.
     */
    protected void notifyObservers() {
        for (PlayerStateObserver observer : observers) {
            observer.update(this);
        }
    }

    /**
     * Restituisce la proprietà del numero di vite del giocatore.
     * 
     * @return la proprietà del numero di vite del giocatore
     */
    public IntegerProperty lifeProperty() {
        return this.life;
    }

    /**
     * Metodo chiamato quando il giocatore perde una vita.
     */
    @Override
    public void loseLife(int amount) {
        loseLife();
    }

    /**
     * Metodo chiamato quando il giocatore perde una vita.
     */
    public void loseLife() {
        if (this.recovering) return;
        this.life.set(getLife() - 1);
        if (isDead()) {
            clearOccupiedTiles();
        }
        this.recovering = true;
        justLostLife = true;
        notifyObservers();
    }

    /**
     * Verifica se il giocatore ha appena perso una vita.
     * 
     * @return true se il giocatore ha appena perso una vita, false altrimenti
     */
    public boolean justLostLife() {
        if (justLostLife) {
            justLostLife = false;
            return true;
        }
        return false;
    }

    /**
     * Verifica la collisione del giocatore con un'altra entità.
     * 
     * @param dx la variazione della coordinata x
     * @param dy la variazione della coordinata y
     * @return l'entità con cui il giocatore ha colliso
     */
    @Override
    public EntityModel checkCollision(int dx, int dy) {
        EntityModel occupant = super.checkCollision(dx, dy);
        if (occupant instanceof EnemyModel) {
            loseLife(1);
        }
        notifyObservers();
        return occupant;
    }
    
    /**
     * Restituisce la proprietà del punteggio del giocatore.
     * 
     * @return la proprietà del punteggio del giocatore
     */
    public IntegerProperty scoreProperty() {
        return this.score;
    }

    /**
     * Restituisce il punteggio del giocatore.
     * 
     * @return il punteggio del giocatore
     */
    public int getScore() {
        return this.score.get();
    }

    /**
     * Restituisce la proprietà del numero di bombe che il giocatore può piazzare.
     * 
     * @return la proprietà del numero di bombe che il giocatore può piazzare
     */
    public IntegerProperty bombCapacityProperty() {
        return this.bombCapacity;
    }

    /**
     * Restituisce il numero di bombe che il giocatore può piazzare.
     * 
     * @return il numero di bombe che il giocatore può piazzare
     */
    public int getBombCapacity() {
        return this.bombCapacity.get();
    }

    /**
     * Aumenta il numero di bombe che il giocatore può piazzare.
     */
    public void increaseBombCapacity() {
        this.bombCapacity.set(this.bombCapacity.get() + 1);
        justGainedPowerUp = true;
        notifyObservers();
    }

    /**
     * Restituisce la proprietà del raggio delle bombe del giocatore.
     * 
     * @return la proprietà del raggio delle bombe del giocatore
     */
    public IntegerProperty bombRadiusProperty() {
        return this.bombRadius;
    }

    /**
     * Restituisce il raggio delle bombe del giocatore.
     * 
     * @return il raggio delle bombe del giocatore
     */
    public int getBombRadius() {
        return this.bombRadius.get();
    }

    /**
     * Aumenta il raggio delle bombe del giocatore.
     */
    public void increaseBombRadius() {
        this.bombRadius.set(this.bombRadius.get() + 1);
        justGainedPowerUp = true;
        notifyObservers();
    }

    /**
     * Aumenta la velocità del giocatore.
     */
    public void increaseSpeed() {
        // this.velocity.set(this.velocity.get() + 0.3);
        if (this.delayMove > 0.01) this.delayMove -= 0.01;
        justGainedPowerUp = true;
        notifyObservers();
    }

    /**
     * Verifica se il giocatore ha appena ottenuto un power-up.
     * 
     * @return true se il giocatore ha appena ottenuto un power-up, false altrimenti
     */
    public boolean justGainedPowerUp() {
        if (justGainedPowerUp) {
            justGainedPowerUp = false;
            return true;
        }
        return false;
    }

    /**
     * Aggiunge un punteggio al punteggio del giocatore.
     * 
     * @param points il punteggio da aggiungere
     */
    public void addScore(int points) {
        this.score.set(this.score.get() + points);
    }

    /**
     * Verifica se il giocatore si trova sulla porta per il livello successivo.
     * 
     * @return true se il giocatore si trova sulla porta per il livello successivo, false altrimenti
     */
    public boolean isOnNextLevelDoor() {
        for (EmptyTile tile : occupiedTiles) {
            if (tile instanceof SpecialTile && ((SpecialTile) tile).getType() == SpecialTileType.nextLevelDoor) {
                return true;
            }
        }
        return false;
    }

    /**
     * Aggiorna il giocatore.
     * 
     * @param elapsedTime il tempo trascorso dall'ultimo aggiornamento
     */
    @Override
    public void update(double elapsedTime) {
        super.update(elapsedTime);
        if (this.recovering) {
            this.recoveryTime--;
            if (this.recoveryTime == 0) {
                this.recovering = false;
                this.recoveryTime = 10;
            }
        }
    }
    
}

