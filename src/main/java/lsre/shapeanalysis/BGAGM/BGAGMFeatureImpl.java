package lsre.shapeanalysis.BGAGM;

import lsre.shapeanalysis.primitive.bean.BaseObject;
import lsre.shapeanalysis.primitive.bean.Circle;
import lsre.shapeanalysis.primitive.bean.Point;
import lsre.shapeanalysis.primitive.bean.Segment;
import lsre.shapeanalysis.primitive.util.PrimitiveUtil;
import lsre.utils.SerializationUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.omg.CORBA.PUBLIC_MEMBER;
import util.ArrayUtil;
import util.DoubleUtil;
import util.ZjzFileUtil;

import java.io.File;
import java.io.PipedInputStream;
import java.util.*;

/**
 * Implement the method mentioned on the article 'Plane Geometry Figure Retrieval based on Bilayer Geometric Attributed
 * Graph Matching'
 *
 * Created by lijun on 16/1/18.
 */
public class BGAGMFeatureImpl {

    double[] data;
    /**
     *data[0]:点数
     *
     * data[1~8] : CP : 8维
     *
     * data[9~14] : LP : 6*data[0]维
     *
     * data[9+6*data[0] ~ 9+6*data[0] + 7] : Layout : 8维
     */
    ArrayList<Double> tmpDataArray;


    private File file;
    private String htmlFilePath;

    //存放指令集合;
    Map<Integer, String> commandMap;
    //存放点集合;
    Map<Integer, Point> pointMap;
    //存放检测出的圆
    Set<Circle> circleSet;
    //存放检测出来的线段;
    Map<Integer, Segment> lineMap;

    double WINDOW_MAX_H = 0;
    double WINDOW_MAX_W = 0;
    //临时的上下左右值,为计算WINDOW_MAX_H & WINDOW_MAX_H
    double WINDOW_UP = 0;
    double WINDOW_DOWN = 0;
    double WINDOW_LEFT = 0;
    double WINDOW_RIGHT = 0;


    public BGAGMFeatureImpl(){}

    public BGAGMFeatureImpl(File file){
        this.file = file;
        String htmlFileName = file.getName().replace("zjz", "htm");
        this.htmlFilePath = file.getParent()+ File.separator + htmlFileName;

        init();
    }

    public void extract(File file){
        this.file = file;
        String htmlFileName = file.getName().replace("zjz", "htm");
        this.htmlFilePath = file.getParent() + File.separator + htmlFileName;
        init();
    }

    private void init() {
        commandMap = new HashMap<Integer, String>();
        pointMap = new HashMap<Integer, Point>();
        circleSet = new HashSet<Circle>();
        lineMap = new HashMap<Integer, Segment>();

        parseFile();

        extract();

        data = new double[tmpDataArray.size()];
        int i = 0;
        for (double num :
                tmpDataArray){
            data[i++] = num;
        }

    }

    private void extract() {

        tmpDataArray = new ArrayList<Double>();

        tmpDataArray.add(0, (double) pointMap.size());
        //提取圆特征(8维)
        extractCircleFeature(circleSet);
        //提取layout特征(8维)
        extractLayoutFeature(circleSet, pointMap);
        //直线特征部分具有(6*点数)维
        extractLineVectorFeature(lineMap, pointMap);

    }

    private void extractLayoutFeature(Set<Circle> circleSet, Map<Integer, Point> pointMap) {
        
        extractRelationBetweenCircleAndSegment(circleSet, pointMap);
        extractRelationBetweenCircles(circleSet);
        
    }

    private void extractRelationBetweenCircles(Set<Circle> circleSet) {
        double disjoint = 0d;
        double external_tangent = 0d;
        double intersected = 0d;
        double internal_tangent = 0d;
        double included = 0d;

        Circle[] circles = circleSet.toArray(new Circle[0]);
        for (int i = 0; i < circles.length; i++) {
            for (int j = i; j < circles.length; j++){
                double radiusDisMax = circles[i].getRadius() + circles[j].getRadius();
                double radiusDisMin = Math.abs(circles[i].getRadius() - circles[j].getRadius());
                double distance = PrimitiveUtil.getDistance(circles[i].getCenter(), circles[j].getCenter());

                if ((distance - radiusDisMax) > 5){
                    disjoint = 1d;
                }else if (DoubleUtil.isEqual_Distance(distance, radiusDisMax)){
                    external_tangent = 1d;
                }else if ((distance -  radiusDisMin) > 5){
                    intersected = 1d;
                }else if (DoubleUtil.isEqual_Distance(distance, radiusDisMin)){
                    internal_tangent = 1d;
                }else {
                    included = 1d;
                }
            }
        }
        tmpDataArray.add(disjoint);
        tmpDataArray.add(external_tangent);
        tmpDataArray.add(intersected);
        tmpDataArray.add(internal_tangent);
        tmpDataArray.add(included);
    }

