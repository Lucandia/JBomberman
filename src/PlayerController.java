public class PlayerController {
    private PlayerModel model;
    private EntityView view;

    public PlayerController(PlayerModel model, EntityView view) {
        this.model = model;
        this.view = view;
    }

    public void input(String directionString) {    
        if (directionString != null) {
            model.startMoving(directionString);
            view.startWalking(directionString);
        }
        else {
            model.stopMoving();
            view.stopWalking(); // ferma il player
        }
    }
    
    public PlayerModel getModel() {
        return model;
    }

    public void update(double elapsed) {
        model.update(elapsed);
    }
}
