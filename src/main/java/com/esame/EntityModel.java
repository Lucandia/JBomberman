package com.esame;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import java.util.List;
import java.util.ArrayList;


/**
 * Classe astratta che rappresenta il modello di un'entità nel gioco.
 * Estende la classe XYModel.
 */
public abstract class EntityModel extends XYModel implements Observable{

    /**
     * La lista degli osservatori dello stato dell'entità.
     */
    protected List<EntityStateObserver> observers = new ArrayList<>();

    /**
     * La vita dell'entità.
     */
    protected final IntegerProperty life = new SimpleIntegerProperty(100);

    /**
     * La velocità dell'entità.
     */
    protected final DoubleProperty velocity = new SimpleDoubleProperty(1);

    /**
     * La coordinata X del centro dell'entità.
     */
    protected IntegerProperty centerX = new SimpleIntegerProperty();

    /**
     * La coordinata Y del centro dell'entità.
     */
    protected IntegerProperty centerY = new SimpleIntegerProperty();

    /**
     * Il tempo trascorso dall'ultimo movimento.
     */
    protected double timeSinceLastMove = 0.0;

    /**
     * Il tempo di ritardo tra i movimenti.
     */
    protected double delayMove = 0.05; // Time in seconds between moves

    /**
     * La bounding box dell'entità.
     */
    protected final int[] boundingBox = {0, 0};

    /**
     * L'offset della bounding box.
     */
    protected final int[] boundingOffset = {0, 0};

    /**
     * Indica se l'entità si sta muovendo.
     */
    protected boolean isMoving = false;

    /**
     * L'ultima direzione dell'entità.
     */
    protected int[] lastDirection = {0, 0};

    /**
     * Lo stage di appartenenza dell'entità.
     */
    protected StageModel stage;

    /**
     * Le caselle occupate dall'entità.
     */
    protected ArrayList<EmptyTile> occupiedTiles = new ArrayList<>();

    /**
     * Costruisce un nuovo oggetto EntityModel con la posizione (x, y) specificata e lo stage di appartenenza.
     * 
     * @param x la coordinata x dell'entità
     * @param y la coordinata y dell'entità
     * @param stage lo stage di appartenenza dell'entità
     */
    public EntityModel(int x, int y, StageModel stage) {
        this(x, y, new int[] {16, 16}, new int[] {16, 16}, 100, null);
    }

    /**
     * Costruisce un nuovo oggetto EntityModel con la posizione (x, y) specificata, lo stage di appartenenza e la vita iniziale.
     * 
     * @param x la coordinata x dell'entità
     * @param y la coordinata y dell'entità
     * @param boundingBox l'array {x, y} che rappresenta la bounding box dell'entità
     * @param boundingOffset l'array {x, y} che rappresenta l'offset della bounding box
     * @param life la vita iniziale dell'entità
     * @param stage lo stage di appartenenza dell'entità
     */
    public EntityModel(int x, int y, int[] boundingBox, int[] boundingOffset,  int life, StageModel stage) {
        super(x, y);
        this.stage = stage;
        this.life.set(life);
        if (boundingBox!=null && boundingOffset.length == 2){
            this.boundingBox[0] = boundingBox[0];
            this.boundingBox[1] = boundingBox[1];
        }
        if (boundingOffset!=null && boundingOffset.length == 2){
            this.boundingOffset[0] = boundingOffset[0];
            this.boundingOffset[1] = boundingOffset[1];
        }
        centerX.bind(this.x.add(this.boundingOffset[0]));
        centerY.bind(this.y.add(this.boundingOffset[1]));
        setOccupiedTiles();
    }


    /**
     * Aggiunge un osservatore dello stato dell'entità.
     * 
     * @param listener l'osservatore da aggiungere
     */
    @Override
    public void addListener(InvalidationListener listener) {
        observers.add((EntityStateObserver) listener);
    }

    /**
     * Rimuove un osservatore dello stato dell'entità.
     * 
     * @param listener l'osservatore da rimuovere
     */
    @Override
    public void removeListener(InvalidationListener listener) {
        observers.remove((EntityStateObserver) listener);
    }

    /**
     * Notifica gli osservatori sullo stato dell'entità.
     */
    protected void notifyListeners() {
        for (EntityStateObserver observer : observers) {
            observer.update(this);
        }
    }

