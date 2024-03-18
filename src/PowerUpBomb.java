public class PowerUpBomb extends PowerUp{

    public PowerUpBomb(int x, int y) {
        super(x, y, PowerUpType.bomb);
    }

    @Override
    public void applyPowerUp(PlayerModel playerModel){
        playerModel.increaseBombCapacity();
    }

}
