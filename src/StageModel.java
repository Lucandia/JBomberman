import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StageModel {
    private final int width = 17;
    private final int height = 13;
    private final int tileSize = 16; // Assuming each tile is 16x16 pixels
    private final int freeSlots = 110;
    private Tile[][] tiles = new Tile[width][height];
    private Random rand = new Random();
    

    public StageModel() {
        this(0.1, 0.1); // Default destructible and non-destructible percentages
    }

    public StageModel(double destructiblePercentage, double nonDestructiblePercentage) {
        // Calculate the total number of free positions
        int destructibleTilesCount = (int) (freeSlots * destructiblePercentage);
        int nonDestructibleTilesCount = (int) ((freeSlots-destructibleTilesCount) * nonDestructiblePercentage);

        // Fill the stage with non-walkable borders and predefined tiles
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (x < 2 || x > width - 3 || y == 0 || y == height - 1 || (x % 2 == 1 && y % 2 == 0)) {
                    tiles[x][y] = new Tile(x, y, false, false); // Non-destructible and not displayable
                }
            }
        }

        List<int[]> freePositions = new ArrayList<>();
        for (int x = 2; x < width - 2; x++) {
            for (int y = 1; y < height - 1; y++) {
                // keep the top right corner always free to start the game
                if (tiles[x][y] == null && (x != 2 && y != 1) && (x != 2 && y != 2) && (x != 3 && y != 1)) {
                    freePositions.add(new int[] {x, y});
                }
            }
        }

        // Randomly place destructible and non-destructible tiles
        for (int i = 0; i < destructibleTilesCount; i++) {
            int[] position = freePositions.remove(rand.nextInt(freePositions.size()));
            tiles[position[0]][position[1]] = new Tile(position[0], position[1], true);
        }
        for (int i = 0; i < nonDestructibleTilesCount; i++) {
            int[] position = freePositions.remove(rand.nextInt(freePositions.size()));
            tiles[position[0]][position[1]] = new Tile(position[0], position[1], false);
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

    public int[] getTileStartCoordinates(int x, int y) {
        int tileX = (int) (x / tileSize);
        int tileY = (int) (y / tileSize);
        return new int[] {tileX * tileSize, tileY * tileSize};
    }

    public boolean isBorder(int x, int y) {
        return x < 2 * tileSize || x >= (width - 3) * tileSize || y < tileSize || y >= (height - 2) * tileSize;
    }

    public void destroyTile(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height && tiles[x][y] != null && tiles[x][y].isDestructible()) {
            tiles[x][y] = null;
        }
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
