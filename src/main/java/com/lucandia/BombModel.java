package com.lucandia;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.BooleanProperty;
import java.util.ArrayList;
import java.util.List;

public class BombModel extends EmptyTile{
    private List<String> detonatePositions = new ArrayList<>();
    private int blastRadius = 1; // Default blast radius
    private BooleanProperty active = new SimpleBooleanProperty(true);
    private final double totalTime = 2.5; // Bomb timer in seconds
    private double timer = 4; // Bomb timer in seconds
    private double walkableTime = 1.0; // time on which you can walk on the bomb

    public BombModel(int x, int y, int radius) {
        super(x, y);
        blastRadius = radius;
    }

    public BombModel(int x, int y, int radius, double time) {
        super(x, y);
        blastRadius = radius;
        time = timer;
    }

    public boolean isDetonable() {
        return true;
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

    public double getTotalTime() {
        return totalTime;
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

    public List<String> getDetonatePositions() {
        return detonatePositions;
    }

    public void addDetonatePosition(int x, int y) {
        detonatePositions.add(x + "," + y);
    }

    public boolean containsDetonatePosition(int x, int y) {
        return detonatePositions.contains(x + "," + y);
    }
}
