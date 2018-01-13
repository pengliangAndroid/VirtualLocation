package com.wstro.virtuallocation.data.net;

import android.content.Context;

import com.wstro.app.common.utils.LogUtil;

import rx.Subscriber;

/**
 * ClassName: TaskSubsciber
 * Function:
 * Date:     2017/11/14 0014 16:10
 *
 * @author pengl
 * @see
 */
public abstract class TaskSubscriber<R> extends Subscriber<R>{
    private Context context;

    public TaskSubscriber() {
    }

    public TaskSubscriber(Context context) {
        this.context = context;
    }

    public void onStart() {
        super.onStart();
        LogUtil.d("ApiSubscriber.onStart()");
    }

    public void onError(Throwable e) {

        LogUtil.d("ApiSubscriber.throwable =" + e.toString());
        LogUtil.d("ApiSubscriber.throwable =" + e.getMessage());
        if(e instanceof Exception) {
            this.onFail(TaskExceptionHandle.handleException(e));
        } else {
            this.onFail(new TaskException(e, 1000));
        }

    }

    public void onNext(R data) {
        onSuccess(data);
    }

    public void onCompleted() {
        LogUtil.d("ApiSubscriber.onCompleted()");
    }

    protected abstract void onFail(TaskException ex);

    public abstract void onSuccess(R data);
}
