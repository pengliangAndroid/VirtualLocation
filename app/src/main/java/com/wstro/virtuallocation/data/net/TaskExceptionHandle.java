package com.wstro.virtuallocation.data.net;

import android.util.Log;

import java.net.ConnectException;

import javax.net.ssl.SSLHandshakeException;

import retrofit2.adapter.rxjava.HttpException;

/**
 * ClassName: TaskExceptionHandle
 * Function:
 * Date:     2017/11/14 0014 16:44
 *
 * @author pengl
 * @see
 */
public class TaskExceptionHandle {

    public TaskExceptionHandle() {
    }

    public static TaskException handleException(Throwable e) {
        Log.i("TaskExceptionHandle", "e.toString = " + e.toString());
        TaskException ex;
        if(e instanceof HttpException) {
            HttpException resultException1 = (HttpException)e;
            ex = new TaskException(e, 1003);
            switch(resultException1.code()) {
                case 401:
                case 403:
                case 408:
                case 500:
                case 504:
                default:
                    ex.message = "访问失败，网络错误";
                    break;
                case 404:
                case 502:
                case 503:
                    ex.message = "服务器异常，请稍后再试";
            }

            return ex;
        }else if(e instanceof ConnectException) {
            ex = new TaskException(e, 1002);
            ex.message = "连接失败";
            return ex;
        } else if(e instanceof SSLHandshakeException) {
            ex = new TaskException(e, 1005);
            ex.message = "证书验证失败";
            return ex;
        } else {
            ex = new TaskException(e, 1000);
            ex.message = "未知错误";
            return ex;
        }
    }
}
