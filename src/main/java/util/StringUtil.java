package util;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by lijun on 15/11/28.
 * 字符串工具类
 *
 * @author lijun
 */
public final class StringUtil {

    public static boolean isEmpty(String str){
        if (str != null){
            str = str.trim();
        }

        return StringUtils.isEmpty(str);
    }

    public static boolean isNotEmpty(String str){
        return !isEmpty(str);
    }

    public static String[] splitString(String str, String regex) {
        return str.split(regex);
    }
}
