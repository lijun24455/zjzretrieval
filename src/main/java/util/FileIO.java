package util;

import java.io.*;

/**
 * Created by lijun on 16/1/15.
 */
public final class FileIO {

    public static InputStream file2InputStream(File file) throws FileNotFoundException {
        return new FileInputStream(file);
    }


}