    private void extractRelationBetweenCircleAndSegment(Set<Circle> circleSet, Map<Integer, Point> pointMap) {
        double intersect = 0d;
        double tangent = 0d;
        double disjoint = 0d;

        for (Circle circle:
                circleSet){
            for (int k:
                    pointMap.keySet()){
                Point tmpPoint = pointMap.get(k);
                if (DoubleUtil.isEqual(PrimitiveUtil.getDistance(circle.getCenter(), tmpPoint), circle.getRadius())){
                    ArrayList<Segment> lineListContainsTmpPoint = getSegmentListContainsTmpPoint(lineMap, tmpPoint);
                    for (Segment segment :
                            lineListContainsTmpPoint){
                        if (DoubleUtil.isEqual_Distance(PrimitiveUtil.getDistancePointAndSegment(circle.getCenter(), segment), circle.getRadius())){
                            tangent = 1d;
                        }
                    }
                    if (DoubleUtil.isEqual( tangent, 0 )){
                        intersect = 1d;
                    }
                }

            }

        }
        if (DoubleUtil.isEqual(tangent, 0) && DoubleUtil.isEqual(intersect, 0)){
            disjoint = 1d;
        }

        tmpDataArray.add(intersect);
        tmpDataArray.add(tangent);
        tmpDataArray.add(disjoint);

    }

    private ArrayList<Segment> getSegmentListContainsTmpPoint(Map<Integer, Segment> lineMap, Point tmpPoint) {
        ArrayList<Segment> lineList = new ArrayList<Segment>();
        for (int k :
                lineMap.keySet()) {
            if (tmpPoint.equals(lineMap.get(k).getStartPoint()) || tmpPoint.equals(lineMap.get(k).getEndPoint())){
                lineList.add(lineMap.get(k));
            }
        }

        return lineList;
    }

    private void extractLineVectorFeature(Map<Integer, Segment> lineMap, Map<Integer, Point> pointMap) {

        Point curPoint;
        for (int k:
                pointMap.keySet()){
            curPoint = pointMap.get(k);

            extracAV(curPoint, lineMap);
            extractDegree(curPoint, lineMap);
            extarcALE(curPoint, lineMap);

        }

    }

    private void extarcALE(Point curPoint, Map<Integer, Segment> lineMap) {
        double max = 0d;
        double min = Double.MAX_VALUE;

        Segment segment;
        for (int k :
                lineMap.keySet()) {
            segment = lineMap.get(k);
            if (curPoint.equals(segment.getStartPoint()) || curPoint.equals(segment.getEndPoint())){

                if (segment.getLength() > max){
                    max = segment.getLength();
                }
                if (segment.getLength() < min){
                    min = segment.getLength();
                }

            }
        }

        max = max / Math.max(WINDOW_MAX_H, WINDOW_MAX_W);
        min = min / Math.max(WINDOW_MAX_H, WINDOW_MAX_W);



        tmpDataArray.add(max);
        tmpDataArray.add(min);
    }

    private void extractDegree(Point curPoint, Map<Integer, Segment> lineMap) {
        double degree = 0d;

        ArrayList<Segment> lineList = new ArrayList<Segment>();
        for (int k :
                lineMap.keySet()) {
            if (curPoint.equals(lineMap.get(k).getStartPoint()) || curPoint.equals(lineMap.get(k).getEndPoint())){
                lineList.add(lineMap.get(k));
            }
        }

        degree = lineList.size();

        tmpDataArray.add(degree);
    }

