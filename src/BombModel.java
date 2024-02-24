public class BombModel extends Tile{

    private int blastRadius = 2; // Default blast radius
    private boolean active = true;
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

    public boolean isActive() {
        return active;
    }

    public void explode() {
        active = false;
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
