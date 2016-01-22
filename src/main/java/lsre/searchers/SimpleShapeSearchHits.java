package lsre.searchers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Created by lijun on 16/1/15.
 */
public class SimpleShapeSearchHits implements ShapeSearcherHits {
    ArrayList<SimpleResult> results;

    public SimpleShapeSearchHits(Collection<SimpleResult> results, double maxDistance){
        this.results = new ArrayList<SimpleResult>(results.size());
        this.results.addAll(results);
    }

    public SimpleShapeSearchHits(Collection<SimpleResult> results, double maxDistance, boolean useSimilarityScore){
        this.results = new ArrayList<SimpleResult>(results.size());
        this.results.addAll(results);

        for (Iterator<SimpleResult> iterator = this.results.iterator(); iterator.hasNext(); ){
            SimpleResult result = iterator.next();
            if (useSimilarityScore){
                result.setDistance((1d - result.getDistance()) / maxDistance);
            }
        }
    }

    @Override
    public int length() {
        return results.size();
    }

    @Override
    public double score(int position) {
        return results.get(position).getDistance();
    }

    @Override
    public int documentID(int position) {
        return results.get(position).getIndexNumber();
    }
}
