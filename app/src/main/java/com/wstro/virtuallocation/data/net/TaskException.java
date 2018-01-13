package com.wstro.virtuallocation.data.net;

/**
 * ClassName: TaskException
 * Function:
 * Date:     2017/11/14 0014 16:44
 *
 * @author pengl
 * @see
 */
public class TaskException extends Exception{
    public int code;
    public String message;

    public TaskException(String message, int code) {
        super(message);
        this.message = message;
        this.code = code;
    }

    public TaskException(Throwable throwable, int code) {
        super(throwable);
        this.code = code;
    }
}
