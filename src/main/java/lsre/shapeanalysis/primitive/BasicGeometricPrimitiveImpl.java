package lsre.shapeanalysis.primitive;

import lsre.shapeanalysis.primitive.bean.*;
import lsre.shapeanalysis.primitive.util.PrimitiveUtil;
import lsre.utils.SerializationUtils;
import org.w3c.dom.Document;
import util.DoubleUtil;
import util.XmlUtil;
import util.ZjzFileUtil;

import java.io.File;
import java.util.*;

/**
 * Created by lijun on 16/1/11.
 */
public class BasicGeometricPrimitiveImpl {
    double data[];
    private File file;
    private String htmlFilePath;

    //存放指令集合;
    Map<Integer, String> commandMap;
    //存放检测出的圆
    Set<Circle> circleSet;
    //存放检测出来的线段;
    Map<Integer, Segment> lineMap;
    //存放检测出来的全部几何元素
    Map<Integer, BaseObject> primitiveMap;

    int numOfPrimitive = 0;
    int numOfLine = 0;
    int numOfCircle = 0;
    int numOfAngle = 0;
    int numOfRectrangle = 0;
    int numOfTrapezoid = 0;


    public BasicGeometricPrimitiveImpl(){}

    public BasicGeometricPrimitiveImpl(File file){
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

        circleSet = new HashSet<Circle>();
        lineMap = new HashMap<Integer, Segment>();
        primitiveMap = new HashMap<Integer, BaseObject>();
        commandMap = new HashMap<Integer, String>();

        parseFile();

        extract();
    }

    private void extract() {

        numOfCircle = circleSet.size();
        numOfLine = lineMap.size();
        numOfPrimitive = primitiveMap.size();
        numOfAngle = countPolyNumByCommand(commandMap, lineMap, PrimitiveUtil.COUNT_TYPE_ANGLE)/6;
        numOfRectrangle = countPolyNumByCommand(commandMap, lineMap, PrimitiveUtil.COUNT_TYPE_RECTANGLE);
        numOfTrapezoid = countPolyNumByCommand(commandMap, lineMap, PrimitiveUtil.COUNT_TYPE_TRAPEZOID);

        ArrayList<Integer> result = new ArrayList<Integer>();
        result.add(numOfPrimitive);
        result.add(numOfLine);
        result.add(numOfCircle);
        result.add(numOfAngle);
        result.add(numOfRectrangle);
        result.add(numOfTrapezoid);

        data = SerializationUtils.toDoubleArray(result);



    }

    private void parseFile() {

        String[] commands = ZjzFileUtil.parseFile2Commands(new File(htmlFilePath));
        if (commands == null || commands.length == 0){
            System.out.println("[ERROR] commands is null!");
            return;
        }

        //处理做图指令数组
        initCommandMap(commands);
        //预置坐标原点
        initBaseOrigin();
        //导出所有几何元素
        figureOutPrimitives(commands);
        //导出所有圆
        figureOutAllCircle(commands);
        //导出所有线段,包括交叉产生的线段
        figureOutAllLines(commands);


    }