    private void extracAV(Point curPoint, Map<Integer, Segment> lineMap) {

        ArrayList<Segment> lineList = new ArrayList<Segment>();
        for (int k :
                lineMap.keySet()) {
            if (curPoint.equals(lineMap.get(k).getStartPoint()) || curPoint.equals(lineMap.get(k).getEndPoint())){
                lineList.add(lineMap.get(k));
            }
        }

        double isMiddleV = 0d;
        double isEndV = 1d;
        double isPedalV = 0d;

        for (int i = 0; i < lineList.size(); i++) {
            Segment firstSegment = lineList.get(i);
            for (int j = i; j < lineList.size(); j++){
                Segment secondSegment = lineList.get(j);

                if (DoubleUtil.isEqual_K(firstSegment.getK(), secondSegment.getK()) && PrimitiveUtil.is2SegmentLengthEqual(firstSegment, secondSegment)){
                    isMiddleV = 1d;
                }

                if (DoubleUtil.isEqual_K(firstSegment.getK(), secondSegment.getK())){
                    isEndV = 0d;
                }

                if (PrimitiveUtil.is2SegmentPedal(firstSegment, secondSegment)){
                    isPedalV = 1d;
                }
            }
        }

        tmpDataArray.add(isMiddleV);
        tmpDataArray.add(isEndV);
        tmpDataArray.add(isPedalV);
    }

    private void extractCircleFeature(Set<Circle> circleSet) {

        int numOfCircle = circleSet.size();


        double radius_AVG = 0d;
        double radius_VAR = 0d;

        double area_AVG = 0d;
        double area_VAR = 0d;

        double circumference_AVG = 0d;
        double circumference_VAR = 0d;

        double center_distance = 0d;

        if (numOfCircle > 0){
            //计算半径,面积,周长的均值;
            for (Circle circle :
                    circleSet) {
                circle.initAandS();

                radius_AVG = (radius_AVG + circle.getRadius() / Math.max(WINDOW_MAX_H, WINDOW_MAX_W));
                area_AVG = area_AVG + (circle.getArea() / (WINDOW_MAX_H * WINDOW_MAX_W));
                circumference_AVG = (circumference_AVG + circle.getCircumference() / ( 2 * (WINDOW_MAX_H + WINDOW_MAX_W)));
            }

            radius_AVG = (double) radius_AVG / numOfCircle;
            area_AVG = (double) area_AVG / numOfCircle;
            circumference_AVG = (double) circumference_AVG / numOfCircle;

            //计算圆心距,如果只有1个圆,则圆心距均值为0;
            if (numOfCircle == 1){
                center_distance = 0;
            }else {
                int count = 0;
                Circle[] circles = circleSet.toArray(new Circle[0]);
                for (int i = 0; i<circles.length; i++){
                    Circle circleA = circles[i];
                    for (int j = i + 1; j<circles.length; j++){
                        count++;
                        Circle circleB = circles[j];
                        double distance = PrimitiveUtil.getDistance(circleA.getCenter(), circleB.getCenter()) / Math.max(WINDOW_MAX_H, WINDOW_MAX_W);
                        center_distance = center_distance + distance;
                    }
                }
                center_distance = (double) (center_distance / count);
            }

            //计算半径,面积,周长的标准差;
            for (Circle circle :
                    circleSet) {
                double tmp_radius_vars = Math.abs( (circle.getRadius() / Math.max(WINDOW_MAX_H, WINDOW_MAX_W)) - radius_AVG );
                double tmp_area_vars = Math.abs( (circle.getArea() / (WINDOW_MAX_H * WINDOW_MAX_W)) - area_AVG );
                double tmp_circum_vars = Math.abs( (circle.getCircumference() / (2 * (WINDOW_MAX_H + WINDOW_MAX_W))) - circumference_AVG);

                radius_VAR = radius_VAR + tmp_radius_vars * tmp_radius_vars;
                area_VAR = area_VAR + tmp_area_vars * tmp_area_vars;
                circumference_VAR = circumference_VAR + tmp_circum_vars * tmp_circum_vars;
            }

            radius_VAR = Math.sqrt(radius_VAR / numOfCircle);
            area_VAR = Math.sqrt(area_VAR / numOfCircle);
            circumference_VAR = Math.sqrt(circumference_VAR / numOfCircle);
        }


        tmpDataArray.add((double) numOfCircle);
        tmpDataArray.add(radius_AVG);
        tmpDataArray.add(radius_VAR);
        tmpDataArray.add(area_AVG);
        tmpDataArray.add(area_VAR);
        tmpDataArray.add(circumference_AVG);
        tmpDataArray.add(circumference_VAR);
        tmpDataArray.add(center_distance);

    }


