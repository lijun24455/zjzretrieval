package lsre.searchers;

import lsre.builders.DocumentBuilder;
import lsre.shapeanalysis.BGAGM.BGAGMFeature;
import lsre.shapeanalysis.FeatureVector;
import lsre.shapeanalysis.PrimitiveFeature;
import lsre.shapeanalysis.primitive.BasicGeometricPrimitiveFeature;
import lsre.utils.SerializationUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * Created by lijun on 16/1/13.
 */
public class Searcher {

    private static final String FILE_PATH_BASE = "/Users/lijun/百度云同步盘/毕业设计/DATA/";


    public static void main(String[] args) throws IOException {

//        PrimitiveFeature feature = new BasicGeometricPrimitiveFeature();

        File indexPath = new File(FILE_PATH_BASE + "ex_7.zjz");

        IndexReader ir = DirectoryReader.open(FSDirectory.open(Paths.get(FILE_PATH_BASE)));
//        ShapeSearcher searcher = new GenericShapeSearcher(50, BasicGeometricPrimitiveFeature.class);
        ShapeSearcher searcher = new GenericShapeSearcher(50, BGAGMFeature.class);



//        for (int i = 0; i<ir.numDocs(); i++){
//            Document doc = ir.document(i);
//            String fileName = doc.getValues(DocumentBuilder.FIELD_NAME_IDENTIFIER)[0];
//            double[] fileProps = SerializationUtils.toDoubleArray(doc.getField(DocumentBuilder.FIELD_NAME_BILAYER_GAG_MATCHING).binaryValue().bytes);
//            System.out.println(doc.getValues(DocumentBuilder.FIELD_NAME_IDENTIFIER)[0] + " : " +
//                               array2String(fileProps));
//        }


        System.out.println("start searching....");
        long time = System.currentTimeMillis();
        ShapeSearcherHits hits = searcher.search(indexPath, ir);
        for (int i = 0; i < hits.length(); i++) {
            String fileName = ir.document(hits.documentID(i)).getValues(DocumentBuilder.FIELD_NAME_IDENTIFIER)[0];
            byte[] fileProps = ir.document(hits.documentID(i)).getField(DocumentBuilder.FIELD_NAME_BILAYER_GAG_MATCHING).binaryValue().bytes;
            double[] feature = SerializationUtils.toDoubleArray(fileProps);
            System.out.println(hits.score(i) + ": \t" + fileName + ", " + array2String(feature));
        }
        System.out.println("time cost:" + (System.currentTimeMillis() - time));

    }

    public static String array2String(double[] array){
        StringBuilder builder = new StringBuilder();
        for (double num:
                array){
            builder.append(num + ", ");
        }
        return builder.toString();
    }
}
