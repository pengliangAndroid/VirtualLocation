package com.wstro.virtuallocation.delegate;

import java.util.concurrent.CountDownLatch;

/**
 * @author pengl
 */

public class InstallTask  {

    private String taskName;

    private CountDownLatch countDownLatch ;

    public InstallTask(int taskSize){
        if(taskSize == 0)
            throw new IllegalArgumentException("taskSize is 0.");

        countDownLatch = new CountDownLatch(taskSize);
    }

    public void finishTask(){
        countDownLatch.countDown();
    }

    public long getCount(){
        return countDownLatch.getCount();
    }



}
