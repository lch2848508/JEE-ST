package com.estudio.impl.webclient.form;

import java.util.ArrayList;
import java.util.List;

import com.estudio.context.SystemCacheManager;
import com.estudio.define.webclient.form.DesignDataSource;
import com.estudio.web.service.DataService4Lookup;

public final class DataSetCacheService4WebClient {

    // private HashMap<Long, ArrayList<DesignDataSource>> formID2DataSetArray =
    // new HashMap<Long,
    // ArrayList<DesignDataSource>>();
    // private HashMap<String, DesignDataSource> name2DataSet = new
    // HashMap<String,
    // DesignDataSource>();

    /**
     * 清理注册表单中的DataSet对象
     * 
     * @param formID
     */
    public void unregisterFormDataSet(final long formID) {
        final String cacheKey = "FormDataSetList-" + formID;
        @SuppressWarnings("unchecked")
        final List<DesignDataSource> list = (List<DesignDataSource>) SystemCacheManager.getInstance().getDesignObject(cacheKey);
        if (list != null) {
            for (final DesignDataSource ds : list) {
                SystemCacheManager.getInstance().removeDesignObject(ds.getName());
                DataService4Lookup.getInstance().deleteDataSetJSON(-1, ds.getName());
            }
            SystemCacheManager.getInstance().removeDesignObject(cacheKey);
        }
    }

    /**
     * 注册DataSet
     * 
     * @param formID
     * @param dataset
     */
    public void registerDataSet(final long formID, final DesignDataSource dataset) {
        final String cacheKey = "FormDataSetList-" + formID;
        @SuppressWarnings("unchecked")
        List<DesignDataSource> list = (List<DesignDataSource>) SystemCacheManager.getInstance().getDesignObject(cacheKey);
        if (list == null) {
            list = new ArrayList<DesignDataSource>();
            SystemCacheManager.getInstance().putDesignObject(cacheKey, list);
        }
        SystemCacheManager.getInstance().putDesignObject(dataset.getName(), dataset);
        list.add(dataset);
    }

    /**
     * 取得DataSet
     * 
     * @param datasetName
     * @return
     */
    public DesignDataSource getDataSet(final String datasetName) {
        return (DesignDataSource) SystemCacheManager.getInstance().getDesignObject(datasetName);
    }

    private DataSetCacheService4WebClient() {

    }

    private static final DataSetCacheService4WebClient INSTANCE = new DataSetCacheService4WebClient();

    public static DataSetCacheService4WebClient getInstance() {
        return INSTANCE;
    }
}
