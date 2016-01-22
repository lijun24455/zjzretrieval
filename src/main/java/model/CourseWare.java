package model;

import java.util.Arrays;

/**
 * 课件Model类
 *
 * Created by lijun on 16/1/1.
 */
public class CourseWare {
    private long id;
    private String fileName;
    private String imgName;
    private String imgPath;
    private String filePath;
    private double[] feature;
    private int numOfLine;
    private int numOfCircle;
    private int numOfAngle;
    private int numOfRectangle;
    private int numOfTrapezoid;
    private int numOfPrimitive;
    private String content;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getImgName() {
        return imgName;
    }

    public void setImgName(String imgName) {
        this.imgName = imgName;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public double[] getFeature() {
        return feature;
    }

    public void setFeature(double[] feature) {
        this.feature = feature;
    }

    public int getNumOfLine() {
        return numOfLine;
    }

    public void setNumOfLine(int numOfLine) {
        this.numOfLine = numOfLine;
    }

    public int getNumOfCircle() {
        return numOfCircle;
    }

    public void setNumOfCircle(int numOfCircle) {
        this.numOfCircle = numOfCircle;
    }

    public int getNumOfAngle() {
        return numOfAngle;
    }

    public void setNumOfAngle(int numOfAngle) {
        this.numOfAngle = numOfAngle;
    }

    public int getNumOfRectangle() {
        return numOfRectangle;
    }

    public void setNumOfRectangle(int numOfRectangle) {
        this.numOfRectangle = numOfRectangle;
    }

    public int getNumOfTrapezoid() {
        return numOfTrapezoid;
    }

    public void setNumOfTrapezoid(int numOfTrapezoid) {
        this.numOfTrapezoid = numOfTrapezoid;
    }

    public int getNumOfPrimitive() {
        return numOfPrimitive;
    }

    public void setNumOfPrimitive(int numOfPrimitive) {
        this.numOfPrimitive = numOfPrimitive;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "CourseWare{" +
                "id=" + id +
                ", fileName='" + fileName + '\'' +
                ", imgName='" + imgName + '\'' +
                ", imgPath='" + imgPath + '\'' +
                ", filePath='" + filePath + '\'' +
                ", feature=" + Arrays.toString(feature) +
                ", numOfLine=" + numOfLine +
                ", numOfCircle=" + numOfCircle +
                ", numOfAngle=" + numOfAngle +
                ", numOfRectangle=" + numOfRectangle +
                ", numOfTrapezoid=" + numOfTrapezoid +
                ", numOfPrimitive=" + numOfPrimitive +
                ", content='" + content + '\'' +
                '}';
    }
}