    private void parseFile() {
        String[] commands = ZjzFileUtil.parseFile2Commands(new File(htmlFilePath));
        if (commands == null || commands.length == 0){
            System.out.println("[ERROR] commands is null!");
            return;
        }

        //处理做图指令数组
        initCommandMap(commands);
        //处理提取所有点集合;
        initPointMap(commands);
        //处理所有圆,并入集合;
        figureOutAllCircle(commands);
        //初始化课件的窗口边界;
        initWindowsMAX();
        //处理所有线段(包含交叉产生的线段)
        figureOutAllLines(commands);
        //剔除无效点
        deletePointlessPoints(pointMap, lineMap);


    }

    private void deletePointlessPoints(Map<Integer, Point> pointMap, Map<Integer, Segment> lineMap) {
        Iterator<Map.Entry<Integer, Point>> it = pointMap.entrySet().iterator();
        while (it.hasNext()){
            Map.Entry<Integer, Point>entry = it.next();
            Point point = entry.getValue();
            if (!isPointLinkedMoreThan2Lines(point, lineMap)){
                it.remove();
            }
        }
    }

    private boolean isPointLinkedMoreThan2Lines(Point tmpPoint, Map<Integer, Segment> lineMap) {

        int count = 0;
        for (int k :
                lineMap.keySet()){
            Segment tmpSegment = lineMap.get(k);
            if (tmpSegment.getStartPoint().equals(tmpPoint) || tmpSegment.getEndPoint().equals(tmpPoint)){
                count++;
            }
        }
        if (count >= 2){
            return true;
        }else {
            return false;
        }
    }

    private void figureOutAllLines(String[] commands) {
        List<String> lineList = new ArrayList<String>();
        for (String command :
                commands) {
            if (command.startsWith("Line")){
                lineList.add(command);
            }
        }

        String[] lineParams;
        Segment segment;
        Point startPoint;
        Point endPoint;

        for (String lineItem :
                lineList) {
            lineParams = lineItem.substring(lineItem.indexOf("(") + 1, lineItem.indexOf(")")).split(",");

            //2和3分别作为x轴和y轴;
            if (Integer.valueOf(lineParams[0]) == 2 || Integer.valueOf(lineParams[0]) == 3){
                continue;
            }

            segment = new Segment(Integer.valueOf(lineParams[0]));
            List<Integer> pointsIDList = getPointIdBySegmentId(Integer.valueOf(lineParams[0]), commands);

            if (pointsIDList == null || pointsIDList.size() < 2){
                System.out.println("[ERROR][figureOutAllLines]command segment parse failure!");
            }

            startPoint = new Point(pointsIDList.get(0));
            setPointByCmdId(startPoint, commands);

            endPoint = new Point(pointsIDList.get(1));
            setPointByCmdId(endPoint, commands);

            segment.setStartPoint(startPoint);
            segment.setEndPoint(endPoint);
            initSegment(segment);
            lineMap.put(Integer.valueOf(lineParams[0]), segment);
        }

        handleInsertionPoint(commands, commandMap, lineMap);
    }

