package util;

import lsre.shapeanalysis.primitive.bean.Point;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * Created by lijun on 15/12/6.
 */
public final class ZjzFileUtil {

    public static String[] parseFile2Commands(File file){

        String fileContent = "";
        try {
            FileInputStream is = new FileInputStream(file);
            fileContent = StreamUtil.getString(is);
            if (fileContent.length() == 0){
                throw new Exception("Empty file!");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Document document = Jsoup.parse(fileContent);
        Element bodyElement = document.body();
        Elements allElements = bodyElement.getAllElements();
        Elements objectElements = allElements.tagName("Object");
        StringBuilder sb = new StringBuilder();
        for (Element element: objectElements
                ) {
            if (element.attributes().get("name").equals("DrawText")) {
                sb.append(element.attributes().get("value"));
            }
        }
        String[] commands = sb.toString().split(";");
        return commands;
    }

    public static String getImgNameByZjzName(String fileName){
        return fileName.replace("zjz", "png");
    }

    public static String getImgPathByZjzPath(String path){
        return path.replace("zjz", "png");
    }

    public static Point extractPointByCommandStr(String command){
        assert(command.startsWith("ObjPosition"));
        //1,cmdID pointID;
        //2,point.x;
        //3,point.y;
        ArrayList<Integer> paramArray = extractParamArrayfromCommandStr(command);
        assert (paramArray.size() >= 3);
        Point result = new Point(paramArray.get(0), paramArray.get(1), paramArray.get(2));

        return result;
    }

    private static ArrayList<Integer> extractParamArrayfromCommandStr(String command) {
        assert (command!=null);
        String params = command.substring(command.indexOf("(") + 1, command.indexOf(")"));
        String[] paramArray = params.split(",");
        ArrayList<Integer> result = new ArrayList<Integer>(paramArray.length);
        for (String param :
                paramArray) {
            result.add(Integer.valueOf(param));
        }
        return result;
    }
}
