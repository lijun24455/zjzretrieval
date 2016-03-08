package service;

import lsre.builders.DocumentBuilder;
import lsre.searchers.GenericShapeSearcher;
import lsre.searchers.ShapeSearcher;
import lsre.searchers.ShapeSearcherHits;
import lsre.shapeanalysis.BGAGM.BGAGMFeature;
import lsre.shapeanalysis.primitive.BasicGeometricPrimitiveFeature;
import lsre.utils.SerializationUtils;
import model.CourseWare;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.FSDirectory;
import org.w3c.dom.Element;
import util.PropsUtil;
import util.XmlUtil;
import util.ZjzFileUtil;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;

/**
 * 提供课件的数据服务
 *
 * Created by lijun on 16/1/1.
 */
public class CourseService {

    private static final String FILE_PATH_BASE;
    private static final String FILES_XML_PATH;
    private static final String FEATURE_FILE_PATH;

//    private static final String LSRE_INDEX_PATH;

    static {
        Properties conf = PropsUtil.loadProps("config.properties");
        FILE_PATH_BASE = conf.getProperty("data.base");
        FILES_XML_PATH = FILE_PATH_BASE + conf.getProperty("data.files");
        FEATURE_FILE_PATH = FILE_PATH_BASE + conf.getProperty("data.feature");
//        LSRE_INDEX_PATH = FILE_PATH_BASE;

    }

    public List<CourseWare> getAllCourseList(){
        List<CourseWare> resultList = new ArrayList<CourseWare>();
        File dir = new File(FILE_PATH_BASE);
        File[] files = dir.listFiles();
        if (files == null || files.length < 1){
            return null;
        }
        for (File file : files){
            if (file.getName().endsWith(".zjz")){
                CourseWare ware = new CourseWare();
                ware.setFileName(file.getName());
                ware.setFilePath(file.getPath());
                ware.setImgName(ZjzFileUtil.getImgNameByZjzName(file.getName()));
                ware.setImgPath(ZjzFileUtil.getImgPathByZjzPath(file.getPath()));
                resultList.add(ware);
            }
        }
        return resultList;
    }

    public List<CourseWare> getCourseListByCourseName(String name) throws IOException {
        File queryFile = new File(FILE_PATH_BASE + name);
        List<CourseWare> result = new ArrayList<CourseWare>();
        IndexReader ir = DirectoryReader.open(FSDirectory.open(Paths.get(FILE_PATH_BASE)));
        ShapeSearcher searcher = new GenericShapeSearcher(50, BasicGeometricPrimitiveFeature.class);
        ShapeSearcherHits hits = searcher.search(queryFile, ir);

        CourseWare tmpWare = null;
        for (int i = 0; i<hits.length(); i++){
            String filePath = ir.document(hits.documentID(i)).getValues(lsre.builders.DocumentBuilder.FIELD_NAME_IDENTIFIER)[0];
            byte[] fileProps = ir.document(hits.documentID(i)).getField(lsre.builders.DocumentBuilder.FIELD_NAME_BAG_OF_SHAPES).binaryValue().bytes;
            double[] feature = SerializationUtils.toDoubleArray(fileProps);
            tmpWare = new CourseWare();
            tmpWare.setFilePath(filePath);
            tmpWare.setFileName(parsePath2FileName(filePath));
            tmpWare.setImgName(parsePath2ImgName(filePath));
            tmpWare.setImgPath(parsePath2ImgPath(filePath));
            assert (feature!=null && feature.length == 6);
            tmpWare.setFeature(feature);
            tmpWare.setNumOfPrimitive((int) feature[0]);
            tmpWare.setNumOfLine((int) feature[1]);
            tmpWare.setNumOfCircle((int) feature[2]);
            tmpWare.setNumOfAngle((int) feature[3]);
            tmpWare.setNumOfRectangle((int) feature[4]);
            tmpWare.setNumOfTrapezoid((int) feature[5]);

            result.add(tmpWare);
        }

        return result;

    }

