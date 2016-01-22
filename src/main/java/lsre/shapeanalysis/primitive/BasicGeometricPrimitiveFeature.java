package lsre.shapeanalysis.primitive;

import lsre.builders.DocumentBuilder;
import lsre.shapeanalysis.LsreFeature;
import lsre.shapeanalysis.PrimitiveFeature;
import lsre.utils.MetricsUtils;
import lsre.utils.SerializationUtils;

import java.io.File;

/**
 *Wrapper for use of PrimitiveFeature
 *
 * Created by lijun on 16/1/8.
 * @author lijun
 */
public class BasicGeometricPrimitiveFeature extends BasicGeometricPrimitiveImpl implements PrimitiveFeature {

    private String featureName = "BasicGeometricPrimitiveFeature";
    private String fieldName = DocumentBuilder.FIELD_NAME_BAG_OF_SHAPES;

    public BasicGeometricPrimitiveFeature(){}

    public BasicGeometricPrimitiveFeature(File file){
        super(file);
    }

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
        if (data == null) throw new UnsupportedOperationException("you feature data is null");
        return SerializationUtils.toByteArray(data);
    }

    @Override
    public void setByteArrayRepresentation(byte[] featureData) {
        data = SerializationUtils.toDoubleArray(featureData);
    }

    @Override
    public double getDistance(LsreFeature feature) {
        assert (feature.getFeatureVector().length == data.length);
        return MetricsUtils.distL1(feature.getFeatureVector(), data);
    }

    @Override
    public double[] getFeatureVector() {
        return data;
    }

    @Override
    public String toString() {
        return "BasicGeometricPrimitiveFeature{" +
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
}