    private void handleInsertionPoint(String[] commands, Map<Integer, String> commandMap, Map<Integer, Segment> lineMap) {

        int lineIdMax = -1;
        for (int lineId : lineMap.keySet() ){
            lineIdMax = lineId > lineIdMax ? lineId : lineIdMax;
        }
        int newLineItemId = lineIdMax + 1;

        Segment newSegment;
        for (int key :
                commandMap.keySet()){
            String commandStr = commandMap.get(key);
            String commandHead = commandStr.substring(0, commandStr.indexOf("("));
            String[] params = commandStr.substring(commandStr.indexOf("(")+1, commandStr.indexOf(")")).split(",");

            if (commandHead.equalsIgnoreCase("Midpoint")){
                int startPointId = Integer.valueOf(params[0]);
                int endPointId = Integer.valueOf(params[1]);
                Point startPoint = new Point(startPointId);
                setPointByCmdId(startPoint, commands);
                Point endPoint = new Point(endPointId);
                setPointByCmdId(endPoint, commands);
                Point midPoint = new Point(key);
                setPointByCmdId(midPoint, commands);

                newSegment = new Segment(newLineItemId);
                newSegment.setStartPoint(startPoint);
                newSegment.setEndPoint(midPoint);
                initSegment(newSegment);

                if (!lineAlreadyExistInMap(lineMap, newSegment)){
                    lineMap.put(newLineItemId++, newSegment);
                }

                newSegment = new Segment(newLineItemId);
                newSegment.setStartPoint(midPoint);
                newSegment.setEndPoint(endPoint);
                initSegment(newSegment);
                if (!lineAlreadyExistInMap(lineMap, newSegment)){
                    lineMap.put(newLineItemId++, newSegment);
                }
            }else if (commandHead.equalsIgnoreCase("PointOnLine")){

                int segmentId = Integer.valueOf(params[0]);
                ArrayList<Integer> pointsIdOnSegment = getStartAndEndPointIdsBySegmentId(commandMap,segmentId);

                if (pointsIdOnSegment == null || pointsIdOnSegment.size() < 2){
                    continue;
                }

                Point startPoint = new Point(pointsIdOnSegment.get(0));
                setPointByCmdId(startPoint, commands);
                Point endPoint = new Point(pointsIdOnSegment.get(1));
                setPointByCmdId(endPoint, commands);
                Point midPoint = new Point(key);
                setPointByCmdId(midPoint, commands);

                newSegment = new Segment(newLineItemId);
                newSegment.setStartPoint(startPoint);
                newSegment.setEndPoint(midPoint);
                initSegment(newSegment);

                if (!lineAlreadyExistInMap(lineMap, newSegment)){
                    lineMap.put(newLineItemId++, newSegment);
                }

                newSegment = new Segment(newLineItemId);
                newSegment.setStartPoint(midPoint);
                newSegment.setEndPoint(endPoint);
                initSegment(newSegment);

                if (!lineAlreadyExistInMap(lineMap, newSegment)){
                    lineMap.put(newLineItemId++, newSegment);
                }
            } else if (commandHead.equalsIgnoreCase("Foot")){

                int segmentId = Integer.valueOf(params[1]);
                ArrayList<Integer> pointsIdsOnSegment = getStartAndEndPointIdsBySegmentId(commandMap, segmentId);

                if (pointsIdsOnSegment == null || pointsIdsOnSegment.size() < 2){
                    continue;
                }

                Point startPoint = new Point(pointsIdsOnSegment.get(0));
                setPointByCmdId(startPoint, commands);
                Point endPoint = new Point(pointsIdsOnSegment.get(1));
                setPointByCmdId(endPoint, commands);
                Point midPoint = new Point(key);
                setPointByCmdId(midPoint, commands);

                newSegment = new Segment(newLineItemId);
                newSegment.setStartPoint(startPoint);
                newSegment.setEndPoint(midPoint);
                initSegment(newSegment);

                if (!lineAlreadyExistInMap(lineMap, newSegment)){
                    lineMap.put(newLineItemId++, newSegment);
                }

                newSegment = new Segment(newLineItemId);
                newSegment.setStartPoint(midPoint);
                newSegment.setEndPoint(endPoint);
                initSegment(newSegment);

                if (!lineAlreadyExistInMap(lineMap, newSegment)){
                    lineMap.put(newLineItemId++, newSegment);
                }

            } else if (commandHead.equalsIgnoreCase("IntersectionOfLine")) {

                int segmentAId = Integer.valueOf(params[0]);
                int segmentBId = Integer.valueOf(params[1]);

                ArrayList<Integer> pointsIdsOnSegmentA = getStartAndEndPointIdsBySegmentId(commandMap, segmentAId);
                ArrayList<Integer> pointsIdsOnSegmentB = getStartAndEndPointIdsBySegmentId(commandMap, segmentBId);

                Point startPoint = new Point(pointsIdsOnSegmentA.get(0));
                setPointByCmdId(startPoint, commands);
                Point endPoint = new Point(pointsIdsOnSegmentA.get(1));
                setPointByCmdId(endPoint, commands);
                Point midPoint = new Point(key);
                setPointByCmdId(midPoint, commands);

                newSegment = new Segment(newLineItemId);
                newSegment.setStartPoint(startPoint);
                newSegment.setEndPoint(midPoint);
                initSegment(newSegment);
                if (!lineAlreadyExistInMap(lineMap, newSegment)){
                    lineMap.put(newLineItemId++, newSegment);
                }
                newSegment = new Segment(newLineItemId);
                newSegment.setStartPoint(midPoint);
                newSegment.setEndPoint(endPoint);
                initSegment(newSegment);
                if (!lineAlreadyExistInMap(lineMap, newSegment)){
                    lineMap.put(newLineItemId++, newSegment);
                }

                startPoint = new Point(pointsIdsOnSegmentB.get(0));
                setPointByCmdId(startPoint, commands);
                endPoint = new Point(pointsIdsOnSegmentB.get(1));
                setPointByCmdId(endPoint, commands);
                midPoint = new Point(key);
                setPointByCmdId(midPoint, commands);

                newSegment = new Segment(newLineItemId);
                newSegment.setStartPoint(startPoint);
                newSegment.setEndPoint(midPoint);
                initSegment(newSegment);
                if (!lineAlreadyExistInMap(lineMap, newSegment)){
                    lineMap.put(newLineItemId++, newSegment);
                }
                newSegment = new Segment(newLineItemId);
                newSegment.setStartPoint(midPoint);
                newSegment.setEndPoint(endPoint);
                initSegment(newSegment);
                if (!lineAlreadyExistInMap(lineMap, newSegment)){
                    lineMap.put(newLineItemId++, newSegment);
                }
            } else if (commandHead.equalsIgnoreCase("IntersectionOfLinePLine") || commandHead.equalsIgnoreCase("IntersectionOfLineVLine")){

                int segmentId = Integer.valueOf(params[0]);
                ArrayList<Integer> pointsIdsOnSegment = getStartAndEndPointIdsBySegmentId(commandMap, segmentId);

                if (pointsIdsOnSegment == null || pointsIdsOnSegment.size() < 2){
                    continue;
                }

                Point startPoint = new Point(pointsIdsOnSegment.get(0));
                setPointByCmdId(startPoint, commands);
                Point endPoint = new Point(pointsIdsOnSegment.get(1));
                setPointByCmdId(endPoint, commands);
                Point midPoint = new Point(key);
                setPointByCmdId(midPoint, commands);

                newSegment = new Segment(newLineItemId);
                newSegment.setStartPoint(startPoint);
                newSegment.setEndPoint(midPoint);
                initSegment(newSegment);

                if (!lineAlreadyExistInMap(lineMap, newSegment)){
                    lineMap.put(newLineItemId++, newSegment);
                }

                newSegment = new Segment(newLineItemId);
                newSegment.setStartPoint(midPoint);
                newSegment.setEndPoint(endPoint);
                initSegment(newSegment);

                if (!lineAlreadyExistInMap(lineMap, newSegment)){
                    lineMap.put(newLineItemId++, newSegment);
                }
            }

        }

    }

