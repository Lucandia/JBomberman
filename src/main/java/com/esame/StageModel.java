package com.esame;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.Observable;
import javafx.beans.InvalidationListener;
import java.util.Random;

/**
 * Questa classe rappresenta il modello dello stage di gioco.
 * Contiene le informazioni sulle dimensioni dello stage e sui vari tipi di caselle.
 */
public class StageModel implements Observable {

    /**
     * La lista degli osservatori dello stage.
     */
    private List<StageObserver> observers = new ArrayList<>();

    /**
     * La larghezza dello stage.
     */
    private final int width = 17;

    /**
     * L'altezza dello stage.
     */
    private final int height = 13;

    /**
     * La probabilità di aggiungere una casella PowerUp.
     */
    private double powerUpProbability = 0.8; // 80% chance of adding a PowerUp tile

    /**
     * La dimensione di un singolo tile.
     */
    private final int tileSize = 16; // Assuming each tile is 16x16 pixels

    /**
     * Il numero di caselle libere nello stage.
     */
    final int freeSlots = 110; // Number of free slots in the stage

    /**
     * Il numero di caselle distruttibili nello stage.
     */
    private final int destructibleTilesStart;

    /**
     * Il numero di caselle distruttibili distrutte.
     */
    private int destructedTiles = 0;

    /**
     * L'elenco degli indici delle caselle libere.
     */
    private final List<int[]> freeTileIndex = new ArrayList<>();

    /**
     * La matrice delle caselle dello stage.
     */
    private Tile[][] tiles = new Tile[width][height];

    /**
     * L'oggetto Random per la generazione di numeri casuali.
     */
    private Random rand = new Random();

    /**
     * Il modello del giocatore.
     */
    private PlayerModel player;

    /**
     * Il danno inflitto da una bomba.
     */
    private int damage = 100;

    /**
     * La casella con la porta al livello successivo.
     */
    private SpecialTile nextLevelDoor;
    

    /**
     * Costruisce un nuovo oggetto StageModel con parametri predefiniti.
     */
    public StageModel() {
        this(0.2, 0.04); // Default destructible and non-destructible percentages
    }