    private int countPolyNumByCommand(Map<Integer, String> commandMap, Map<Integer, Segment> lineMap, int type) {
        int result = 0;
        Set<Integer> keys = lineMap.keySet();

        int MAX = 0;
        for (int key : lineMap.keySet()){
            MAX = MAX>key ? MAX : key;
        }
        boolean flag[][];

        Segment firstSegment;
        Segment secondSegment;
        Segment thirdSegment;

        Point startPoint;
        Point midPoint;
        Point endPoint;

        switch (type){

            //三角形
            case PrimitiveUtil.COUNT_TYPE_ANGLE:

                //第一条边;
                for (int k_1 :
                        keys) {

                    firstSegment = lineMap.get(k_1);
                    Point[] points = {firstSegment.getStartPoint(), firstSegment.getEndPoint()};

                    for (Point firstPoint :
                            points){

                        if (PrimitiveUtil.isSamePointByCommandId(firstPoint, firstSegment.getStartPoint())){
                            startPoint = firstSegment.getEndPoint();
                        }else {
                            startPoint = firstSegment.getStartPoint();
                        }

                        //第二条边;
                        for (int k_2 :
                                keys) {
                            if (k_2 == k_1){
                                continue;
                            }

                            secondSegment = lineMap.get(k_2);
                            Point[] points_2 = {secondSegment.getStartPoint(), secondSegment.getEndPoint()};

                            midPoint = null;

                            if (PrimitiveUtil.isSamePointByCommandId(firstPoint, points_2[0])){
                                midPoint = points_2[1];
                            }else if (PrimitiveUtil.isSamePointByCommandId(firstPoint, points_2[1])){
                                midPoint = points_2[0];
                            }else {
                                continue;
                            }

                            if (midPoint == null){
                                continue;
                            }else {

                                ArrayList<Point> pointList = new ArrayList<Point>();
                                pointList.add(firstPoint);
                                pointList.add(startPoint);
                                pointList.add(midPoint);

                                if (is3PointsOnSameSegment(pointList, commandMap)){
                                    continue;
                                }

                                //第三条边;
                                for (int k_3 :
                                        keys) {
                                    if (k_3 == k_2 || k_3 == k_1) {
                                        continue;
                                    }
                                    thirdSegment = lineMap.get(k_3);
                                    Point[] points_3 = {thirdSegment.getStartPoint(), thirdSegment.getEndPoint()};

                                    endPoint = null;

                                    if (PrimitiveUtil.isSamePointByCommandId(midPoint, points_3[0])){
                                        endPoint = points_3[1];
                                    }else if (PrimitiveUtil.isSamePointByCommandId(midPoint, points_3[1])){
                                        endPoint = points_3[0];
                                    }else {
                                        continue;
                                    }

                                    if (endPoint == null){
                                        continue;
                                    }else {

                                        if (PrimitiveUtil.isSamePointByCommandId(startPoint, endPoint)){
                                            result += 1;
                                        }
                                    }

                                }
                            }

                        }
                    }
                }

                break;

            //平行四边形
            case PrimitiveUtil.COUNT_TYPE_RECTANGLE:
                //用来存放平行等边对儿
                List<ParallelSegment> ePPLList = new ArrayList<ParallelSegment>();

                flag = new boolean[MAX+1][MAX+1];

                for (int i :
                        lineMap.keySet()) {
                    for (int j :
                            lineMap.keySet()) {
                        Segment lineA = lineMap.get(i);
                        Segment lineB = lineMap.get(j);
                        //同边则跳过
                        if (PrimitiveUtil.isSameSegment(lineA, lineB) || lineA.getId() == lineB.getId()){
                            continue;
                        }
                        //已经添加过则跳过
                        if (flag[ Math.min(lineA.getId(), lineB.getId()) ][ Math.max(lineA.getId(), lineB.getId()) ]){
                            continue;
                        }

                        if (PrimitiveUtil.is2SegmentFitEPPL(lineA, lineB)){
                            ParallelSegment parallelSegment = new ParallelSegment(lineA, lineB);
                            if (ePPLList.add(parallelSegment)){
                                flag[ Math.min(lineA.getId(), lineB.getId()) ][ Math.max(lineA.getId(), lineB.getId()) ] = true;
                            }
                        }


                    }
                }

                for (int i = 0; i<ePPLList.size(); i++){
                    for (int j = i; j<ePPLList.size(); j++){
                        ParallelSegment firstParallel = ePPLList.get(i);
                        ParallelSegment secondParallel = ePPLList.get(j);

                        if (firstParallel.isFormed() || secondParallel.isFormed()){
                            continue;
                        }

                        if (PrimitiveUtil.isSamePoint(firstParallel.getX_MidPoint(), secondParallel.getX_MidPoint())){

                            if ( DoubleUtil.isEqual_Distance(firstParallel.getE_LDistance(), secondParallel.getX_LDistance())
                                    && DoubleUtil.isEqual_K(firstParallel.getE_Lk(), secondParallel.getX_Lk()) ){
                                result += 1;
                                firstParallel.setFormed(true);
                                secondParallel.setFormed(true);
                            }
                        }
                    }

                }

//                Vector<Integer> vector = new Vector<Integer>();
//
//                //第一条边:
//                for (int k_1 :
//                        keys) {
//                    firstSegment = lineMap.get(k_1);
//                    Point[] points = {firstSegment.getStartPoint(), firstSegment.getEndPoint()};
//
//                    for (Point firstPoint :
//                            points) {
//
//                        if (PrimitiveUtil.isSamePointByCommandId(firstPoint, firstSegment.getStartPoint())) {
//                            startPoint = firstSegment.getEndPoint();
//                        } else {
//                            startPoint = firstSegment.getStartPoint();
//                        }
//
//                        //第二条边;
//                        for (int k_2 :
//                                keys) {
//                            if (k_2 == k_1) {
//                                continue;
//                            }
//
//                            secondSegment = lineMap.get(k_2);
//                            Point[] points_2 = {secondSegment.getStartPoint(), secondSegment.getEndPoint()};
//
//                            midPoint = null;
//
//                            if (PrimitiveUtil.isSamePointByCommandId(firstPoint, points_2[0])) {
//                                midPoint = points_2[1];
//                            } else if (PrimitiveUtil.isSamePointByCommandId(firstPoint, points_2[1])) {
//                                midPoint = points_2[0];
//                            } else {
//                                continue;
//                            }
//
//                            if (midPoint!=null){
//
//                                ArrayList<Point> pointList = new ArrayList<Point>();
//                                pointList.add(firstPoint);
//                                pointList.add(startPoint);
//                                pointList.add(midPoint);
//
//                                if (is3PointsOnSameSegment(pointList, commandMap)){
//                                    continue;
//                                }
//
//                                //第三条边;
//                                for (int k_3 :
//                                        keys) {
//                                    if (k_3 == k_2 || k_3 == k_1) {
//                                        continue;
//                                    }
//                                    thirdSegment = lineMap.get(k_3);
//                                    Point[] points_3 = {thirdSegment.getStartPoint(), thirdSegment.getEndPoint()};
//
//                                    midPoint2 = null;
//
//                                    if (PrimitiveUtil.isSamePointByCommandId(midPoint, points_3[0])){
//                                        midPoint2 = points_3[1];
//                                    }else if (PrimitiveUtil.isSamePoint(midPoint, points_3[1])){
//                                        midPoint2 = points_3[0];
//                                    }else {
//                                        continue;
//                                    }
//
//                                    if (PrimitiveUtil.isSamePointByCommandId(midPoint2, startPoint)){
//                                        continue;
//                                    }else {
//
//                                        pointList.clear();
//                                        pointList.add(startPoint);
//                                        pointList.add(midPoint);
//                                        pointList.add(midPoint2);
//
//                                        if (is3PointsOnSameSegment(pointList, commandMap)){
//                                            continue;
//                                        }
//
//                                        vector.add(firstPoint.getId());
//                                        vector.add(startPoint.getId());
//                                        vector.add(midPoint.getId());
//                                        vector.add(midPoint2.getId());
//
//                                        //第四条边;
//                                        for (int k_4:
//                                                keys){
//                                            if (k_4 == k_3 || k_4 == k_2 || k_4 == k_1){
//                                                continue;
//                                            }
//
//                                            fourthSegment = lineMap.get(k_4);
//                                            Point[] points_4 = {fourthSegment.getStartPoint(), fourthSegment.getEndPoint()};
//
//                                            endPoint = null;
//                                            if (PrimitiveUtil.isSamePointByCommandId(midPoint2, points_4[0])){
//                                                endPoint = points_4[1];
//                                            }else if (PrimitiveUtil.isSamePointByCommandId(midPoint2, points_4[1])){
//                                                endPoint = points_4[0];
//                                            }else {
//                                                continue;
//                                            }
//                                            if (endPoint == null){
//                                                continue;
//                                            }else {
//
//                                                pointList.clear();
//                                                pointList.add(midPoint);
//                                                pointList.add(midPoint2);
//                                                pointList.add(endPoint);
//
//                                                if (is3PointsOnSameSegment(pointList, commandMap)){
//                                                    continue;
//                                                }
//
//                                                if (PrimitiveUtil.isSamePointByCommandId(endPoint, firstPoint)){
//                                                    result += 1;
//                                                }
//                                            }
//                                        }
//                                    }
//
//
//
//                                }
//                            }
//                        }
//                    }
//                }

                break;

            //梯形
            case PrimitiveUtil.COUNT_TYPE_TRAPEZOID:

                //用来存放平行不等边对儿
                List<ParallelSegment> nePPLList = new ArrayList<ParallelSegment>();

                flag = new boolean[MAX+1][MAX+1];

                for (int i :
                        lineMap.keySet()) {
                    for (int j :
                            lineMap.keySet()) {
                        Segment lineA = lineMap.get(i);
                        Segment lineB = lineMap.get(j);
                        //同边则跳过
                        if (PrimitiveUtil.isSameSegment(lineA, lineB) || lineA.getId() == lineB.getId()) {
                            continue;
                        }
                        //已经添加过则跳过
                        if (flag[Math.min(lineA.getId(), lineB.getId())][Math.max(lineA.getId(), lineB.getId())]) {
                            continue;
                        }
                        if (PrimitiveUtil.is2SegmentFitNEPPL(lineA, lineB)) {
                            ParallelSegment parallelSegment = new ParallelSegment(lineA, lineB);
                            if (nePPLList.add(parallelSegment)) {
                                flag[Math.min(lineA.getId(), lineB.getId())][Math.max(lineA.getId(), lineB.getId())] = true;
                            }
                        }
                    }
                }

                for (int i = 0; i<nePPLList.size(); i++){
                    if (nePPLList.get(i).isFormed()){
                        continue;
                    }
                    Segment lineA = nePPLList.get(i).getLineA();
                    Segment lineB = nePPLList.get(i).getLineB();

                    Point lineAStart = PrimitiveUtil.getStartPoint(lineA);
                    Point lineAEnd = PrimitiveUtil.getEndPoint(lineA);

                    Point lineBStart = PrimitiveUtil.getStartPoint(lineB);
                    Point lineBEnd = PrimitiveUtil.getEndPoint(lineB);

                    //期望连接Start点的线段
                    Segment expectLineLinkedStartPoints = new Segment(0);
                    expectLineLinkedStartPoints.setStartPoint(lineAStart);
                    expectLineLinkedStartPoints.setEndPoint(lineBStart);

                    //期望连接End点的线段
                    Segment expectLineLinkedEndPoints = new Segment(0);
                    expectLineLinkedEndPoints.setStartPoint(lineAEnd);
                    expectLineLinkedEndPoints.setEndPoint(lineBEnd);

                    for (int linkStart :
                            lineMap.keySet()) {
                        Segment lineLinkStartPoints = lineMap.get(linkStart);
                        if (PrimitiveUtil.isSameSegment(lineLinkStartPoints, lineA) || PrimitiveUtil.isSameSegment(lineLinkStartPoints, lineB)){
                            continue;
                        }
                        if (PrimitiveUtil.isSameSegment(lineLinkStartPoints, expectLineLinkedStartPoints)){
                            for (int linkEnd:
                                    lineMap.keySet()) {
                                Segment lineLinkedEndPoints = lineMap.get(linkEnd);
                                if (PrimitiveUtil.isSameSegment(lineLinkedEndPoints, lineA) || PrimitiveUtil.isSameSegment(lineLinkedEndPoints, lineB)){
                                    continue;
                                }
                                if (PrimitiveUtil.isSameSegment(lineLinkStartPoints, lineLinkedEndPoints)){
                                    continue;
                                }
                                if (PrimitiveUtil.isSameSegment(lineLinkedEndPoints, expectLineLinkedEndPoints)){
                                    result += 1;
                                }
                            }
                        }

                    }

                }
                break;
            default:
                break;
        }
        return result;
    }

