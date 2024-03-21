import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;

public class StageView {
    private Pane pane = new Pane(); // Pane to hold the stage and tiles
    private Image stageImage; // Background image of the stage
    private Image tilesImage; // Image containing tile sprites
    private StageModel stage;
    private ImageView combinedView;

    public StageView(int levelNumber, StageModel stage) {
        this.stage = stage;
        // Load stage and tiles images based on the level number
        stageImage = new Image(getClass().getResourceAsStream("resources/sprites/level" + levelNumber + "_stage.png"));
        tilesImage = new Image(getClass().getResourceAsStream("resources/sprites/level" + levelNumber + "_tiles.png"));
        combinedView = new ImageView();
        pane.getChildren().add(combinedView); // Add the combined image view to the pane
        updateView(); // Initial update
    }

    public void updateView() {
        int tileSize = stage.getTileSize();
        int width = stage.getWidth() * tileSize;
        int height = stage.getHeight() * tileSize;
        WritableImage combinedImage = new WritableImage(width, height);
        PixelWriter writer = combinedImage.getPixelWriter();

        // Copy the stage background to the combined image
        PixelReader stageReader = stageImage.getPixelReader();
        writer.setPixels(0, 0, width, height, stageReader, 0, 0);

        // Iterate over the tiles and add them to the combined image
        PixelReader tilesReader = tilesImage.getPixelReader();
        for (int x = 0; x < stage.getWidth(); x++) {
            for (int y = 0; y < stage.getHeight(); y++) {
                Tile tile = stage.getTile(x, y);
                if (tile.isDisplayable()) {
                    if (tile instanceof SpecialTile) {
                        PixelReader powerUpReader = new Image(getClass().getResourceAsStream("resources/sprites/" + ((SpecialTile) tile).getType().toString() + ".png")).getPixelReader();
                        writer.setPixels(x * tileSize, y * tileSize, tileSize, tileSize, powerUpReader, 0, 0);
                    }
                    else {
                        int srcX = tile.isDestructible() ? 17 : 0;
                        writer.setPixels(x * tileSize, y * tileSize, tileSize, tileSize, tilesReader, srcX, 0);
                    }
                }
            }
        }

        combinedView.setImage(combinedImage);
    }

    public Pane getPane() {
        return pane;
    }
}
