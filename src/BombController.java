import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import javafx.scene.layout.Pane;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class BombController {
    private LinkedHashMap<BombModel, BombView> bombMap = new LinkedHashMap<>();
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
        if (bombMap.size() < maxBombs.get()) {
            int[] startPosition = stage.getTileStartCoordinates(currentX.get() + bombPlayerYOffset[0], currentY.get() + bombPlayerYOffset[1]);
            if (stage.addBombAtPosition(startPosition[0], startPosition[1], bombRadius.get())) {
                bombMap.put(stage.getBombAtPosition(startPosition[0], startPosition[1]), new BombView(stage.getBombAtPosition(startPosition[0], startPosition[1]), pane, stage));
            }
        }
    }

    public boolean destroyTile(int x, int y) {
        // uso le stringe perche' se uso int[], equals non funziona bene (cerca il riferimento)
        Tile tile = stage.getTile(x, y);
        if (tile == null) return false;
        else if (tile.isDestructible()) {
            if (tile instanceof BombModel) {
                // Se la bomba e' gia' stata esaminata, non la esplodo
                DetonateBomb((BombModel) tile);
                return true;
            }
            else {
                stage.destroyTile(x, y);
                return true;
            }
        }
        return true;
    }

    public void DetonateBomb(BombModel bomb) {
        int blast = bomb.getBlastRadius();
        bomb.explode();
        bombMap.get(bomb).update();
        int tileX = (int) bomb.getX() / stage.getTileSize();
        int tileY = (int) bomb.getY() / stage.getTileSize();
        stage.setTile(tileX, tileY, null);
        for (int x = -1; x >= -blast; x--) {
            // if (avoidTiles.contains(tileX + x + "," + tileY)) continue;
            if (destroyTile(tileX + x, tileY)) break;
        }
        for (int x = 1; x <= blast; x++) {
            // if (avoidTiles.contains(tileX + x + "," + tileY)) continue;
            if (destroyTile(tileX + x, tileY)) break;
        }
        for (int y = -1; y >= -blast; y--) {
            // if (avoidTiles.contains(tileX + "," + tileY + y)) continue;
            if (destroyTile(tileX, tileY + y)) break;
        }
        for (int y = 1; y <= blast; y++) {
            // if (avoidTiles.contains(tileX + "," + tileY + y)) continue;
            if (destroyTile(tileX, tileY + y)) break;
        }
    }

    public void update(double elapsed) {
        List<BombModel> toRemove = new ArrayList<>();
        for (BombModel bomb : bombMap.keySet()) {
            bomb.update(elapsed);
            if (!bomb.isActive()) {
                DetonateBomb(bomb);
                toRemove.add(bomb); // Mark this bomb for removal
            }
        }
        for (BombModel bomb : toRemove) {
            bombMap.remove(bomb);
        }
    }
}
