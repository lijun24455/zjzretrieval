package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;

/**
 * Created by lijun on 15/11/30.
 * 编码与解码操作工具类
 *
 * @author lijun
 * @since 1.0.0
 */
public final class CodecUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(CodecUtil.class);

    /**
     * 将URL编码
     */
    public static String encodeURL(String source){
        String target;
        try {
            target = URLEncoder.encode(source, "UTF-8");
        } catch (Exception e) {
            LOGGER.error("encode url failure", e);
            throw new RuntimeException(e);
        }
        return target;
    }

    /**
     * 将URL解码
     */
    public static String decodeURL(String source){
        String target;
        try {
            target = URLEncoder.encode(source, "UTF-8");
        } catch (Exception e) {
            LOGGER.error("decode url failure", e);
            throw new RuntimeException(e);
        }
        return target;
    }
}
