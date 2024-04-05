package com.esame;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.BooleanProperty;
import java.util.ArrayList;
import java.util.List;

/**
 * Questa classe rappresenta il modello di una bomba nel gioco JBomberman.
 * Estende la classe EmptyTile e contiene informazioni sulla posizione, raggio di esplosione,
 * stato di attivazione, timer e altre proprietà della bomba.
 */
public class BombModel extends EmptyTile{

    /**
     * La lista delle posizioni detonate dalla bomba.
     */
    private List<String> detonatePositions = new ArrayList<>();

    /**
     * Il raggio di esplosione della bomba.
     */
    private int blastRadius = 1; // Default blast radius

    /**
     * La proprietà booleana di JavaFX che rappresenta lo stato di attivazione della bomba.
     */
    private BooleanProperty active = new SimpleBooleanProperty(true); // Bomb is active by default  

    /**
     * Il timer della bomba.
     */
    private final double totalTime = 3; // Bomb timer in seconds

    /**
     * Il timer della bomba.
     */
    private double timer = totalTime; // Bomb timer in seconds

    /**
     * Il tempo in cui la bomba è camminabile.
     */
    private double walkableTime = totalTime / 5; // time on which you can walk on the bomb

    /**
     * Costruttore della classe BombModel.
     * Crea un oggetto BombModel con le coordinate x e y specificate e il raggio di esplosione.
     *
     * @param x la coordinata x della bomba
     * @param y la coordinata y della bomba
     * @param radius il raggio di esplosione della bomba
     */
    public BombModel(int x, int y, int radius) {
        super(x, y);
        blastRadius = radius;
    }

    /**
     * Costruttore della classe BombModel.
     * Crea un oggetto BombModel con le coordinate x e y specificate, il raggio di esplosione e il timer.
     *
     * @param x la coordinata x della bomba
     * @param y la coordinata y della bomba
     * @param radius il raggio di esplosione della bomba
     * @param time il timer della bomba
     */
    public BombModel(int x, int y, int radius, double time) {
        super(x, y);
        blastRadius = radius;
        time = timer;
    }

    /**
     * Restituisce true se la bomba è detonabile, altrimenti restituisce false.
     *
     * @return true se la bomba è detonabile, altrimenti false
     */
    public boolean isDetonable() {
        return true;
    }

    /**
     * Restituisce il raggio di esplosione della bomba.
     *
     * @return il raggio di esplosione della bomba
     */
    public int getBlastRadius() {
        return blastRadius;
    }

    /**
     * Restituisce la proprietà booleana di JavaFX che rappresenta lo stato di attivazione della bomba.
     *
     * @return la proprietà booleana JavaFX che rappresenta lo stato di attivazione della bomba
     */
    public BooleanProperty activeProperty() {
        return active;
    }

    /**
     * Restituisce true se la bomba è attiva, altrimenti restituisce false.
     *
     * @return true se la bomba è attiva, altrimenti false
     */
    public boolean isActive() {
        return active.get();
    }

    /**
     * Questo metodo "esplode" la bomba, disattivandola e impostando il timer a 0.
     */
    public void explode() {
        active.set(false);
        setTimer(0);
    }

    /**
     * Restituisce il timer della bomba.
     *
     * @return il timer della bomba
     */
    public double getTimer() {
        return timer;
    }

    /**
     * Imposta il timer della bomba.
     *
     * @param time il timer della bomba
     */
    public void setTimer(double time) {
        timer = time;
    }

    /**
     * Restituisce true se la bomba è camminabile, altrimenti restituisce false.
     *
     * @return true se la bomba è camminabile, altrimenti false
     */
    public double getTotalTime() {
        return totalTime;
    }

    /**
     * Restituisce la lista delle posizioni detonate dalla bomba.
     *
     * @return la lista delle posizioni detonate dalla bomba
     */
    public List<String> getDetonatePositions() {
        return detonatePositions;
    }

    /**
     * Aggiunge una posizione alla lista delle posizioni detonate dalla bomba.
     *
     * @param x la coordinata x della posizione
     * @param y la coordinata y della posizione
     */
    public void addDetonatePosition(int x, int y) {
        detonatePositions.add(x + "," + y);
    }

    /**
     * Restituisce true se la bomba contiene una posizione detonata specificata, altrimenti restituisce false.
     *
     * @param x la coordinata x della posizione
     * @param y la coordinata y della posizione
     * @return true se la lista delle posizioni detonate contiene le coordinate di input, altrimenti false
     */
    public boolean containsDetonatePosition(int x, int y) {
        return detonatePositions.contains(x + "," + y);
    }
    

    /**
        * Aggiorna lo stato della bomba.
        * 
        * @param elapsed il tempo trascorso dall'ultimo aggiornamento in millisecondi
        */
    public void update(double elapsed) {
        if (isActive()) {
            timer -= elapsed;
            walkableTime -= elapsed;
            if (walkableTime <= 0) {
                setWalkable(false);
            }
            if (timer <= 0) {
                // Bomb should explode
                explode();
            }
        }
    }
}
