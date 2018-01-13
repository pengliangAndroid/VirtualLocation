package com.wstro.virtuallocation.data.net;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.wstro.app.common.data.DataConstants;
import com.wstro.app.common.utils.LogUtil;
import com.wstro.app.common.utils.NetUtils;
import com.wstro.virtuallocation.BuildConfig;
import com.wstro.virtuallocation.Constants;

import java.io.File;
import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.CookieJar;
import okhttp3.Interceptor;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 *
 * 使用retrofit进行网络访问的入口类
 */
public class RetrofitHelper {

    public static final int DEFAULT_TIMEOUT = DataConstants.DEFAULT_TIMEOUT;

    //private HashMap<String, Object> serviceMap;
    private Context context;
    private OkHttpClient okHttpClient;
    private Retrofit retrofit;

    //private final Lock lock = new ReentrantLock();

    public RetrofitHelper(Context context) {
        this.context = context;
        okHttpClient = createOkHttpClient();
        retrofit = createRetrofit(okHttpClient);
    }

    public OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }


    public <T> T getService(Class<T> tClass) {
        return retrofit.create(tClass);
    }


    @NonNull
    private OkHttpClient createOkHttpClient() {
        //custom OkHttp
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        //timeout
        builder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        builder.writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        builder.readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);

        //token header
        builder.addInterceptor(new HeaderInterceptor());
        builder.addInterceptor(new TokenInterceptor());

        //Log信息拦截器
       HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                if(BuildConfig.DEBUG) {
                    LogUtil.d("HttpHelper",message);
                }
            }
        });
        logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        builder.addInterceptor(logInterceptor);

        //cache
        File httpCacheDirectory = new File(context.getCacheDir(), "OkHttpCache");
        builder.cache(new Cache(httpCacheDirectory, 10 * 1024 * 1024));
        builder.addInterceptor(new CacheControlInterceptor());

        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        CookieJar cookieJar = new JavaNetCookieJar(cookieManager);
        builder.cookieJar(cookieJar);

        return builder.build();
    }

    @NonNull
    public OkHttpClient createTempOkHttpClient() {
        //custom OkHttp
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        //timeout
        builder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        builder.writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        builder.readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);

        //Log信息拦截器
        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                if(BuildConfig.DEBUG) {
                    LogUtil.d("HttpHelper",message);
                }
            }
        });
        logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.addInterceptor(logInterceptor);

        return builder.build();
    }


    private Retrofit createRetrofit(OkHttpClient client){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_DOMAIN)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(client)
                .build();

        return retrofit;
    }


    private class HeaderInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request original = chain.request();
            // Request customization: add request headers
            Request.Builder requestBuilder = original.newBuilder();

            if(DataConstants.accessToken != null) {
                String authorization = original.header("Authorization");
                if(TextUtils.isEmpty(authorization)) {
                    requestBuilder.addHeader("Authorization", DataConstants.accessToken);
                }
            }
            Request request = requestBuilder.build();
            return chain.proceed(request);
        }
    }

    private class TokenInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Response response = chain.proceed(request);

            return response;
        }

        /**
         * 根据Response，判断Token是否失效
         *
         * @param response
         * @return
         */
        private boolean isTokenExpired(Response response) {
            if (response.code() == 401) {
                return true;
            }
            return false;
        }

    }


    private class CacheControlInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            if (!NetUtils.isConnected(context)) {
                request = request.newBuilder()
                        .cacheControl(CacheControl.FORCE_CACHE)
                        .build();
            }

            Response response = chain.proceed(request);

            if (NetUtils.isConnected(context)) {
                int maxAge = 60 * 60; // read from cache for 1 hour
                response.newBuilder()
                        .removeHeader("Pragma")
                        .header("Cache-Control", "public, max-age=" + maxAge)
                        .build();
            } else {
                int maxStale = 60 * 60 * 24 * 28; // tolerate 4-weeks stale
                response.newBuilder()
                        .removeHeader("Pragma")
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                        .build();
            }
            return response;
        }
    }

    public void destroy(){
        okHttpClient.dispatcher().cancelAll();
        context = null;
        okHttpClient = null;
    }
}
