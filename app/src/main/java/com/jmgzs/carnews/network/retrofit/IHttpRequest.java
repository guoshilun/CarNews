package com.jmgzs.carnews.network.retrofit;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**Retrofit http 请求通用接口
 * Created by Wxl on 2017/6/9.
 */

public interface IHttpRequest {

    @GET("{url}")
    Observable<ResponseBody> get(@Path("url") String url, @HeaderMap Map<String, String> headers);

    @POST("{url}")
    Observable<ResponseBody> post(@Path("url") String url, String body, @HeaderMap Map<String, String> headers);
}
