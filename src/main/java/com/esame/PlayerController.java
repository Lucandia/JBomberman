package com.esame;
/**
 * Questa classe rappresenta il controller del giocatore.
 * Gestisce l'input del giocatore e aggiorna il modello e la vista corrispondenti.
 */
public class PlayerController {
    private PlayerModel model;
    private EntityView view;

    /**
     * Crea un nuovo oggetto PlayerController con il modello e la vista specificati.
     *
     * @param model il modello del giocatore
     * @param view  la vista del giocatore
     */
    public PlayerController(PlayerModel model, EntityView view) {
        this.model = model;
        this.view = view;
    }

    /**
     * Gestisce l'input del giocatore.
     * Se la direzione non è nulla, avvia il movimento del giocatore nella direzione specificata.
     * Altrimenti, ferma il movimento del giocatore.
     *
     * @param directionString la direzione del movimento del giocatore come stringa
     */
    public void input(String directionString) {
        if (directionString != null) {
            model.startMoving(directionString);
        } else {
            model.stopMoving();
        }
    }

    /**
     * Restituisce il modello del giocatore.
     *
     * @return il modello del giocatore
     */
    public PlayerModel getModel() {
        return model;
    }

    /**
     * Aggiorna il modello e la vista del giocatore.
     *
     * @param elapsed il tempo trascorso dall'ultimo aggiornamento
     */
    public void update(double elapsed) {
        model.update(elapsed);
        view.update(elapsed);
    }
}
