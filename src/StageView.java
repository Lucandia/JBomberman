import javafx.scene.layout.Pane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;

public class StageView {
    private Pane pane = new Pane(); // Pane to hold the stage and tiles
    private Image stageImage; // Background image of the stage
    private Image tilesImage; // Image containing tile sprites

    public StageView(int levelNumber, StageModel stage) {
        int tileSize = stage.getTileSize(); // Get the size of each tile
        // Load stage and tiles images based on the level number
        stageImage = new Image(getClass().getResourceAsStream("resources/sprites/level" + levelNumber + "_stage.png"));
        tilesImage = new Image(getClass().getResourceAsStream("resources/sprites/level" + levelNumber + "_tiles.png"));

        // Create and add the background of the stage
        ImageView background = new ImageView(stageImage);
        pane.getChildren().add(background); // Add the stage background to the pane

        // Iterate over the stage tiles and add them to the pane
        int width = stage.getWidth();
        int height = stage.getHeight();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Tile tile = stage.getTile(x, y);
                if (tile != null && tile.isDisplayable()) {
                    // Determine the correct part of the tilesImage to use based on whether the tile is destructible
                    int srcX = tile.isDestructible() ? 17 : 0;

                    // Create a subimage (sprite) for the tile
                    WritableImage tileSprite = new WritableImage(tilesImage.getPixelReader(), srcX, 0, tileSize, tileSize);

                    // Create an ImageView for the tile and set its position
                    ImageView tileView = new ImageView(tileSprite);
                    tileView.setX(x * tileSize);
                    tileView.setY(y * tileSize);

                    // Add the tile to the pane
                    pane.getChildren().add(tileView);
                }
            }
        }
    }

    public Pane getPane() {
        return pane; // Return the pane containing the stage and tiles for display
    }
}
