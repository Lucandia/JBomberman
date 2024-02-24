public class BombModel extends EntityModel{
    private int blastRadius = 2; // Default blast radius
    private boolean active = true;
    private double timer = 5.0; // Bomb timer in seconds
    private double walkableTime = 1.0; // time on which you can walk on the bomb

    public BombModel(int x, int y, StageModel stage, int radius) {
        super(x, y, 0, new int[] {16, 16}, new int[] {8, 8}, stage);
        blastRadius = radius;
    }

    public BombModel(int x, int y, StageModel stage, int radius, double time) {
        super(x, y, 0, new int[] {16, 16}, new int[] {8, 8}, stage);
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
                stage.getTileAtPosition(getX(), getY()).setWalkable(false);
            }
            if (timer <= 0) {
                // Bomb should explode
                explode();
            }
        }
    }
}
