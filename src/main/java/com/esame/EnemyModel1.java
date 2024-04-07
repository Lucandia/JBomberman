package com.esame;

/**
 * Questa classe rappresenta il primo tipo di nemico nel gioco.
 * Estende la classe EnemyModel e cambia il comportamento del nemico.
 * Il nemico si muove in in una direzione avanti e indietro.
 */
public class EnemyModel1 extends EnemyModel {

    /**
     * Costruttore della classe EnemyModel.
     * 
     * @param initialX le coordinate x iniziali del nemico
     * @param initialY le coordinate y iniziali del nemico
     * @param stage il modello dello stage di gioco
     */
    public EnemyModel1(int initialX, int initialY, StageModel stage) {
        super(initialX, initialY, new int[] {15, 15}, new int[] {8, 17}, 100, stage);
    }

    /**
     * Implementa il comportamento di movimento per il nemico.
     * Se il nemico non si sta muovendo, inizia a muoversi nella direzione opposta all'ultima direzione.
     */
    @Override
    public void movingBehaviour() {
        if (!this.isMoving) {
            this.startMoving(new int[] {-lastDirection[0], -lastDirection[1]});
        }
    }
}

