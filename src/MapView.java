import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class MapView {
    private final ImageView mapImage;
    private MapModel model;
    private Pane mapPane = new Pane();

    public MapView(MapModel model) {
        this.model = model;

        // Load the map image
        Image image = new Image(getClass().getResourceAsStream("resources/sprites/map"+ model.getLevel() +".png"));
        this.mapImage = new ImageView(image);
        // Render the map based on the model
        mapPane.getChildren().add(mapImage);
    }

    public Pane getMapPane() {
        return mapPane;
    }   
    // Methods to update the view when the model changes
}
