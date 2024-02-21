public class Tile {
    protected boolean isDestructible;
    protected boolean isDisplayable = true;
    protected int x, y;

    public Tile(int x, int y, boolean isDestructible) {
        this.x = x;
        this.y = y;
        this.isDestructible = isDestructible;
    }

    public Tile(int x, int y, boolean isDestructible, boolean isDisplayable) {
        this.x = x;
        this.y = y;
        this.isDestructible = isDestructible;
        this.isDisplayable = isDisplayable;
    }

    public boolean isDestructible() {
        return isDestructible;
    }

    public boolean isDisplayable() {
        return isDisplayable;
    }

    public void update() {

    }; // Implement according to your rendering logic
}
