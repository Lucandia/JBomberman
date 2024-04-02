package com.lucandia;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Rectangle2D;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.BooleanProperty;

public class BombView {
    private ImageView bombSprite;
    private Timeline bombAnimation = null;
    private StageModel stage;
    private BooleanProperty active = new SimpleBooleanProperty(false);
    private Pane pane;
    private BombModel model;

    public BombView(BombModel bombModel, Pane pane, StageModel stage) {
        this.pane = pane;
        this.model = bombModel;
        this.stage = stage;
        active.bind(bombModel.activeProperty());
        // Assuming the sprite sheet is in the same package as the BombView
        Image image = new Image(getClass().getResourceAsStream("/sprites/bomb.png"));
        bombSprite = new ImageView(image);
        // Set the initial viewport to show the first sprite
        bombSprite.setViewport(new Rectangle2D(0, 0, 16, 16));
        // Bind the ImageView's position to the model's position
        bombSprite.layoutXProperty().set(bombModel.getX());
        bombSprite.layoutYProperty().set(bombModel.getY());

        // Animation timer to cycle through the sprites
        double frameTime = bombModel.getTotalTime() / 10; // Time to cycle through all sprites
        bombAnimation = new Timeline(
            new KeyFrame(Duration.seconds(0), e -> bombSprite.setViewport(new Rectangle2D(0, 0, 16, 16))),
            new KeyFrame(Duration.seconds(frameTime * 2), e -> bombSprite.setViewport(new Rectangle2D(17, 0, 16, 16))),
            new KeyFrame(Duration.seconds(frameTime * 3), e -> bombSprite.setViewport(new Rectangle2D(34, 0, 16, 16)))
        );
        bombAnimation.setCycleCount(Animation.INDEFINITE);
        bombAnimation.setAutoReverse(true);
        bombAnimation.play();
        addToPane();
    }

