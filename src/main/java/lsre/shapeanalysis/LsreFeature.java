package lsre.shapeanalysis;

/**
 * Created by lijun on 16/1/7.
 *
 *
 * @author lijun
 */
public interface LsreFeature extends FeatureVector{

    public String getFeatureName();

    public String getFieldName();

    public byte[] getByteArrayRepresentation();

    public void setByteArrayRepresentation(byte[] featureData);

    public double getDistance(LsreFeature feature);


}
