import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;

/**
 * Represents an observable 2D point with x and y coordinates.
 */
public class ObservablePoint2D {
    private final DoubleProperty x = new SimpleDoubleProperty();
    private final DoubleProperty y = new SimpleDoubleProperty();

    /**
     * Constructs a new ObservablePoint2D with the specified x and y coordinates.
     *
     * @param x The x coordinate of the point.
     * @param y The y coordinate of the point.
     */
    public ObservablePoint2D(double x, double y) {
        this.x.set(x);
        this.y.set(y);
    }

    /**
     * Returns the x coordinate of the point.
     *
     * @return The x coordinate.
     */
    public double getX() {
        return x.get();
    }

    /**
     * Returns the property representing the x coordinate of the point.
     *
     * @return The x coordinate property.
     */
    public DoubleProperty xProperty() {
        return x;
    }

    /**
     * Returns the y coordinate of the point.
     *
     * @return The y coordinate.
     */
    public double getY() {
        return y.get();
    }

    /**
     * Returns the property representing the y coordinate of the point.
     *
     * @return The y coordinate property.
     */
    public DoubleProperty yProperty() {
        return y;
    }

    /**
     * Returns a Point2D object representing the point.
     *
     * @return The Point2D object.
     */
    public Point2D getPoint2D() {
        return new Point2D(getX(), getY());
    }

    /**
     * Sets the x coordinate of the point.
     *
     * @param x The new x coordinate.
     */
    public void setX(double x) {
        this.x.set(x);
    }

    /**
     * Sets the y coordinate of the point.
     *
     * @param y The new y coordinate.
     */
    public void setY(double y) {
        this.y.set(y);
    }

    /**
     * Adds the specified values to the x and y coordinates of the point.
     *
     * @param dx The value to add to the x coordinate.
     * @param dy The value to add to the y coordinate.
     * @return A new ObservablePoint2D object with the updated coordinates.
     */
    public ObservablePoint2D add(double dx, double dy) {
        this.x.set(getX() + dx);
        this.y.set(getY() + dy);
        return new ObservablePoint2D(getX() + dx, getY() + dy);
    }

    /**
     * Adds the specified ObservablePoint2D to this point.
     *
     * @param other The ObservablePoint2D to add.
     * @return A new ObservablePoint2D object with the updated coordinates.
     */
    public ObservablePoint2D add(ObservablePoint2D other) {
        double newX = getX() + other.getX();
        double newY = getY() + other.getY();
        this.x.set(newX);
        this.y.set(newY);
        return new ObservablePoint2D(newX, newY);
    }

    /**
     * Multiplies the x and y coordinates of the point by the specified factor.
     *
     * @param factor The factor to multiply the coordinates by.
     * @return A new ObservablePoint2D object with the updated coordinates.
     */
    public ObservablePoint2D multiply(double factor) {
        this.x.set(getX() * factor);
        this.y.set(getY() * factor);
        return new ObservablePoint2D(getX() * factor, getY() * factor);
    }
    
}