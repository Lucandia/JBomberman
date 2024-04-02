package com.lucandia;
public class PlayerController {
    private PlayerModel model;
    private EntityView view;
    private int audoDelay = 40;


    public PlayerController(PlayerModel model, EntityView view) {
        this.model = model;
        this.view = view;
    }

    public void input(String directionString) {    
        if (directionString != null) {
            model.startMoving(directionString);
        }
        else {
            model.stopMoving();
        }
    }
    
    public PlayerModel getModel() {
        return model;
    }

    public void update(double elapsed) {
        model.update(elapsed);
        view.update(elapsed);
        if (model.isMoving()) {
            audoDelay -= elapsed;
            if (audoDelay <= 0) {
                AudioUtils.playSoundEffect("Walking.mp3");
                audoDelay = 40;
            }
        }
    }

}
