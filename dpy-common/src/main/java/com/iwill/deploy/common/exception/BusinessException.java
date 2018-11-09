package com.iwill.deploy.common.exception;


import com.iwill.deploy.common.utils.string.StringUtil;

/**
 * Created by shumingl on 2017/08/31.
 * 自定义异常
 */
public class BusinessException extends RuntimeException {
    private String code;
    private String message;
    private Throwable throwable;
    private ExceptionInfo info;

    public BusinessException(ExceptionInfo info) {
        this.info = info;
    }

    public BusinessException(ExceptionInfo info, Throwable cause) {
        super(info.toString(), cause);
        this.info = info;
        this.code = info.getCode();
        this.message = info.getMessage();
        this.throwable = cause;
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
        this.throwable = cause;
    }

    public BusinessException(String message) {
        super(message);
        this.message = message;
    }

    public ExceptionInfo getDetail() {
        return info;
    }

    public String toString() {
        String thisMsg, throwMsg = "";

        if (StringUtil.isNOE(this.code)) thisMsg = this.message;
        else thisMsg = String.format("[CODE-%s]%s", this.code, this.message);

        if (throwable != null) throwMsg = throwable.getMessage();

        return thisMsg + " : " + throwMsg;
    }

    public String getMessage() {
        return this.toString();
    }

}