    private boolean is3PointsOnSameSegment(ArrayList<Point> pointList, Map<Integer, String> commandMap) {
        for (int i = 0; i<pointList.size(); i++){

            int id = pointList.get(i).getId();
            if (commandMap.containsKey(id)){

                String commandStr = commandMap.get(id);
                String[] params = commandStr.substring(commandStr.indexOf("(")+1, commandStr.indexOf(")")).split(",");

                Point secondPoint = pointList.get((i + 1)%pointList.size());
                Point thirdPoint = pointList.get((i + 2)%pointList.size());

                if (commandStr.startsWith("Midpoint")){

                    if ((Integer.valueOf(params[0]) == secondPoint.getId() && Integer.valueOf(params[1]) == thirdPoint.getId()) ||
                            (Integer.valueOf(params[1]) == secondPoint.getId() && Integer.valueOf(params[0]) == thirdPoint.getId())){
                        return true;
                    }

                }else if (commandStr.startsWith("PointOnLine")){

                    int lineId = Integer.valueOf(params[0]);
                    String lineCommandStr = commandMap.get(lineId);
                    if (lineCommandStr.startsWith("Segment")){
                        String[] lineParams = lineCommandStr.substring(lineCommandStr.indexOf("(") + 1, lineCommandStr.indexOf(")")).split(",");
                        if ((Integer.valueOf(lineParams[0]) == secondPoint.getId() && Integer.valueOf(lineParams[1]) == thirdPoint.getId()) ||
                                (Integer.valueOf(lineParams[1]) == secondPoint.getId() && Integer.valueOf(lineParams[0]) == thirdPoint.getId())) {
                            return true;
                        }
                    }

                }else if (commandStr.startsWith("IntersectionOfLine")){

                    int lineAId = Integer.valueOf(params[0]);
                    int lineBId = Integer.valueOf(params[1]);

                    String lineCommandStr = commandMap.get(lineAId);
                    if (lineCommandStr.startsWith("Segment")){
                        String[] lineParams = lineCommandStr.substring(lineCommandStr.indexOf("(") + 1, lineCommandStr.indexOf(")")).split(",");

                        if ((Integer.valueOf(lineParams[0]) == secondPoint.getId() && Integer.valueOf(lineParams[1]) == thirdPoint.getId()) ||
                                (Integer.valueOf(lineParams[1]) == secondPoint.getId() && Integer.valueOf(lineParams[0]) == thirdPoint.getId())) {

                            return true;
                        }
                    }

                    lineCommandStr = commandMap.get(lineBId);
                    if (lineCommandStr.startsWith("Segment")){
                        String[] lineParams = lineCommandStr.substring(lineCommandStr.indexOf("(") + 1, lineCommandStr.indexOf(")")).split(",");
                        if ((Integer.valueOf(lineParams[0]) == secondPoint.getId() && Integer.valueOf(lineParams[1]) == thirdPoint.getId()) ||
                                (Integer.valueOf(lineParams[1]) == secondPoint.getId() && Integer.valueOf(lineParams[0]) == thirdPoint.getId())) {

                            return true;
                        }
                    }

                }else if (commandStr.startsWith("Foot")){

                    int lineId = Integer.valueOf(params[1]);

                    String lineCommandStr = commandMap.get(lineId);
                    if (lineCommandStr.startsWith("Segment")){
                        String[] lineParams = lineCommandStr.substring(lineCommandStr.indexOf("(") + 1, lineCommandStr.indexOf(")")).split(",");
                        if ((Integer.valueOf(lineParams[0]) == secondPoint.getId() && Integer.valueOf(lineParams[1]) == thirdPoint.getId()) ||
                                (Integer.valueOf(lineParams[1]) == secondPoint.getId() && Integer.valueOf(lineParams[0]) == thirdPoint.getId())) {
                            return true;
                        }
                    }

                }else {
//                    Toast(pointList.get(0).getId() +"-->"+ pointList.get(1).getId() +"-->"+ pointList.get(2).getId());
//                    return false;
                }
            }
        }
        return false;
    }

