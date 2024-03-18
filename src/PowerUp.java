public abstract class PowerUp extends EmptyTile{
    private PowerUpType type;
    private boolean applied = false;

    public PowerUp(int x, int y, PowerUpType type) {
        super(x, y);
        this.type = type;
        setDisplayable(true);
    }

    @Override
    public void setOccupant(EntityModel occupant) {
        super.setOccupant(occupant);
        if (applied) return;
        if (occupant instanceof PlayerModel) {
            applyPowerUp((PlayerModel) occupant);
        }
        setDisplayable(false);
        applied = true;
    }

    public abstract void applyPowerUp(PlayerModel playerModel);
 
    public PowerUpType getType() {
        return type;
    }
}
