package com.esame;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;
import javafx.scene.layout.Pane;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Questa classe gestisce il controllo delle bombe nel gioco.
 * Mantiene una mappa delle bombe attive e delle loro Views e fornisce metodi per piazzare, detonare e aggiornare le bombe.
 */
public class BombController {
    
    /**
     * La lista delle bombe attive.
     */
    private List<BombModel> bombList = new <BombModel>ArrayList();

    /**
     * Pannello in cui vengono visualizzate le bombe.
     */
    private Pane pane;

    /**
     * Il modello dello stage.
     */
    private StageModel stage;

    /**
     * La coordinata x corrente del giocatore.
     */
    private IntegerProperty currentX = new SimpleIntegerProperty();

    /**
     * La coordinata y corrente del giocatore.
     */
    private IntegerProperty currentY = new SimpleIntegerProperty();


    /**
     * Il numero massimo di bombe che il giocatore può piazzare.
     */
    private IntegerProperty maxBombs = new SimpleIntegerProperty();


    /**
     * Il raggio di esplosione delle bombe.
     */
    private IntegerProperty bombRadius = new SimpleIntegerProperty();


    /**
     * Questa classe rappresenta il controller delle bombe nel gioco.
     * Si occupa di gestire la posizione e la detonazione delle bombe
     * in base al modello del bomberman.
     *
     * @param playerModel il modello del giocatore
     * @param pane il pannello in cui vengono visualizzate le bombe
     */
    public BombController(PlayerModel playerModel, Pane pane) {
        currentX.bind(playerModel.centerXProperty());
        currentY.bind(playerModel.centerYProperty());
        maxBombs.bind(playerModel.bombCapacityProperty());
        bombRadius.bind(playerModel.bombRadiusProperty());
        this.pane = pane;
        this.stage = playerModel.getStage();
    }

    
    /**
    * Gestisce l'input per piazzare una bomba.
    * Se il numero di bombe presenti sulla mappa è inferiore al numero massimo consentito,
    * viene calcolata la posizione della bomba in base alla posizione corrente del giocatore.
    * Se è possibile piazzare la bomba nella posizione calcolata, la bomba viene aggiunta alla 
    * mappa delle bombe, insieme alla sua view, e viene riprodotto un effetto sonoro.
    */
    public void input() {
        if (bombList.size() < maxBombs.get()) {
            int[] startPosition = stage.getTileStartCoordinates(currentX.get(), currentY.get());
            if (stage.addBombAtPosition(startPosition[0], startPosition[1], bombRadius.get())) {
                AudioUtils.playSoundEffect("PlaceBomb.mp3");
                BombModel bomb = stage.getBombAtPosition(startPosition[0], startPosition[1]);
                BombView bombView = new BombView(pane, stage);
                bomb.addListener(bombView);
                bombList.add(bomb);
            }
        }
    }

    /**
     * Questo metodo controlla se una bomba può distruggere una casella (una Tile) in una determinata posizione.
     * Restituisce true se la casella può essere distrutta, altrimenti restituisce false.
     *
     * @param bomb l'oggetto BombModel che rappresenta la bomba
     * @param x la coordinata x della posizione della casella
     * @param y la coordinata y della posizione della casella
     * @return true se la casella può essere distrutta, altrimenti false
     */
    public boolean destroyLimit(BombModel bomb, int x, int y) {
        // uso le stringe perche' se uso int[], equals non funziona bene (cerca il riferimento)
        Tile tile = stage.getTile(x, y);
        if (tile == null) return true; // Tile is out of bounds
        // the tile is destructible
        else if (!tile.isDestructible()) {
            return true;
        }
        else if (tile.isDetonable()) {
            bomb.addDetonatePosition(x, y);
            if (tile instanceof BombModel) {
                // Se la bomba e' gia' stata esaminata, non la esplodo
                DetonateBomb((BombModel) tile);
                return true;
            }
            else {
                stage.destroyTile(x, y);
                return true;
            }
        }
        bomb.addDetonatePosition(x, y);
        return false;
    }


    /**
     * Detona una bomba e distrugge i blocchi nelle direzioni orizzontali e verticali.
     * 
     * @param bomb la bomba da far detonare
     */
    public void DetonateBomb(BombModel bomb) {
        AudioUtils.playSoundEffect("BombExplodes.mp3");
        int blast = bomb.getBlastRadius();
        int tileX = (int) bomb.getX() / stage.getTileSize();
        int tileY = (int) bomb.getY() / stage.getTileSize();
        stage.destroyTile(tileX, tileY);
        for (int x = -1; x >= -blast; x--) {
            if (destroyLimit(bomb, tileX + x, tileY)) break;
        }
        for (int x = 1; x <= blast; x++) {
            if (destroyLimit(bomb, tileX + x, tileY)) break;
        }
        for (int y = -1; y >= -blast; y--) {
            if (destroyLimit(bomb, tileX, tileY + y)) break;
        }
        for (int y = 1; y <= blast; y++) {
            if (destroyLimit(bomb, tileX, tileY + y)) break;
        }
        bomb.explode();
    }


    /**
    * Aggiorna tutte le bombe, fa detonare quelle che non sono piu' attive e rimuove le bombe inattive.
    * 
    * @param elapsed il tempo trascorso dall'ultimo aggiornamento
    */
    public void updateState(double elapsed) {
        // Update all bombs
        bombList.forEach(bomb -> bomb.updateState(elapsed));
        // Detonate bombs that not active anymore
        bombList.stream()
            .filter(bomb -> !bomb.isActive())
            .forEach(bomb -> DetonateBomb(bomb));
        // Remove inactive bombs
        bombList.removeIf(bomb -> !bomb.isActive());
    }
}