    /**
     * Costruisce un nuovo oggetto StageModel con la percentuale specificata di caselle distruttibili e non distruttibili.
     *
     * @param destructiblePercentage   La percentuale di caselle distruttibili.
     * @param nonDestructiblePercentage La percentuale di caselle non distruttibili.
     */
    public StageModel(double destructiblePercentage, double nonDestructiblePercentage) {
        // Calculate the total number of free positions
        int destructibleTilesCount = (int) (freeSlots * destructiblePercentage);
        destructibleTilesStart = destructibleTilesCount;
        int nonDestructibleTilesCount = (int) ((freeSlots-destructibleTilesCount) * nonDestructiblePercentage);

        // Fill the stage with non-walkable borders and predefined tiles
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (x < 2 || x > width - 3 || y == 0 || y == height - 1 || (x % 2 == 1 && y % 2 == 0)) {
                    tiles[x][y] = new Tile(x * tileSize, y * tileSize, false, false); // Non-destructible and not displayable
                }
            }
        }
        // lascia la posizione in alto a sinsitra per il giocatore
        tiles[2][1] = new EmptyTile(2, 1);
        tiles[2][2] = new EmptyTile(2, 2);
        tiles[3][1] = new EmptyTile(3, 1);
        for (int x = 2; x < width - 2; x++) {
            for (int y = 1; y < height - 1; y++) {
                // lascia le posizioni nell'angolo in alto a sinistra libere per far muovere il giocatore
                if (tiles[x][y] == null && !(x == 2 && y == 1) && !(x == 2 && y == 2) && !(x == 3 && y == 1)) {
                    freeTileIndex.add(new int[] {x, y});
                }   
            }
        }
    
        // Randomly place destructible and non-destructible tiles
        for (int i = 0; i < destructibleTilesCount; i++) {
            int[] position = freeTileIndex.remove(rand.nextInt(freeTileIndex.size()));
            tiles[position[0]][position[1]] = new Tile(position[0] * tileSize, position[1] * tileSize, true);
        }
        for (int i = 0; i < nonDestructibleTilesCount; i++) {
            int[] position = freeTileIndex.remove(rand.nextInt(freeTileIndex.size()));
            tiles[position[0]][position[1]] = new Tile(position[0] * tileSize, position[1] * tileSize, false);
        }

        // in all the other freeTileIndex, add empty tiles
        for (int[] position : freeTileIndex) {
            tiles[position[0]][position[1]] = new EmptyTile(position[0], position[1]);
        }
    }

    /**
     * Imposta il giocatore principale nello stage.
     *
     * @param player il giocatore da impostare
     */
    public void setPlayer(PlayerModel player) {
        this.player = player;
    }

    /**
     * Restituisce la Tile corrispondente alle coordinate specificate.
     * Le coordinate x e y sono in termini di caselle, non di pixel.
     *
     * @param x la coordinata x della Tile
     * @param y la coordinata y della Tile
     * @return la Tile corrispondente alle coordinate specificate, o null se le coordinate sono fuori dai limiti
     */
    public Tile getTile(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            return tiles[x][y];
        }
        return null; // Out of bounds
    }

    /**
     * Restituisce una casella vuota nella posizione specificata.
     * Le coordinate x e y sono in termini di caselle, non di pixel.
     *
     * @param x la coordinata x della casella
     * @param y la coordinata y della casella
     * @return la casella vuota nella posizione specificata, o una casella vuota fittizia se la posizione è fuori dai limiti
     */
    public EmptyTile getEmptyTile(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            if (tiles[x][y] instanceof EmptyTile)
            return (EmptyTile) tiles[x][y];
        }
        return new EmptyTile(x, y); // return a dummy empty tile
    }

    /**
     * Restituisce la casella alla posizione specificata.
     * Le coordinate x e y sono in termini di pixel.
     *
     * @param x la coordinata x della posizione
     * @param y la coordinata y della posizione
     * @return la casella alla posizione specificata
     */
    public Tile getTileAtPosition(int x, int y) {
        int tileX = (int) (x / tileSize);
        int tileY = (int) (y / tileSize);
        return getTile(tileX, tileY);
    }

    /**
     * Restituisce la casella vuota alla posizione specificata.
     * Le coordinate x e y sono in termini di pixel.
     *
     * @param x la coordinata x della posizione
     * @param y la coordinata y della posizione
     * @return la casella vuota alla posizione specificata
     */
    public EmptyTile getEmptyTileAtPosition(int x, int y) {
        int tileX = (int) (x / tileSize);
        int tileY = (int) (y / tileSize);
        return getEmptyTile(tileX, tileY);
    }

    /**
     * Restituisce l'elenco degli indici delle caselle libere.
     *
     * @return l'elenco degli indici delle caselle libere
     */
    public List<int[]> getFreeTileIndex() {
        return freeTileIndex;
    }

    /**
     * Restituisce la casella con la porta al livello successivo.
     *
     * @return la casella al livello successivo
     */
    public SpecialTile getNextLevelDoor() {
        return nextLevelDoor;
    }

    /**
     * Imposta una casella nella posizione specificata.
     * Le coordinate x e y sono in termini di caselle, non di pixel.
     *
     * @param x la coordinata x della casella
     * @param y la coordinata y della casella
     * @param tile la casella da impostare
     */
    public void setTile(int x, int y, Tile tile) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            tiles[x][y] = tile;
        }
    }

    /**
     * Imposta la casella alla posizione specificata.
     * Le coordinate x e y sono in termini di pixel.
     *
     * @param x la coordinata x della posizione
     * @param y la coordinata y della posizione
     * @param tile la casella da impostare
     */
    public void setTileAtPosition(int x, int y, Tile tile) {
        int tileX = (int) (x / tileSize);
        int tileY = (int) (y / tileSize);
        setTile(tileX, tileY, tile);
    }

    /**
     * Restituisce la bomba alla posizione specificata.
     * Le coordinate x e y sono in termini di pixel.
     *
     * @param x la coordinata x della posizione
     * @param y la coordinata y della posizione
     * @return la bomba alla posizione specificata
     */
    public BombModel getBombAtPosition(int x, int y) {
        int tileX = (int) (x / tileSize);
        int tileY = (int) (y / tileSize);
        if (getTile(tileX, tileY) instanceof BombModel) {
            return (BombModel) getTile(tileX, tileY);
        }
        return null;
    }

    /**
     * Restituisce la casella vuota alla posizione specificata.
     * Le coordinate x e y sono in termini di pixel.
     *
     * @param x la coordinata x della posizione
     * @param y la coordinata y della posizione
     * @return la casella vuota alla posizione specificata
     */
    public int[] getTileStartCoordinates(int x, int y) {
        int tileX = (int) (x / tileSize);
        int tileY = (int) (y / tileSize);
        return new int[] {tileX * tileSize, tileY * tileSize};
    }

    /**
     * Verifica se la posizione specificata è un bordo.
     * Le coordinate x e y sono in termini di caselle.
     *
     * @param x la coordinata x della posizione
     * @param y la coordinata y della posizione
     * @return true se la posizione specificata è un bordo, false altrimenti
     */
    public boolean isBorder(int x, int y) {
        return x < 2 * tileSize || x >= (width - 3) * tileSize || y < tileSize || y >= (height - 2) * tileSize;
    }

    /**
     * Distrugge una casella nella posizione specificata.
     * Le coordinate x e y sono in termini di caselle, non di pixel.
     * 
     * @param x la coordinata x della casella da distruggere
     * @param y la coordinata y della casella da distruggere
     * @return true se la casella è stata distrutta con successo, false se la casella è fuori dai limiti
     */
    public boolean destroyTile(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height && tiles[x][y] != null) {
            // the tile is not destructible
            if (!tiles[x][y].isDestructible()) return true;
            // the tile is empty but occupied
            if ((tiles[x][y] instanceof EmptyTile) && ((EmptyTile) tiles[x][y]).isOccupied()) {
                EmptyTile occupiedTile = (EmptyTile) tiles[x][y];
                EntityModel occupant = occupiedTile.getOccupant();
                occupant.loseLife(damage);
                if (tiles[x][y] instanceof BombModel) {
                    setTile(x, y, new EmptyTile(x, y));
                    ((EmptyTile) tiles[x][y]).setOccupant(occupant);
                }
                if (player != null && !(occupant instanceof PlayerModel)) {
                    player.addScore(damage);
                }
            }
            // the tile is a special tile (but not a next level door)
            else if (tiles[x][y] instanceof PowerUp) {
                setTile(x, y, new EmptyTile(x, y));
            }
            // the tiles is destructible tile, in case add powerup or next level door
            else if (!(tiles[x][y] instanceof EmptyTile)){
                double nextLevelDoorProbability = (double) destructedTiles / destructibleTilesStart;
                if (this.nextLevelDoor == null && rand.nextDouble() < nextLevelDoorProbability) {
                        setTile(x, y, new SpecialTile(x, y, SpecialTileType.nextLevelDoor)); // Create a new instance of NextLevelDoor
                        this.nextLevelDoor = (SpecialTile) tiles[x][y];
                } 
                else if (rand.nextDouble() < powerUpProbability) {
                    setTile(x, y, new PowerUp(x, y, SpecialTileType.getRandomPowerUpType())); // Create a new instance of PowerUp
                }
                else setTile(x, y, new EmptyTile(x, y));
                destructedTiles++;
            }
            // the tile was simply empty
            else {
                setTile(x, y, new EmptyTile(x, y));
            }
            freeTileIndex.add(new int[] {x, y});
            // tile destroyed
            return true;
        }
        // tile out of bounds
        return false;
    }

    /**
    * Distrugge la casella alla posizione specificata.
    *
    * @param x la coordinata x della posizione
    * @param y la coordinata y della posizione
    * @return true se la casella è stata distrutta con successo, false altrimenti
    */
    public boolean destroyTileAtPosition(int x, int y) {
        int tileX = (int) (x / tileSize);
        int tileY = (int) (y / tileSize);
        return destroyTile(tileX, tileY);
    }

    /**
     * Verifica se la posizione specificata può esplodere.
     * Le coordinate x e y sono in termini di pixel.
     *
     * @param x la coordinata x della posizione
     * @param y la coordinata y della posizione
     * @return true se la posizione specificata può esplodere, false altrimenti
     */
    public boolean canExplodeAtPosition(int x, int y) {
        Tile tile = getTileAtPosition(x, y);
        return tile == null || tile.isDestructible();
    }

    /**
     * Aggiunge una bomba alla posizione specificata.
     * Le coordinate x e y sono in termini di pixel.
     *
     * @param x la coordinata x della posizione
     * @param y la coordinata y della posizione
     * @param bombRadius il raggio della bomba
     * @return true se la bomba è stata aggiunta con successo, false altrimenti
     */
    public boolean addBombAtPosition(int x, int y, int bombRadius) {
        int tileX = (int) (x / tileSize);
        int tileY = (int) (y / tileSize);
        Tile tile = tiles[tileX][tileY];
        if (!(tile instanceof EmptyTile) || tile instanceof BombModel || tile == this.nextLevelDoor) {
            return false;
        }
        EntityModel previousOccupant = ((EmptyTile) tiles[tileX][tileY]).getOccupant();
        tiles[tileX][tileY] = new BombModel(tileX * tileSize, tileY * tileSize, bombRadius);
        ((EmptyTile) tiles[tileX][tileY]).setOccupant(previousOccupant);
        return true;
    }

    
    /**
     * Restituisce la dimensione di un singolo tile.
     *
     * @return la dimensione del tile
     */
    public int getTileSize() {
        return tileSize;
    }

    /**
     * Restituisce la larghezza del modello dello stage.
     *
     * @return la larghezza del modello dello stage
     */
    public int getWidth() {
        return width;
    }

    /**
     * Restituisce l'altezza del modello dello stage.
     *
     * @return l'altezza del modello dello stage
     */
    public int getHeight() {
        return height;
    }

    /**
     * Restituisce un array di interi che rappresenta una posizione casuale di una casella libera nel modello dello stage.
     * 
     * @return un array di interi che rappresenta una posizione casuale di una casella libera, o null se non ci sono più caselle libere disponibili
     */
    public int[] getRandomFreeTile() {
        if (freeTileIndex.isEmpty()) {
            return null; // No more free tiles available
        }
        int randomIndex = rand.nextInt(freeTileIndex.size());
        return freeTileIndex.remove(randomIndex);
    }

    /**
     * Aggiunge un osservatore dello stage.
     */
    public void addListener(InvalidationListener listener) {
        observers.add((StageObserver) listener);
    }

    /**
     * Rimuove un osservatore dello stage.
     */
    public void removeListener(InvalidationListener listener) {
        observers.remove((StageObserver) listener);
    }

    /**
     * Notifica tutti gli osservatori dello stage.
     */
    public void notifyListeners() {
        observers.forEach(observer -> observer.update(this));
    }

}
