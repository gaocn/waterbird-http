package waterbird.space.http.request.param;

/**
 * Created by 高文文 on 2016/12/19.
 */

public enum  CacheMode {
    /**
     * indicating request is un-cachable
     */
    NetOnly,

    /**
     *indicating if request is unavaiable( not created), then use cached request
     */
    NetFirst,

    /**
     *indicating firstly check this request in cache, if this is one then use it, otherwise use new created request
     */
    CacheFirst,

    /**
     *indicating only use cached request, if there is no cached request, then stop this process
     */
    CacheOnly
}
