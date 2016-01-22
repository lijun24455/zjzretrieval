package lsre.shapeanalysis;

import java.io.File;

/**
 * Created by lijun on 16/1/7.
 */
public interface Extractor {

    /**
     * 提取特征向量
     *
     * @param file
     */
    public void extract(File file);
}
