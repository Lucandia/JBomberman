package com.esame;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.scene.layout.Pane;

/**
 * Questa classe rappresenta il controller degli avversari nel gioco.
 * Gestisce la creazione, l'aggiunta, la rimozione e l'aggiornamento degli avversari.
 */
public class EnemiesController {

    /**
     * La lista degli avversari.
     */
    private List<EnemyModel> enemies = new ArrayList<EnemyModel>();

    /**
     * Costruttore della classe EnemiesController.
     * 
     * @param numberOfEnemies il numero di nemici da creare
     * @param stageModel il modello dello stage di gioco
     * @param gameLayer il layer di gioco in cui posizionare i nemici
     * @param level il livello di gioco corrente
     */
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
            if ((i % (numberOfEnemies / level )) != 0) { // alternate between enemy types
                enemyModel = new EnemyModel(tileIndex[0] * stageModel.getTileSize(), tileIndex[1] * stageModel.getTileSize() - 10,  stageModel);
                enemyView = new EntityView("enemy1");
            }
            else {
                enemyModel = new EnemyModel2(tileIndex[0] * stageModel.getTileSize(), tileIndex[1] * stageModel.getTileSize() - 10, stageModel);
                enemyView = new EntityView("enemy2", true, 6);
            }
            if (stageModel.getTile(tileIndex[0], tileIndex[1] - 1) instanceof EmptyTile || stageModel.getTile(tileIndex[0], tileIndex[1] + 1) instanceof EmptyTile) {
                enemyModel.startMoving("UP");
            } else {
                enemyModel.startMoving("RIGHT");
            }
            enemyModel.addListener(enemyView); // add view as observer
            gameLayer.getChildren().add(enemyView.getEntitySprite());
            addEnemy(enemyModel);
            i++;
        }
    }

    /**
    * Aggiunge un nemico al controller dei nemici.
    * 
    * @param enemy il modello del nemico da aggiungere
    */
    public void addEnemy(EnemyModel enemy) {
        enemies.add(enemy);
    }

    /**
     * Rimuove un nemico dal controller dei nemici.
     * 
     * @param enemy il modello del nemico da rimuovere
     */
    public void removeEnemy(EnemyModel enemy) {
        int index = enemies.indexOf(enemy);
        if (index != -1) {
            enemies.remove(index);
        }
    }

    /**
     * Ottiene la lista dei nemici.
     * 
     * @return la lista dei nemici
     */
    public List<EnemyModel> getEnemies() {
        return enemies;
    }


    /**
     * Aggiorna i nemici.
     * Rimuove i nemici morti.
     *
     * @param elapsed il tempo trascorso dall'ultimo aggiornamento
     */
    public void updateState(double elapsed) {
        // update enemy
        enemies.forEach(enemy -> enemy.updateState(elapsed));

        // Remove dead enemies
        IntStream.range(0, enemies.size()) //iterate over indices
            .filter(i -> enemies.get(i).isDead()) // filter, keep only dead enemies
            .boxed() // convert to Integer
            .collect(Collectors.toCollection(LinkedList::new)) // collect to LinkedList
            .descendingIterator() // iterate in reverse order to avoid index shift
            .forEachRemaining(i -> { // remove dead enemies
                enemies.remove((int)i);
            });
    }

}
