package lsre.shapeanalysis.primitive.bean;

/**
 * Created by lijun on 15/12/6.
 */
public class Circle extends BaseObject{

    private Point center;
    private double radius;
    private double UP;
    private double DOWN;
    private double LEFT;
    private double RIGHT;

    private double area;
    private double circumference;

    public Circle(int id) {
        super(id);
        setTYPE(PrimitiveType.TYPE_CIRCLE);
        UP = DOWN = LEFT = RIGHT = 0;
    }

    public Point getCenter() {
        return center;
    }

    public void setCenter(Point center) {
        this.center = center;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    @Override
    public String toString() {
        return "Circle{" +
                "center=" + center +
                ", radius=" + radius +
                '}';
    }

    public double getUP() {
        return UP;
    }

    public double getDOWN() {
        return DOWN;
    }

    public double getLEFT() {
        return LEFT;
    }

    public double getRIGHT() {
        return RIGHT;
    }

    public void initWindow() {
        this.LEFT = this.center.getX() - radius;
        this.RIGHT = this.center.getX() + radius;
        this.UP = this.center.getY() - radius;
        this.DOWN = this.center.getY() + radius;
    }

    public void initAandS(){
        this.area = 3.14 * radius * radius;
        this.circumference = 2 * 3.14 *radius;
    }

    public double getArea() {
        return area;
    }

    public double getCircumference() {
        return circumference;
    }
}
