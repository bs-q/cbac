package hq.remview.data.remote;

import hq.remview.data.model.api.ResponseListObj;
import hq.remview.data.model.api.ResponseWrapper;
import hq.remview.data.model.api.request.CreateAccountRequest;
import hq.remview.data.model.api.request.LoginRequest;
import hq.remview.data.model.api.request.VerifyQRCodeRequest;
import hq.remview.data.model.api.response.CreateAccountResponse;
import hq.remview.data.model.api.response.LoginResponse;
import hq.remview.data.model.api.response.VerifyQRCodeResponse;
import hq.remview.data.model.api.response.VerifyTokenResponse;
import hq.remview.data.model.api.response.news.NewsDetailResponse;
import hq.remview.data.model.api.response.news.NewsResponse;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @POST("v1/account/login")
    @Headers({"IgnoreAuth: 1"})
    Observable<ResponseWrapper<LoginResponse>> login(@Body LoginRequest request);

    @GET("v1/account/profile")
    Observable<ResponseWrapper<LoginResponse>> profile();

    @POST("v1/agency/register")
    @Headers({"IgnoreAuth: 1"})
    Observable<ResponseWrapper<CreateAccountResponse>> createAccount(@Body CreateAccountRequest request);

    @POST("/v1/device/verify-qrcode")
    Observable<ResponseWrapper<VerifyQRCodeResponse>> verifyQrCode(@Body VerifyQRCodeRequest request);

    @POST("/v1/device/verify-token")
    Observable<ResponseWrapper<VerifyTokenResponse>> verifyToken();

    @GET("/v1/news/client-list")
    Observable<ResponseWrapper<ResponseListObj<NewsResponse>>> listNews(@Query("page") Integer page,@Query("size") Integer size);

    @GET("/v1/news/client-get/{id}")
    Observable<ResponseWrapper<NewsDetailResponse>> newsDetail(@Path("id") Long id);

}
