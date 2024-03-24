public class PowerUp extends SpecialTile{
    private boolean applied = false;
    private PowerUpBehaviour behaviour;

    public PowerUp(int x, int y, SpecialTileType type) {
        super(x, y, type);
        if (type == SpecialTileType.pupBlast) {
            this.behaviour = new PowerUpBlast();
        }
        else if (type == SpecialTileType.pupBomb) {
            this.behaviour = new PowerUpBomb();
        }
        else if (type == SpecialTileType.pupSpeed) {
            this.behaviour = new PowerUpSpeed();
        }
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

    @Override
    public boolean isDetonable() {
        return true;
    }

    public void applyPowerUp(PlayerModel playerModel) {
        behaviour.applyPowerUp(playerModel);
    };
}
