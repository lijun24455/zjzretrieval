package lsre.shapeanalysis.primitive.util;

import lsre.shapeanalysis.primitive.bean.Point;
import lsre.shapeanalysis.primitive.bean.Segment;
import util.DoubleUtil;

/**
 * Created by lijun on 15/12/6.
 */
public final class PrimitiveUtil {

    public final static String[] TYPE_POINT = {
            "Point","Midpoint",
            "PointOnLine","PointOnPLine","PointOnVLine",
            "PointOnPloygon",
            "Foot","IsosTriangle","Parallelogram","Square","SymmetricPoint",
            "PointOfAngleBisector","DivisionPoint",
            "IntersectionOfLine","IntersectionOfLinePLine","IntersectionOfLineVLine","IntersectionOfPLinePLine","IntersectionOfPLineVLine","IntersectionOfVLineVLine",
            "Centroid","Orthocenter","Circumcenter",
            "Incenter","RatioPoint","PointFlexRotate",
            "ConicLeftFocus","ConicRightFocus",
            "ConicLeftVertex","ConicRightVertex","ConicLowerVertex","ConicUpperVertex",
            "PointOnConic","PointOnCircle","CircleCentre","ConicCentre",
            "IntersectionOfCircle","IntersectionOfLineConic",
            "VertexOfPolygon","VertexOfCircle","VertexOfCircleEx",
    };

    public final static String[] TYPE_LINE = {
            "Segment","Vector","Radial","Line","ParallelLine","VerticalLine","AngleBisector",
            "LineOfPointSlope","LineOfPointAngle","LineOfPointXIntercept","LineOfPointYIntercept",
            "LineOfIntercept","LineOfSlopeIntercept","LineOfEquation",
            "CommonTangent","Tangent","Directrix","Asymptote",
    };

    public final static String[] TYPE_CIRCLE = {
            "Circle","CircleOfTPRadius","CircleOfRadius","CircleOf3Point",
            "AngleArc","ThreePointArc","ArcOnCircle",
    };

    public final static int COUNT_TYPE_ANGLE = 1000;
    public final static int COUNT_TYPE_RECTANGLE = 1001;
    public final static int COUNT_TYPE_TRAPEZOID = 1002;

    public final static boolean isSamePoint(Point a, Point b){
//        if (a.getX()!=b.getX()){
//            return false;
//        }else if (a.getY()!=b.getY()){
//            return false;
//        }else{
//            return true;
//        }

        if (Double.compare(a.getX(), b.getX()) != 0) return false;
        return Double.compare(a.getY(), b.getY()) == 0;

    }

    public static boolean isSamePointByCommandId(Point a, Point b){
        return a.getId() == b.getId();
    }

    public static boolean isSameSegment(Segment a, Segment b){
        if ((   isSamePointByCommandId(a.getStartPoint(), b.getStartPoint()) &&
                        isSamePointByCommandId(a.getEndPoint(), b.getEndPoint())) ||
                (isSamePointByCommandId(a.getStartPoint(), b.getEndPoint()) &&
                        isSamePointByCommandId(a.getEndPoint(), b.getStartPoint()))){
            return true;
        }else {
            return false;
        }

    }

    public static boolean is2SegmentFitEPPL(Segment lineA, Segment lineB){
        if ( !DoubleUtil.isEqual_K(lineA.getK(), lineB.getK()) ){
            return false;
        } else if ( !DoubleUtil.isEqual_Distance(lineA.getLength(), lineB.getLength()) ) {
            return false;
        } else if ( is2SegmentOnSameLine(lineA, lineB) ){
            return false;
        } else {
            return true;
        }
    }

    public static boolean is2SegmentFitNEPPL(Segment lineA, Segment lineB){
        if ( !DoubleUtil.isEqual_K(lineA.getK(), lineB.getK()) ){
            return false;
        } else if ( DoubleUtil.isEqual_Distance(lineA.getLength(), lineB.getLength()) ) {
            return false;
        } else if ( is2SegmentOnSameLine(lineA, lineB) ){
            return false;
        } else {
            return true;
        }

    }

    private static boolean is2SegmentOnSameLine(Segment lineA, Segment lineB) {
        Point startA = lineA.getStartPoint();
        Point endA = lineA.getEndPoint();
        Point startB = lineB.getStartPoint();
        Point endB = lineB.getEndPoint();

        if (isSamePoint(startA, startB) || isSamePoint(startA, endB) || isSamePoint(endA, startB) || isSamePoint(endA, endB)){
            return true;
        }else {
            return false;
        }
    }

    public static Point getStartPoint(Segment segment){
        return (segment.getStartPoint().getX() + segment.getStartPoint().getY()) < (segment.getEndPoint().getX() + segment.getEndPoint().getY()) ?
                segment.getStartPoint() : segment.getEndPoint();
    }

    public static Point getEndPoint(Segment segment){
        return (segment.getStartPoint().getX() + segment.getStartPoint().getY()) > (segment.getEndPoint().getX() + segment.getEndPoint().getY()) ?
                segment.getStartPoint() : segment.getEndPoint();
    }


    public static double getDistance(Point centerPoint, Point onCirclePoint) {
        return centerPoint.getDistance(onCirclePoint);
    }

    public static Point getCenterPointBy3Point(Point pointA, Point pointB, Point pointC) {
        Point resultPoint = new Point(0);
        double p_x = (pointA.getX() + pointB.getX() + pointC.getX()) / 3.0;
        double p_y = (pointA.getY() + pointB.getY() + pointC.getY()) / 3.0;
        resultPoint.setX(p_x);
        resultPoint.setY(p_y);
        return resultPoint;
    }

    public static boolean is2SegmentLengthEqual(Segment segmentA, Segment segmentB){
        return DoubleUtil.isEqual(segmentA.getLength(), segmentB.getLength());
    }

    public static boolean is2SegmentPedal(Segment segmentA, Segment segmentB) {
        return DoubleUtil.isEqual(-1d, segmentB.getK() * segmentA.getK());
    }

    public static double getDistancePointAndSegment(Point center, Segment segment) {
        if (DoubleUtil.isEqual(segment.getK(), Double.POSITIVE_INFINITY)){
            return Math.abs(center.getX() - segment.getB());
        }else{
            double up =Math.abs( segment.getK() * center.getX() - center.getY() + segment.getB() );
            double base = Math.sqrt(segment.getK() * segment.getK() + 1);
            return up/base;
        }

    }
}
