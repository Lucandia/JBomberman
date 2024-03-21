public class PowerUpBlast implements PowerUpBehaviour{

    @Override
    public void applyPowerUp(PlayerModel playerModel){
        playerModel.increaseBombRadius();
    }

}
