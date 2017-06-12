package com.jmgzs.carnews.network.retrofit;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Url;

/**Retrofit http 请求通用接口
 * Created by Wxl on 2017/6/9.
 */

public interface IHttpRequest {

    @GET()
    Observable<ResponseBody> get(@Url String url, @HeaderMap Map<String, String> headers);

    @POST()
    Observable<ResponseBody> post(@Url String url, @Body RequestBody body, @HeaderMap Map<String, String> headers);
}