    /**
     * Toglie vita all'entità.
     * 
     * @param amount la quantità di vita da rimuovere
     */
    public void loseLife(int amount) {
        AudioUtils.playSoundEffect("LoseLife.mp3");
        this.life.set(getLife() - amount);
        if (isDead()) {
            clearOccupiedTiles();
        }
        notifyListeners();
    }

    /**
     * Controlla se l'entità è morta.
     * 
     * @return true se l'entità è morta, false altrimenti
     */
    public boolean isDead() {
        return getLife() <= 0;
    }

    /**
     * Imposta la vita dell'entità.
     * 
     * @param amount la quantità di vita da impostare
     */
    public void setLife(int amount) {
        this.life.set(amount);
        notifyListeners();
    }

    /**
     * Ritorna la proprietà della vita dell'entità.
     * (il IntegerProperty è un tipo di ObservableValue che contiene un valore di tipo int)
     * 
     * @return l' IntegerProperty della vita 
     */
    public int getLife() {
        return life.get();
    }


    /**
     * Ritorna la proprietà della velocita' dell'entità.
     * (il DoubleProperty è un tipo di ObservableValue che contiene un valore di tipo double)
     * 
     * @return il DoubleProperty della velocità 
     */
    public DoubleProperty velocityProperty() {
        return velocity;
    }

    /**
     * Ritorna la velocità dell'entità.
     * 
     * @return la velocità dell'entità
     */
    public double getVelocity() {
        return velocity.get();
    }

    /**
     * Imposta la velocità dell'entità.
     * 
     * @param velocity la velocità da impostare
     */
    public void setVelocity(double velocity) {
        this.velocity.set(velocity);
        notifyListeners();
    }

    /**
     * Restituisce il modello dello stage.
     *
     * @return il modello dello stage
     */
    public StageModel getStage() {
        return stage;
    }

    /**
     * Restituisce la bounding box dell'entità.
     *
     * @return l'array {x,y} che rappresenta la bounding box dell'entità
     */
    public int[] getBoundingBox() {
        return boundingBox;
    }

    /**
     * Restituisce l'offset della bounding box.
     *
     * @return l'array {x,y} che rappresenta l'offset della bounding box
     */
    public int[] getBoundingOffset() {
        return boundingOffset;
    }

    /**
     * Restituisce l'ultima direzione dell'entità.
     *
     * @return l'array {x,y} che rappresenta l'ultima direzione dell'entità
     */
    public int[] getLastDirection() {
        return lastDirection;
    }

    /**
     * Restituisce una stringa che rappresenta l'ultima direzione dell'entità.
     *
     * @return la stringa che rappresenta l'ultima direzione dell'entità
     */
    public String getLastDirectionString() {
        if (lastDirection[0] == 0 && lastDirection[1] == -1) return "UP";
        else if (lastDirection[0] == 0 && lastDirection[1] == 1) return "DOWN";
        else if (lastDirection[0] == -1 && lastDirection[1] == 0) return "LEFT";
        else if (lastDirection[0] == 1 && lastDirection[1] == 0) return "RIGHT";
        else return "";
    }

    /**
     * Restituisce true se l'entità si sta muovendo, altrimenti restituisce false.
     *
     * @return true se l'entità si sta muovendo, altrimenti false.
     */
    public boolean isMoving() {
        return isMoving;
    }

    /**
     * Imposta il bounding box dell'entità.
     *
     * @param boundingBox un array di interi contenente le coordinate del bounding box
     */
    public void setBoundingBox(int[] boundingBox) {
        this.boundingBox[0] = boundingBox[0];
        this.boundingBox[1] = boundingBox[1];
    }

    /**
     * Imposta l'offset di delimitazione dell'entità.
     *
     * @param boundingOffset l'array contenente l'offset di delimitazione dell'entità, con il primo elemento rappresentante l'offset sull'asse x e il secondo elemento rappresentante l'offset sull'asse y
     */
    public void setBoundingOffset(int[] boundingOffset) {
        this.boundingOffset[0] = boundingOffset[0];
        this.boundingOffset[1] = boundingOffset[1];
    }

    /**
     * Imposta la mappa do gioco (stage) per l'entità.
     *
     * @param stage il modello dello stage
     */
    public void setStage(StageModel stage) {
        this.stage = stage;
        notifyListeners();
    }

