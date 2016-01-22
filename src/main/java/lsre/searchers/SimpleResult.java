package lsre.searchers;

/**
 * Created by lijun on 16/1/15.
 */
public class SimpleResult implements Comparable<SimpleResult> {

    private double distance = -1d;
    private int indexNumber = -1;

    /**
     * Constructor for a result
     *
     * @param distance
     * @param indexNumber
     */
    public SimpleResult(double distance, int indexNumber){
        this.distance = distance;
        this.indexNumber = indexNumber;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getIndexNumber() {
        return indexNumber;
    }

    public void setIndexNumber(int indexNumber) {
        this.indexNumber = indexNumber;
    }

    /**
     * Compare the distance values to allow sorting in a treeMap
     *
     * @param o
     * @return
     */
    @Override
    public int compareTo(SimpleResult o) {
        int compareValue = (int) Math.signum(distance - ((SimpleResult) o).distance);
        if (compareValue==0 && indexNumber != o.indexNumber) {
            return (int) Math.signum(indexNumber-o.indexNumber);
        }
        return compareValue;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SimpleResult))
            return false;
        else return (indexNumber == ((SimpleResult) obj).indexNumber);
    }
}
