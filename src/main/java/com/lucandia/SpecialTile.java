package com.lucandia;
public class SpecialTile extends EmptyTile{
    protected SpecialTileType type;

    public SpecialTile(int x, int y, SpecialTileType type) {
        super(x, y);
        setType(type);
        setDisplayable(true);
    }

    public SpecialTileType getType() {
        return type;
    }

    public void setType(SpecialTileType type) {
        this.type = type;
    }
}
