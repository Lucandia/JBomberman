package com.esame;

/**
 * Questa classe rappresenta un nemico nel gioco.
 * Estende la classe EntityModel e implementa le specifiche
 * caratteristiche generali di un nemico.
 */
public abstract class EnemyModel extends EntityModel {

    /**
     * Costruttore della classe EnemyModel.
     * 
     * @param initialX le coordinate x iniziali del nemico
     * @param initialY le coordinate y iniziali del nemico
     * @param boundingBox il bounding box dell'entità
     * @param boundingOffset l'offset del bounding box
     * @param life la vita iniziale del nemico
     * @param stage il modello dello stage di gioco
     */
    public EnemyModel(int initialX, int initialY, int[] boundingBox, int[] boundingOffset, int life, StageModel stage) {
        super(initialX, initialY, boundingBox, boundingOffset, life, stage);
    }


    /**
    * Controlla la collisione con un'altra entità.
    * Se la collisione avviene con un giocatore, il giocatore perde una vita.
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
        notifyListeners();
        return occupant;
    }

    /**
     * Metodo per far muovere il nemico.
     * 
     */
    public abstract void movingBehaviour();

    /**
     * Aggiorna il modello del nemico.
     * 
     * @param elapsedTime il tempo trascorso dall'ultimo aggiornamento in millisecondi
     */
    @Override
    public void updateState(double elapsedTime) {
        super.updateState(elapsedTime);
        movingBehaviour();
        notifyListeners();
    }
}

