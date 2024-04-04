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
 * un'immagine contenente le sprite delle tessere.
 */
public class StageView {
    private Pane pane = new Pane(); // Pannello per contenere il palco e le tessere
    private Image stageImage; // Immagine di sfondo del palco
    private Image tilesImage; // Immagine contenente le sprite delle tessere
    private StageModel stage;
    private ImageView combinedView;

    /**
     * Costruttore della classe StageView.
     * Carica le immagini del palco e delle caselle in base al numero di livello fornito.
     * Inizializza la vista combinata e aggiunge la vista dell'immagine al pannello.
     * Esegue l'aggiornamento iniziale della vista.
     *
     * @param levelNumber Il numero di livello
     * @param stage Il modello del palco
     */
    public StageView(int levelNumber, StageModel stage) {
        this.stage = stage;
        // Carica le immagini del palco e delle tessere in base al numero di livello
        stageImage = new Image(getClass().getResourceAsStream("/sprites/level" + levelNumber + "_stage.png"));
        tilesImage = new Image(getClass().getResourceAsStream("/sprites/level" + levelNumber + "_tiles.png"));
        combinedView = new ImageView();
        pane.getChildren().add(combinedView); // Aggiunge la vista combinata all'interno del pannello
        updateView(); // Aggiornamento iniziale
    }

    /**
     * Aggiorna la vista del palco.
     * Crea un'immagine combinata che contiene lo sfondo del palco e le tessere.
     */
    public void updateView() {
        int tileSize = stage.getTileSize();
        int width = stage.getWidth() * tileSize;
        int height = stage.getHeight() * tileSize;
        WritableImage combinedImage = new WritableImage(width, height);
        PixelWriter writer = combinedImage.getPixelWriter();

        // Copia lo sfondo del palco nell'immagine combinata
        PixelReader stageReader = stageImage.getPixelReader();
        writer.setPixels(0, 0, width, height, stageReader, 0, 0);

        // Itera sulle tessere e le aggiunge all'immagine combinata
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
                    // Se la tessera non è visualizzabile, ma la tessera sopra di essa non è distruttibile, visualizza la tessera ombra
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
}