    public void playExplosionAnimation() { // Ensure to pass the correct pane where the game is rendered
        int radius = model.getBlastRadius();
        final int size = stage.getTileSize(); // Adjust according to your sprite size
        Image explosionImage = new Image(getClass().getResourceAsStream("/sprites/explosion.png"));
        // List to hold all explosion sprites
        List<ImageView> explosionSprites = new ArrayList<>();
        // Assuming the bomb is placed in the center of the tile
        int bombX = (int) bombSprite.layoutXProperty().get();
        int bombY = (int) bombSprite.layoutYProperty().get();
        int tileX = (int) bombX / size;
        int tileY = (int) bombY / size;

        // Generate explosion at the bomb position
        ImageView mainExplosionSprite = new ImageView(explosionImage);
        mainExplosionSprite.setViewport(new Rectangle2D(32, 32, size, size)); // Set to the initial frame
        mainExplosionSprite.layoutXProperty().set(bombX); // Centering the explosion sprite
        mainExplosionSprite.layoutYProperty().set(bombY);
        explosionSprites.add(mainExplosionSprite);
        pane.getChildren().add(mainExplosionSprite);

        // Generate explosion on the positive x axis
        int max_dx = radius;
        for (int dx = 1; dx <= radius; dx++) {
            if (model.containsDetonatePosition(tileX + dx, tileY)) {
                // Check the explosion to display
                if (!model.containsDetonatePosition(tileX + dx + 1, tileY)) {
                    max_dx = dx;
                }
                    ImageView explosionSprite = new ImageView(explosionImage);
                    int rect_x = 3;
                    int rect_y = 2;
                    if (dx == max_dx) {
                        rect_x = 4;
                    }
                    explosionSprite.setViewport(new Rectangle2D(rect_x * size, rect_y * size, size, size)); // Set to the initial frame
                    explosionSprite.layoutXProperty().set(bombX + dx * size); // Centering the explosion sprite
                    explosionSprite.layoutYProperty().set(bombY);
                    explosionSprites.add(explosionSprite);
                    pane.getChildren().add(explosionSprite);
                }
            else break;
            if (dx == max_dx) break;
        }

        // Generate explosion on the negative x axis
        int min_dx = -radius;
        for (int dx = -1; dx >= -radius; dx--) {
            // Check the explosion to display
            if (model.containsDetonatePosition(tileX + dx, tileY)) {
                if (!model.containsDetonatePosition(tileX + dx - 1, tileY)) {
                    min_dx = dx;
                }
                ImageView explosionSprite = new ImageView(explosionImage);
                int rect_x = 1;
                int rect_y = 2;
                if (dx == min_dx) {
                    rect_x = 0;
                }
                explosionSprite.setViewport(new Rectangle2D(rect_x * size, rect_y * size, size, size)); // Set to the initial frame
                explosionSprite.layoutXProperty().set(bombX + dx * size); // Centering the explosion sprite
                explosionSprite.layoutYProperty().set(bombY);
                explosionSprites.add(explosionSprite);
                pane.getChildren().add(explosionSprite);
            }
            else break;
            if (dx == min_dx) break;
        }

        // Generate explosion on the positive y axis
        int max_dy = radius;
        for (int dy = 1; dy <= radius; dy++) {
            // Check the explosion to display
            if (model.containsDetonatePosition(tileX, tileY + dy)) {
                if (!model.containsDetonatePosition(tileX, tileY + dy + 1)) {
                    max_dy = dy;
                }
                ImageView explosionSprite = new ImageView(explosionImage);
                int rect_x = 2;
                int rect_y = 3;
                if (dy == max_dy) {
                    rect_y = 4;
                }
                explosionSprite.setViewport(new Rectangle2D(rect_x * size, rect_y * size, size, size)); // Set to the initial frame
                explosionSprite.layoutXProperty().set(bombX); // Centering the explosion sprite
                explosionSprite.layoutYProperty().set(bombY + dy * size);
                explosionSprites.add(explosionSprite);
                pane.getChildren().add(explosionSprite);
            }
            else break;
            if (dy == max_dy) break;
        }

        // Generate explosion on the negative y axis
        int min_dy = -radius;
        for (int dy = -1; dy >= -radius; dy--) {
            // Check the explosion to display
            if (model.containsDetonatePosition(tileX, tileY + dy)) {
                if (!model.containsDetonatePosition(tileX, tileY + dy - 1)){
                    min_dy = dy;
                }
                ImageView explosionSprite = new ImageView(explosionImage);
                int rect_x = 2;
                int rect_y = 1;
                if (dy == min_dy) {
                    rect_y = 0;
                }
                explosionSprite.setViewport(new Rectangle2D(rect_x * size, rect_y * size, size, size)); // Set to the initial frame
                explosionSprite.layoutXProperty().set(bombX); // Centering the explosion sprite
                explosionSprite.layoutYProperty().set(bombY + dy * size);
                explosionSprites.add(explosionSprite);
                pane.getChildren().add(explosionSprite);
            }
            else break;
            if (dy == min_dy) break;
        }
    
        double frameTime = model.getTotalTime() / 25; // Time between each frame of the explosion animation
        // Create and play the explosion animation
        Timeline explosionAnimation = new Timeline();
        for (int frameIndex = 0; frameIndex < 5; frameIndex++) { // For each frame of the explosion
            final int index = frameIndex;
            explosionAnimation.getKeyFrames().add(new KeyFrame(Duration.seconds(index * frameTime), e -> {
                for (ImageView sprite : explosionSprites) {
                    Rectangle2D viewport = sprite.getViewport();
                    sprite.setViewport(new Rectangle2D(viewport.getMinX() + 16 * 5 * index, viewport.getMinY(), size, size));
                }
            }));
        }
        explosionAnimation.setOnFinished(e -> explosionSprites.forEach(sprite -> pane.getChildren().remove(sprite))); // Remove sprites after animation
        explosionAnimation.play();
    }
    
    public ImageView getbombSprite() {
        return bombSprite;
    }

    public void addToPane() {
        pane.getChildren().add(getbombSprite());
    }

    public void update() {
        if (!active.get()) {
            playExplosionAnimation();
            pane.getChildren().remove(bombSprite);
            }
        }
}
