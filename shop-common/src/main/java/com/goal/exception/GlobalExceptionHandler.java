package com.goal.exception;

import com.goal.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Result handler(Exception e) {

        log.error("[非业务异常: {}]", e.getMessage());
        return Result.result(-1, "系统内部异常，请联系管理员处理");
    }

    @ExceptionHandler(BizException.class)
    public Result handler(BizException e) {
        log.error("[业务异常: {}]", e.getMessage());
        return Result.result(e.getCode(), e.getMessage());
    }

}
