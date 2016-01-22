package lsre.shapeanalysis.primitive;

import lsre.builders.DocumentBuilder;
import lsre.shapeanalysis.PrimitiveFeature;
import lsre.shapeanalysis.PrimitiveFeatureExtractor;

import java.io.File;

/**
 * Implementation based on the paper Bag of Shapes;
 * Created by lijun on 16/1/10.
 *
 * @author lijun
 */
public class BasicGeometricPrimitiveFeatureExtractor implements PrimitiveFeatureExtractor {

    private BasicGeometricPrimitiveFeature feature;
    private String fieldName = DocumentBuilder.FIELD_NAME_BAG_OF_SHAPES;
    private String featureName = DocumentBuilder.FIELD_NAME_BAG_OF_SHAPES;

    public BasicGeometricPrimitiveFeatureExtractor(){
    }

    public BasicGeometricPrimitiveFeatureExtractor(PrimitiveFeature feature){
        this.feature = (BasicGeometricPrimitiveFeature) feature;
    }

    @Override
    public PrimitiveFeature getFeature() {
        return feature;
    }

    @Override
    public void extract(File file) {
        this.feature = new BasicGeometricPrimitiveFeature(file);
    }

    @Override
    public Class<? extends PrimitiveFeature> getClassOfFeature() {
        return BasicGeometricPrimitiveFeature.class;
    }

    public String getFieldName() {
        return fieldName;
    }
}
