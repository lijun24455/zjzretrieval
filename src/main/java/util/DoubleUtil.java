package util;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Created by lijun on 15/12/19.
 */
public final class DoubleUtil {

    public static boolean isEqual(double a, double b){
        return Math.abs(a-b) < 0.11;
    }

    public static boolean isEqual_K(double a, double b) { return Math.abs(a-b) < 0.1; }

    public static boolean isEqual_Distance(double a, double b){ return  Math.abs(a-b) < 5; }

    public static double changeDouble(Double dou){
        NumberFormat nf = new DecimalFormat("0.0");
        return Double.parseDouble(nf.format(dou));
    }

}
