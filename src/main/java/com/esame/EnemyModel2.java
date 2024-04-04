package com.esame;

/**
 * Questa classe rappresenta il secondo tipo di nemico nel gioco.
 * Estende la classe EnemyModel e cambia il comportamento del nemico.
 */
public class EnemyModel2 extends EnemyModel {

    /**
     * Costruttore per la classe EnemyModel2.
     * 
     * @param initialX la coordinata x iniziale del nemico
     * @param initialY la coordinata y iniziale del nemico
     * @param stage il modello dello stage in cui si trova il nemico
     */
    public EnemyModel2(int initialX, int initialY, StageModel stage) {
        super(initialX, initialY, stage);
        this.life.set(200); // Vita iniziale
        this.setBoundingBox(new int[] {15, 15});
        this.setBoundingOffset(new int[] {8, 17});
        // Metà della velocità del nemico originale, altrimenti e' troppo difficile da battere
        this.setVelocity(this.getVelocity() * 0.7);
    }

    /**
     * Metodo per togliere vita al nemico. Quando il nemico perde vita, 
     * si teletrasporta in una casella casuale libera.
     *
     * @param amount la quantità di vita da rimuovere
     */
    @Override
    public void loseLife(int amount) {
        super.loseLife(amount);
        if (isDead()) return;
        boolean new_pos = false;
        // Imposta l'occupante della tile corrente a null
        ((EmptyTile) getStage().getTileAtPosition((int) this.centerOfMass()[0], (int) this.centerOfMass()[1])).setOccupant(null);
        while (!new_pos) {
            int[] randomXY = getStage().getRandomFreeTile();
            if (getStage().getTile(randomXY[0], randomXY[1]) instanceof EmptyTile && ((EmptyTile) getStage().getTile(randomXY[0], randomXY[1])).getOccupant() == null) {
                this.xProperty().set(randomXY[0] * getStage().getTileSize());
                this.yProperty().set(randomXY[1] * getStage().getTileSize());
                new_pos = true;
            }
        }
    }

    /**
     * Metodo per aggiornare lo stato del nemico.
     * 
     * @param elapsedTime il tempo trascorso dall'ultimo aggiornamento
     */
    @Override
    public void update(double elapsedTime) {
        super.update(elapsedTime);
        int lastX = lastDirection[0];
        int lastY = lastDirection[1];
        int randomDirection = (int) (Math.random() * 4); // Genera un numero casuale tra 0 e 3
        // Muove il nemico in una direzione casuale
        if (randomDirection == 0 && canMoveTo(1, 0) && lastX != -1) {
            lastDirection[0] = 1;
            lastDirection[1] = 0;
        }
        else if (randomDirection == 1 && canMoveTo(-1, 0) && lastX != 1) {
            lastDirection[0] = -1;
            lastDirection[1] = 0;
        }
        else if (randomDirection == 2 && canMoveTo(0, -1) && lastY != 1) {
            lastDirection[0] = 0;
            lastDirection[1] = -1;
        }
        else if (randomDirection == 3 && canMoveTo(0, 1) && lastY != -1) {
            lastDirection[0] = 0;
            lastDirection[1] = 1;
        }
    }
}

