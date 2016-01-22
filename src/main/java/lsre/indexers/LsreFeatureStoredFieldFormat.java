package lsre.indexers;

import org.apache.lucene.codecs.StoredFieldsFormat;
import org.apache.lucene.codecs.compressing.CompressingStoredFieldsFormat;
import org.apache.lucene.codecs.compressing.CompressionMode;

/**
 * Created by lijun on 16/1/7.
 */
public class LsreFeatureStoredFieldFormat extends CompressingStoredFieldsFormat {

    public LsreFeatureStoredFieldFormat(){
        super("LsreFeatureStoredFieldFormat", CompressionMode.FAST_DECOMPRESSION, 1<<14, 128, 1024);
    }
}