    /**
     * Restituisce la proprietà SimpleIntegerProperty della coordinata X del centro dell'entità.
     *
     * @return la proprietà centerX come SimpleIntegerProperty
     */
    public SimpleIntegerProperty centerXProperty() {
        return (SimpleIntegerProperty) centerX;
    }

    /**
     * Restituisce la proprietà SimpleIntegerProperty della coordinata Y del centro dell'entità.
     *
     * @return la proprietà centerY come SimpleIntegerProperty
     */
    public SimpleIntegerProperty centerYProperty() {
        return (SimpleIntegerProperty) centerY;
    }

    /**
     * Restituisce la coordinata X del centro dell'entità.
     *
     * @return la coordinata X del centro dell'entità
     */
    public int getCenterX() {
        return centerX.get();
    }

    /**
     * Restituisce la coordinata Y del centro dell'entità.
     *
     * @return la coordinata Y del centro dell'entità
     */
    public int getCenterY() {
        return centerY.get();
    }

    /**
     * Calcola il centro di massa dell'entità.
     *
     * @return un array di interi contenente le coordinate x e y del centro di massa dell'entità
     */
    public int[] centerOfMass() {
        return new int[] {centerX.get(), centerY.get()};
    }

    /**
     * Cancella tutte le caselle occupate.
     */
    public void clearOccupiedTiles() {
        for (EmptyTile tile : occupiedTiles) {
            tile.setOccupant(null);
        }
        occupiedTiles.clear();
    }

