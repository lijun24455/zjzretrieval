package lsre.indexers;

import lsre.shapeanalysis.Extractor;
import lsre.shapeanalysis.LsreFeature;
import lsre.shapeanalysis.PrimitiveFeature;

/**
 * The container for all features;
 *
 * contains{class of extractor, instance of extractor, instance of feature, field name of feature}
 *
 * Created by lijun on 16/1/7.
 *
 * @author lijun
 */
public class ExtractorItem {
    private Class<? extends Extractor> extractorClass;
    private Extractor extractorInstance;
    private LsreFeature featureInstance;

    private String fieldName;

    public ExtractorItem(Class<? extends Extractor> extractorClass){
        if (extractorClass == null) throw new UnsupportedOperationException("extratorClass cannot be null!");
        this. extractorClass = extractorClass;
        try {
            this.extractorInstance =extractorClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        this.fieldName = ((PrimitiveFeature)extractorInstance).getFieldName();
        this.featureInstance = ((PrimitiveFeature) extractorInstance);
    }

    public Class<? extends Extractor> getExtractorClass() {
        return extractorClass;
    }

    public Extractor getExtractorInstance() {
        return extractorInstance;
    }

    public LsreFeature getFeatureInstance() {
        return featureInstance;
    }

    public String getFieldName() {
        return fieldName;
    }

    @Override
    protected ExtractorItem clone() throws CloneNotSupportedException {
        ExtractorItem clone = new ExtractorItem((Class<? extends PrimitiveFeature>) extractorClass);
        return clone;
    }

    @Override
    public String toString() {
        return "ExtractorItem{" +
                "extractorClass=" + extractorClass.getName() +
                ", extractorInstance=" + extractorInstance.toString() +
                ", featureInstance=" + featureInstance.toString() +
                ", fieldName='" + fieldName + '\'' +
                '}';
    }
}
