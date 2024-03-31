package com.lucandia;
public class EmptyTile extends Tile{
    private EntityModel occupant = null;

    public EmptyTile(int x, int y) {
        super(x, y, true, false, true);
    }

    // Method to check if the tile is occupied
    public boolean isOccupied() {
        return occupant != null;
    }

    public boolean isDetonable() {
        return isOccupied();
    }

    // Method to get the occupant
    public EntityModel getOccupant() {
        return occupant;
    }

    // Method to set the occupant
    public void setOccupant(EntityModel occupant) {
        this.occupant = occupant;
    }
}