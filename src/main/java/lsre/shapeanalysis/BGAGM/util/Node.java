package lsre.shapeanalysis.BGAGM.util;

import java.util.Arrays;

/**
 * Created by lijun on 16/1/21.
 */
public class Node {

    private int id;
    private double[] feature;

    public Node(int id, double[] feature) {
        this.id = id;
        this.feature = feature;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double[] getFeature() {
        return feature;
    }

    public void setFeature(double[] feature) {
        this.feature = feature;
    }

    @Override
    public String toString() {
        return "node{" +
                "id=" + id +
                ", feature=" + Arrays.toString(feature) +
                '}';
    }
}
