package com.yyself.tool.exception;

/**
 * @author yangyu
 * @date 2020/6/24 下午7:32
 */
public class KotException extends RuntimeException {
    public KotException(String message, Throwable cause) {
        super(message, cause);
    }

    public KotException(Throwable cause) {
        super(cause);
    }
}
