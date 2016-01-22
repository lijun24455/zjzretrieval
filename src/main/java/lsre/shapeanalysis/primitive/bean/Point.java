package lsre.shapeanalysis.primitive.bean;

/**
 * Created by lijun on 15/12/4.
 */
public class Point extends BaseObject{

    private double X;
    private double Y;

    public Point(int id) {
        super(id);
        setTYPE(PrimitiveType.TYPE_POINT);
        this.X = 0d;
        this.Y = 0d;
    }

    public Point(int id, double x, double y){
        super(id);
        setTYPE(PrimitiveType.TYPE_POINT);
        this.X = x;
        this.Y = y;
    }

    public double getX() {
        return X;
    }

    public void setX(double x) {
        X = x;
    }

    public double getY() {
        return Y;
    }

    public void setY(double y) {
        Y = y;
    }

    @Override
    public String toString() {
        return "Point{" +
                "X=" + X +
                ", Y=" + Y +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Point point = (Point) o;

        if (Double.compare(point.X, X) != 0) return false;
        return Double.compare(point.Y, Y) == 0;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(X);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(Y);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    public double getDistance(Point b){
        double distance_X = Math.abs(this.X - b.getX());
        double distance_Y = Math.abs(this.Y - b.getY());
        return Math.sqrt(distance_X * distance_X + distance_Y * distance_Y);
    }
}
