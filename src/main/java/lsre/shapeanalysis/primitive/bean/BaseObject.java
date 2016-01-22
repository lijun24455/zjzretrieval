package lsre.shapeanalysis.primitive.bean;

/**
 * Created by lijun on 15/12/6.
 */
public class BaseObject {

    private int id;
    private int TYPE;

    public BaseObject(int id) {
        this.id = id;
    }

    public BaseObject(int id, int type){
        this.id = id;
        this.TYPE = type;
    }

    public int getTYPE() {
        return TYPE;
    }

    public void setTYPE(int TYPE) {
        this.TYPE = TYPE;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
