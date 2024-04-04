package com.esame;

/**
 * Questa classe rappresenta un nemico nel gioco.
 * Estende la classe EntityModel e implementa le specifiche
 * caratteristiche di un nemico.
 */
public class EnemyModel extends EntityModel {

    /**
     * Costruttore della classe EnemyModel.
     * 
     * @param initialX le coordinate x iniziali del nemico
     * @param initialY le coordinate y iniziali del nemico
     * @param stage il modello dello stage di gioco
     */
    public EnemyModel(int initialX, int initialY, StageModel stage) {
        super(initialX, initialY, new int[] {15, 15}, new int[] {8, 17}, 100, stage);
    }


    /**
    * Controlla la collisione con un'altra entità.
    * 
    * @param dx lo spostamento sull'asse x
    * @param dy lo spostamento sull'asse y
    * @return l'entità con cui si è verificata la collisione (null se non c'è stata collisione)
    */
    @Override
    public EntityModel checkCollision(int dx, int dy) {
        EntityModel occupant = super.checkCollision(dx, dy);
        if (occupant instanceof PlayerModel) {
            occupant.loseLife(1);
        }
        return occupant;
    }

    /**
     * Aggiorna il modello del nemico.
     * 
     * @param elapsedTime il tempo trascorso dall'ultimo aggiornamento in millisecondi
     */
    @Override
    public void update(double elapsedTime) {
        super.update(elapsedTime);
        if (!this.isMoving) {
            this.startMoving(new int[] {-lastDirection[0], -lastDirection[1]});
        }
    }
}

