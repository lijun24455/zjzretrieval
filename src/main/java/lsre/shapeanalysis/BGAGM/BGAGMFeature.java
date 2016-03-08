package lsre.shapeanalysis.BGAGM;

import lsre.builders.DocumentBuilder;
import lsre.shapeanalysis.BGAGM.util.BGAGMFeatureUtil;
import lsre.shapeanalysis.BGAGM.util.HungarianAlgorithm;
import lsre.shapeanalysis.LsreFeature;
import lsre.shapeanalysis.PrimitiveFeature;
import lsre.utils.MetricsUtils;
import lsre.utils.SerializationUtils;

import java.io.File;

/**
 * Created by lijun on 16/1/18.
 */
public class BGAGMFeature extends BGAGMFeatureImpl implements PrimitiveFeature {

    private String featureName = "BGAGMFeature";
    private String fieldName = DocumentBuilder.FIELD_NAME_BILAYER_GAG_MATCHING;

    public BGAGMFeature(){}

    public BGAGMFeature(File file) {
        super(file);
    }

    @Override
    public String getFeatureName() {
        return this.featureName;
    }

    @Override
    public String getFieldName() {
        return this.fieldName;
    }

    @Override
    public byte[] getByteArrayRepresentation() {
        if (data == null) throw new UnsupportedOperationException("you feature data is null");
        return SerializationUtils.toByteArray(data);
    }

    @Override
    public void setByteArrayRepresentation(byte[] featureData) {
        data = SerializationUtils.toDoubleArray(featureData);
    }

    @Override
    public double getDistance(LsreFeature feature) {
        System.out.println("compare with feature : " + toastDoubleArray(feature.getFeatureVector()));
        double[] data_1 = this.data;
        double[] data_2 = feature.getFeatureVector();

        int pointNum_1 = (int) data_1[0];
        int pointNum_2 = (int) data_2[0];

        double[] cp_1 = BGAGMFeatureUtil.getCPFromFeatureVector(data_1);
        double[] cp_2 = BGAGMFeatureUtil.getCPFromFeatureVector(data_2);

        double[] layout_1 = BGAGMFeatureUtil.getLayoutFromFeatureVector(data_1);
        double[] layout_2 = BGAGMFeatureUtil.getLayoutFromFeatureVector(data_2);

        double cost_cp = MetricsUtils.cosineCoefficient(cp_1, cp_2);
        double cost_layout = MetricsUtils.distL1(layout_1, layout_2);

        double cost_gag = 0d;
        double[][] distanceMetric;
        if (pointNum_1!=0 && pointNum_2!=0){
            distanceMetric = BGAGMFeatureUtil.getDistanceMetric(BGAGMFeatureUtil.getNodeListFromFeatureVector(pointNum_1, data_1), BGAGMFeatureUtil.getNodeListFromFeatureVector(pointNum_2, data_2));
            cost_gag = HungarianAlgorithm.hgAlgorithm(distanceMetric, "min");
        }

        return cost_cp + cost_layout + cost_gag;
    }

    @Override
    public double[] getFeatureVector() {
        return data;
    }

    @Override
    public String toString() {
        return "BGAGMFeature{" +
                "featureName='" + featureName + '\'' +
                ", fieldName='" + fieldName + '\'' +
//                ", featureVector=" + toastDoubleArray(getFeatureVector()) +
                '}';
    }

    private String toastDoubleArray(double[] array){
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i<array.length; i++){
            builder.append(array[i] + " ");
        }
        return builder.toString();
    }

    private void printMetric(double[][] metric){
        for (int r = 0; r < metric.length; r++){
            for (int c = 0; c < metric[r].length; c++){
                System.out.print(metric[r][c] + ", ");
            }
            System.out.println();
        }
    }
}