    /**
     * Occupa le caselle su cui si trova l'entità.
     */
    public void setOccupiedTiles() {
        clearOccupiedTiles();     
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                // skip the cornern tiles {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
                if (x != 0 && y !=0 && (x == y || x == -y)) continue;
                int tileX = centerX.get() + x * boundingBox[0] / 2;
                int tileY = centerY.get() + y * boundingBox[1] / 2;
                EmptyTile tile = stage.getEmptyTileAtPosition(tileX, tileY);
                tile.setOccupant(this);
                occupiedTiles.add(tile);
            }
        }
    }


    /**
     * Sposta l'entità di una determinata quantità nelle direzioni specificate.
     *
     * @param dx La quantità di spostamento sull'asse x.
     * @param dy La quantità di spostamento sull'asse y.
     */
    public void move(int dx, int dy) {
        int x_move = (int) Math.round(this.velocityProperty().get() * Double.valueOf(dx)); // explicit cast to int
        int y_move = (int) Math.round(this.velocityProperty().get() * Double.valueOf(dy)); // explicit cast to int
        if (canMoveTo(dx, dy) && (checkCollision(dx, dy) == null || checkCollision(dx, dy) == this)) {
            xProperty().set(getX() + x_move);
            yProperty().set(getY() + y_move);
            // Update tiles occupancy
            setOccupiedTiles();
            notifyListeners();
        }
        else isMoving = false;
    }

    /**
     * Controlla se la casella in cui l'entita' si vuole muovere e' gia' occupata da un'altra entita'.
     * 
     * @param dx la quantita' di spostamento sull'asse x
     * @param dy la quantita' di spostamento sull'asse y
     * 
     * @return l'entita' che occupa la casella in cui l'entita' si vuole muovere
     */
    public EntityModel checkCollision(int dx, int dy) {
        int xSign = Integer.signum(dx);
        int ySign = Integer.signum(dy);
        int directionOffset = 3;
        int tileXCollision = (int) centerX.get() + xSign * boundingBox[0] / 2 + xSign * directionOffset;
        int tileYCollision = (int) centerY.get()  + ySign * boundingBox[1] / 2 + ySign * directionOffset;
        return stage.getEmptyTileAtPosition(tileXCollision, tileYCollision).getOccupant();
    }

    /**
     * Verifica se l'entità può spostarsi in una determinata direzione.
     * 
     * @param dx lo spostamento sull'asse x
     * @param dy lo spostamento sull'asse y
     * @return true se l'entità può spostarsi, false altrimenti
     */
    protected boolean canMoveTo(int dx, int dy) {
        int xSign = Integer.signum(dx);
        int ySign = Integer.signum(dy);
        int tileX = centerX.get();
        int tileY = centerY.get() ;
        int directionOffset = 2;
        Tile collisionTile;
        if (dx != 0){
            int tileXCollision = (int) centerX.get() + xSign * boundingBox[0] / 2 + xSign * directionOffset;
            collisionTile = stage.getTileAtPosition(tileXCollision, tileY);
            // controlla la tile direttamente sull'asse x della bounding box
            if (!collisionTile.isWalkable()) return false;
            // scontro col bordo di una tile mentre sopra non c'e' nulla --> e' uno spigolo e bisogna fermarsi
            int tileYCollision = (int) centerY.get()  - boundingBox[1] / 2;
            collisionTile = stage.getTileAtPosition(tileXCollision, tileYCollision);
            if (stage.getTileAtPosition(tileX, tileYCollision) instanceof EmptyTile && !collisionTile.isWalkable()) return false;
            tileYCollision = (int) centerY.get()  + boundingBox[1] / 2;
            collisionTile = stage.getTileAtPosition(tileXCollision, tileYCollision);
            if (stage.getTileAtPosition(tileX, tileYCollision) instanceof EmptyTile && !collisionTile.isWalkable()) return false;
        }
        // stessa cosa se lo spostamento e' sull'asse y
        else if (dy != 0){
            int tileYCollision = (int) centerY.get() + ySign * boundingBox[1] / 2 + ySign * directionOffset;
            collisionTile = stage.getTileAtPosition(tileX, tileYCollision);
            if (!collisionTile.isWalkable()) return false;
            int tileXCollision = (int) centerX.get() - boundingBox[0] / 2;
            collisionTile = stage.getTileAtPosition(tileXCollision, tileYCollision);
            if (stage.getTileAtPosition(tileXCollision, tileY) instanceof EmptyTile && !collisionTile.isWalkable()) return false;
            tileXCollision = (int) centerX.get() + boundingBox[0] / 2;
            collisionTile = stage.getTileAtPosition(tileXCollision, tileYCollision);
            if (stage.getTileAtPosition(tileXCollision, tileY) instanceof EmptyTile && !collisionTile.isWalkable()) return false;
        }
        return true; // in tutte le direzioni della bouinding box non c'e' collisione
    }


    /**
     * Avvia il movimento dell'entità nella direzione specificata.
     *
     * @param direction un array di interi che rappresenta la direzione del movimento.
     *                  L'elemento in posizione 0 rappresenta la direzione sull'asse x,
     *                  mentre l'elemento in posizione 1 rappresenta la direzione sull'asse y.
     */
    public void startMoving(int[] direction) {
            lastDirection[0] = direction[0];
            lastDirection[1] = direction[1];
            isMoving = true;
            if (direction[0] == 0 && direction[1] == 0) {
                isMoving = false;
            }
    }

    /**
     * Avvia il movimento dell'entità nella direzione specificata.
     *
     * @param direction la direzione del movimento (UP, DOWN, LEFT, RIGHT)
     */
    public void startMoving(String direction) {
        switch (direction) {
            case "UP":
                lastDirection[0] = 0;
                lastDirection[1] = -1;
                isMoving = true;
                break;
            case "DOWN":
                lastDirection[0] = 0;
                lastDirection[1] = 1;
                isMoving = true;
                break;
            case "LEFT":
                lastDirection[0] = -1;
                lastDirection[1] = 0;
                isMoving = true;
                break;
            case "RIGHT":
                lastDirection[0] = 1;
                lastDirection[1] = 0;
                isMoving = true;
                break;
            default:
                isMoving = false;
                break;
        }
    }

    /**
    * Ferma il movimento dell'entità.
    */
    public void stopMoving() {
        isMoving = false;
        notifyListeners();
    }

    /**
     * Aggiorna il modello dell'entità.
     * 
     * @param elapsedTime il tempo trascorso dall'ultimo aggiornamento
     */
    public void updateState(double elapsedTime){
        if (isDead()) {
            return;
        }
        timeSinceLastMove += elapsedTime;
        // il tempo di delay viene diviso per la velocita' in modo da diminuire se aumenta la velocita'
        if (isMoving && timeSinceLastMove >= delayMove / velocity.get()) {
            move(lastDirection[0], lastDirection[1]);
            timeSinceLastMove = 0.0; // Reset the timer
        }
        notifyListeners();
    }

}
