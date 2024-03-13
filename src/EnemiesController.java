import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javafx.scene.layout.Pane;

public class EnemiesController {
    private List<EnemyModel> enemies = new ArrayList<EnemyModel>();
    private List<EntityView> views = new ArrayList<EntityView>();
    private List<int[]> directions = new ArrayList<int []>();

    public EnemiesController(int numberOfEnemies, String enemyType, StageModel stageModel, Pane gameLayer) {
        List<int[]> freeTileIndex = stageModel.getFreeTileIndex();
        Random random = new Random();
        for (int i = 0; i < numberOfEnemies; i++) {
            int randomIndex = random.nextInt(freeTileIndex.size());
            int[] tileIndex = freeTileIndex.get(randomIndex);
            EnemyModel enemyModel = new EnemyModel(tileIndex[0] * stageModel.getTileSize(), tileIndex[1] * stageModel.getTileSize() - 10, 0.8, new int[] {7, 5}, new int[] {8, 17}, stageModel, Integer.parseInt(enemyType) * 100);
            if (stageModel.getTile(tileIndex[0], tileIndex[1] - 1) == null || stageModel.getTile(tileIndex[0], tileIndex[1] + 1) == null) {
                enemyModel.startMoving("UP");
            } else {
                enemyModel.startMoving("RIGHT");
            }
            EntityView enemyView = new EntityView(enemyModel, "enemy" + enemyType, 4);
            gameLayer.getChildren().add(enemyView.getEntitySprite());
            addEnemy(enemyModel, enemyView);
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

    public void update(double elapsed) {
        for (int i = 0; i < enemies.size(); i++) {
            EnemyModel enemy = enemies.get(i);
            enemy.update(elapsed);
            int[] lastDirection = enemies.get(i).getLastDirection();
            if (enemy.getLastDirection()[0] != lastDirection[0] || enemy.getLastDirection()[1] != lastDirection[1]) {
                directions.set(i, lastDirection);
            }
            String stringDirection = "";
            if (directions.get(i)[0] == 0 && directions.get(i)[1] == 1) stringDirection = "DOWN";
            else if (directions.get(i)[0] == 0 && directions.get(i)[1] == -1) stringDirection = "UP";
            else if (directions.get(i)[0] == 1 && directions.get(i)[1] == 0) stringDirection = "RIGHT";
            else if (directions.get(i)[0] == -1 && directions.get(i)[1] == 0) stringDirection = "LEFT";
            if (enemy.isMoving()) views.get(i).startWalking(stringDirection);
            else views.get(i).stopWalking();
        }
    }
}