    private static ArrayList<Integer> getStartAndEndPointIdsBySegmentId(Map<Integer, String> commandMap, int segmentId) {

        ArrayList<Integer> result = null;
        String cmdStr = commandMap.get(segmentId);
        String cmdHead = cmdStr.substring(0, cmdStr.indexOf("("));
        String[] params = cmdStr.substring(cmdStr.indexOf("(") + 1 ,cmdStr.indexOf(")")).split(",");

        if (cmdHead.equalsIgnoreCase("Segment")){
            result = new ArrayList<Integer>();
            result.add(0, Integer.valueOf(params[0]));
            result.add(1, Integer.valueOf(params[1]));
        }
        return result;
    }

    private void initSegment(Segment segment) {
        segment.initKB();
    }

    private void setPointByCmdId(Point point, String[] commands) {
        for (int i = 0 ; i<commands.length ; i++){

            String command = commands[i];
            if (!command.contains("=")){
                continue;
            }

            int pointId = Integer.valueOf(command.substring(0, command.indexOf("=")));

            //找到对应点指令,在指令附近寻找ObjPosition,设置点的坐标
            if (pointId == point.getId()) {
                for(int next = i+1; next<i+3; next++){
                    String nearCommand = commands[next];
                    if (nearCommand.contains("=")){
                        System.out.println("[ERROR] Point" + pointId + "set x y failure!(from setPointByCmdId())");
                        return;
                    }else if (nearCommand.startsWith("ObjPosition")){
                        String[] params = nearCommand.substring(nearCommand.indexOf("(") + 1, nearCommand.indexOf(")")).split(",");
                        if (Integer.valueOf(params[0]) == point.getId()){
                            point.setX(Double.parseDouble(params[1]));
                            point.setY(Double.parseDouble(params[2]));
                            return;
                        }
                    }
                }
            }

        }
    }

    private static boolean lineAlreadyExistInMap(Map<Integer, Segment> lineMap, Segment newSegment) {

        for (int key : lineMap.keySet()){
            Segment segment = lineMap.get(key);

            if (PrimitiveUtil.isSameSegment(segment, newSegment)){
                return true;
            }
        }
        return false;
    }

