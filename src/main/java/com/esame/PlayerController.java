package com.esame;
/**
 * Questa classe rappresenta il controller del giocatore.
 * Gestisce l'input del giocatore e aggiorna il modello e la vista corrispondenti.
 */
public class PlayerController {

    /**
     * Il modello del giocatore.
     */
    private PlayerModel model;

    /**
     * La vista del giocatore.
     */
    private EntityView view;

    /**
     * L'audio del giocatore.
     */
    private PlayerSound audio;

    /**
     * Crea un nuovo oggetto PlayerController con il modello e la vista specificati.
     *
     * @param model il modello del giocatore
     * @param avatar il numero dell'avatar del giocatore
     */
    public PlayerController(PlayerModel model, int avatar) {
        this.model = model;
        this.view = new EntityView("bomberman", true, 3, avatar - 1);
        this.audio = new PlayerSound(); // inizializza il playerSound
        model.addListener(view);
        model.addListener(audio);
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
     * Restituisce la vista del giocatore.
     *
     * @return la vista del giocatore
     */
    public EntityView getView() {
        return view;
    }

    /**
     * Aggiorna il modello e la vista del giocatore.
     *
     * @param elapsed il tempo trascorso dall'ultimo aggiornamento
     */
    public void updateState(double elapsed) {
        model.updateState(elapsed);
    }
}
