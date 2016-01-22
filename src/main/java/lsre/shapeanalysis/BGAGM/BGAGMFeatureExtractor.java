package lsre.shapeanalysis.BGAGM;

import lsre.builders.DocumentBuilder;
import lsre.shapeanalysis.PrimitiveFeature;
import lsre.shapeanalysis.PrimitiveFeatureExtractor;
import org.omg.CORBA.PUBLIC_MEMBER;

import java.io.File;

/**
 * Created by lijun on 16/1/21.
 */
public class BGAGMFeatureExtractor implements PrimitiveFeatureExtractor {

    private BGAGMFeature feature;
    private String fieldName = DocumentBuilder.FIELD_NAME_BILAYER_GAG_MATCHING;
    private String featureName = DocumentBuilder.FIELD_NAME_BILAYER_GAG_MATCHING;

    public BGAGMFeatureExtractor(){}

    public BGAGMFeatureExtractor(PrimitiveFeature feature){
        this.feature = (BGAGMFeature) feature;
    }

    @Override
    public PrimitiveFeature getFeature() {
        return feature;
    }

    @Override
    public Class<? extends PrimitiveFeature> getClassOfFeature() {
        return BGAGMFeature.class;
    }

    @Override
    public void extract(File file) {
        this.feature = new BGAGMFeature(file);
    }
}