    private List<Integer> getPointIdBySegmentId(Integer segmentID, String[] commands) {

        ArrayList<Integer> result = new ArrayList<Integer>(2);

        for (String command:
                commands){
            if (!command.contains("=")){
                continue;
            }

            int primitiveId = Integer.valueOf(command.substring(0, command.indexOf("=")));

            if (primitiveId != segmentID){
                continue;
            }

            String[] params = command.substring(command.indexOf("(") + 1, command.indexOf(")")).split(",");

            assert (params.length >= 2);
            result.add(0, Integer.valueOf(params[0]));
            result.add(1, Integer.valueOf(params[1]));
        }

        return result;
    }

    private void initWindowsMAX() {
        initCircleSetMAX(circleSet);
        initPointSetMAX(pointMap);

        WINDOW_MAX_H = WINDOW_DOWN - WINDOW_UP;
        WINDOW_MAX_W = WINDOW_RIGHT - WINDOW_LEFT;
    }

    private void initPointSetMAX(Map<Integer, Point> pointMap) {
        Point tmp;
        for (int key:
                pointMap.keySet()){
            tmp = pointMap.get(key);
            if (tmp.getX() > WINDOW_RIGHT){
                WINDOW_RIGHT = tmp.getX();
            }
            if (tmp.getX() < WINDOW_LEFT){
                WINDOW_LEFT = tmp.getX();
            }
            if (tmp.getY() > WINDOW_DOWN){
                WINDOW_DOWN = tmp.getY();
            }
            if (tmp.getY() < WINDOW_UP){
                WINDOW_UP = tmp.getY();
            }
        }
    }

    private void initCircleSetMAX(Set<Circle> circleSet) {

        for (Circle circle :
                circleSet) {
            circle.initWindow();
            WINDOW_UP = circle.getUP();
            WINDOW_DOWN = circle.getDOWN();
            WINDOW_LEFT = circle.getLEFT();
            WINDOW_RIGHT = circle.getRIGHT();
        }
        for (Circle circle:
                circleSet){
            if (circle.getUP() < WINDOW_UP){
                WINDOW_UP = circle.getUP();
            }
            if (circle.getDOWN() > WINDOW_DOWN){
                WINDOW_DOWN = circle.getDOWN();
            }
            if (circle.getLEFT() < WINDOW_LEFT){
                WINDOW_LEFT = circle.getLEFT();
            }
            if (circle.getRIGHT() > WINDOW_RIGHT){
                WINDOW_RIGHT = circle.getRIGHT();
            }
        }

    }

    /**
     * 初始化点集合;即提取所有的点入集合pointMap
     *
     * @param commands  指令数组
     */
    private void initPointMap(String[] commands) {

        Point tmpPoint;
        for (String command :
                commands) {
            if (command.startsWith("ObjPosition")) {
                tmpPoint = ZjzFileUtil.extractPointByCommandStr(command);
                pointMap.put(tmpPoint.getId(), tmpPoint);
            }else
                continue;
            }

    }

