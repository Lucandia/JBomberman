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
        super(initialX, initialY, new int[] {15, 15}, new int[] {8, 17}, 200, stage);
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
                this.xProperty().set(randomXY[0] * getStage().getTileSize() - this.getBoundingBox()[0] / 2);
                this.yProperty().set(randomXY[1] * getStage().getTileSize() - this.getBoundingBox()[1] / 2);
                new_pos = true;
            }
        }
        notifyListeners();
    }

    /**
     * Metodo per far muovere il nemico.
     * Il nemico conntrolla in quale direzione si è mosso l'ultima volta e 
     * si muove in una direzione casuale eccetto tornare indietro.
     */
    @Override
    public void movingBehaviour() {
        int lastX = lastDirection[0];
        int lastY = lastDirection[1];
        // Se il nemico non si sta muovendo, resetta le ultime coordinate
        if (!isMoving()) {
            lastX = 0;
            lastY = 0;
        }
        int randomDirection = (int) (Math.random() * 4); // Genera un numero casuale tra 0 e 3
        // Muove il nemico in una direzione casuale
        if (randomDirection == 0 && canMoveTo(1, 0) && lastX != -1) {
            startMoving(1, 0);
        }
        else if (randomDirection == 1 && canMoveTo(-1, 0) && lastX != 1) {
            startMoving(-1, 0);
        }
        else if (randomDirection == 2 && canMoveTo(0, -1) && lastY != 1) {
            startMoving(0, -1);
        }
        else if (randomDirection == 3 && canMoveTo(0, 1) && lastY != -1) {
            startMoving(0, 1);
        }
    }

}

