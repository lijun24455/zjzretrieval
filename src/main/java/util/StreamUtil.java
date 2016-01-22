package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by lijun on 15/11/30.
 * 流操作工具类
 *
 * @author lijun
 * @since 1.0.0
 */
public final class StreamUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(StreamUtil.class);

    public static String getString(InputStream is){
        StringBuilder sb = new StringBuilder();

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String line;
            while ((line = reader.readLine()) != null){
                sb.append(line);
            }
        } catch (Exception e) {
            LOGGER.error("get string failure.", e);
            throw new RuntimeException(e);
        }

        return sb.toString();
    }


}
