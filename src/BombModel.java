import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.BooleanProperty;

public class BombModel extends Tile{

    private int blastRadius = 1; // Default blast radius
    private BooleanProperty active = new SimpleBooleanProperty(true);
    private double timer = 5.0; // Bomb timer in seconds
    private double walkableTime = 1.0; // time on which you can walk on the bomb

    public BombModel(int x, int y, int radius) {
        super(x, y, true, false, true);
        blastRadius = radius;
    }

    public BombModel(int x, int y, int radius, double time) {
        super(x, y, true, false, true);
        blastRadius = radius;
        time = timer;
    }

    public int getBlastRadius() {
        return blastRadius;
    }

    public BooleanProperty activeProperty() {
        return active;
    }

    public boolean isActive() {
        return active.get();
    }

    public void explode() {
        active.set(false);
        setTimer(0);
    }

    public double getTimer() {
        return timer;
    }

    public void setTimer(double time) {
        timer = time;
    }

    // Method to decrement the timer, call this method every second
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
