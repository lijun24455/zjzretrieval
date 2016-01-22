package util;

/**
 * Created by lijun on 15/11/28.
 * 转型操作工具类
 */
public final class CastUtil {

    /**
     * 转为String
     * @param obj
     * @return
     */
    public static String castString(Object obj){
        return CastUtil.castString(obj, "");
    }

    public static String castString(Object obj, String defaultValue) {
        return obj != null ? String.valueOf(obj) : defaultValue;
    }

    /**
     * 转为Int
     * @param obj
     * @return
     */
    public static int castInt(Object obj) {
        return CastUtil.castInt(obj, 0);
    }

    public static int castInt(Object obj, int defaultValue) {
        int intValue = defaultValue;
        if (obj != null){
            String strValue = castString(obj);
            if (StringUtil.isNotEmpty(strValue)){
                try {
                    intValue = Integer.parseInt(strValue);
                } catch (NumberFormatException e) {
                    intValue = defaultValue;
                }
            }
        }
        return intValue;
    }

    /**
     * 转为Double
     * @param obj
     * @return
     */
    public static double castDouble(Object obj){
        return CastUtil.castDouble(obj, 0);
    }

    public static double castDouble(Object obj, double defaultValue) {
        double doubleValue = defaultValue;
        if (obj != null){
            String strValue = castString(obj);
            if (StringUtil.isNotEmpty(strValue)){
                try {
                    doubleValue = Double.parseDouble(strValue);
                }catch (NumberFormatException e){
                    doubleValue = defaultValue;
                }
            }
        }
        return doubleValue;
    }

    /**
     * 转为Long
     * @param obj
     * @return
     */
    public static long castLong(Object obj){
        return CastUtil.castLong(obj, 0);
    }

    public static long castLong(Object obj, long defaultValue) {
        long longValue = defaultValue;
        if (obj != null){
            String strValue = castString(obj);
            if (StringUtil.isNotEmpty(strValue)){
                try {
                    longValue = Long.parseLong(strValue);
                }catch (NumberFormatException e){
                    longValue = defaultValue;
                }
            }
        }
        return longValue;
    }

    /**
     * 转为Boolean
     * @param obj
     * @return
     */
    public static boolean castBoolean(Object obj){
        return CastUtil.castBoolean(obj, false);
    }

    private static boolean castBoolean(Object obj, boolean defaultValue) {
        boolean booleanValue = defaultValue;
        if (obj != null){
            booleanValue = Boolean.parseBoolean(castString(obj));
        }
        return booleanValue;
    }

}
