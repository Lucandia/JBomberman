import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javafx.scene.layout.Pane;

public class EnemiesController {
    private List<EnemyModel> enemies = new ArrayList<EnemyModel>();
    private List<EntityView> views = new ArrayList<EntityView>();
    private List<int[]> directions = new ArrayList<int []>();

    public EnemiesController(int numberOfEnemies, StageModel stageModel, Pane gameLayer, int level) {
        List<int[]> freeTileIndex = stageModel.getFreeTileIndex();
        Random random = new Random();
        int i = 1;
        while (i <= numberOfEnemies) {
            int randomIndex = random.nextInt(freeTileIndex.size());
            int[] tileIndex = freeTileIndex.get(randomIndex);
            if (((EmptyTile) stageModel.getTile(tileIndex[0], tileIndex[1])).getOccupant() != null) {
                continue;
            }
            EnemyModel enemyModel;
            EntityView enemyView;
            // if ((i % (numberOfEnemies / level )) != 0) { // alternate between enemy types
                enemyModel = new EnemyModel(tileIndex[0] * stageModel.getTileSize(), tileIndex[1] * stageModel.getTileSize() - 10,  stageModel);
                enemyView = new EntityView(enemyModel, "enemy1");
            // }
            // else {
            //     enemyModel = new EnemyModel2(tileIndex[0] * stageModel.getTileSize(), tileIndex[1] * stageModel.getTileSize() - 10, stageModel);
            //     enemyView = new EntityView(enemyModel, "enemy2", true, 6);
            // }
            if (stageModel.getTile(tileIndex[0], tileIndex[1] - 1) instanceof EmptyTile || stageModel.getTile(tileIndex[0], tileIndex[1] + 1) instanceof EmptyTile) {
                enemyModel.startMoving("UP");
            } else {
                enemyModel.startMoving("RIGHT");
            }
            gameLayer.getChildren().add(enemyView.getEntitySprite());
            addEnemy(enemyModel, enemyView);
            i++;
        }
    }

    public void addEnemy(EnemyModel enemy, EntityView enemyView) {
        enemies.add(enemy);
        views.add(enemyView);
        directions.add(enemy.getLastDirection());
    }

    public void removeEnemy(EnemyModel enemy) {
        int index = enemies.indexOf(enemy);
        if (index != -1) {
            enemies.remove(index);
            views.remove(index);
            directions.remove(index);
        }
    }

    public List<EnemyModel> getEnemies() {
        return enemies;
    }

    public void update(double elapsed) {
        for (int i = enemies.size()-1; i >= 0; i--) {
            EnemyModel enemy = enemies.get(i);
            if (enemy.isDead()) {
                views.get(i).update(elapsed);
                removeEnemy(enemy);
                continue;
            }
            enemy.update(elapsed);
            int[] lastDirection = enemies.get(i).getLastDirection();
            if (enemy.getLastDirection()[0] != lastDirection[0] || enemy.getLastDirection()[1] != lastDirection[1]) {
                directions.set(i, lastDirection);
            }
            views.get(i).update(elapsed);
        }
    }
}
