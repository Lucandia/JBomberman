package com.lucandia;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.geometry.Rectangle2D;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;


/**
 * La classe EntityView rappresenta la vista di un'entità nel gioco.
 * Questa classe gestisce l'immagine sprite dell'entità e fornisce metodi per animare l'entità in diverse direzioni.
 */
public class EntityView {
    private final ImageView EntitySprite;
    private EntityModel model;
    private String lastDirection;
    // Create a fixed HashMap with keys and values
    private Map<String, Integer> directionSprite = new HashMap<>();
    private Timeline walkAnimation = null;
    private boolean autoReverse = false;
    // private String spriteName;
    private int frames = 4;
    int row = 0;

    /**
     * Costruisce una nuova istanza di EntityView.
     *
     * @param model       il modello dell'entità
     * @param spriteName  il nome dello sprite dell'entità
    **/
    public EntityView(EntityModel model, String spriteName)
    {
        this(model, spriteName, false, 4, 0);
    }

    /**
     * Costruisce una nuova istanza di EntityView.
     * 
     * @param model il modello dell'entità
     * @param spriteName il nome dello sprite dell'entità
     * @param autoReverse indica se l'animazione dello sprite deve essere riprodotta in modo inverso automaticamente
     * @param frames il numero di frame dell'animazione dello sprite
     */
    public EntityView(EntityModel model, String spriteName, boolean autoReverse, int frames)
    {
        this(model, spriteName, autoReverse, frames, 0);
    }
    
    /**
     * Costruisce una nuova istanza di EntityView.
     * 
     * @param model il modello dell'entità
     * @param spriteName il nome dello sprite dell'entità
     * @param autoReverse indica se l'animazione dello sprite deve essere riprodotta in modo inverso automaticamente
     * @param frames il numero di frame dell'animazione dello sprite
     * @param row la riga dello sprite dell'entità
     */
    public EntityView(EntityModel model, String spriteName, boolean autoReverse, int frames, int row) {
        this.model = model;
        this.autoReverse = autoReverse;
        this.frames = frames;
        this.row = row;
        // this.spriteName = spriteName;
        this.directionSprite = new HashMap<String, Integer>() {{
            put("DOWN",  16 + 16 * frames * 0);
            put("LEFT",  16 + 16 * frames * 1);
            put("RIGHT", 16 + 16 * frames * 2);
            put("UP",    16 + 16 * frames * 3);
        }};

        // Load the sprite image
        Image image = new Image(getClass().getResourceAsStream("/sprites/" + spriteName + ".png"));
        this.EntitySprite = new ImageView(image);
        
        // Set the default sprite for the entity
        EntitySprite.setViewport(new Rectangle2D(directionSprite.get("DOWN") + 16, 24 * row, 16, 24)); 

        // Bind the ImageView's position to the model's position
        EntitySprite.layoutXProperty().bind(model.xProperty());
        EntitySprite.layoutYProperty().bind(model.yProperty());
    }

    /**
     * Restituisce lo sprite dell'entità come un oggetto ImageView.
     *
     * @return lo sprite dell'entità come un oggetto ImageView
     */
    public ImageView getEntitySprite() {
        return EntitySprite;
    }

    /**
     * Crea e avvia l'animazione della camminata dell'entità nella direzione specificata.
     *
     * @param direction La direzione in cui l'entità deve camminare.
     */
    public void startWalking(String direction) {
        if (lastDirection != direction) {
            lastDirection = direction;
            EntitySprite.setViewport(new Rectangle2D(directionSprite.get(direction) + 16, 24 * row, 16, 24)); // Iniziliazza la direzione della view
            // Stop the current animation if it's running
            if (walkAnimation != null && walkAnimation.getStatus() == Animation.Status.RUNNING) {
                walkAnimation.stop();
            }
            double animationFrameTime = 0.2 / model.velocityProperty().get();
            walkAnimation = new Timeline();
            IntStream.range( 0, frames).forEach(i -> {
                walkAnimation.getKeyFrames().add(
                    new KeyFrame(Duration.seconds(animationFrameTime * i), e -> EntitySprite.setViewport(new Rectangle2D(directionSprite.get(direction) + 16 * i, 24 * row, 16, 24)))
                );
            });
            walkAnimation.setAutoReverse(autoReverse);
            walkAnimation.setCycleCount(Animation.INDEFINITE);
            walkAnimation.play();
        }
        if (walkAnimation.getStatus() !=  Animation.Status.RUNNING){
            walkAnimation.play();
        }
    }

    /**
     * Ferma l'animazione della camminata dell'entità e imposta la vista dell'entità
     * all'ultima direzione di movimento.
     */
    public void stopWalking() {
        if (walkAnimation != null) {
            walkAnimation.stop();
        }
        if (lastDirection != null) {
            EntitySprite.setViewport(new Rectangle2D(directionSprite.get(lastDirection) + 16, 24 * row, 16, 24));
        }
    }

    /**
     * Imposta il numero di frames per l'entità.
     *
     * @param frames il numero di frames da impostare
     */
    public void setFrames(int frames) {
        this.frames = frames;
    }

    /**
     * Aggiorna la vista dell'entità.
     *
     * @param elapsed il tempo trascorso dall'ultimo aggiornamento
     */
    public void update(double elapsed) {
        if (model.isDead()) {
            EntitySprite.setVisible(false);
            return;
        }
        if (model.isMoving()) {
            startWalking(model.getLastDirectionString());
        }
        else {
            stopWalking();
        }
    }
}
