package com.esame;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;

/**
 * La classe StageView rappresenta la vista dello stage di gioco.
 * Contiene un pannello con un'immagine di sfondo del palco e
 * un'immagine contenente le sprite delle caselle.
 */
public class StageView implements StageObserver {

    /**
     * Il pannello per contenere il palco e le caselle.
     */
    private Pane pane = new Pane(); // Pannello per contenere il palco e le caselle

    /**
     * L'immagine di sfondo del palco.
     */
    private Image stageImage; // Immagine di sfondo del palco

    /**
     * L'immagine contenente le sprite delle caselle.
     */ 
    private Image tilesImage; // Immagine contenente le sprite delle caselle

    /**
     * L'immagine combinata che contiene lo sfondo del palco e le caselle.
     */
    private ImageView combinedView;

    /**
     * Costruttore della classe StageView.
     * Carica le immagini del palco e delle caselle in base al numero di livello fornito.
     * Inizializza la vista combinata e aggiunge la vista dell'immagine al pannello.
     * Esegue l'aggiornamento iniziale della vista.
     *
     * @param levelNumber Il numero di livello
     */
    public StageView(int levelNumber) {
        // Carica le immagini del palco e delle caselle in base al numero di livello
        stageImage = new Image(getClass().getResourceAsStream("/sprites/level" + levelNumber + "_stage.png"));
        tilesImage = new Image(getClass().getResourceAsStream("/sprites/level" + levelNumber + "_tiles.png"));
        combinedView = new ImageView();
        pane.getChildren().add(combinedView); // Aggiunge la vista combinata all'interno del pannello
    }

    /**
     * Aggiorna la vista del palco.
     * Crea un'immagine combinata che contiene lo sfondo del palco e le caselle.
     * 
     * @param stage Il modello dello stage
     */
    @Override
    public void update(StageModel stage) {
        int tileSize = stage.getTileSize();
        int width = stage.getWidth() * tileSize;
        int height = stage.getHeight() * tileSize;
        WritableImage combinedImage = new WritableImage(width, height);
        PixelWriter writer = combinedImage.getPixelWriter();

        // Copia lo sfondo del palco nell'immagine combinata
        PixelReader stageReader = stageImage.getPixelReader();
        writer.setPixels(0, 0, width, height, stageReader, 0, 0);

        // Itera sulle caselle e le aggiunge all'immagine combinata
        PixelReader tilesReader = tilesImage.getPixelReader();
        for (int x = 0; x < stage.getWidth(); x++) {
            for (int y = 0; y < stage.getHeight(); y++) {
                Tile tile = stage.getTile(x, y);
                if (tile.isDisplayable()) {
                    if (tile instanceof SpecialTile) {
                        PixelReader powerUpReader = new Image(getClass().getResourceAsStream("/sprites/" + ((SpecialTile) tile).getType().toString() + ".png")).getPixelReader();
                        writer.setPixels(x * tileSize, y * tileSize, tileSize, tileSize, powerUpReader, 0, 0);
                    }
                    else {
                        int srcX = tile.isDestructible() ? 16 : 0;
                        writer.setPixels(x * tileSize, y * tileSize, tileSize, tileSize, tilesReader, srcX, 0);
                    }
                }
                else if (tile instanceof EmptyTile && !stage.getTile(x, y - 1).isDestructible()){
                    // Se la casella non è visualizzabile, ma la casella sopra di essa non è distruttibile, visualizza la casella ombra
                    writer.setPixels(x * tileSize, y * tileSize, tileSize, tileSize, tilesReader, 32, 0);
                }
            }
        }
        combinedView.setImage(combinedImage);
    }

    /**
     * Restituisce il pannello che contiene la vista del palco.
     *
     * @return Il pannello che contiene la vista del palco
     */
    public Pane getPane() {
        return pane;
    }

    /**
     * Rimuove la vista combinata dal pannello se il modello dello stage è stato invalidato.
     */
    public void invalidated(javafx.beans.Observable observable) {
        pane.getChildren().remove(combinedView);
    }

}
