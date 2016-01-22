package lsre.shapeanalysis;


import lsre.utils.MetricsUtils;
import lsre.utils.SerializationUtils;

/**
 * Created by lijun on 16/1/7.
 *
 * @author lijun
 */
public class GenericDoubleLsreFeature implements LsreFeature {

    private double[] data = null;
    private String featureName = "DoubleLsreFeature";
    private String fieldName = "GenericDouble";

    @Override
    public String getFeatureName() {
        return featureName;
    }

    @Override
    public String getFieldName() {
        return fieldName;
    }

    @Override
    public byte[] getByteArrayRepresentation() {
        return new byte[0];
    }

    @Override
    public void setByteArrayRepresentation(byte[] featureData) {
        this.data = SerializationUtils.toDoubleArray(featureData);

    }

    @Override
    public double getDistance(LsreFeature feature) {

        assert(feature.getFeatureVector().length == data.length);
        return MetricsUtils.distL2(feature.getFeatureVector(), data);
    }

    @Override
    public double[] getFeatureVector() {
        return data;
    }


    public void setData(double[] data){
        this.data = data;
    }

    public void setFieldName(String fieldName){
        this.fieldName = fieldName;
    }

    public void setFeatureName(String featureName){
        this.featureName = featureName;
    }
}
