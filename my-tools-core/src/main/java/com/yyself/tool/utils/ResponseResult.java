package com.yyself.tool.utils;


import com.yyself.tool.common.ResultEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yangyu
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseResult {

    private Integer code;
    private String message;
    private String detailMsg;
    private Object data;
    private Long cost;


    public static ResponseResult ok(Object data) {
        return new ResponseResult().success(data);
    }

    public static ResponseResult ok() {
        return new ResponseResult().success();
    }

    public static ResponseResult err(ResultEnum resultEnum) {
        return new ResponseResult().error(resultEnum);
    }

    public static ResponseResult err(ResultEnum resultEnum, String msg) {
        return ResponseResult.builder().code(resultEnum.code).message(resultEnum.message).detailMsg(msg).build();
    }

    public ResponseResult success(Object data) {
        this.code = ResultEnum.SUCCESS.code;
        this.message = ResultEnum.SUCCESS.message;
        this.data = data;
        return this;
    }

    public ResponseResult error(ResultEnum resultEnum) {
        this.code = resultEnum.code;
        this.message = resultEnum.message;
        return this;
    }

    public ResponseResult success() {
        this.success(null);
        return this;
    }

    public ResponseResult(ResultEnum resultEnum) {
        this.code = resultEnum.code;
        this.message = resultEnum.message;
    }

    public ResponseResult(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