    public List<CourseWare> getCourseListByCourseNameGAG(String name) throws IOException {
        File queryFile = new File(FILE_PATH_BASE + name);
        List<CourseWare> result = new ArrayList<CourseWare>();
        IndexReader ir = DirectoryReader.open(FSDirectory.open(Paths.get(FILE_PATH_BASE)));
        ShapeSearcher searcher = new GenericShapeSearcher(50, BGAGMFeature.class);
        ShapeSearcherHits hits = searcher.search(queryFile, ir);

        CourseWare tmpWare = null;
        for (int i = 0; i<hits.length(); i++){
            String filePath = ir.document(hits.documentID(i)).getValues(lsre.builders.DocumentBuilder.FIELD_NAME_IDENTIFIER)[0];
            byte[] fileProps = ir.document(hits.documentID(i)).getField(DocumentBuilder.FIELD_NAME_BILAYER_GAG_MATCHING).binaryValue().bytes;
            double[] feature = SerializationUtils.toDoubleArray(fileProps);
            tmpWare = new CourseWare();
            tmpWare.setFilePath(filePath);
            tmpWare.setFileName(parsePath2FileName(filePath));
            tmpWare.setImgName(parsePath2ImgName(filePath));
            tmpWare.setImgPath(parsePath2ImgPath(filePath));
            assert (feature!=null && feature.length == 6);
            tmpWare.setFeature(feature);
            tmpWare.setNumOfPrimitive((int) feature[0]);
            tmpWare.setNumOfLine((int) feature[1]);
            tmpWare.setNumOfCircle((int) feature[2]);
            tmpWare.setNumOfAngle((int) feature[3]);
            tmpWare.setNumOfRectangle((int) feature[4]);
            tmpWare.setNumOfTrapezoid((int) feature[5]);

            result.add(tmpWare);
        }

        return result;

    }

    public String parsePath2FileName(String path){
        assert (path != null);
        return path.substring(path.lastIndexOf("/"));
    }

    public String parsePath2ImgName(String path){
        assert (path != null);
        String fileName = parsePath2FileName(path);
        return fileName.replace("zjz", "png");

    }

    public String parsePath2ImgPath(String path){
        assert (path != null);
        return path.replace("zjz", "png");
    }


//    public List<CourseWare> getCourseListByCourseName(String name) {
//        List<CourseWare> result = new ArrayList<CourseWare>();
//
//        ArrayList<String> featureList = readFile2Lines(FEATURE_FILE_PATH);
//
//        if (featureList.size() <= 0){
//            Toast.out("[ERROR]Read feature failure!");
//            return result;
//        }
//
//        Map<String, String> featureMap = new LinkedHashMap<String, String>();
//        for (String line :
//                featureList) {
//            String[] tmp = line.split(":");
//            featureMap.put(tmp[0], tmp[1]);
//        }
//
//        String[] inputFeature = featureMap.get(name.replace("zjz", "htm")).split(",");
//        float input_f1 = Float.valueOf(inputFeature[0]);
//        float input_f2 = Float.valueOf(inputFeature[1]);
//        float input_f3 = Float.valueOf(inputFeature[2]);
//        float input_f4 = Float.valueOf(inputFeature[3]);
//        float input_f5 = Float.valueOf(inputFeature[4]);
//
//        Map<String, Double> resultMap = new LinkedHashMap<String, Double>();
//        for (String line :
//                featureList){
//            String[] tmp = line.split(":");
//            String fileName = tmp[0];
//
//            String[] features = tmp[1].split(",");
//            float file_f1 = Float.valueOf(features[0]);
//            float file_f2 = Float.valueOf(features[1]);
//            float file_f3 = Float.valueOf(features[2]);
//            float file_f4 = Float.valueOf(features[3]);
//            float file_f5 = Float.valueOf(features[4]);
//
//            float result_up = input_f1 * file_f1 +
//                    input_f2 * file_f2 +
//                    input_f3 * file_f3 +
//                    input_f4 * file_f4 +
//                    input_f5 * file_f5;
//
//            float result_base_1 = input_f1 * input_f1 + file_f1 * file_f1;
//            float result_base_2 = input_f2 * input_f2 + file_f2 * file_f2;
//            float result_base_3 = input_f3 * input_f3 + file_f3 * file_f3;
//            float result_base_4 = input_f4 * input_f4 + file_f4 * file_f4;
//            float result_base_5 = input_f5 * input_f5 + file_f5 * file_f5;
//
//            double result_base = Math.sqrt(result_base_1) +
//                    Math.sqrt(result_base_2) +
//                    Math.sqrt(result_base_3) +
//                    Math.sqrt(result_base_4) +
//                    Math.sqrt(result_base_5);
//
//            double score = result_up / result_base;
//
//            //欧式距离
//            double dis_param_1 = Math.abs(input_f1 - file_f1);
//            double dis_param_2 = Math.abs(input_f2 - file_f2);
//            double dis_param_3 = Math.abs(input_f3 - file_f3);
//            double dis_param_4 = Math.abs(input_f4 - file_f4);
//            double dis_param_5 = Math.abs(input_f5 - file_f5);
//            double dis_score = Math.sqrt(dis_param_1 * dis_param_1 + dis_param_2 * dis_param_2 +
//            dis_param_3 * dis_param_3 + dis_param_4 * dis_param_4 + dis_param_5 * dis_param_5);
//
//            //dis_score是欧氏距离;
//            //score是余弦距离;
//            resultMap.put(fileName, dis_score);
//        }
//
//        resultMap = sortByValue(resultMap, false);
//        Set<String> htmSet = resultMap.keySet();
//        CourseWare tmpWare;
//        for (String htmStr :
//                htmSet) {
//            tmpWare = getCourseWareByName(htmStr);
//            result.add(tmpWare);
//        }
//        return result;
//    }

