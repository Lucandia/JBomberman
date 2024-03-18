public class PowerUpBlast extends PowerUp{

    public PowerUpBlast(int x, int y) {
        super(x, y, PowerUpType.blast);
    }

    @Override
    public void applyPowerUp(PlayerModel playerModel){
        playerModel.increaseBombRadius();
    }

}
