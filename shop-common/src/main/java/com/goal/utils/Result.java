package com.goal.utils;

import com.goal.enums.BizCodeEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Result<T> {

    private int code;
    private T data;
    private String message;

    public Result(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public static <T> Result<T> result(int code, String message) {
        return new Result<>(code, null, message);
    }

    public static <T> Result<T> success() {
        return new Result<>(BizCodeEnum.OPS_SUCCESS.getCode(), null, BizCodeEnum.OPS_SUCCESS.getMessage());
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(BizCodeEnum.OPS_SUCCESS.getCode(), data, BizCodeEnum.OPS_SUCCESS.getMessage());
    }

    public static <T> Result<T> fail(BizCodeEnum bizCodeEnum) {
        return new Result<>(bizCodeEnum.getCode(), null, bizCodeEnum.getMessage());
    }

    public static <T> Result<T> fail(String message) {
        return new Result<>(BizCodeEnum.OPS_ERROR.getCode(), null, message);
    }

    public static boolean isSuccess(Result result) {
        return result.getCode() == BizCodeEnum.OPS_SUCCESS.getCode();
    }
}
