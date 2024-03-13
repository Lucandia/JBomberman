import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StageModel {
    private final int width = 17;
    private final int height = 13;
    private final int tileSize = 16; // Assuming each tile is 16x16 pixels
    private final List<int[]> freeTileIndex = new ArrayList<>();
    private Tile[][] tiles = new Tile[width][height];
    private Random rand = new Random();
    

    public StageModel() {
        this(0.3, 0.05); // Default destructible and non-destructible percentages
    }

    public StageModel(double destructiblePercentage, double nonDestructiblePercentage) {
        final int freeSlots = 110; // Number of free slots in the stage
        // Calculate the total number of free positions
        int destructibleTilesCount = (int) (freeSlots * destructiblePercentage);
        int nonDestructibleTilesCount = (int) ((freeSlots-destructibleTilesCount) * nonDestructiblePercentage);

        // Fill the stage with non-walkable borders and predefined tiles
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (x < 2 || x > width - 3 || y == 0 || y == height - 1 || (x % 2 == 1 && y % 2 == 0)) {
                    tiles[x][y] = new Tile(x * tileSize, y * tileSize, false, false); // Non-destructible and not displayable
                }
            }
        }

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
    }

    public Tile getTile(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            return tiles[x][y];
        }
        return null; // Out of bounds
    }

    public Tile getTileAtPosition(int x, int y) {
        int tileX = (int) (x / tileSize);
        int tileY = (int) (y / tileSize);
        return getTile(tileX, tileY);
    }

    public List<int[]> getFreeTileIndex() {
        return freeTileIndex;
    }

    public void setTile(int x, int y, Tile tile) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            tiles[x][y] = tile;
        }
    }

    public BombModel getBombAtPosition(int x, int y) {
        int tileX = (int) (x / tileSize);
        int tileY = (int) (y / tileSize);
        if (getTile(tileX, tileY) instanceof BombModel) {
            return (BombModel) getTile(tileX, tileY);
        }
        return null;
    }

    public int[] getTileStartCoordinates(int x, int y) {
        int tileX = (int) (x / tileSize);
        int tileY = (int) (y / tileSize);
        return new int[] {tileX * tileSize, tileY * tileSize};
    }

    public boolean isBorder(int x, int y) {
        return x < 2 * tileSize || x >= (width - 3) * tileSize || y < tileSize || y >= (height - 2) * tileSize;
    }

    public boolean destroyTile(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height && tiles[x][y] != null) {
            if (!tiles[x][y].isDestructible()) return true;
            setTile(x, y, null);
            freeTileIndex.add(new int[] {x, y});
            return true;
        }
        return false;
    }

    public boolean destroyTileAtPosition(int x, int y) {
        int tileX = (int) (x / tileSize);
        int tileY = (int) (y / tileSize);
        return destroyTile(tileX, tileY);
    }

    public boolean canExplodeAtPosition(int x, int y) {
        Tile tile = getTileAtPosition(x, y);
        return tile == null || tile.isDestructible();
    }

    public boolean addBombAtPosition(int x, int y, int bombRadius) {
        int tileX = (int) (x / tileSize);
        int tileY = (int) (y / tileSize);
        if (tiles[tileX][tileY] != null) {
            return false;
        }
        tiles[tileX][tileY] = new BombModel(tileX * tileSize, tileY * tileSize, bombRadius);
        return true;
    }

    public int getTileSize() {
        return tileSize;
    }

    // Getters for width and height if needed
    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    // Additional methods as needed for game logic
}
