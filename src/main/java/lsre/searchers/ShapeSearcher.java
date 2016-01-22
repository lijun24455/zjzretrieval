package lsre.searchers;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;

import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by lijun on 16/1/15.
 */
public interface ShapeSearcher {

    /**
     * Searches for images similar to given file
     *
     * @param file the example file to search for
     * @param reader the IndexReader which is used to search through the files;
     * @return result(sorted list of hits)
     * @throws IOException
     */
    public ShapeSearcherHits search(File file, IndexReader reader) throws IOException;

    /**
     * Searchers for Shapes similar to the given file(defined by Document from the index)
     *
     * @param doc
     * @param reader
     * @return
     * @throws IOException
     */
    public ShapeSearcherHits search(Document doc, IndexReader reader) throws IOException;


    /**
     * Searches for images similar to the given file;
     *
     * @param stream
     * @param reader
     * @return
     * @throws IOException
     */

}
