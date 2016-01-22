package lsre.shapeanalysis.primitive.bean;

import util.DoubleUtil;

/**
 * Created by lijun on 15/12/6.
 */
public class Segment extends BaseObject {

    private Point startPoint;
    private Point endPoint;

    private double k;
    private double b;

    private double length;

    public Segment(int id) {
        super(id);
        setTYPE(PrimitiveType.TYPE_SEGMENT);
    }

    public Point getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(Point startPoint) {
        this.startPoint = startPoint;
    }

    public Point getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(Point endPoint) {
        this.endPoint = endPoint;
    }

    public double getK() {
        return k;
    }

    public void setK(double k) {
        this.k = k;
    }

    public double getB() {
        return b;
    }

    public void setB(double b) {
        this.b = b;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    @Override
    public String toString() {
        return "Segment{" +
                "id=" + getId() +
                ", startPoint=" + startPoint +
                ", endPoint=" + endPoint +
                ", k=" + k +
                ", b=" + b +
                ", length=" + length +
                '}';
    }

    public void initKB() {
        Point startPoint = this.getStartPoint();
        Point endPoint = this.getEndPoint();

        double sX = startPoint.getX();
        double sY = startPoint.getY();

        double eX = endPoint.getX();
        double eY = endPoint.getY();

        if (DoubleUtil.isEqual(sX, eX)){
            this.setK(Double.POSITIVE_INFINITY);
            this.setB(0);
        }else {
            this.setK(DoubleUtil.changeDouble( (eY - sY)/(eX - sX) ));
            this.setB(DoubleUtil.changeDouble( sY - this.getK() * sX ));
        }

        double lengthX = Math.abs(sX - eX);
        double lengthY = Math.abs(sY - eY);

        this.setLength(DoubleUtil.changeDouble( Math.sqrt(lengthX * lengthX + lengthY * lengthY) ));
    }
}
