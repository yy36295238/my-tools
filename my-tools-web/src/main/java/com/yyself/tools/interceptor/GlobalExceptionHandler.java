package com.yyself.tools.interceptor;

import com.yyself.tool.common.ResultEnum;
import com.yyself.tool.utils.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author yangyu
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    @ResponseBody
    @ExceptionHandler(Exception.class)
    public ResponseResult exceptionHandler(Exception ex) {
        log.error("system error: ", ex);
        return ResponseResult.err(ResultEnum.SYS_ERR);
    }
}
