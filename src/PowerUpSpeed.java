public class PowerUpSpeed extends PowerUp{

    public PowerUpSpeed(int x, int y) {
        super(x, y, PowerUpType.speed);
    }

    @Override
    public void applyPowerUp(PlayerModel playerModel){
        playerModel.increaseSpeed();
    }

}
