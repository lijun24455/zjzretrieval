package lsre.searchers;

import lsre.builders.DocumentBuilder;
import lsre.builders.PrimitiveDocumentBuilder;
import lsre.indexers.ExtractorItem;
import lsre.shapeanalysis.LsreFeature;
import lsre.shapeanalysis.PrimitiveFeature;
import lsre.shapeanalysis.PrimitiveFeatureExtractor;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.util.Bits;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.TreeSet;

/**
 * Created by lijun on 16/1/15.
 */
public class GenericShapeSearcher implements ShapeSearcher {

    protected String fieldName;
    protected LsreFeature cachedInstance = null;
    protected ExtractorItem extractorItem;
    protected boolean isCaching = false;

    protected LinkedHashMap<Integer, byte[]> featureCache = null;
    protected IndexReader reader = null;

    protected int maxHits = 20;
    protected TreeSet<SimpleResult> docs = new TreeSet<SimpleResult>();
    protected double maxDistance;
    protected boolean useSimilarityScore = false;


    public GenericShapeSearcher(int maxHits, Class<? extends PrimitiveFeature> primitiveFeature){
        this.maxHits = maxHits;
        this.extractorItem = new ExtractorItem(primitiveFeature);
        this.fieldName = extractorItem.getFieldName();
        try {
            this.cachedInstance = (LsreFeature) extractorItem.getExtractorInstance().getClass().newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        init();
    }

    private void init() {
        if (isCaching && reader != null){
            Bits liveDocs = MultiFields.getLiveDocs(reader);
            int docs = reader.numDocs();
            featureCache = new LinkedHashMap<Integer, byte[]>(docs);
            try {
                Document d;
                for (int i = 0; i<docs; i++){
                    if (!(reader.hasDeletions()) && !liveDocs.get(i)){
                        d = reader.document(i);
                        cachedInstance.setByteArrayRepresentation(d.getField(fieldName).binaryValue().bytes);
                        featureCache.put(i, cachedInstance.getByteArrayRepresentation());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public ShapeSearcherHits search(File file, IndexReader reader) throws IOException {
        SimpleShapeSearchHits searchHits = null;

        PrimitiveDocumentBuilder builder = new PrimitiveDocumentBuilder();
        PrimitiveFeature primitiveFeature = builder.extractFeatures(file, (PrimitiveFeature) extractorItem.getExtractorInstance());

        double maxDistance = findSimilar(reader, primitiveFeature);

        if (!useSimilarityScore){
            searchHits = new SimpleShapeSearchHits(this.docs, maxDistance);
        }else {
            searchHits = new SimpleShapeSearchHits(this.docs, maxDistance, useSimilarityScore);
        }

        return searchHits;
    }



    @Override
    public ShapeSearcherHits search(Document doc, IndexReader reader) throws IOException {

        SimpleShapeSearchHits searchHits = null;

        LsreFeature lsreFeature = extractorItem.getFeatureInstance();

        if (doc.getField(fieldName).binaryValue() != null && doc.getField(fieldName).binaryValue().length > 0){
            lsreFeature.setByteArrayRepresentation(doc.getField(fieldName).binaryValue().bytes);
        }
        double maxDitance = findSimilar(reader, lsreFeature);

        if (!useSimilarityScore){
            searchHits = new SimpleShapeSearchHits(this.docs, maxDitance);
        }else {
            searchHits = new SimpleShapeSearchHits(this.docs, maxDitance, useSimilarityScore);
        }

        return searchHits;

    }

    private double findSimilar(IndexReader reader, LsreFeature lsreFeature) throws IOException {

        maxDistance = -1d;
        docs.clear();

        Bits liveDocs = MultiFields.getLiveDocs(reader);
        Document d;
        double tmpDistance;
        int docs = reader.numDocs();
        if (!isCaching){
            //read each and every document from the index and then we compare it to the query
            for (int i = 0; i<docs; i++){
                if (reader.hasDeletions() && liveDocs.get(i)) continue;
                d = reader.document(i);
                tmpDistance = getDistance(d, lsreFeature);
                assert (tmpDistance > 0);
                //if the array is not full yet
                if (this.docs.size() < maxHits){
                    this.docs.add(new SimpleResult(tmpDistance, i));
                    if (tmpDistance > maxDistance) maxDistance = tmpDistance;
                }else if (tmpDistance < maxDistance){
                    this.docs.remove(this.docs.last());
                    this.docs.add(new SimpleResult(tmpDistance, i));
                    maxDistance = this.docs.last().getDistance();
                }
            }

        } else {
            System.out.println("Please set isCaching false!!!");
        }
        return maxDistance;
    }

    /**
     *
     * @param document doc in index;
     * @param lsreFeature  query input's feature, already extracted!;
     * @return
     */
    private double getDistance(Document document, LsreFeature lsreFeature) {
        if (document.getField(fieldName).binaryValue() != null && document.getField(fieldName).binaryValue().length > 0) {
            cachedInstance.setByteArrayRepresentation(document.getField(fieldName).binaryValue().bytes);

            return lsreFeature.getDistance(cachedInstance);
        } else {
            System.out.println("[Warning!] No feature stored in this document" + document.toString());
        }

        return 0d;
    }

    @Override
    public String toString() {
        return "GenericShapeSearcher{" +
                "fieldName='" + fieldName + '\'' +
                ", cachedInstance=" + cachedInstance +
                ", extractorItem=" + extractorItem +
                ", isCaching=" + isCaching +
                ", featureCache=" + featureCache +
                ", reader=" + reader +
                ", maxHits=" + maxHits +
                ", docs=" + docs +
                ", maxDistance=" + maxDistance +
                ", useSimilarityScore=" + useSimilarityScore +
                '}';
    }
}
