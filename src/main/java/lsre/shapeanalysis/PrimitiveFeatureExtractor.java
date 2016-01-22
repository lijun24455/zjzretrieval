package lsre.shapeanalysis;

import java.util.List;

/**
 * Created by lijun on 16/1/10.
 */
public interface PrimitiveFeatureExtractor extends Extractor {

    public PrimitiveFeature getFeature();

    public Class<? extends PrimitiveFeature> getClassOfFeature();
}