    private CourseWare getCourseWareByName(String htmName) {
        CourseWare ware = new CourseWare();
        ware.setFileName(htmName.replace("htm", "zjz"));
        ware.setImgName(htmName.replace("htm", "png"));
        ware.setFilePath(FILE_PATH_BASE + ware.getFileName());
        ware.setImgPath(FILE_PATH_BASE + ware.getImgName());

        Element root = XmlUtil.getRootElementFromFile(new File(FILES_XML_PATH));
        ArrayList<Element> elementsList = (ArrayList<Element>) XmlUtil.getChildElements(root, "File");
        for (Element element :
                elementsList) {
            String fileName = element.getAttribute("name");
            if (fileName.startsWith(htmName.substring(0, htmName.indexOf(".")))){
                int lineNum = Integer.valueOf(XmlUtil.getElementValue(element, XmlUtil.TAG_ITEM_PROPS_LINE_NUM));
                int primitiveNum = Integer.valueOf(XmlUtil.getElementValue(element, XmlUtil.TAG_ITEM_PROPS_PRIMITIVE_NUM));
                int circleNum = Integer.valueOf(XmlUtil.getElementValue(element, XmlUtil.TAG_ITEM_PROPS_CIRCLE_NUM));
                int angleNum = Integer.valueOf(XmlUtil.getElementValue(element, XmlUtil.TAG_ITEM_PROPS_ANGLE_NUM));
                int rectangleNum = Integer.valueOf(XmlUtil.getElementValue(element, XmlUtil.TAG_ITEM_PROPS_RECTRANGLE_NUM));
                int trapezoidNum = Integer.valueOf(XmlUtil.getElementValue(element, XmlUtil.TAG_ITEM_PROPS_TRAPEZOID_NUM));

                ware.setNumOfPrimitive(primitiveNum);
                ware.setNumOfLine(lineNum);
                ware.setNumOfCircle(circleNum);
                ware.setNumOfAngle(angleNum);
                ware.setNumOfRectangle(rectangleNum);
                ware.setNumOfTrapezoid(trapezoidNum);

                break;
            }else {
                ware.setNumOfPrimitive(0);
                ware.setNumOfLine(0);
                ware.setNumOfCircle(0);
                ware.setNumOfAngle(0);
                ware.setNumOfRectangle(0);
                ware.setNumOfTrapezoid(0);
            }

        }
        return ware;
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue( Map<K, V> map , boolean isReverse) {
        List<Map.Entry<K, V>> list =
                new LinkedList<Map.Entry<K, V>>( map.entrySet() );

        if (isReverse){
            Collections.sort( list, new Comparator<Map.Entry<K, V>>() {
                public int compare( Map.Entry<K, V> o1, Map.Entry<K, V> o2 ) {
                    //o1.(o2)顺序;
                    //o2.(o1)逆序;
                    return (o2.getValue()).compareTo(o1.getValue());
                }
            } );
        }else {
            Collections.sort( list, new Comparator<Map.Entry<K, V>>() {
                public int compare( Map.Entry<K, V> o1, Map.Entry<K, V> o2 ) {
                    //o1.(o2)顺序;
                    //o2.(o1)逆序;
                    return (o1.getValue()).compareTo(o2.getValue());
                }
            } );
        }
        Map<K, V> result = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list) {
            result.put( entry.getKey(), entry.getValue() );
        }
        return result;
    }

    private ArrayList<String> readFile2Lines(String filePath) {
        File file = new File(filePath);
        BufferedReader reader = null;
        ArrayList<String> results = new ArrayList<String>();
        try {
            reader = new BufferedReader(new FileReader(file));
            String lineStr = null;
            while ((lineStr = reader.readLine()) != null){
                results.add(lineStr);
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return results;
    }
}
