package lsre.shapeanalysis.primitive.bean;

import util.DoubleUtil;

/**
 * Created by lijun on 15/12/22.
 */
public class ParallelSegment {

    private Segment lineA;
    private Segment lineB;

    private Point X_MidPoint;
    private double X_LDistance;
    private double X_Lk;

    private double E_LDistance;
    private double E_Lk;

    private boolean isFormed;


    public ParallelSegment(Segment lineA, Segment lineB) {
        this.lineA = lineA;
        this.lineB = lineB;

        this.X_LDistance = DoubleUtil.changeDouble((lineA.getLength() + lineB.getLength()) / 2);
        this.X_Lk = DoubleUtil.changeDouble((lineA.getK() + lineB.getK()) / 2);

        Point tmpStartMidPoint = new Point(0,
                ((lineA.getStartPoint().getX() + lineB.getStartPoint().getX()) / 2),
                ((lineA.getStartPoint().getY() + lineB.getStartPoint().getY()) / 2));
        Point tmpEndMidPoint = new Point(0,
                ((lineA.getEndPoint().getX() + lineB.getEndPoint().getX()) / 2),
                ((lineA.getEndPoint().getY() + lineB.getEndPoint().getY()) / 2));
        this.X_MidPoint = new Point(0, ((tmpStartMidPoint.getX() + tmpEndMidPoint.getX()) / 2), ((tmpStartMidPoint.getY() + tmpEndMidPoint.getY()) / 2));


        Point tmpLineAMidPoint = new Point(0,
                ((lineA.getStartPoint().getX() + lineA.getEndPoint().getX()) / 2),
                ((lineA.getStartPoint().getY() + lineA.getEndPoint().getY()) / 2));
        Point tmpLineBMidPoint = new Point(0,
                ((lineB.getStartPoint().getX() + lineB.getEndPoint().getX()) / 2),
                ((lineB.getStartPoint().getY() + lineB.getEndPoint().getY()) / 2));
        double sub_x = Math.abs(tmpLineAMidPoint.getX() - tmpLineBMidPoint.getX());
        double sub_y = Math.abs(tmpLineAMidPoint.getY() - tmpLineBMidPoint.getY());
        this.E_LDistance = DoubleUtil.changeDouble( Math.sqrt(sub_x * sub_x + sub_y * sub_y) );

        if (sub_x == 0){
            this.E_Lk = Double.POSITIVE_INFINITY;
        }else {
            this.E_Lk = DoubleUtil.changeDouble(sub_y / sub_x);
        }

        this.isFormed = false;
    }

    public Segment getLineA() {
        return lineA;
    }

    public void setLineA(Segment lineA) {
        this.lineA = lineA;
    }

    public Segment getLineB() {
        return lineB;
    }

    public void setLineB(Segment lineB) {
        this.lineB = lineB;
    }

    public Point getX_MidPoint() {
        return X_MidPoint;
    }

    public void setX_MidPoint(Point x_MidPoint) {
        X_MidPoint = x_MidPoint;
    }

    public double getX_LDistance() {
        return X_LDistance;
    }

    public void setX_LDistance(double x_LDistance) {
        X_LDistance = x_LDistance;
    }

    public double getX_Lk() {
        return X_Lk;
    }

    public void setX_Lk(double x_Lk) {
        X_Lk = x_Lk;
    }

    public double getE_LDistance() {
        return E_LDistance;
    }

    public void setE_LDistance(double e_LDistance) {
        E_LDistance = e_LDistance;
    }

    public double getE_Lk() {
        return E_Lk;
    }

    public void setE_Lk(double e_Lk) {
        E_Lk = e_Lk;

    }

    public boolean isFormed() {
        return isFormed;
    }

    public void setFormed(boolean formed) {
        isFormed = formed;
    }

    @Override
    public String toString() {
        return "ParallelSegment{" +
                "lineA=" + lineA +
                ", lineB=" + lineB +
                ", X_MidPoint=" + X_MidPoint +
                ", X_LDistance=" + X_LDistance +
                ", X_Lk=" + X_Lk +
                ", E_LDistance=" + E_LDistance +
                ", E_Lk=" + E_Lk +
                ", isFormed=" + isFormed +
                '}';
    }
}
