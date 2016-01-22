package util;

import org.apache.commons.lang3.ArrayUtils;

/**
 * Created by lijun on 15/11/30.
 * 数组工具类
 *
 * @author lijun
 * @since 1.0.0
 */
public final class ArrayUtil {

    public static boolean isNotEmpty(Object[] array){
        return !ArrayUtils.isEmpty(array);
    }

    public static boolean isEmpty(Object[] array){
        return ArrayUtils.isEmpty(array);
    }

}
