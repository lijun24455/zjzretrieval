package lsre.builders;

import lsre.indexers.ExtractorItem;
import lsre.shapeanalysis.PrimitiveFeatureExtractor;
import lsre.shapeanalysis.Extractor;
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
 * Created by lijun on 16/1/7.
 */
public abstract class AbstractLocalDocumentBuilder implements DocumentBuilder {

    protected List<ExtractorItem> extractorItems = new LinkedList<ExtractorItem>();
    protected HashMap<ExtractorItem, String> fieldNamesDictionary = new HashMap<ExtractorItem, String>();

    protected boolean docsCreated = false;

    //extractLocalFeatures()

    //
    public Extractor extractFeatures(File file, Extractor extractor){
        assert (file!=null);

        extractor.extract(file);
        return extractor;
    }


    @Override
    public Field[] createDescriptorFields(File file) {
        docsCreated = true;
        LinkedList<Field> resultList = new LinkedList<Field>();
        Field[] fields;
        if (extractorItems.size()>0){
            for (ExtractorItem extractorItem : extractorItems){
                PrimitiveFeatureExtractor extractor = (PrimitiveFeatureExtractor) extractFeatures(file, extractorItem.getExtractorInstance());
                resultList.add(new StoredField(fieldNamesDictionary.get(extractorItem), extractor.getFeature().getByteArrayRepresentation()));
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
}
