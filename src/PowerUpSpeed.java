public class PowerUpSpeed implements PowerUpBehaviour{
    
    @Override
    public void applyPowerUp(PlayerModel playerModel){
        playerModel.increaseSpeed();
    }

}
