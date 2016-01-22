package lsre.shapeanalysis;

/**
 * Created by lijun on 16/1/7.
 *
 * 特征值抽象接口
 *
 * @author lijun
 */
public interface FeatureVector {
    /**
     * Convenience method to get the feature vector as double[] array.
     * @return the feature vector as a double[] array.
     */
    public double[] getFeatureVector();

}
