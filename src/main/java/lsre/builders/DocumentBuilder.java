package lsre.builders;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by lijun on 16/1/7.
 */
public interface DocumentBuilder {

    int NUM_OF_THREAD = 16;

    String HASH_FIELD_DUFFIX = "_hash";

    String FIELD_NAME_IDENTIFIER = "ZjzIdentifier";

    String FIELD_NAME_BAG_OF_SHAPES = "BagOfShapes";
    String FIELD_NAME_BILAYER_GAG_MATCHING = "BilayerGeometricAttributedGraph";

    Field[] createDescriptorFields(File file);

    Document creatDocument(File file, String identifier) throws FileNotFoundException;



}
