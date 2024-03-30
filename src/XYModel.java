import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * This class represents the abstract model for any entity in the game.
 * It includes common properties like position, which can be observed
 * for changes using JavaFX's property system.
 */
public abstract class XYModel {

    // Properties
    private final IntegerProperty x = new SimpleIntegerProperty();
    private final IntegerProperty y = new SimpleIntegerProperty();

    public XYModel() {
        this(0, 0);
    }

    public XYModel(int x, int y) {
        this.x.set(x);
        this.y.set(y);
    }


    /**
     * Gets the x property of the entity.
     * 
     * @return The x property of the entity.
     */
    public IntegerProperty xProperty() {
        return x;
    }

    /**
     * Gets the y property of the entity.
     * 
     * @return The y property of the entity.
     */
    public IntegerProperty yProperty() {
        return y;
    }

    public int getX() {
        return x.get();
    }

    public int getY() {
        return y.get();
    }   

    public void setX(int x) {
        this.x.set(x);
    }

    public void setY(int y) {
        this.y.set(y);
    }

    public void setPosition(int x, int y) {
        this.x.set(x);
        this.y.set(y);
    }
}