public class MapModel {
    private int level;
    // private TileType[][] tiles;

    public MapModel(int level, int width, int height) {
        this.level = level;
        // tiles = new TileType[width][height];
        // Initialize the map with default tiles
    }

    public int getLevel() {
        return level;
    }

    // Methods to update and query the map
}
