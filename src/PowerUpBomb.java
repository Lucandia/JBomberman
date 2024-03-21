public class PowerUpBomb implements PowerUpBehaviour{
    
    @Override
    public void applyPowerUp(PlayerModel playerModel){
        playerModel.increaseBombCapacity();
    }
}
