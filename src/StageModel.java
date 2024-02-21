import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StageModel {
    private final int width = 17;
    private final int height = 13;
    private final int freeSlots = 110;
    private Tile[][] tiles = new Tile[width][height];
    private Random rand = new Random();

    public StageModel() {
        this(0.4, 0.1); // Default destructible and non-destructible percentages
    }

    public StageModel(double destructiblePercentage, double nonDestructiblePercentage) {
        // Calculate the total number of free positions
        int destructibleTilesCount = (int) (freeSlots * destructiblePercentage);
        int nonDestructibleTilesCount = (int) ((freeSlots-destructibleTilesCount) * nonDestructiblePercentage);

        // Fill the stage with non-walkable borders and predefined tiles
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (x < 2 || x > width - 3 || y == 0 || y == height - 1 || (x % 2 == 1 && y % 2 == 0) || (x == 2 && y == 1) || (x == 3 && y == 1) || (x == 2 && y == 2)) {
                    tiles[x][y] = new Tile(x, y, false, false); // Non-destructible and not displayable
                }
            }
        }

        List<int[]> freePositions = new ArrayList<>();
        for (int x = 2; x < width - 2; x++) {
            for (int y = 1; y < height - 1; y++) {
                if (tiles[x][y] == null) {
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

    // Getters for width and height if needed
    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    // Additional methods as needed for game logic
}
