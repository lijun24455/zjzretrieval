package lsre.shapeanalysis.BGAGM.util;

import lsre.shapeanalysis.BGAGM.util.Node;
import lsre.utils.MetricsUtils;
import lsre.utils.SerializationUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lijun on 16/1/21.
 */
public final class BGAGMFeatureUtil {


    public static double[] getCPFromFeatureVector(double[] feature) {
        double[] result = new double[8];
        int index = 0;
        for (int i = 1; i <= 8; i++){
            result[index++] = feature[i];
        }
        return result;
    }

    public static double[] getLayoutFromFeatureVector(double[] feature) {
        double[] result = new double[8];
        int index = 0;
        for (int i = 9; i <= 16; i++){
            result[index++] = feature[i];
        }
        return result;
    }

    public static List<Node> getNodeListFromFeatureVector(int pointNum, double[] feature){

        ArrayList<Node> result = new ArrayList<Node>(pointNum);
        int num = 0;
        ArrayList<Double> nodeFeature = new ArrayList<Double>(6);
        int nodeID = 0;
        for (int i = 17; i<feature.length; i++){
            nodeFeature.add(feature[i]);
            num++;
            if (num == 6){
                result.add(new Node(nodeID++, SerializationUtils.doubleListToDoubleArray(nodeFeature)));
                num = 0;
                nodeFeature.clear();
            }
        }
        return result;
    }

    public static double[][] getDistanceMetric(List<Node> nodes_1, List<Node> nodes_2){
        double[][] result = new double[nodes_1.size()][nodes_2.size()];

        for (Node node_1:
                nodes_1){
            for (Node node_2:
                    nodes_2){
                result[node_1.getId()][node_2.getId()] = MetricsUtils.distL2(node_1.getFeature(), node_2.getFeature());
            }
        }

        return result;
    }

}
