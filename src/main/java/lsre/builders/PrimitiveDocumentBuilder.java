package lsre.builders;

import lsre.indexers.ExtractorItem;
import lsre.shapeanalysis.Extractor;
import lsre.shapeanalysis.PrimitiveFeature;
import lsre.shapeanalysis.PrimitiveFeatureExtractor;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by lijun on 16/1/10.
 */
public class PrimitiveDocumentBuilder implements DocumentBuilder {

    protected List<ExtractorItem> extractorItems = new LinkedList<ExtractorItem>();
    protected HashMap<ExtractorItem, String> fieldNamesDictionary = new HashMap<ExtractorItem, String>();

    protected boolean docsCreated = false;

    public PrimitiveDocumentBuilder(){

    }

    public PrimitiveDocumentBuilder(Class<? extends PrimitiveFeature> primitiveFeatureClass){
        addExtractor(primitiveFeatureClass);
    }

    public void addExtractor(Class<? extends PrimitiveFeature> primitiveFeatureClass) {
        addExtractor(new ExtractorItem(primitiveFeatureClass));
    }

    private void addExtractor(ExtractorItem extractorItem) {
        String fieldName = extractorItem.getFieldName();

        extractorItems.add(extractorItem);
        fieldNamesDictionary.put(extractorItem, extractorItem.getFieldName());
        System.out.println("ADD EXTRACTORITEM:" + extractorItem.toString());
    }


    @Override
    public Field[] createDescriptorFields(File file) {
        docsCreated = true;
        LinkedList<Field> resultList = new LinkedList<Field>();
        Field[] fields;
        if (extractorItems.size()>0){
            for (ExtractorItem extractorItem : extractorItems){
                PrimitiveFeature feature = extractFeatures(file, (PrimitiveFeature) extractorItem.getFeatureInstance());
                resultList.add(new StoredField(fieldNamesDictionary.get(extractorItem), feature.getByteArrayRepresentation()));
            }
        }
        return resultList.toArray(new Field[resultList.size()]);
    }

    @Override
    public Document creatDocument(File file, String identifier) throws FileNotFoundException {
        Document doc = new Document();

        if (identifier != null){
            doc.add(new StringField(DocumentBuilder.FIELD_NAME_IDENTIFIER, identifier, Field.Store.YES));
        }

        //add other fields?
        Field[] fields = createDescriptorFields(file);
        for (Field field : fields){
            doc.add(field);
        }
        return doc;
    }

    public PrimitiveFeature extractFeatures(File file, PrimitiveFeature primitiveFeature){
        assert (file!=null);
        primitiveFeature.extract(file);
        return primitiveFeature;
    }

}
