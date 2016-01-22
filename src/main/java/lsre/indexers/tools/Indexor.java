package lsre.indexers.tools;

import lsre.builders.DocumentBuilder;
import lsre.builders.PrimitiveDocumentBuilder;
import lsre.indexers.LsreCustomCodec;
import lsre.shapeanalysis.BGAGM.BGAGMFeature;
import lsre.shapeanalysis.primitive.BasicGeometricPrimitiveFeature;
import lsre.utils.LuceneUtils;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import util.ZjzFileUtil;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created by lijun on 16/1/7.
 */
public class Indexor {

    private static final String FILE_PATH_BASE = "/Users/lijun/百度云同步盘/毕业设计/DATA/";
    private LinkedList<File> fileList = new LinkedList<File>();

    public static void main(String[] args) throws IOException {

        Indexor indexor = new Indexor();

        File dir = new File(FILE_PATH_BASE);
        File[] files = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                String fileLast = pathname.getName();
                if (fileLast.endsWith("zjz")){
                    return true;
                }else
                    return false;
            }
        });
        if (files.length > 0){
           for (File file : files){
               if (file.exists()){
                   indexor.addFileToList(file);
               }
           }
        }

        indexor.run();
//        IndexWriter iw = LuceneUtils.createIndexWriter(FILE_PATH_BASE, true, LuceneUtils.AnalyzerType.WhitespaceAnalyzer);
//        for (File file :
//                files) {
//            Document doc = builder.creatDocument(file, file.getPath());
//            iw.addDocument(doc);
//        }
//        LuceneUtils.closeWriter(iw);
    }

    private void run() {
        try {
            PrimitiveDocumentBuilder builder = new PrimitiveDocumentBuilder(BasicGeometricPrimitiveFeature.class);
            builder.addExtractor(BGAGMFeature.class);
//            IndexWriter indexWriter = LuceneUtils.createIndexWriter(FILE_PATH_BASE, false, LuceneUtils.AnalyzerType.WhitespaceAnalyzer);
            IndexWriterConfig config = new IndexWriterConfig(new WhitespaceAnalyzer());
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
//            config.setCodec(new LsreCustomCodec());
            IndexWriter indexWriter = new IndexWriter(FSDirectory.open(Paths.get(FILE_PATH_BASE)), config);
            for (Iterator<File> iterator = fileList.iterator(); iterator.hasNext(); ) {
                File inputFile = iterator.next();
                System.out.println("Processing " + inputFile.getPath() + ".");
                readFile(indexWriter, inputFile, builder);
                System.out.println("Indexing finished.");
            }
            LuceneUtils.commitWriter(indexWriter);
//            LuceneUtils.optimizeWriter(indexWriter);
            LuceneUtils.closeWriter(indexWriter);
//            indexWriter.commit();
//            indexWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readFile(IndexWriter indexWriter, File inputFile, DocumentBuilder builder) {
        Document doc = null;
        try {
            doc = builder.creatDocument(inputFile, inputFile.getPath());
            indexWriter.addDocument(doc);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addFileToList(File file) {
        this.fileList.add(file);
    }

}
