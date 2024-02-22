import java.util.ArrayList;
import javafx.scene.layout.Pane;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class BombController {
    private ArrayList<BombModel> bombs = new ArrayList<>();
    private ArrayList<BombView> bombViews = new ArrayList<>();
    private Pane pane;
    private StageModel stage;
    private IntegerProperty currentX = new SimpleIntegerProperty();
    private IntegerProperty currentY = new SimpleIntegerProperty();
    private int[] bombPlayerYOffset;
    private IntegerProperty maxBombs = new SimpleIntegerProperty();

    public BombController(PlayerModel playerModel, Pane pane) {
        currentX.bind(playerModel.xProperty());
        currentY.bind(playerModel.yProperty());
        maxBombs.bind(playerModel.bombCapacityProperty());
        bombPlayerYOffset = playerModel.getBoundingOffset();
        this.pane = pane;
        this.stage = playerModel.getStage();
    }

    public void input() {
        if (bombs.size() < maxBombs.get()) {
            int[] startPosition = stage.getTileStartCoordinates(currentX.get() + bombPlayerYOffset[0], currentY.get() + bombPlayerYOffset[1]);
            BombModel bomb = new BombModel(startPosition[0], startPosition[1], stage, 2);
            bombs.add(bomb);
            BombView bombView = new BombView(bomb, pane);
            bombViews.add(bombView);
            stage.addBombAtPosition(startPosition[0], startPosition[1]);
        }
    }

    public BombModel[] getBombs() {
        return bombs.toArray(new BombModel[0]);
    }

    public void removeBomb(BombModel bomb) {
        bombs.remove(bomb);
    }

    public void update(double elapsed) {
        for (BombModel bomb : bombs) {
            bomb.update(elapsed);
        }
        removeInactiveBombs();
    }

    public void removeInactiveBombs() {
        for (int i = bombs.size() - 1; i >= 0; i--) { // itera in ordine inverso per evitare problemi di rimozione
            BombModel bomb = bombs.get(i);
            if (!bomb.isActive()) {
                int blast = bomb.getBlastRadius();
                for (int x = - blast; x <= + blast; x++) {
                    stage.destroyTileAtPosition(bomb.getX() + x * stage.getTileSize(), bomb.getY());
                }
                // for (int y = - blast; y <= blast; y++) {
                //     stage.destroyTileAtPosition(bomb.getX(), bomb.getY() + y * stage.getTileSize());
                // }
                BombView bombView = bombViews.get(i);
                bombView.removeFromPane();
                bombViews.remove(i);
                bombs.remove(i);
            }
        }
    }
}
