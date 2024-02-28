import java.util.ArrayList;
import javafx.scene.layout.Pane;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class BombController {
    private ArrayList<int[]> bombsPosition = new ArrayList<>();
    private ArrayList<BombView> bombViews = new ArrayList<>();
    private Pane pane;
    private StageModel stage;
    private IntegerProperty currentX = new SimpleIntegerProperty();
    private IntegerProperty currentY = new SimpleIntegerProperty();
    private int[] bombPlayerYOffset;
    private IntegerProperty maxBombs = new SimpleIntegerProperty();
    private IntegerProperty bombRadius = new SimpleIntegerProperty();

    public BombController(PlayerModel playerModel, Pane pane) {
        currentX.bind(playerModel.xProperty());
        currentY.bind(playerModel.yProperty());
        maxBombs.bind(playerModel.bombCapacityProperty());
        bombRadius.bind(playerModel.bombRadiusProperty());
        bombPlayerYOffset = playerModel.getBoundingOffset();
        this.pane = pane;
        this.stage = playerModel.getStage();
    }

    public void input() {
        if (bombsPosition.size() < maxBombs.get()) {
            int[] startPosition = stage.getTileStartCoordinates(currentX.get() + bombPlayerYOffset[0], currentY.get() + bombPlayerYOffset[1]);
            if (stage.addBombAtPosition(startPosition[0], startPosition[1], bombRadius.get())) {
                bombsPosition.add(new int[]{startPosition[0], startPosition[1]});
                BombView bombView = new BombView(stage.getBombAtPosition(startPosition[0], startPosition[1]), pane, stage);
                bombViews.add(bombView);
            }
        }
    }

    public BombModel[] getBombsPosition() {
        return bombsPosition.toArray(new BombModel[0]);
    }

    public void removeBomb(int[] position) {
        bombsPosition.remove(position);
    }

    public void update(double elapsed) {
        for (int i = bombsPosition.size() - 1; i >= 0; i--) { // itera in ordine inverso per evitare problemi di rimozione
            int[] position = bombsPosition.get(i);
            BombModel bomb = stage.getBombAtPosition(position[0], position[1]);
            if (bomb == null) {
                bombViews.get(i).update();
                bombViews.remove(i);
                bombsPosition.remove(i);
            }
            else{
                bomb.update(elapsed);
                if (!bomb.isActive()) {
                    stage.DetonateBomb(bomb);
                    bombViews.get(i).update();
                    bombViews.remove(i);
                    bombsPosition.remove(i);
                }
            }
        }
    }
}
