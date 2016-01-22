package lsre.searchers;

/**
 * Created by lijun on 16/1/15.
 */
public interface ShapeSearcherHits {

    /**
     * return the size of result list;
     *
     * @return the size of the result list
     */
    public int length();

    /**
     * return the score(distance) of document at given position
     *
     * @param position defines the position
     * @return the score of document at given position, Lower is better.
     */
    public double score(int position);

    /**
     *return the actual document number within the IndexReader.
     *
     * @param position
     * @return
     */
    public int documentID(int position);



}
