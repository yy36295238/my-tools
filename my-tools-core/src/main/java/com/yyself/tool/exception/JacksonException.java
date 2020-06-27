package com.yyself.tool.exception;

/**
 * @author yangyu
 * @date 2020/6/18 下午10:36
 */
public class JacksonException extends RuntimeException {
    public JacksonException() {
        super();
    }

    public JacksonException(String message) {
        super(message);
    }

    public JacksonException(Throwable cause) {
        super(cause);
    }

    public JacksonException(String message, Throwable cause) {
        super(message, cause);
    }

}
