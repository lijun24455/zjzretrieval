package lsre.indexers;

import org.apache.lucene.codecs.Codec;
import org.apache.lucene.codecs.FilterCodec;
import org.apache.lucene.codecs.StoredFieldsFormat;
import org.apache.lucene.codecs.lucene54.Lucene54Codec;

/**
 * Created by lijun on 16/1/7.
 */
public final class LsreCustomCodec extends FilterCodec {

    public LsreCustomCodec() {
        super("LsreCustomCodec", new Lucene54Codec());
    }

    @Override
    public StoredFieldsFormat storedFieldsFormat() {
        return new LsreFeatureStoredFieldFormat();
    }
}
