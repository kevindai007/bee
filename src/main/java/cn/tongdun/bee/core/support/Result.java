package cn.tongdun.bee.core.support;

/**
 * Created by admin on 17/3/3.
 */
public class Result<T> {

    /**
     * 执行成功状态
     */
    private boolean success = false;

    /**
     * 执行描述信息
     */
    private String message;

    /**
     * 执行描述信息
     */
    private T data;

    public Result() {
    }

    public Result(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public Result(boolean success, T data) {
        this.success = success;
        this.data = data;
    }

    public Result(boolean success) {
        this.success = success;
    }

    public static <R> Result<R> failureResult(String message, R data) {
        return new Result<R>(false, message, data);
    }

    public static <R> Result<R> failureResult(String message) {
        return new Result<R>(false, message, null);
    }

    public static <R> Result<R> failureResult(R data) {
        return new Result<R>(false, null, data);
    }

    public static Result<Void> failureResult() {
        return new Result<Void>(false, null, null);
    }

    public static <R> Result<R> successResult(String message, R data) {
        return new Result<R>(true, message, data);
    }

    public static <R> Result<R> successResult(String message) {
        return new Result<R>(true, message, null);
    }

    public static <R> Result<R> successResult(R data) {
        return new Result<R>(true, null, data);
    }

    public static Result<Void> successResult() {
        return new Result<Void>(true, null, null);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public Result<T> setMessage(String message) {
        this.message = message;
        return this;
    }

    public T getData() {
        return data;
    }

    public Result<T> setData(T data) {
        this.data = data;
        return this;
    }
}