    private void figureOutAllCircle(String[] commands) {
        assert (commands!=null && commands.length>0);

        for (String command :
                commands) {
            if (!command.contains("=")) {
                continue;
            }
            int indexOfCommandStart = command.indexOf(":") + 1;
            int indexOfCommandEnd = command.indexOf("(");

            String comId = command.substring(0, command.indexOf("="));
            String comBody = command.substring(indexOfCommandStart, indexOfCommandEnd);
            String[] comParams = command.substring(indexOfCommandEnd+1, command.indexOf(")")).split(",");

            Point centerPoint;
            Circle circle;
            if (comBody.startsWith("Circle")){
                if (comBody.equalsIgnoreCase("Circle")){
                    int centerPointID = Integer.valueOf(comParams[0]);
                    if (!pointMap.containsKey(centerPointID)){
                        System.out.println("[ERROR][Circle]centerPoint not exist!!" + htmlFilePath.substring(htmlFilePath.lastIndexOf("/")));
                        centerPoint = new Point(centerPointID);
                    }else {
                        centerPoint = pointMap.get(centerPointID);
                    }
                    Point onCirclePoint;
                    int onCirclePointID = Integer.valueOf(comParams[1]);
                    if (!pointMap.containsKey(onCirclePointID)){
                        System.out.println("[ERROE][Circle]onCirclePoint not exist!!" + htmlFilePath.substring(htmlFilePath.lastIndexOf("/")));
                        onCirclePoint = new Point(onCirclePointID);
                    }else {
                        onCirclePoint = pointMap.get(onCirclePointID);
                    }
                    double radius = PrimitiveUtil.getDistance(centerPoint, onCirclePoint);
                    circle = new Circle(Integer.valueOf(comId));
                    circle.setCenter(centerPoint);
                    circle.setRadius(radius);
                    circleSet.add(circle);

                }else if (comBody.equalsIgnoreCase("CircleOfTPRadius")){
                    int centerPointID = Integer.valueOf(comParams[0]);
                    if (!pointMap.containsKey(centerPointID)){
                        System.out.println("[ERROR][CircleOfTPRadius]centerPoint not exist!!" + htmlFilePath.substring(htmlFilePath.lastIndexOf("/")));
                        centerPoint = new Point(centerPointID);
                    }else {
                        centerPoint = pointMap.get(centerPointID);
                    }

                    int radiusAID = Integer.valueOf(comParams[1]);
                    int radiusBID = Integer.valueOf(comParams[2]);
                    Point radiusA;
                    Point radiusB;
                    if (!pointMap.containsKey(radiusAID) || !pointMap.containsKey(radiusBID)){
                        System.out.println("[ERROR][CircleOfTPRadius]radius points not exist!!" + file.getName());
                        radiusA = new Point(radiusAID);
                        radiusB = new Point(radiusBID);
                    }else {
                        radiusA = pointMap.get(radiusAID);
                        radiusB = pointMap.get(radiusBID);
                    }

                    double radius = PrimitiveUtil.getDistance(radiusA, radiusB);
                    circle = new Circle(Integer.valueOf(comId));
                    circle.setCenter(centerPoint);
                    circle.setRadius(radius);
                    circleSet.add(circle);

                }else if (comBody.equalsIgnoreCase("CircleOfRadius")){
                    int centerPointID = Integer.valueOf(comParams[0]);
                    if (!pointMap.containsKey(centerPointID)){
                        System.out.println("[ERROR][CircleOfRadius]centerPoint not exist!!" + htmlFilePath.substring(htmlFilePath.lastIndexOf("/")));
                        centerPoint = new Point(centerPointID);
                    }else {
                        centerPoint = pointMap.get(centerPointID);
                    }

                    double radius = Double.valueOf(comParams[1]) / 0.02;

                    circle = new Circle(Integer.valueOf(comId));
                    circle.setCenter(centerPoint);
                    circle.setRadius(radius);
                    circleSet.add(circle);

                }else if (comBody.equalsIgnoreCase("CircleOf3Point")){
                    circle = new Circle(Integer.valueOf(comId));
                    int pointAID = Integer.valueOf(comParams[0]);
                    int pointBID = Integer.valueOf(comParams[1]);
                    int pointCID = Integer.valueOf(comParams[2]);

                    Point pointA = pointMap.get(pointAID);
                    Point pointB = pointMap.get(pointBID);
                    Point pointC = pointMap.get(pointCID);

                    centerPoint =  PrimitiveUtil.getCenterPointBy3Point(pointA, pointB, pointC);
                    double radius = PrimitiveUtil.getDistance(centerPoint, pointA);
                    circle.setCenter(centerPoint);
                    circle.setRadius(radius);
                    circleSet.add(circle);
                }
            }

        }
    }

    private void initCommandMap(String[] commands) {
        assert (commands!=null && commands.length>0);
        for (String command :
                commands) {
            if (!command.contains(":") ){
                continue;
            }
            int indexOfEqual = command.indexOf("=");
            int comId = Integer.valueOf(command.substring(0, indexOfEqual));
            String comStr = command.substring(command.indexOf(":")+1);
            commandMap.put(comId, comStr);
        }
    }

    private void print(){

        System.out.println(file.getName() + " has point:  " + pointMap.size());
        for (double num :
                data){
            System.out.print(num + ",");
        }

    }

    private void printPointMap(){
        for (int k:
                pointMap.keySet()){
            System.out.println(k + " : " + pointMap.get(k).toString());
        }
    }

    public static void main(String[] args){
        String FILEPATH = "/Users/lijun/git/zjzretrieval/src/main/resources/data/";
        File dir = new File(FILEPATH);
        File[] files = dir.listFiles();
        BGAGMFeatureImpl imp;
        for (File file :
                files){
            if (!file.getName().contains("zjz")){
                continue;
            }
            imp = new BGAGMFeatureImpl(file);
            imp.print();
            System.out.println();
        }


    }

}
