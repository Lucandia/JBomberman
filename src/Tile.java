public class Tile extends XYModel{
    protected boolean isDestructible;
    protected boolean isDisplayable = true;
    protected boolean isWalkable = false;

    public Tile(int x, int y, boolean isDestructible) {
        super(x, y);
        this.isDestructible = isDestructible;
    }

    public Tile(int x, int y, boolean isDestructible, boolean isDisplayable) {
        super(x, y);
        this.isDestructible = isDestructible;
        this.isDisplayable = isDisplayable;
    }

    public Tile(int x, int y, boolean isDestructible, boolean isDisplayable, boolean isWalkable) {
        super(x, y);
        this.isDestructible = isDestructible;
        this.isDisplayable = isDisplayable;
        this.isWalkable = isWalkable;
    }

    // ignora le tile che non sono mostrate
    public boolean isDestructible() {
        return isDestructible;
    }

    public boolean isWalkable() {
        return isWalkable;
    }

    public boolean isDetonable() {
        return isDestructible && isDisplayable;
    }

    public void setWalkable(boolean walkable) {
        isWalkable = walkable;
    }

    public boolean isDisplayable() {
        return isDisplayable;
    }

    public void setDisplayable(boolean displayable) {
        isDisplayable = displayable;
    }

    public void update() {
    }; // Implement according to your rendering logic
}