    private void figureOutAllLines(String[] commands) {
        assert (commands!=null && commands.length>0);

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
            int[] pointIds = getPointIdBySegmentId(Integer.valueOf(lineParams[0]), commands);

            if (pointIds == null){
                System.out.println("command segment parse failure!");
            }

            startPoint = new Point(pointIds[0], Double.valueOf(lineParams[1]), Double.valueOf(lineParams[2]));
            setPointByCmdId(startPoint, commands);

            endPoint = new Point(pointIds[1], Double.valueOf(lineParams[3]), Double.valueOf(lineParams[4]));
            setPointByCmdId(endPoint, commands);

            segment.setStartPoint(startPoint);
            segment.setEndPoint(endPoint);
            initSegment(segment);

            lineMap.put(Integer.valueOf(lineParams[0]), segment);
        }

        //处理交叉点
        handleIntersectionPoint(commands,commandMap);

    }

    private void handleIntersectionPoint(String[] commands, Map<Integer, String> commandMap) {
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

    private ArrayList<Integer> getStartAndEndPointIdsBySegmentId(Map<Integer, String> commandMap, int segmentId) {
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

    private boolean lineAlreadyExistInMap(Map<Integer, Segment> lineMap, Segment newSegment) {
        for (int key : lineMap.keySet()){
            Segment segment = lineMap.get(key);

            if (PrimitiveUtil.isSameSegment(segment, newSegment)){
                return true;
            }
        }
        return false;
    }

    private void initSegment(Segment segment) {
        Point startPoint = segment.getStartPoint();
        Point endPoint = segment.getEndPoint();

        double sX = startPoint.getX();
        double sY = startPoint.getY();

        double eX = endPoint.getX();
        double eY = endPoint.getY();

        if (DoubleUtil.isEqual(sX, eX)){
            segment.setK(Double.POSITIVE_INFINITY);
            segment.setB(0);
        }else {
            segment.setK(DoubleUtil.changeDouble( (Math.abs(eY - sY))/(Math.abs(eX - sX) )));
            segment.setB(DoubleUtil.changeDouble( sY - segment.getK() * sX ));
        }

        double lengthX = Math.abs(sX - eX);
        double lengthY = Math.abs(sY - eY);

        segment.setLength(DoubleUtil.changeDouble( Math.sqrt(lengthX * lengthX + lengthY * lengthY) ));

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
            }else {
                continue;
            }

        }
    }

    private int[] getPointIdBySegmentId(int segmentId, String[] commands) {
        int[] result = new int[2];

        for (String command:
                commands){
            if (!command.contains("=")){
                continue;
            }

            int primitiveId = Integer.valueOf(command.substring(0, command.indexOf("=")));

            if (primitiveId != segmentId){
                continue;
            }

            String[] params = command.substring(command.indexOf("(") + 1, command.indexOf(")")).split(",");

            if (params.length<2){
                return null;
            }

            result[0] = Integer.valueOf(params[0]);
            result[1] = Integer.valueOf(params[1]);
        }

        return result;
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
                    centerPoint = new Point(Integer.valueOf(comParams[0]));

                    //未设置半径;
                    circle = new Circle(Integer.valueOf(comId));
                    circle.setCenter(centerPoint);

                    circleSet.add(circle);
                }else if (comBody.equalsIgnoreCase("CircleOfTPRadius")){
                    centerPoint = new Point(Integer.valueOf(comParams[0]));

                    Point radiusA = new Point(Integer.valueOf(comParams[1]));
                    Point radiusB = new Point(Integer.valueOf(comParams[2]));

                    //未设置半径
                    circle = new Circle(Integer.valueOf(comId));
                    circle.setCenter(centerPoint);

                    circleSet.add(circle);

                }else if (comBody.equalsIgnoreCase("CircleOfRadius")){
                    centerPoint = new Point(Integer.valueOf(comParams[0]));

                    double radius = Double.valueOf(comParams[1]);

                    circle = new Circle(Integer.valueOf(comId));
                    circle.setCenter(centerPoint);
                    circle.setRadius(radius);
                    circleSet.add(circle);

                }else if (comBody.equalsIgnoreCase("CircleOf3Point")){

                    circle = new Circle(Integer.valueOf(comId));
                    //还没有计算圆的圆心和半径;
                    circleSet.add(circle);
                }
            }

        }
    }

    private void figureOutPrimitives(String[] commands) {
        assert (commands!=null && commands.length>0);
        for (String command :
                commands) {
            if (!command.contains("=")) {
                continue;
            }
            int indexOfEqual = command.indexOf("=");
            int primitiveId = Integer.valueOf(command.substring(0, indexOfEqual));
            int indexOfCommandStart = command.indexOf(":") + 1;
            String com = command.substring(indexOfCommandStart);
            String commandBody = com.substring(0, com.indexOf("("));
            String[] commandParams = com.substring(com.indexOf("(") + 1, com.indexOf(")")).split(",");
            int primitiveType = figureOutPrimitiveType(commandBody);
            //如果是其他类型的命令就直接pass;
            if (primitiveType == 0){
                continue;
            }

            primitiveMap.put(primitiveId, new BaseObject(primitiveId, primitiveType));
        }
    }

    private int figureOutPrimitiveType(String commandBody) {
        int resultType = 0;

        for (String command :
                PrimitiveUtil.TYPE_POINT) {
            if (command.equalsIgnoreCase(commandBody)) {
                resultType = PrimitiveType.TYPE_POINT;
                break;
            }
        }

        for (String command :
                PrimitiveUtil.TYPE_LINE){
            if (resultType==0 && command.equalsIgnoreCase(commandBody)){
                resultType = PrimitiveType.TYPE_SEGMENT;
                break;
            }
        }

        for (String command :
                PrimitiveUtil.TYPE_CIRCLE){
            if (resultType==0 && command.equalsIgnoreCase(commandBody)){
                resultType = PrimitiveType.TYPE_CIRCLE;
                break;
            }
        }

        return resultType;
    }

    private void initBaseOrigin() {
        Point origin = new Point(1,0,0);
        Segment axisX = new Segment(2);
        Segment axisY = new Segment(3);

        primitiveMap.put(origin.getId(), origin);
        primitiveMap.put(axisX.getId(), axisX);
        primitiveMap.put(axisY.getId(), axisY);
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

}
